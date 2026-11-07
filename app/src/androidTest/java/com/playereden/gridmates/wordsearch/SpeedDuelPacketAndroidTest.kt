package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/** API35 instrumentation: recipient filtering is a wire-level contract. */
class SpeedDuelPacketAndroidTest {
    private fun frames(): Triple<CoopSnapshot, CoopSnapshot, WordSearchPuzzle> {
        val run = SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 71777L)
        val room = NearbyCoopSession(run, "ws06c-privacy-room", NearbyMatch.SPEED_DUEL)
        val duel = SpeedDuelSession(room.puzzle)
        room.admitOrRejoinGuest()
        assertEquals(DuelCommandStatus.READY, duel.ready(CoopSeat.HOST, true).status)
        assertEquals(DuelCommandStatus.STARTED, duel.ready(CoopSeat.GUEST, true).status)
        val h = room.puzzle.targets[0]
        val g = room.puzzle.targets[1]
        duel.submit(CoopSeat.HOST, "host-1", h.positions.first(), h.positions.last(), true)
        duel.submit(CoopSeat.GUEST, "guest-1", g.positions.first(), g.positions.last(), true)
        val base = room.snapshot()
        return Triple(base.copy(duel = duel.view(CoopSeat.HOST)),
            base.copy(duel = duel.view(CoopSeat.GUEST)), room.puzzle)
    }

    @Test fun v6GuestPacketContainsNoHostWordIdsOrPaths() {
        val (host, guest, puzzle) = frames()
        val hostId = puzzle.targets[0].id
        val guestId = puzzle.targets[1].id
        assertFalse(guest.duel!!.ownFoundIds.contains(hostId))
        assertFalse(host.duel!!.ownFoundIds.contains(guestId))
        val hostWire = NearbyPacket.state(host)
        val guestWire = NearbyPacket.state(guest)
        assertTrue(guestWire.toString().contains(guestId))
        assertFalse(guestWire.toString().contains(hostId))
        assertEquals(0, guestWire.getJSONArray("claims").length())
        assertFalse(guestWire.getJSONObject("duel").has("opponentWords"))
        assertEquals(1, guestWire.getJSONObject("duel").getInt("hostCount"))
        assertEquals(1, guestWire.getJSONObject("duel").getInt("guestCount"))
        assertEquals(guest, NearbyPacket.parseState(guestWire))
        assertEquals(host, NearbyPacket.parseState(hostWire))
        assertEquals(guestId, NearbyPacket.parseState(guestWire).foundIds.single())
    }

    @Test fun malformedDuelStateIsRejectedAndCoopCannotCarryDuel() {
        val (_, guest, _) = frames()
        val good = NearbyPacket.state(guest)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(good.toString()).apply {
                getJSONObject("duel").put("guestCount", 2)
            })
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(good.toString()).apply {
                getJSONObject("duel").put("winner", "HOST")
            })
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(good.toString()).apply {
                getJSONObject("duel").put("own", JSONArray().put("not-a-real-word"))
            })
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(good.toString()).apply {
                put("match", "COOP")
            })
        }.isFailure)
        assertTrue(runCatching {
            NearbyPacket.parseState(JSONObject(good.toString()).apply {
                remove("duel")
            })
        }.isFailure)
    }

    @Test fun finalAdmissionRejectsHostPrivateViewAndStateRegression() {
        val (_, guest, _) = frames()
        val offered = guest.copy(guestConnected = true)
        assertTrue(NearbyHandshake.matchesFinal(offered, offered))
        assertFalse(NearbyHandshake.matchesFinal(offered,
            offered.copy(duel = offered.duel!!.copy(recipient = CoopSeat.HOST))))
        assertFalse(NearbyHandshake.matchesFinal(offered,
            offered.copy(duel = offered.duel!!.copy(revision = 0))))
        assertFalse(NearbyHandshake.matchesFinal(offered,
            offered.copy(duel = offered.duel!!.copy(ownFoundIds = emptySet()))))
    }
}
