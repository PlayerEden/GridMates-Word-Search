package com.playereden.gridmates.wordsearch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Real loopback sockets exercise each phase in the v4 admission sequence.
 * This is not evidence of real NSD discovery or separate physical phones.
 */
@RunWith(AndroidJUnit4::class)
class NearbyAdmissionAndroidTest {
    private fun newRoom() = NearbyCoopSession(SoloProgressRules.freshV2(
        WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
        PuzzleChallenge.RELAXED, 11500L), "ws05d-admission-room")

    @Test fun missingReceiptCannotReserveFirstGuestAndRetryCanJoin() {
        val room = newRoom()
        val token = "a".repeat(40)
        val preview = room.snapshot().copy(guestReserved = true, guestConnected = true)
        // Initial offer did not change the host's actual seat.
        assertFalse(room.snapshot().guestReserved)
        assertFalse(room.snapshot().guestConnected)
        assertTrue(NearbyHandshake.accepts("accept", room.roomId, token,
            room.roomId, token))
        assertFalse(NearbyHandshake.accepts("accept", room.roomId, "wrong",
            room.roomId, token))
        assertFalse(NearbyHandshake.accepts("accept", "other", token,
            room.roomId, token))
        assertFalse(NearbyHandshake.accepts("hello", room.roomId, token,
            room.roomId, token))
        // A dropped offer left the host free; subsequent accepted attempt works.
        val joined = room.admitOrRejoinGuest()
        assertTrue(NearbyHandshake.matchesFinal(preview, joined))
        assertTrue(joined.guestReserved)
        assertTrue(joined.guestConnected)
    }

    @Test fun actualTcpHelloWelcomeAcceptJoinedRoundTrip() {
        val room = newRoom()
        val token = "b".repeat(40)
        val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress())
        val done = CountDownLatch(1)
        val error = AtomicReference<Throwable?>(null)
        val t = Thread {
            try {
                server.accept().use { socket ->
                    socket.soTimeout = 5_000
                    val hello = NearbyPacket.read(socket)
                    assertEquals("hello", hello.getString("type"))
                    val preview = room.snapshot().copy(
                        guestReserved = true, guestConnected = true)
                    NearbyPacket.write(socket, JSONObject()
                        .put("type", "welcome").put("token", token)
                        .put("roomState", NearbyPacket.state(preview)))
                    // Commit only when the real guest's receipt has arrived.
                    val ack = NearbyPacket.read(socket)
                    assertTrue(NearbyHandshake.accepts(ack.getString("type"),
                        ack.getString("room"), ack.getString("token"),
                        room.roomId, token))
                    val joined = room.admitOrRejoinGuest()
                    NearbyPacket.write(socket, JSONObject().put("type", "joined")
                        .put("roomState", NearbyPacket.state(joined)))
                }
            } catch (e: Throwable) { error.set(e) }
            finally { done.countDown() }
        }
        t.start()
        try {
            Socket(InetAddress.getLoopbackAddress(), server.localPort).use { guest ->
                guest.soTimeout = 5_000
                NearbyPacket.write(guest, JSONObject().put("type", "hello"))
                val welcome = NearbyPacket.read(guest)
                val offered = NearbyPacket.parseState(welcome.getJSONObject("roomState"))
                val credential = welcome.getString("token")
                assertEquals(token, credential)
                assertFalse(room.snapshot().guestReserved)
                NearbyPacket.write(guest, NearbyHandshake.receipt(offered.roomId, credential))
                val confirmation = NearbyPacket.read(guest)
                assertEquals("joined", confirmation.getString("type"))
                val final = NearbyPacket.parseState(confirmation.getJSONObject("roomState"))
                assertTrue(NearbyHandshake.matchesFinal(offered, final))
                assertTrue(final.guestReserved)
            }
            assertTrue(done.await(8, TimeUnit.SECONDS))
            error.get()?.let { throw AssertionError(it) }
        } finally { server.close() }
    }

    @Test fun finalReplyMustMatchOriginalRoomAndPuzzle() {
        val room = newRoom()
        val preview = room.snapshot().copy(guestReserved = true, guestConnected = true)
        val final = room.admitOrRejoinGuest()
        assertTrue(NearbyHandshake.matchesFinal(preview, final))
        assertFalse(NearbyHandshake.matchesFinal(preview,
            final.copy(roomId = "wrong-room")))
        assertFalse(NearbyHandshake.matchesFinal(preview,
            final.copy(guestConnected = false)))
        assertFalse(NearbyHandshake.matchesFinal(preview,
            final.copy(spec = final.spec.copy(boardFingerprint = "0".repeat(64)))))
    }
}
