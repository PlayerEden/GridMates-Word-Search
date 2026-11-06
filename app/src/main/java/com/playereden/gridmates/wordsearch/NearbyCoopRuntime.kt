package com.playereden.gridmates.wordsearch

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.security.SecureRandom
import java.util.Collections
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Only protocol-compatible devices on a trusted local network can join. */
private const val WS_WIRE_VERSION = 1
private const val WS_NSD_TYPE = "_gridmatesws._tcp."
private const val WS_PACKET_MAX = 16_384

internal data class NearbyListing(
    val roomId: String, val name: String, val address: String, val port: Int,
    val requiresCode: Boolean
)

internal enum class NearbyRole { NONE, HOST, GUEST }

internal data class NearbyUiState(
    val role: NearbyRole = NearbyRole.NONE,
    val busy: Boolean = false,
    val scanning: Boolean = false,
    val listings: List<NearbyListing> = emptyList(),
    val message: String = "",
    val roomId: String = "",
    val address: String = "",
    val port: Int = 0,
    val code: String = "",
    val requiresCode: Boolean = false,
    val hostName: String = "",
    val guestName: String = "",
    val snapshot: CoopSnapshot? = null,
    val disconnected: Boolean = false
)

/** Small, bounded length-prefixed JSON wire; never deserialize arbitrary objects. */
internal object NearbyPacket {
    fun write(socket: Socket, value: JSONObject) {
        val bytes = value.put("v", WS_WIRE_VERSION).toString().toByteArray(Charsets.UTF_8)
        require(bytes.size in 1..WS_PACKET_MAX) { "Message too large" }
        synchronized(socket) {
            val out = DataOutputStream(socket.getOutputStream())
            out.writeInt(bytes.size)
            out.write(bytes)
            out.flush()
        }
    }

    fun read(socket: Socket): JSONObject {
        val input = DataInputStream(socket.getInputStream())
        val len = input.readInt()
        if (len !in 1..WS_PACKET_MAX) throw IOException("Invalid message size")
        val data = ByteArray(len)
        input.readFully(data)
        return JSONObject(String(data, Charsets.UTF_8)).also {
            if (it.optInt("v", -1) != WS_WIRE_VERSION)
                throw IOException("The two phones need the same version.")
        }
    }

    fun state(s: CoopSnapshot): JSONObject {
        val spec = s.spec
        val claims = JSONArray()
        s.claims.forEach { claim ->
            claims.put(JSONObject().put("id", claim.wordId)
                .put("seat", claim.seat.name).put("rev", claim.revision))
        }
        return JSONObject().put("type", "state").put("room", s.roomId)
            .put("theme", spec.theme.name).put("board", spec.board.name)
            .put("challenge", spec.challenge.name).put("seed", spec.seed)
            .put("puzzleId", spec.puzzleId).put("digest", spec.boardFingerprint)
            .put("revision", s.revision).put("claims", claims)
            .put("reserved", s.guestReserved).put("guestHere", s.guestConnected)
            .put("hostHere", s.hostConnected).put("finished", s.finished)
    }

    fun parseState(json: JSONObject): CoopSnapshot {
        require(json.optString("type") == "state")
        val spec = CoopPuzzleSpec(
            WordTheme.valueOf(json.getString("theme")),
            PuzzleBoardSize.valueOf(json.getString("board")),
            PuzzleChallenge.valueOf(json.getString("challenge")),
            json.getLong("seed"), json.getString("puzzleId"), json.getString("digest")
        )
        val arr = json.getJSONArray("claims")
        require(arr.length() <= 30) { "Too many claims" }
        val claims = (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            CoopWordClaim(obj.getString("id"),
                CoopSeat.valueOf(obj.getString("seat")), obj.getLong("rev"))
        }
        return CoopSnapshot(json.getString("room"), spec, json.getLong("revision"),
            claims, json.getBoolean("reserved"), json.getBoolean("guestHere"),
            json.getBoolean("hostHere"), json.getBoolean("finished"))
            .also { NearbyCoopSession.validateSnapshot(it) }
    }
}

