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
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/** Only protocol-compatible devices on a trusted local network can join. */
private const val WS_WIRE_VERSION = 7
private const val WS_NSD_TYPE = "_gridmatesws._tcp."
private const val WS_PACKET_MAX = 16_384

internal data class NearbyListing(
    val roomId: String, val name: String, val address: String, val port: Int,
    val requiresCode: Boolean,
    val match: NearbyMatch = NearbyMatch.COOP
)

internal enum class NearbyRole { NONE, HOST, GUEST }

/** Additive metadata inside existing v7 state frames (old peers ignore it).
 * Always scoped to the guest's submitted request; does not expose Duel paths.
 */
internal data class NearbyClaimReceipt(val requestId: String, val status: String) {
    fun description(): String = when (status) {
        "FOUND" -> "Word confirmed by host."
        "ALREADY_FOUND" -> "Someone already found that word."
        "INVALID_PATH" -> "Host did not accept that selection."
        "NOT_READY", "NOT_CONNECTED", "NOT_STARTED" -> "Partner not ready. Try again when connected."
        "FINISHED" -> "That round is already complete."
        "INVALID_REQUEST", "WRONG_ROOM" -> "Claim rejected. Reconnect before trying again."
        "UNCONFIRMED" -> "No host confirmation yet. Check connection before retrying."
        else -> "Word selection was not accepted."
    }

    companion object {
        fun validId(value: String): Boolean =
            value.length in 1..64 && value.all { ch ->
                ch.code in 0..127 && (ch.isLetterOrDigit() || ch == '-' || ch == '_')
            }

        fun validStatus(value: String): Boolean =
            value in CoopClaimStatus.entries.map { it.name } ||
                value in DuelCommandStatus.entries.map { it.name }
    }
}

