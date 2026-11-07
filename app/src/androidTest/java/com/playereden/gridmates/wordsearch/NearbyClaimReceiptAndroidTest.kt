package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * Local packet-level tests, not physical two-device acceptance. A guest is
 * entitled to a host confirmation or explicit rejection for its OWN request.
 * The existing v7 "state" remains backward compatible: old peers ignore the
 * optional receipt field; no extra opponent paths are transmitted.
 */
class NearbyClaimReceiptAndroidTest {
    private fun room(): NearbyCoopSession {
        val puzzle = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 40707L)
        return NearbyCoopSession(puzzle, "ws07d-receipt-room")
    }

    @Test fun acceptedAndRejectedClaimsCarryStatusWithoutChangingSnapshot() {
        val game = room()
        game.admitOrRejoinGuest()
        val word = game.puzzle.targets.first()
        val first = game.submit(CoopSeat.GUEST, game.roomId, "claim-1",
            word.positions.first(), word.positions.last())
        assertEquals(CoopClaimStatus.FOUND, first.status)
        val receipt = NearbyClaimReceipt("claim-1", first.status.name)
        val wire = NearbyPacket.state(game.snapshot(), receipt)
        assertEquals(receipt, NearbyPacket.readClaimReceipt(wire))
        assertEquals(game.snapshot(), NearbyPacket.parseState(wire))
        assertEquals("Word confirmed by host.", receipt.description())

        val stolen = game.submit(CoopSeat.HOST, game.roomId, "claim-2",
            word.positions.first(), word.positions.last())
        assertEquals(CoopClaimStatus.ALREADY_FOUND, stolen.status)
        val stolenPacket = NearbyPacket.state(game.snapshot(),
            NearbyClaimReceipt("claim-2", stolen.status.name))
        assertEquals("Someone already found that word.",
            requireNotNull(NearbyPacket.readClaimReceipt(stolenPacket)).description())
        assertEquals(1L, NearbyPacket.parseState(stolenPacket).revision)

        val invalid = game.submit(CoopSeat.GUEST, game.roomId, "claim-3",
            GridPoint(-1, -1), GridPoint(50, 50))
        assertEquals(CoopClaimStatus.INVALID_PATH, invalid.status)
        assertEquals("Host did not accept that selection.",
            NearbyClaimReceipt("claim-3", invalid.status.name).description())
        assertEquals(1L, game.snapshot().revision)
    }

    @Test fun oldV7SnapshotWithoutReceiptStillParsesAndMalformedReceiptFailsClosed() {
        val standard = NearbyPacket.state(room().snapshot())
        assertNull(NearbyPacket.readClaimReceipt(standard))
        assertEquals(room().snapshot().match, NearbyPacket.parseState(standard).match)
        for (ack in listOf(
            JSONObject().put("id", "bad id").put("status", "FOUND"),
            JSONObject().put("id", "a").put("status", "GRANTED"),
            JSONObject().put("id", "").put("status", "INVALID_PATH")
        )) {
            val modified = JSONObject(standard.toString()).put("claimReceipt", ack)
            assertTrue(runCatching { NearbyPacket.readClaimReceipt(modified) }.isFailure)
        }
        val wrongShape = JSONObject(standard.toString())
            .put("claimReceipt", "not an object")
        assertTrue(runCatching { NearbyPacket.readClaimReceipt(wrongShape) }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.state(room().snapshot(),
                NearbyClaimReceipt("bad id", "FOUND"))
        }.isFailure)
    }

    @Test fun existingFramedTcpStateCanCarryPrivateGuestAckWithoutNewPacketType() {
        val game = room()
        game.admitOrRejoinGuest()
        val wire = NearbyPacket.state(game.snapshot(),
            NearbyClaimReceipt("guest-one", "INVALID_PATH"))
        ServerSocket(0, 1, InetAddress.getLoopbackAddress()).use { server ->
            Socket(InetAddress.getLoopbackAddress(), server.localPort).use { sender ->
                server.accept().use { receiver ->
                    sender.soTimeout = 5_000
                    receiver.soTimeout = 5_000
                    NearbyPacket.write(sender, wire)
                    val result = NearbyPacket.read(receiver)
                    assertEquals("state", result.getString("type"))
                    assertEquals(7, result.getInt("v"))
                    assertEquals(game.snapshot(), NearbyPacket.parseState(result))
                    assertEquals(NearbyClaimReceipt("guest-one", "INVALID_PATH"),
                        NearbyPacket.readClaimReceipt(result))
                }
            }
        }
    }

    @Test fun receiptContainsOnlyRequestAndStatusNeverOpponentDuelPaths() {
        val game = room()
        game.admitOrRejoinGuest()
        val duel = SpeedDuelSession(game.puzzle)
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        val hidden = game.puzzle.targets.first()
        duel.submit(CoopSeat.HOST, "private-1", hidden.positions.first(),
            hidden.positions.last(), true)
        val guestView = duel.view(CoopSeat.GUEST)
        val guestState = game.snapshot().copy(match = NearbyMatch.SPEED_DUEL,
            duel = guestView, finished = guestView.finished,
            boardLookId = CoopBoardLook.CLASSIC.id)
        val sent = NearbyPacket.state(guestState,
            NearbyClaimReceipt("guest-two", "ALREADY_FOUND"))
        val parsed = NearbyPacket.parseState(sent)
        assertEquals(1, requireNotNull(parsed.duel).hostCount)
        assertTrue(parsed.duel!!.ownFoundIds.isEmpty())
        assertFalse(sent.getJSONObject("claimReceipt").has("wordId"))
        assertFalse(sent.getJSONObject("claimReceipt").has("path"))
        assertEquals("ALREADY_FOUND",
            NearbyPacket.readClaimReceipt(sent)?.status)
    }
}
