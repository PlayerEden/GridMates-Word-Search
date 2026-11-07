package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/** On-device codec/handshake proof for temporary host-authoritative board looks. */
class SharedCoopBoardLookAndroidTest {
    private fun session(host: CoopBoardLook) = NearbyCoopSession(
        SoloProgressRules.freshV2(WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 18012L), "ws06g-android-room",
        NearbyMatch.COOP, host)

    @Test fun firstGuestPreferenceIsIdenticalInOfferJoinAndBothSeats() {
        val room = session(CoopBoardLook.LINEN)
        val offeredLook = room.offeredBoardLook("blueprint")
        assertEquals(CoopBoardLook.BLUEPRINT, offeredLook)
        val offered = room.snapshot().copy(guestReserved = true,
            guestConnected = true, boardLookId = offeredLook.id)
        assertEquals("linen", room.snapshot().boardLookId)
        assertEquals(offered, NearbyPacket.parseState(NearbyPacket.state(offered)))
        room.freezeGuestBoardLook("blueprint")
        val accepted = room.admitOrRejoinGuest()
        assertTrue(NearbyHandshake.matchesFinal(offered, accepted))
        assertEquals("blueprint", NearbyPacket.parseState(
            NearbyPacket.state(accepted)).boardLookId)
        assertEquals(0, accepted.claims.size)
    }

    @Test fun forgedOrChangedLookPacketsAreRejected() {
        val room = session(CoopBoardLook.CLASSIC)
        val offered = room.snapshot().copy(
            guestReserved = true, guestConnected = true, boardLookId = "linen")
        room.freezeGuestBoardLook("linen")
        val joined = room.admitOrRejoinGuest()
        assertTrue(NearbyHandshake.matchesFinal(offered, joined))
        assertFalse(NearbyHandshake.matchesFinal(offered,
            joined.copy(boardLookId = "blueprint")))
        val wire = NearbyPacket.state(joined)
        assertTrue(runCatching { NearbyPacket.parseState(
            JSONObject(wire.toString()).put("look", "../untrusted.png")) }.isFailure)
        assertTrue(runCatching {
            val missing = JSONObject(wire.toString())
            missing.remove("look")
            NearbyPacket.parseState(missing)
        }.isFailure)
        assertTrue(runCatching { NearbyPacket.parseState(
            JSONObject(wire.toString()).put("match", "SHARED_BOARD_RACE")) }.isFailure)
    }

    @Test fun reconnectAndMidRoundClaimsKeepSameLook() {
        val room = session(CoopBoardLook.CLASSIC)
        room.freezeGuestBoardLook("blueprint")
        room.admitOrRejoinGuest()
        val target = room.puzzle.targets[0]
        room.submit(CoopSeat.GUEST, room.roomId, "guest-claim",
            target.positions.first(), target.positions.last())
        val before = room.snapshot()
        assertEquals(1L, before.revision)
        room.guestDisconnected()
        assertEquals("blueprint", room.offeredBoardLook("classic").id)
        room.freezeGuestBoardLook("classic")
        val back = room.admitOrRejoinGuest()
        assertEquals(before.boardLookId, back.boardLookId)
        assertEquals(before.claims, back.claims)
        assertTrue(NearbyHandshake.matchesFinal(before.copy(guestConnected = true), back))
    }

    @Test fun paletteColorsKeepHighContrastTextIndependentOfPersonalFont() {
        val classic = BoardLookPalettes.forId("classic")
        val linen = BoardLookPalettes.forId("linen")
        val blueprint = BoardLookPalettes.forId("blueprint")
        assertNotEquals(classic.background, linen.background)
        assertNotEquals(linen.background, blueprint.background)
        for (palette in listOf(classic, linen, blueprint)) {
            val back = palette.background
            val ink = palette.letters
            val contrast = (maxOf(back.red, ink.red) - minOf(back.red, ink.red)) +
                (maxOf(back.green, ink.green) - minOf(back.green, ink.green)) +
                (maxOf(back.blue, ink.blue) - minOf(back.blue, ink.blue))
            assertTrue("Readability colors too similar", contrast > 0.75f)
        }
        assertEquals(classic, BoardLookPalettes.forId("unknown"))
    }
}