internal data class NearbyJoinRecovery(
    val roomId: String, val address: String, val port: Int, val code: String
)

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
    val chat: List<NearbyChatLine> = emptyList(),
    val pendingChat: PendingNearbyChat? = null,
    val recovery: NearbyJoinRecovery? = null,
    val disconnected: Boolean = false,
    val pendingClaimId: String? = null,
    val claimReceipt: NearbyClaimReceipt? = null
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

    fun state(s: CoopSnapshot, claimReceipt: NearbyClaimReceipt? = null): JSONObject {
        val spec = s.spec
        val claims = JSONArray()
        s.claims.forEach { claim ->
            claims.put(JSONObject().put("id", claim.wordId)
                .put("seat", claim.seat.name).put("rev", claim.revision))
        }
        val wire = JSONObject().put("type", "state").put("room", s.roomId)
            .put("theme", spec.theme.name).put("board", spec.board.name)
            .put("challenge", spec.challenge.name).put("seed", spec.seed)
            .put("puzzleId", spec.puzzleId).put("digest", spec.boardFingerprint)
            .put("revision", s.revision).put("claims", claims)
            .put("reserved", s.guestReserved).put("guestHere", s.guestConnected)
            .put("hostHere", s.hostConnected).put("finished", s.finished)
            .put("match", s.match.name)
            .put("look", s.boardLookId)
        s.duel?.let { d ->
            // Recipient-specific only! No path or ID of the opponent is sent.
            wire.put("duel", JSONObject()
                .put("rev", d.revision).put("recipient", d.recipient.name)
                .put("hostReady", d.hostReady).put("guestReady", d.guestReady)
                .put("started", d.started).put("finished", d.finished)
                .put("winner", d.winner?.name ?: "")
                .put("hostCount", d.hostCount).put("guestCount", d.guestCount)
                .put("own", JSONArray(d.ownFoundIds.sorted())))
        }
        claimReceipt?.let { acknowledgement ->
            require(NearbyClaimReceipt.validId(acknowledgement.requestId))
            require(NearbyClaimReceipt.validStatus(acknowledgement.status))
            wire.put("claimReceipt", JSONObject()
                .put("id", acknowledgement.requestId)
                .put("status", acknowledgement.status))
        }
        return wire
    }

    /** State can carry an optional guest-only claim receipt. Invalid metadata
     * is rejected; neither the saved snapshot nor the LAN version changes.
     */
    fun readClaimReceipt(json: JSONObject): NearbyClaimReceipt? {
        if (!json.has("claimReceipt")) return null
        val detail = json.getJSONObject("claimReceipt")
        val acknowledgement = NearbyClaimReceipt(
            detail.getString("id"), detail.getString("status"))
        require(NearbyClaimReceipt.validId(acknowledgement.requestId))
        require(NearbyClaimReceipt.validStatus(acknowledgement.status))
        return acknowledgement
    }

    fun chatLine(line: NearbyChatLine): JSONObject =
        JSONObject().put("type", "chat")
            .put("sequence", line.sequence).put("seat", line.seat.name)
            .put("text", line.text).put("sentAt", line.sentAtMs)
            .put("requestId", line.requestId)

    fun readChatLine(json: JSONObject): NearbyChatLine {
        require(json.getString("type") == "chat")
        val line = NearbyChatLine(json.getLong("sequence"),
            CoopSeat.valueOf(json.getString("seat")),
            json.getString("text"), json.getLong("sentAt"),
            json.getString("requestId"))
        NearbyChatLog.validateHistory(listOf(line))
        return line
    }

    fun chatHistory(lines: List<NearbyChatLine>): JSONArray =
        JSONArray().also { data ->
            NearbyChatLog.validateHistory(lines).forEach { data.put(chatLine(it)) }
        }

    fun readChatHistory(json: JSONArray): List<NearbyChatLine> {
        require(json.length() <= NearbyChatPolicy.MAX_HISTORY)
        val lines = (0 until json.length()).map { readChatLine(json.getJSONObject(it)) }
        return NearbyChatLog.validateHistory(lines)
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
        val duel = json.optJSONObject("duel")?.let { data ->
            val own = data.getJSONArray("own")
            require(own.length() <= 30)
            val ids = (0 until own.length()).map { own.getString(it) }
            require(ids.distinct().size == ids.size) { "Duplicate private discovery" }
            DuelView(data.getLong("rev"),
                CoopSeat.valueOf(data.getString("recipient")),
                data.getBoolean("hostReady"), data.getBoolean("guestReady"),
                data.getBoolean("started"), data.getBoolean("finished"),
                data.getString("winner").takeIf { it.isNotEmpty() }
                    ?.let { CoopSeat.valueOf(it) },
                data.getInt("hostCount"), data.getInt("guestCount"), ids.toSet())
        }
        return CoopSnapshot(json.getString("room"), spec, json.getLong("revision"),
            claims, json.getBoolean("reserved"), json.getBoolean("guestHere"),
            json.getBoolean("hostHere"), json.getBoolean("finished"),
            NearbyMatch.valueOf(json.getString("match")), duel,
            json.getString("look"))
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
    private val pendingAdmissions = Semaphore(4)
    private val chatLog = NearbyChatLog()
    private val heartbeat = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "ws05-heartbeat").apply { isDaemon = true }
    }
    private val lock = Any()
    @Volatile private var closed = false
    private var listener: ServerSocket? = null
    private var peer: Socket? = null
    private var client: Socket? = null
    private var authority: NearbyCoopSession? = null
    /** Only host owns both private ledgers. Never serialize this full object. */
    private var duelAuthority: SpeedDuelSession? = null
    private var reservedToken: String? = null
    private var admissionPending = false
    private var pendingGuestChat: PendingNearbyChat? = null
    private var registration: NsdManager.RegistrationListener? = null
    private var discovery: NsdManager.DiscoveryListener? = null
    private var stoppingDiscovery: NsdManager.DiscoveryListener? = null
    private var refreshRequested = false
    private val discoveredInfos = mutableMapOf<String, NsdServiceInfo>()
    private val discoveryQueue = NearbyDiscoveryQueue()
    private val joinInProgress = AtomicBoolean(false)
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
            .filter { network ->
                (network.name.startsWith("wlan") || network.name.startsWith("ap")) &&
                    runCatching { network.isUp && !network.isLoopback }.getOrDefault(false)
            }
        return interfaces.asSequence().flatMap { Collections.list(it.inetAddresses).asSequence() }
            .firstOrNull { it is Inet4Address && it.isSiteLocalAddress && !it.isLoopbackAddress }
    }

    /** Add private Duel data only for the intended viewer. */
    private fun forSeat(shared: CoopSnapshot, seat: CoopSeat): CoopSnapshot {
        if (shared.match != NearbyMatch.SPEED_DUEL) return shared
        val duel = synchronized(lock) { requireNotNull(duelAuthority) }
        val view = duel.view(seat)
        return shared.copy(duel = view, finished = view.finished)
    }

    fun host(name: String, theme: WordTheme, board: PuzzleBoardSize,
             challenge: PuzzleChallenge, match: NearbyMatch = NearbyMatch.COOP,
             boardLookId: String = CoopBoardLook.CLASSIC.id) {
        if (state.busy || state.role != NearbyRole.NONE) return
        post { it.copy(busy = true, message = "Opening your nearby game…") }
        io.execute {
            try {
                val wifi = localWifiIp() ?: throw IOException(
                    "Connect to a local Wi-Fi network to host a game.")
                val run = SoloProgressRules.freshV2(theme, board, challenge,
                    System.currentTimeMillis())
                val session = NearbyCoopSession(run, match = match,
                    hostLook = CoopBoardLook.safe(boardLookId))
                val socket = ServerSocket(0, 2, wifi)
                val roomName = name.trim().take(24).ifBlank { "Player" }
                synchronized(lock) {
                    if (closed) { socket.close(); return@execute }
                    authority = session
                    duelAuthority = if (match == NearbyMatch.SPEED_DUEL)
                        SpeedDuelSession(session.puzzle) else null
                    liveHostName = roomName
                    listener = socket
                    hostRoomCode = if (requireCodePreference)
                        SecureRandom().nextInt(1_000_000).toString().padStart(6, '0') else ""
                }
                post { it.copy(role = NearbyRole.HOST, busy = false,
                    message = when (match) {
                        NearbyMatch.SPEED_DUEL -> "Duel room open. Your partner joins, then both tap Ready."
                        NearbyMatch.SHARED_BOARD_RACE -> "Race room open. Invite one opponent on the same Wi-Fi."
                        NearbyMatch.COOP -> "Room open. Ask a friend to join on the same Wi-Fi."
                    },
                    roomId = session.roomId, address = wifi.hostAddress ?: "",
                    port = socket.localPort, code = hostRoomCode,
                    requiresCode = hostRoomCode.isNotBlank(),
                    hostName = roomName,
                    snapshot = forSeat(session.snapshot(), CoopSeat.HOST)) }
                main.post {
                    if (!closed) advertise(session.roomId, roomName, socket.localPort,
                        hostRoomCode.isNotBlank(), match)
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

    private fun advertise(id: String, name: String, port: Int, needsCode: Boolean,
                          match: NearbyMatch) {
        val info = NsdServiceInfo().apply {
            serviceType = WS_NSD_TYPE
            serviceName = "GridMates - " + name
            this.port = port
            setAttribute("room", id)
            setAttribute("pin", if (needsCode) "1" else "0")
            setAttribute("proto", WS_WIRE_VERSION.toString())
            setAttribute("match", match.name)
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
            if (!pendingAdmissions.tryAcquire()) {
                runCatching { socket.close() }
                continue
            }
            io.execute {
                try { handleGuest(socket) } finally { pendingAdmissions.release() }
            }
        }
    }

    /**
     * Initial admission is prepared, but NOT reserved, until the guest has
     * durably saved the reconnect credential and explicitly acknowledges it.
     * If the final "joined" packet is lost, the guest already has its token.
     */
    private fun handleGuest(socket: Socket) {
        var admitted = false
        var offered = false
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
            // The guest only proposes a known, free local preview style. The
            // host chooses/finalizes once and never trusts remote files/colors.
            val requestedLook = CoopBoardLook.safe(hello.optString("look", "classic"))
            val session = synchronized(lock) { authority } ?: reject("Room closed.")
            if (hello.optString("room").isNotBlank() &&
                hello.optString("room") != session.roomId) reject("This room has changed.")

            val credential = synchronized(lock) {
                if (closed || listener == null) reject("Room closed.")
                if (peer != null || admissionPending) reject("This room already has two players.")
                val current = reservedToken
                val token = hello.optString("token")
                if (current != null && token != current)
                    reject("This room is reserved for its previous partner.")
                if (current == null && hostRoomCode.isNotBlank()) {
                    val address = socket.inetAddress.hostAddress ?: "unknown"
                    val now = System.currentTimeMillis()
                    val attempts = invalidCodes.getOrPut(address) { mutableListOf() }
                    attempts.removeAll { now - it > 60_000L }
                    if (attempts.size >= 5) reject("Too many incorrect codes. Try later.")
                    if (hello.optString("code") != hostRoomCode) {
                        attempts.add(now)
                        reject("Incorrect room code.")
                    }
                    attempts.clear()
                }
                admissionPending = true
                offered = true
                current ?: secret()
            }
            // The guest can validate the proposed board before committing its token.
            val proposedLook = if (session.match == NearbyMatch.COOP)
                session.offeredBoardLook(requestedLook.id).id else CoopBoardLook.CLASSIC.id
            val preview = forSeat(session.snapshot().copy(
                guestReserved = true, guestConnected = true,
                boardLookId = proposedLook), CoopSeat.GUEST)
            NearbyPacket.write(socket, JSONObject().put("type", "welcome")
                .put("token", credential).put("hostName", liveHostName)
                .put("roomState", NearbyPacket.state(preview))
                .put("chatHistory", NearbyPacket.chatHistory(chatLog.history())))
            val receipt = NearbyPacket.read(socket)
            if (!NearbyHandshake.accepts(receipt.optString("type"),
                    receipt.optString("room"), receipt.optString("token"),
                    session.roomId, credential)) reject("Join confirmation was not received.")
            val accepted = synchronized(lock) {
                if (closed || peer != null || !admissionPending) reject("Room closed.")
                // This is the first point where a new guest can reserve the seat.
                if (reservedToken == null) reservedToken = credential
                peer = socket
                peerLastReadNanos = System.nanoTime()
                admitted = true
                if (session.match == NearbyMatch.COOP)
                    session.freezeGuestBoardLook(requestedLook.id)
                val finalState = session.admitOrRejoinGuest()
                // Send Joined before exposing this socket to heartbeat/host
                // updates. The lock is reentrant and bounds this short frame.
                NearbyPacket.write(socket, JSONObject().put("type", "joined")
                    .put("roomState", NearbyPacket.state(forSeat(finalState, CoopSeat.GUEST))))
                admissionPending = false
                offered = false
                finalState
            }
            post { it.copy(guestName = name,
                snapshot = forSeat(accepted, CoopSeat.HOST),
                message = when (session.match) {
                    NearbyMatch.SPEED_DUEL -> "Connected with " + name + ". Both players must tap Ready."
                    NearbyMatch.SHARED_BOARD_RACE -> "Connected with " + name + ". First claim wins each word!"
                    NearbyMatch.COOP -> "Connected with " + name + ". Find words together!"
                }) }
            socket.soTimeout = 11_000
            while (!closed && !socket.isClosed) {
                val msg = try { NearbyPacket.read(socket) }
                    catch (_: SocketTimeoutException) { continue }
                peerLastReadNanos = System.nanoTime()
                if (synchronized(lock) { peer !== socket }) break
                when (msg.optString("type")) {
                    "pong" -> Unit
                    "chat_send" -> synchronized(lock) {
                        val response = chatLog.add(CoopSeat.GUEST, msg.optString("id"),
                            msg.optString("text"), true, System.currentTimeMillis())
                        if (response.status == NearbyChatStatus.ACCEPTED ||
                            response.status == NearbyChatStatus.DUPLICATE) {
                            response.line?.let(::publishChat)
                        } else {
                            runCatching {
                                NearbyPacket.write(socket, JSONObject()
                                    .put("type", "chat_error").put("id", msg.optString("id"))
                                    .put("message", when (response.status) {
                                        NearbyChatStatus.TOO_FAST -> "Send messages a little slower."
                                        NearbyChatStatus.EMPTY -> "Write a message first."
                                        else -> "Message was not accepted."
                                    }))
                            }
                        }
                    }
                    "duel_ready" -> {
                        if (session.match != NearbyMatch.SPEED_DUEL)
                            throw IOException("Not a Duel room")
                        val duel = synchronized(lock) { duelAuthority }
                            ?: throw IOException("Duel room is unavailable")
                        duel.ready(CoopSeat.GUEST,
                            session.snapshot().guestConnected && session.snapshot().hostConnected)
                        publishHost(session.snapshot())
                    }
                    "claim" -> {
                        val points = try {
                            GridPoint(msg.getInt("r1"), msg.getInt("c1")) to
                                GridPoint(msg.getInt("r2"), msg.getInt("c2"))
                        } catch (_: Exception) { continue }
                        if (session.match == NearbyMatch.SPEED_DUEL) {
                            val duel = synchronized(lock) { duelAuthority }
                                ?: throw IOException("Duel room is unavailable")
                            val current = session.snapshot()
                            val requestId = msg.optString("id")
                            val outcome = duel.submit(CoopSeat.GUEST, requestId,
                                points.first, points.second,
                                current.guestConnected && current.hostConnected)
                            publishHost(session.snapshot(), if (NearbyClaimReceipt.validId(requestId))
                                NearbyClaimReceipt(requestId, outcome.status.name) else null)
                        } else {
                            val requestId = msg.optString("id")
                            val outcome = session.submit(CoopSeat.GUEST, session.roomId,
                                requestId, points.first, points.second)
                            publishHost(outcome.snapshot, if (NearbyClaimReceipt.validId(requestId))
                                NearbyClaimReceipt(requestId, outcome.status.name) else null)
                        }
                    }
                    else -> throw IOException("Unsupported game operation")
                }
            }
        } catch (_: Exception) {
            // A failed offer does not reserve a new guest. Confirmed guests
            // still retain their session token for reconnect after any drop.
        } finally {
            if (offered) synchronized(lock) { admissionPending = false }
            if (admitted) dropGuest(socket) else runCatching { socket.close() }
        }
    }

    private fun publishChat(line: NearbyChatLine) = synchronized(lock) {
        post { current ->
            if (current.chat.any { it.sequence == line.sequence }) current else
                current.copy(chat = (current.chat + line).takeLast(NearbyChatPolicy.MAX_HISTORY))
        }
        peer?.let { current ->
            runCatching { NearbyPacket.write(current, NearbyPacket.chatLine(line)) }
                .onFailure { dropGuest(current) }
        }
    }

    /** Only one guest chat at a time: show unconfirmed text until host confirms. */
    fun sendChat(raw: String): Boolean {
        val clean = NearbyChatPolicy.clean(raw)
        if (clean.isEmpty()) return false
        val current = state.snapshot ?: return false
        if (!current.guestConnected || state.disconnected) {
            post { it.copy(message = "Wait for your partner to connect before chatting.") }
            return false
        }
        return when (state.role) {
            NearbyRole.HOST -> {
                io.execute {
                    synchronized(lock) {
                        val decision = chatLog.add(CoopSeat.HOST, UUID.randomUUID().toString(),
                            clean, true, System.currentTimeMillis())
                        if (decision.status == NearbyChatStatus.ACCEPTED)
                            publishChat(requireNotNull(decision.line))
                        else post { it.copy(message = if (decision.status == NearbyChatStatus.TOO_FAST)
                            "Send messages a little slower." else "Write a message first.") }
                    }
                }
                true
            }
            NearbyRole.GUEST -> {
                val socket: Socket
                val pending: PendingNearbyChat
                synchronized(lock) {
                    socket = client ?: return false
                    if (pendingGuestChat != null) return false
                    pending = PendingNearbyChat(UUID.randomUUID().toString(), clean)
                    pendingGuestChat = pending
                }
                post { it.copy(pendingChat = pending) }
                dispatchGuestChat(socket, pending)
                true
            }
            NearbyRole.NONE -> false
        }
    }

    private fun dispatchGuestChat(socket: Socket, pending: PendingNearbyChat) {
        io.execute {
            runCatching {
                NearbyPacket.write(socket, JSONObject().put("type", "chat_send")
                    .put("id", pending.requestId).put("text", pending.text))
            }.onFailure {
                markChatNeedsRetry(pending.requestId,
                    "Message not confirmed. Reconnect or retry.")
            }
        }
        // No automatic re-send: a lost receipt can mean the host already stored it.
        heartbeat.schedule({
            markChatNeedsRetry(pending.requestId,
                "No chat confirmation yet. Tap Retry to check safely.")
        }, 9, TimeUnit.SECONDS)
    }

    private fun markChatNeedsRetry(id: String, message: String) {
        val pending = synchronized(lock) {
            val old = pendingGuestChat
            if (old?.requestId != id || old.needsRetry) null
            else old.copy(needsRetry = true).also { pendingGuestChat = it }
        } ?: return
        post { current ->
            if (current.pendingChat?.requestId != id) current
            else current.copy(pendingChat = pending, message = message)
        }
    }

    /** Retry uses the exact same request ID, so the host cannot double-award it. */
    fun retryChat(): Boolean {
        if (state.role != NearbyRole.GUEST || state.disconnected || state.busy) return false
        val socket: Socket
        val retry: PendingNearbyChat
        synchronized(lock) {
            socket = client ?: return false
            val existing = pendingGuestChat ?: return false
            if (!existing.needsRetry) return false
            retry = existing.copy(needsRetry = false)
            pendingGuestChat = retry
        }
        post { current -> if (current.pendingChat?.requestId == retry.requestId)
            current.copy(pendingChat = retry, message = "Checking message delivery…")
            else current }
        dispatchGuestChat(socket, retry)
        return true
    }

    private fun confirmGuestChat(line: NearbyChatLine) {
        val confirmed = synchronized(lock) {
            if (NearbyChatDelivery.confirmed(pendingGuestChat, line)) {
                pendingGuestChat = null
                true
            } else false
        }
        if (confirmed) post { current ->
            if (current.pendingChat?.requestId == line.requestId)
                current.copy(pendingChat = null, message = "Message delivered.")
            else current
        }
    }

    private fun publishHost(snapshot: CoopSnapshot,
                            receipt: NearbyClaimReceipt? = null) {
        // NEVER send the host's private found IDs or claim receipts to a
        // different room. Receipt is an optional field on guest state only.
        post { it.copy(snapshot = forSeat(snapshot, CoopSeat.HOST)) }
        val socket = synchronized(lock) { peer } ?: return
        runCatching {
            NearbyPacket.write(socket, NearbyPacket.state(
                forSeat(snapshot, CoopSeat.GUEST), receipt))
        }.onFailure { dropGuest(socket) }
    }

    private fun dropGuest(socket: Socket) {
        synchronized(lock) {
            runCatching { socket.close() }
            if (peer !== socket) return
            peer = null
            duelAuthority?.onGuestDisconnected()
            val snapshot = authority?.guestDisconnected()
            if (snapshot != null) {
                post { it.copy(snapshot = forSeat(snapshot, CoopSeat.HOST),
                    message = "Partner disconnected. Their progress is saved in this room.") }
            }
        }
    }

    private fun stopDiscovery() {
        refreshRequested = false
        val current = discovery ?: return
        discovery = null
        stoppingDiscovery = current
        discoveryQueue.reset()
        discoveredInfos.clear()
        runCatching { nsd.stopServiceDiscovery(current) }.onFailure {
            stoppingDiscovery = null
            post { it.copy(message = "Could not restart nearby search. Use manual join.") }
        }
        post { it.copy(scanning = false) }
    }

    /** Refresh without overlapping old and new discovery registrations. */
    fun refreshSearch() {
        if (state.role != NearbyRole.NONE || closed) return
        post { it.copy(listings = emptyList(), message = "Refreshing nearby rooms…") }
        if (stoppingDiscovery != null) {
            refreshRequested = true
            return
        }
        val active = discovery
        if (active == null) {
            search()
            return
        }
        refreshRequested = true
        discovery = null
        stoppingDiscovery = active
        discoveryQueue.reset()
        discoveredInfos.clear()
        runCatching { nsd.stopServiceDiscovery(active) }.onFailure {
            stoppingDiscovery = null
            refreshRequested = false
            post { it.copy(scanning = false,
                message = "Could not refresh discovery. Use manual join.") }
        }
    }

    /**
     * NsdManager's older resolveService API must be driven sequentially:
     * simultaneous resolve requests are rejected on some Android versions.
     */
    private fun resolveQueued(name: String, owner: NsdManager.DiscoveryListener) {
        if (closed || discovery !== owner) return
        val info = discoveredInfos[name]
        val ticket = discoveryQueue.ticket(name)
        if (info == null) {
            discoveryQueue.finished(name)?.let { resolveQueued(it, owner) }
            return
        }
        fun advance() {
            if (closed || discovery !== owner) return
            discoveryQueue.finished(name)?.let { resolveQueued(it, owner) }
        }
        @Suppress("DEPRECATION")
        val cb = object : NsdManager.ResolveListener {
            override fun onResolveFailed(service: NsdServiceInfo, code: Int) = advance()
            override fun onServiceResolved(result: NsdServiceInfo) {
                try {
                    if (closed || discovery !== owner || !discoveryQueue.isLive(name) ||
                        discoveryQueue.ticket(name) != ticket) return
                    val ip = result.host?.hostAddress ?: return
                    val id = result.attributes["room"]?.toString(Charsets.UTF_8) ?: return
                    if (result.attributes["proto"]?.toString(Charsets.UTF_8) !=
                        WS_WIRE_VERSION.toString()) return
                    if (id.isBlank() || id.length > 120 || result.port !in 1..65535) return
                    val advertised = result.attributes["match"]?.toString(Charsets.UTF_8)
                        ?: return
                    val match = runCatching { NearbyMatch.valueOf(advertised) }
                        .getOrNull() ?: return
                    val entry = NearbyListing(id, name, ip, result.port,
                        result.attributes["pin"]?.toString(Charsets.UTF_8) != "0",
                        match)
                    post { current ->
                        if (discovery !== owner || current.role != NearbyRole.NONE ||
                            !discoveryQueue.isLive(name) ||
                            discoveryQueue.ticket(name) != ticket) current
                        else current.copy(listings =
                            (current.listings.filterNot {
                                it.roomId == id || it.name == entry.name
                            } + entry).sortedBy { it.name })
                    }
                } finally { advance() }
            }
        }
        runCatching { nsd.resolveService(info, cb) }
            .onFailure { advance() }
    }

    fun search() {
        if (closed || state.role != NearbyRole.NONE ||
            discovery != null || stoppingDiscovery != null) return
        val cb = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(type: String) {
                if (discovery === this) post { it.copy(scanning = true) }
            }
            override fun onServiceFound(service: NsdServiceInfo) {
                if (discovery !== this || closed ||
                    !service.serviceType.startsWith("_gridmatesws._tcp")) return
                val first = discoveryQueue.found(service.serviceName)
                if (!discoveryQueue.isLive(service.serviceName)) return
                discoveredInfos[service.serviceName] = service
                first?.let { resolveQueued(it, this) }
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                if (discovery !== this || closed) return
                discoveredInfos.remove(service.serviceName)
                discoveryQueue.lost(service.serviceName)
                post { current ->
                    if (discovery !== this || current.role != NearbyRole.NONE) current
                    else current.copy(listings =
                        current.listings.filterNot { it.name == service.serviceName })
                }
            }
            override fun onDiscoveryStopped(type: String) {
                if (discovery !== this && stoppingDiscovery !== this) return
                if (discovery === this) discovery = null
                if (stoppingDiscovery === this) stoppingDiscovery = null
                discoveryQueue.reset()
                discoveredInfos.clear()
                post { it.copy(scanning = false, listings = emptyList()) }
                if (refreshRequested) {
                    refreshRequested = false
                    main.post { if (!closed && state.role == NearbyRole.NONE) search() }
                }
            }
            override fun onStartDiscoveryFailed(type: String, code: Int) {
                if (discovery !== this) return
                discovery = null
                discoveryQueue.reset()
                discoveredInfos.clear()
                post { it.copy(scanning = false,
                    message = "Nearby search failed. Try refreshing or join manually.") }
            }
            override fun onStopDiscoveryFailed(type: String, code: Int) {
                if (stoppingDiscovery === this) stoppingDiscovery = null
                refreshRequested = false
                post { it.copy(scanning = false,
                    message = "Could not stop nearby search. Use manual join.") }
            }
        }
        discovery = cb
        discoveryQueue.reset()
        discoveredInfos.clear()
        post { it.copy(listings = emptyList(), message = "Looking for nearby games…") }
        runCatching { nsd.discoverServices(WS_NSD_TYPE, NsdManager.PROTOCOL_DNS_SD, cb) }
            .onFailure {
                discovery = null
                post { it.copy(scanning = false,
                    message = "Discovery unavailable. Join with a host address.") }
            }
    }

    fun join(address: String, port: Int, roomId: String, code: String, name: String,
             boardLookId: String = CoopBoardLook.CLASSIC.id) {
        if (state.busy || state.role != NearbyRole.NONE && !state.disconnected) return
        if (!joinInProgress.compareAndSet(false, true)) return
        if (state.role == NearbyRole.GUEST && roomId != state.roomId) {
            joinInProgress.set(false)
            post { it.copy(message = "Leave this game before joining a different room.") }
            return
        }
        if (port !in 1..65535 || address.isBlank()) {
            joinInProgress.set(false)
            post { it.copy(message = "Enter a valid host address and port.") }; return
        }
        post { it.copy(busy = true, message = "Connecting to your partner…",
            recovery = if (it.recovery?.roomId == roomId) it.recovery else null) }
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
                    .put("name", name.trim().take(24).ifBlank { "Player" })
                    .put("look", CoopBoardLook.safe(boardLookId).id))
                val welcome = NearbyPacket.read(socket)
                if (welcome.optString("type") == "error")
                    throw IOException(welcome.optString("message", "Join declined."))
                require(welcome.optString("type") == "welcome")
                val offered = NearbyPacket.parseState(welcome.getJSONObject("roomState"))
                require(offered.duel?.recipient != CoopSeat.HOST) {
                    "Host private discoveries cannot be sent to the guest"
                }
                val offeredChat = NearbyPacket.readChatHistory(welcome.getJSONArray("chatHistory"))
                if (roomId.isNotBlank() && offered.roomId != roomId)
                    throw IOException("The room changed. Find it again.")
                val credential = welcome.getString("token")
                require(credential.length == 40 && credential.all { it in '0'..'9' || it in 'a'..'f' }) {
                    "Invalid session credential"
                }
                // Persist FIRST. If "joined" is lost the host may have reserved
                // this seat, but our next connection already has its identity.
                if (!prefs.edit().putString("token_" + offered.roomId, credential).commit())
                    throw IOException("Could not save rejoin credential.")
                // This survives a lost Joined packet, including manual-IP joins
                // where the initial lookup did not include a room ID.
                val recovery = NearbyJoinRecovery(offered.roomId, address, port, code)
                post { it.copy(recovery = recovery) }
                NearbyPacket.write(socket, NearbyHandshake.receipt(offered.roomId, credential))
                val joined = NearbyPacket.read(socket)
                if (joined.optString("type") == "error")
                    throw IOException(joined.optString("message", "Join declined."))
                require(joined.optString("type") == "joined") { "Join was not confirmed." }
                val snapshot = NearbyPacket.parseState(joined.getJSONObject("roomState"))
                require(snapshot.duel?.recipient != CoopSeat.HOST) {
                    "Host private discoveries cannot be sent to the guest"
                }
                require(NearbyHandshake.matchesFinal(offered, snapshot)) {
                    "The room changed during joining."
                }
                synchronized(lock) {
                    if (closed) throw IOException("Screen was closed.")
                    client?.close()
                    client = socket
                    lastHost = address
                    lastPort = port
                }
                main.post { if (!closed) stopDiscovery() }
                val outstanding = synchronized(lock) {
                    if (NearbyChatDelivery.confirmedInHistory(pendingGuestChat, offeredChat))
                        pendingGuestChat = null
                    else pendingGuestChat = pendingGuestChat?.copy(needsRetry = true)
                    pendingGuestChat
                }
                post { it.copy(role = NearbyRole.GUEST, busy = false, disconnected = false,
                    pendingClaimId = null, claimReceipt = null,
                    snapshot = snapshot, roomId = snapshot.roomId,
                    chat = offeredChat, pendingChat = outstanding, recovery = null,
                    hostName = welcome.optString("hostName", "Host"),
                    address = address, port = port,
                    message = when (snapshot.match) {
                        NearbyMatch.SPEED_DUEL -> "Connected! Both tap Ready to start your private boards."
                        NearbyMatch.SHARED_BOARD_RACE -> "Connected! Race for words on the shared board."
                        NearbyMatch.COOP -> "Connected! You both share this puzzle."
                    }) }
                io.execute { readHost(socket) }
            } catch (e: Exception) {
                runCatching { socket.close() }
                post { it.copy(busy = false,
                    message = if (it.recovery != null)
                        "Connection interrupted. Your room can be retried safely."
                    else e.message ?: "Could not join.",
                    disconnected = it.role == NearbyRole.GUEST) }
            } finally {
                joinInProgress.set(false)
            }
        }
    }

    fun retryInterruptedJoin(name: String) {
        val recovery = state.recovery ?: return
        if (state.busy || state.role != NearbyRole.NONE) return
        join(recovery.address, recovery.port, recovery.roomId, recovery.code, name)
    }

    private fun readHost(socket: Socket) {
        try {
            socket.soTimeout = 16_000
            while (!closed && !socket.isClosed) {
                val msg = NearbyPacket.read(socket)
                when (msg.optString("type")) {
                    "ping" -> NearbyPacket.write(socket, JSONObject().put("type", "pong"))
                    "chat" -> {
                        val line = NearbyPacket.readChatLine(msg)
                        confirmGuestChat(line)
                        post { current ->
                            val latest = current.chat.lastOrNull()?.sequence ?: 0L
                            if (line.sequence <= latest) current
                            else current.copy(chat =
                                (current.chat + line).takeLast(NearbyChatPolicy.MAX_HISTORY))
                        }
                    }
                    "chat_error" -> {
                        val id = msg.optString("id")
                        markChatNeedsRetry(id,
                            msg.optString("message", "Message was not accepted."))
                    }
                    "state" -> {
                        val incoming = NearbyPacket.parseState(msg)
                        val receipt = NearbyPacket.readClaimReceipt(msg)
                        if (incoming.duel?.recipient == CoopSeat.HOST)
                            throw IOException("Host private state was sent to the guest")
                        post { current ->
                            val old = current.snapshot
                            // Bind cross-module nullable properties to local vals; Kotlin
                            // cannot smart-cast an external public data-class property.
                            val before = old?.duel
                            val after = incoming.duel
                            val duelRegressed = before != null && (after == null ||
                                after.revision < before.revision ||
                                !after.ownFoundIds.containsAll(before.ownFoundIds) ||
                                before.started && !after.started ||
                                before.finished && !after.finished)
                            if (old != null && (old.roomId != incoming.roomId ||
                                incoming.revision < old.revision ||
                                incoming.match != old.match || incoming.spec != old.spec ||
                                (old.guestReserved &&
                                    incoming.boardLookId != old.boardLookId) ||
                                (incoming.revision == old.revision &&
                                    incoming.claims != old.claims) ||
                                duelRegressed)) current
                            else {
                                // A stale or unrelated confirmation must not
                                // replace the latest pending guest selection.
                                val matched = receipt?.takeIf {
                                    it.requestId == current.pendingClaimId
                                }
                                current.copy(snapshot = incoming,
                                    pendingClaimId = if (matched == null) current.pendingClaimId else null,
                                    claimReceipt = matched ?: current.claimReceipt)
                            }
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
            if (wasCurrent) {
                val pending = synchronized(lock) {
                    pendingGuestChat = pendingGuestChat?.copy(needsRetry = true)
                    pendingGuestChat
                }
                post { if (it.role == NearbyRole.GUEST) it.copy(
                    disconnected = true, pendingChat = pending,
                    message = "Connection lost. Tap Reconnect to restore your shared board.")
                    else it }
            }
        }
    }

    fun readySpeedDuel() {
        val s = state.snapshot ?: return
        if (s.match != NearbyMatch.SPEED_DUEL || s.duel?.started == true) return
        when (state.role) {
            NearbyRole.HOST -> {
                val session = synchronized(lock) { authority } ?: return
                val duel = synchronized(lock) { duelAuthority } ?: return
                val current = session.snapshot()
                duel.ready(CoopSeat.HOST, current.guestConnected && current.hostConnected)
                publishHost(session.snapshot())
            }
            NearbyRole.GUEST -> {
                if (state.disconnected) return
                val socket = synchronized(lock) { client } ?: return
                io.execute {
                    runCatching {
                        NearbyPacket.write(socket, JSONObject().put("type", "duel_ready"))
                    }.onFailure {
                        post { it.copy(message = "Ready signal not delivered. Reconnect and try again.") }
                    }
                }
            }
            NearbyRole.NONE -> Unit
        }
    }

    fun claim(start: GridPoint, end: GridPoint) {
        val s = state.snapshot ?: return
        when (state.role) {
            NearbyRole.HOST -> {
                val session = synchronized(lock) { authority } ?: return
                if (s.match == NearbyMatch.SPEED_DUEL) {
                    val duel = synchronized(lock) { duelAuthority } ?: return
                    val current = session.snapshot()
                    duel.submit(CoopSeat.HOST, UUID.randomUUID().toString(), start, end,
                        current.guestConnected && current.hostConnected)
                    publishHost(session.snapshot())
                } else {
                    publishHost(session.submit(CoopSeat.HOST, s.roomId,
                        UUID.randomUUID().toString(), start, end).snapshot)
                }
            }
            NearbyRole.GUEST -> {
                if (state.disconnected) return
                val socket = synchronized(lock) { client } ?: return
                val requestId = UUID.randomUUID().toString()
                post { current ->
                    if (current.role == NearbyRole.GUEST && current.roomId == s.roomId)
                        current.copy(pendingClaimId = requestId, claimReceipt = null)
                    else current
                }
                io.execute {
                    runCatching {
                        NearbyPacket.write(socket, JSONObject().put("type", "claim")
                            .put("id", requestId)
                            .put("r1", start.row).put("c1", start.col)
                            .put("r2", end.row).put("c2", end.col))
                    }.onFailure {
                        post { current ->
                            if (current.pendingClaimId == requestId)
                                current.copy(pendingClaimId = null,
                                    claimReceipt = NearbyClaimReceipt(requestId, "UNCONFIRMED"),
                                    message = "Could not send selection; reconnect to continue.")
                            else current
                        }
                    }
                }
                // Never repeat a claim automatically: a lost acknowledgement
                // may follow successful host-side acceptance.
                heartbeat.schedule({
                    post { current ->
                        if (current.pendingClaimId == requestId &&
                            current.role == NearbyRole.GUEST && current.roomId == s.roomId)
                            current.copy(pendingClaimId = null,
                                claimReceipt = NearbyClaimReceipt(requestId, "UNCONFIRMED"))
                        else current
                    }
                }, 9, TimeUnit.SECONDS)
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
