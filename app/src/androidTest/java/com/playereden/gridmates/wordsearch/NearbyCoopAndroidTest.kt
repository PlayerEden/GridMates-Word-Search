package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/** On-device loopback wire tests; physical Wi-Fi/NSD still requires two phones. */
class NearbyCoopAndroidTest {
    private fun session(): NearbyCoopSession {
        val game = SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 110625L)
        return NearbyCoopSession(game, "ws05-android-room")
    }

    @Test fun stateCodecRoundTripsCanonicalHostClaimsAndReconnectPresence() {
        val room = session()
        room.admitOrRejoinGuest()
        val target = room.puzzle.targets.first()
        val result = room.submit(CoopSeat.GUEST, room.roomId, "android-one",
            target.positions.first(), target.positions.last())
        assertEquals(CoopClaimStatus.FOUND, result.status)
        val packet = NearbyPacket.state(room.guestDisconnected())
        assertEquals(room.snapshot(), NearbyPacket.parseState(packet))
        val after = NearbyPacket.parseState(NearbyPacket.state(room.admitOrRejoinGuest()))
        assertEquals(1L, after.revision)
        assertEquals(CoopSeat.GUEST, after.claims.single().seat)
        assertTrue(after.guestConnected)
    }

    @Test fun corruptHostStateIsRejectedBeforeBeingShown() {
        val room = session()
        val original = NearbyPacket.state(room.snapshot())
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(original.toString())
                .put("digest", "bad"))
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(original.toString())
                .put("finished", true))
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(original.toString())
                .put("revision", -1))
        }.isFailure)
    }

    @Test fun versionedLengthBoundedPacketSurvivesActualLocalSocket() {
        val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress())
        val done = CountDownLatch(1)
        val failure = AtomicReference<Throwable?>(null)
        val reader = Thread {
            try {
                server.accept().use { socket ->
                    socket.soTimeout = 5_000
                    val message = NearbyPacket.read(socket)
                    assertEquals("hello", message.getString("type"))
                    assertEquals("Café", message.getString("name"))
                    NearbyPacket.write(socket,
                        JSONObject().put("type", "welcome").put("token", "safe"))
                }
            } catch (e: Throwable) { failure.set(e) } finally { done.countDown() }
        }
        reader.start()
        try {
            Socket(InetAddress.getLoopbackAddress(), server.localPort).use { socket ->
                socket.soTimeout = 5_000
                NearbyPacket.write(socket,
                    JSONObject().put("type", "hello").put("name", "Café"))
                val response = NearbyPacket.read(socket)
                assertEquals("welcome", response.getString("type"))
            }
            assertTrue(done.await(8, TimeUnit.SECONDS))
            failure.get()?.let { throw AssertionError(it) }
        } finally { server.close() }
    }

    @Test fun chatPacketsKeepRoomOrderAndRejectForgedHistory() {
        val log = NearbyChatLog()
        val host = log.add(CoopSeat.HOST, "h1", "Found one!", true, 1_000L)
        val guest = log.add(CoopSeat.GUEST, "g1", "Great!", true, 1_001L)
        assertEquals(NearbyChatStatus.ACCEPTED, host.status)
        assertEquals(NearbyChatStatus.ACCEPTED, guest.status)
        val history = log.history()
        assertEquals(history, NearbyPacket.readChatHistory(NearbyPacket.chatHistory(history)))
        assertEquals(history.first(), NearbyPacket.readChatLine(
            NearbyPacket.chatLine(history.first())))
        assertTrue(runCatching {
            NearbyPacket.readChatLine(NearbyPacket.chatLine(history.first())
                .put("text", "bad\\nmessage"))
        }.isFailure)
        val forged = NearbyPacket.chatHistory(history)
        forged.put(NearbyPacket.chatLine(history.first()))
        assertTrue(runCatching { NearbyPacket.readChatHistory(forged) }.isFailure)
    }

    @Test fun oversizedOrWrongVersionPacketsFailClosed() {
        ServerSocket(0, 1, InetAddress.getLoopbackAddress()).use { server ->
            Socket(InetAddress.getLoopbackAddress(), server.localPort).use { client ->
                server.accept().use { peer ->
                    DataOutputStream(client.getOutputStream()).apply {
                        writeInt(100_000); flush()
                    }
                    assertTrue(runCatching { NearbyPacket.read(peer) }.isFailure)
                }
            }
        }
        ServerSocket(0, 1, InetAddress.getLoopbackAddress()).use { server ->
            Socket(InetAddress.getLoopbackAddress(), server.localPort).use { client ->
                server.accept().use { peer ->
                    val data = JSONObject().put("type", "hello").put("v", 999)
                        .toString().toByteArray(Charsets.UTF_8)
                    DataOutputStream(client.getOutputStream()).apply {
                        writeInt(data.size); write(data); flush()
                    }
                    assertTrue(runCatching { NearbyPacket.read(peer) }.isFailure)
                }
            }
        }
    }
}