/**
 * Android LAN bridge. Host owns all state; guest only proposes endpoints.
 * Work is on IO threads and state publication is on the main thread.
 * A room reserves its first guest's cryptographic token for reconnect.
 * Stopping the host closes its room; no fake cloud persistence.
 */
internal class NearbyCoopRuntime(context: Context) : AutoCloseable {
    private val app = context.applicationContext
    private val prefs = app.getSharedPreferences("ws05_nearby", Context.MODE_PRIVATE)
    private val nsd = app.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val main = Handler(Looper.getMainLooper())
    private val io = Executors.newCachedThreadPool { runnable ->
        Thread(runnable, "ws05-lan").apply { isDaemon = true }
    }
    private val heartbeat = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "ws05-heartbeat").apply { isDaemon = true }
    }
    private val lock = Any()
    private var closed = false
    private var listener: ServerSocket? = null
    private var peer: Socket? = null
    private var client: Socket? = null
    private var authority: NearbyCoopSession? = null
    private var reservedToken: String? = null
    private var registration: NsdManager.RegistrationListener? = null
    private var discovery: NsdManager.DiscoveryListener? = null
    private val invalidCodes = mutableMapOf<String, MutableList<Long>>()
    @Volatile private var peerLastReadNanos: Long = 0L
    private var hostRoomCode = ""
    private var liveHostName = ""
    private var lastHost = ""
    private var lastPort = 0

    var state by mutableStateOf(NearbyUiState())
        private set

    var requireCodePreference by mutableStateOf(prefs.getBoolean("requires_code", false))
        private set

    private fun post(update: (NearbyUiState) -> NearbyUiState) {
        main.post { if (!closed) state = update(state) }
    }

    fun updateRequireCodePreference(yes: Boolean) {
        requireCodePreference = yes
        prefs.edit().putBoolean("requires_code", yes).apply()
    }

    private fun secret(): String = ByteArray(20).also { SecureRandom().nextBytes(it) }
        .joinToString("") { "%02x".format(it.toInt() and 255) }

    private fun localWifiIp(): InetAddress? {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            .filter { runCatching { it.isUp && !it.isLoopback }.getOrDefault(false) }
            .sortedBy { if (it.name.startsWith("wlan") || it.name.startsWith("ap")) 0 else 1 }
        return interfaces.asSequence().flatMap { Collections.list(it.inetAddresses).asSequence() }
            .firstOrNull { it is Inet4Address && it.isSiteLocalAddress && !it.isLoopbackAddress }
    }

    fun host(name: String, theme: WordTheme, board: PuzzleBoardSize,
             challenge: PuzzleChallenge) {
        if (state.busy || state.role != NearbyRole.NONE) return
        post { it.copy(busy = true, message = "Opening your nearby game…") }
        io.execute {
            try {
                val wifi = localWifiIp() ?: throw IOException(
                    "Connect to a local Wi-Fi network to host a game.")
                val run = SoloProgressRules.freshV2(theme, board, challenge,
                    System.currentTimeMillis())
                val session = NearbyCoopSession(run)
                val socket = ServerSocket(0, 2, wifi)
                val roomName = name.trim().take(24).ifBlank { "Player" }
                synchronized(lock) {
                    if (closed) { socket.close(); return@execute }
                    authority = session
                    liveHostName = roomName
                    listener = socket
                    hostRoomCode = if (requireCodePreference)
                        SecureRandom().nextInt(1_000_000).toString().padStart(6, '0') else ""
                }
                post { it.copy(role = NearbyRole.HOST, busy = false,
                    message = "Room open. Ask a friend to join on the same Wi-Fi.",
                    roomId = session.roomId, address = wifi.hostAddress ?: "",
                    port = socket.localPort, code = hostRoomCode,
                    requiresCode = hostRoomCode.isNotBlank(),
                    hostName = roomName, snapshot = session.snapshot()) }
                main.post {
                    if (!closed) advertise(session.roomId, roomName, socket.localPort,
                        hostRoomCode.isNotBlank())
                }
                io.execute { acceptLoop(socket) }
                heartbeat.scheduleAtFixedRate({
                    val live = synchronized(lock) { peer }
                    if (live != null) {
                        if (System.nanoTime() - peerLastReadNanos >
                            TimeUnit.SECONDS.toNanos(20)) dropGuest(live)
                        else runCatching {
                            NearbyPacket.write(live, JSONObject().put("type", "ping"))
                        }.onFailure { dropGuest(live) }
                    }
                }, 5, 5, TimeUnit.SECONDS)
            } catch (e: Exception) {
                post { it.copy(busy = false, message = e.message ?: "Could not host.") }
            }
        }
    }

    private fun advertise(id: String, name: String, port: Int, needsCode: Boolean) {
        val info = NsdServiceInfo().apply {
            serviceType = WS_NSD_TYPE
            serviceName = "GridMates - " + name
            this.port = port
            setAttribute("room", id)
            setAttribute("pin", if (needsCode) "1" else "0")
        }
        val cb = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) = Unit
            override fun onRegistrationFailed(info: NsdServiceInfo, code: Int) {
                post { it.copy(message = "Room open, but discovery isn't available. Share your host address.") }
            }
            override fun onServiceUnregistered(info: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(info: NsdServiceInfo, code: Int) = Unit
        }
        registration = cb
        runCatching { nsd.registerService(info, NsdManager.PROTOCOL_DNS_SD, cb) }
            .onFailure {
                post { it.copy(message = "Room open. Share your host address to join manually.") }
            }
    }

    private fun acceptLoop(server: ServerSocket) {
        while (!closed && !server.isClosed) {
            val socket = try { server.accept() } catch (_: IOException) { break }
            io.execute { handleGuest(socket) }
        }
    }

    private fun handleGuest(socket: Socket) {
        var admitted = false
        try {
            socket.soTimeout = 5_000
            socket.tcpNoDelay = true
            val hello = NearbyPacket.read(socket)
            fun reject(message: String): Nothing {
                runCatching { NearbyPacket.write(socket,
                    JSONObject().put("type", "error").put("message", message)) }
                throw IOException(message)
            }
            if (hello.optString("type") != "hello") reject("Invalid join request.")
            val name = hello.optString("name").trim().take(24).ifBlank { "Player" }
            val session = synchronized(lock) { authority } ?: reject("Room closed.")
            if (hello.optString("room").isNotBlank() &&
                hello.optString("room") != session.roomId) reject("This room has changed.")
            synchronized(lock) {
                if (closed || listener == null) reject("Room closed.")
                if (peer != null) reject("This room already has two players.")
                val current = reservedToken
                val token = hello.optString("token")
                if (current != null && token != current)
                    reject("This room is reserved for its previous partner.")
                if (current == null && hostRoomCode.isNotBlank() &&
                    hello.optString("code") != hostRoomCode) {
                    val address = socket.inetAddress.hostAddress ?: "unknown"
                    val now = System.currentTimeMillis()
                    val attempts = invalidCodes.getOrPut(address) { mutableListOf() }
                    attempts.removeAll { now - it > 60_000L }
                    if (attempts.size >= 5) reject("Too many incorrect codes. Try later.")
                    attempts.add(now)
                    reject("Incorrect room code.")
                }
                if (current == null) reservedToken = secret()
                peer = socket
                peerLastReadNanos = System.nanoTime()
                admitted = true
                val snapshot = session.admitOrRejoinGuest()
                val welcome = JSONObject().put("type", "welcome")
                    .put("token", reservedToken).put("name", name)
                    .put("hostName", liveHostName)
                    .put("roomState", NearbyPacket.state(snapshot))
                NearbyPacket.write(socket, welcome)
                post { it.copy(guestName = name, snapshot = snapshot,
                    message = "Connected with " + name + ". Find words together!") }
            }
            socket.soTimeout = 11_000
            while (!closed && !socket.isClosed) {
                val msg = try { NearbyPacket.read(socket) }
                    catch (_: SocketTimeoutException) { continue }
                peerLastReadNanos = System.nanoTime()
                if (synchronized(lock) { peer !== socket }) break
                when (msg.optString("type")) {
                    "pong" -> Unit
                    "claim" -> {
                        val points = try {
                            GridPoint(msg.getInt("r1"), msg.getInt("c1")) to
                                GridPoint(msg.getInt("r2"), msg.getInt("c2"))
                        } catch (_: Exception) { continue }
                        val outcome = session.submit(CoopSeat.GUEST, session.roomId,
                            msg.optString("id"), points.first, points.second)
                        publishHost(outcome.snapshot)
                    }
                    else -> throw IOException("Unsupported game operation")
                }
            }
        } catch (_: Exception) {
            // Preserve room and all found words; a reserved guest can rejoin.
        } finally {
            if (admitted) dropGuest(socket) else runCatching { socket.close() }
        }
    }

    private fun publishHost(snapshot: CoopSnapshot) {
        post { it.copy(snapshot = snapshot) }
        val socket = synchronized(lock) { peer } ?: return
        runCatching { NearbyPacket.write(socket, NearbyPacket.state(snapshot)) }
            .onFailure { dropGuest(socket) }
    }

    private fun dropGuest(socket: Socket) {
        synchronized(lock) {
            runCatching { socket.close() }
            if (peer !== socket) return
            peer = null
            val snapshot = authority?.guestDisconnected()
            if (snapshot != null) {
                post { it.copy(snapshot = snapshot,
                    message = "Partner disconnected. Their progress is saved in this room.") }
            }
        }
    }

    fun search() {
        if (state.role != NearbyRole.NONE || discovery != null) return
        val cb = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(type: String) { post { it.copy(scanning = true) } }
            override fun onServiceFound(service: NsdServiceInfo) {
                if (!service.serviceType.startsWith("_gridmatesws._tcp")) return
                @Suppress("DEPRECATION")
                nsd.resolveService(service, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(service: NsdServiceInfo, code: Int) = Unit
                    override fun onServiceResolved(info: NsdServiceInfo) {
                        val ip = info.host?.hostAddress ?: return
                        val id = info.attributes["room"]?.toString(Charsets.UTF_8) ?: return
                        if (id.isBlank() || info.port !in 1..65535) return
                        val entry = NearbyListing(id, info.serviceName, ip, info.port,
                            info.attributes["pin"]?.toString(Charsets.UTF_8) != "0")
                        post { current ->
                            current.copy(listings =
                                (current.listings.filterNot { it.roomId == id } + entry)
                                    .sortedBy { it.name })
                        }
                    }
                })
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                post { it.copy(listings = it.listings.filterNot { r -> r.name == service.serviceName }) }
            }
            override fun onDiscoveryStopped(type: String) { post { it.copy(scanning = false) } }
            override fun onStartDiscoveryFailed(type: String, code: Int) {
                post { it.copy(scanning = false,
                    message = "Nearby search failed. Try manual address entry.") }
            }
            override fun onStopDiscoveryFailed(type: String, code: Int) = Unit
        }
        discovery = cb
        runCatching { nsd.discoverServices(WS_NSD_TYPE, NsdManager.PROTOCOL_DNS_SD, cb) }
            .onFailure { discovery = null
                post { it.copy(message = "Discovery unavailable. Join with a host address.") } }
    }

    fun join(address: String, port: Int, roomId: String, code: String, name: String) {
        if (state.busy || state.role != NearbyRole.NONE && !state.disconnected) return
        if (port !in 1..65535 || address.isBlank()) {
            post { it.copy(message = "Enter a valid host address and port.") }; return
        }
        post { it.copy(busy = true, message = "Connecting to your partner…") }
        io.execute {
            val socket = Socket()
            try {
                socket.connect(InetSocketAddress(address, port), 4_000)
                socket.soTimeout = 8_000
                socket.tcpNoDelay = true
                val token = if (roomId.isNotBlank())
                    prefs.getString("token_" + roomId, "") ?: "" else ""
                NearbyPacket.write(socket, JSONObject().put("type", "hello")
                    .put("room", roomId).put("token", token).put("code", code)
                    .put("name", name.trim().take(24).ifBlank { "Player" }))
                val welcome = NearbyPacket.read(socket)
                if (welcome.optString("type") == "error")
                    throw IOException(welcome.optString("message", "Join declined."))
                require(welcome.optString("type") == "welcome")
                val snapshot = NearbyPacket.parseState(welcome.getJSONObject("roomState"))
                if (roomId.isNotBlank() && snapshot.roomId != roomId)
                    throw IOException("The room changed. Find it again.")
                val credential = welcome.getString("token")
                require(credential.length == 40) { "Invalid session credential" }
                synchronized(lock) {
                    if (closed) throw IOException("Screen was closed.")
                    client?.close()
                    client = socket
                    lastHost = address
                    lastPort = port
                }
                prefs.edit().putString("token_" + snapshot.roomId, credential).apply()
                post { it.copy(role = NearbyRole.GUEST, busy = false, disconnected = false,
                    snapshot = snapshot, roomId = snapshot.roomId,
                    hostName = welcome.optString("hostName", "Host"),
                    address = address, port = port,
                    message = "Connected! You both share this puzzle.") }
                io.execute { readHost(socket) }
            } catch (e: Exception) {
                runCatching { socket.close() }
                post { it.copy(busy = false, message = e.message ?: "Could not join.",
                    disconnected = it.role == NearbyRole.GUEST) }
            }
        }
    }

    private fun readHost(socket: Socket) {
        try {
            socket.soTimeout = 16_000
            while (!closed && !socket.isClosed) {
                val msg = NearbyPacket.read(socket)
                when (msg.optString("type")) {
                    "ping" -> NearbyPacket.write(socket, JSONObject().put("type", "pong"))
                    "state" -> {
                        val incoming = NearbyPacket.parseState(msg)
                        post { current ->
                            val old = current.snapshot
                            if (old != null && (old.roomId != incoming.roomId ||
                                incoming.revision < old.revision ||
                                (incoming.revision == old.revision &&
                                    incoming.claims != old.claims))) current
                            else current.copy(snapshot = incoming)
                        }
                    }
                    "error" -> throw IOException(msg.optString("message"))
                    else -> throw IOException("Unknown host message")
                }
            }
        } catch (_: Exception) {
            // The room remains on the host; leave the last accepted state visible.
        } finally {
            val wasCurrent = synchronized(lock) {
                if (client === socket) { client = null; true } else false
            }
            runCatching { socket.close() }
            if (wasCurrent) post { if (it.role == NearbyRole.GUEST) it.copy(
                disconnected = true,
                message = "Connection lost. Tap Reconnect to restore your shared board.")
                else it }
        }
    }

    fun claim(start: GridPoint, end: GridPoint) {
        val s = state.snapshot ?: return
        when (state.role) {
            NearbyRole.HOST -> {
                val authority = synchronized(lock) { authority } ?: return
                publishHost(authority.submit(CoopSeat.HOST, s.roomId,
                    UUID.randomUUID().toString(), start, end).snapshot)
            }
            NearbyRole.GUEST -> {
                if (state.disconnected) return
                val socket = synchronized(lock) { client } ?: return
                io.execute {
                    runCatching {
                        NearbyPacket.write(socket, JSONObject().put("type", "claim")
                            .put("id", UUID.randomUUID().toString())
                            .put("r1", start.row).put("c1", start.col)
                            .put("r2", end.row).put("c2", end.col))
                    }.onFailure {
                        post { it.copy(message = "Could not send selection; reconnect to continue.") }
                    }
                }
            }
            else -> Unit
        }
    }

    fun reconnect(name: String) {
        if (state.role != NearbyRole.GUEST || !state.disconnected) return
        join(lastHost, lastPort, state.roomId, "", name)
    }

    override fun close() {
        synchronized(lock) {
            if (closed) return
            closed = true
            runCatching { listener?.close() }
            runCatching { peer?.close() }
            runCatching { client?.close() }
            authority?.setHostConnected(false)
        }
        registration?.let { runCatching { nsd.unregisterService(it) } }
        discovery?.let { runCatching { nsd.stopServiceDiscovery(it) } }
        heartbeat.shutdownNow()
        io.shutdownNow()
    }
}
