package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test

class NearbyMatchShareTest {
    private fun room(mode: NearbyMatch, seed: Long = 4601L) =
        NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, seed),
            "ws06e-share-room-$seed", mode)

    @Test fun completedCoOpIncludesOnlyTruthfulPublicFacts() {
        val session = room(NearbyMatch.COOP)
        session.admitOrRejoinGuest()
        session.puzzle.targets.forEachIndexed { index, target ->
            session.submit(if (index % 2 == 0) CoopSeat.HOST else CoopSeat.GUEST,
                session.roomId, "co-$index",
                target.positions.first(), target.positions.last())
        }
        val text = NearbyMatchShare.text(session.snapshot(), CoopSeat.HOST)
        assertTrue(text.contains("Nearby Co-op · Puzzle complete"))
        assertTrue(text.contains("We found all 12 words together!"))
        assertTrue(text.contains("Good puzzles. Good company."))
        assertFalse(text.contains(session.roomId))
        assertFalse(text.contains(session.puzzle.targets[0].id))
        assertFalse(text.contains(session.puzzle.id))
        assertFalse(text.contains(session.puzzle.seed.toString()))
        assertFalse(text.contains("Room code"))
        assertFalse(text.contains("chat"))
    }

    @Test fun completedRaceIsRelativeToRecipientAndKeepsWordListPrivate() {
        val session = room(NearbyMatch.SHARED_BOARD_RACE, 4602)
        session.admitOrRejoinGuest()
        session.puzzle.targets.forEachIndexed { index, target ->
            val seat = if (index < 4) CoopSeat.GUEST else CoopSeat.HOST
            session.submit(seat, session.roomId, "race-$index",
                target.positions.first(), target.positions.last())
        }
        val host = NearbyMatchShare.text(session.snapshot(), CoopSeat.HOST)
        val guest = NearbyMatchShare.text(session.snapshot(), CoopSeat.GUEST)
        assertTrue(host.contains("I won! · Me 8 / Friend 4"))
        assertTrue(guest.contains("My friend won! · Me 4 / Friend 8"))
        assertFalse(host.contains(session.roomId))
        assertFalse(guest.contains(session.puzzle.targets[1].id))
        assertFalse(host.contains(session.puzzle.targets[1].word))
    }

    @Test fun finishedDuelUsesOnlyRecipientCountAndConfirmedOutcome() {
        val session = room(NearbyMatch.SPEED_DUEL, 4603)
        val duel = SpeedDuelSession(session.puzzle)
        session.admitOrRejoinGuest()
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        session.puzzle.targets.take(3).forEachIndexed { i, target ->
            duel.submit(CoopSeat.HOST, "host-$i",
                target.positions.first(), target.positions.last(), true)
        }
        session.puzzle.targets.forEachIndexed { i, target ->
            duel.submit(CoopSeat.GUEST, "guest-$i",
                target.positions.first(), target.positions.last(), true)
        }
        val base = session.snapshot()
        val guest = base.copy(duel = duel.view(CoopSeat.GUEST), finished = true)
        val host = base.copy(duel = duel.view(CoopSeat.HOST), finished = true)
        val won = NearbyMatchShare.text(guest, CoopSeat.GUEST)
        val lost = NearbyMatchShare.text(host, CoopSeat.HOST)
        assertTrue(won.contains("I finished first! · Me 12 / Friend 3"))
        assertTrue(lost.contains("My friend finished first! · Me 3 / Friend 12"))
        assertFalse(won.contains(session.roomId))
        assertFalse(won.contains(session.puzzle.targets[0].id))
        assertTrue(runCatching { NearbyMatchShare.text(guest, CoopSeat.HOST) }.isFailure)
        assertTrue(runCatching { NearbyMatchShare.text(host, CoopSeat.GUEST) }.isFailure)
    }

    @Test fun unfinishedOrUnadmittedMatchCannotClaimPublicVictory() {
        val session = room(NearbyMatch.SHARED_BOARD_RACE, 4604)
        assertTrue(runCatching {
            NearbyMatchShare.text(session.snapshot(), CoopSeat.HOST)
        }.isFailure)
        session.admitOrRejoinGuest()
        assertTrue(runCatching {
            NearbyMatchShare.text(session.snapshot(), CoopSeat.HOST)
        }.isFailure)
        val bad = session.snapshot().copy(finished = true)
        assertTrue(runCatching { NearbyMatchShare.text(bad, CoopSeat.HOST) }.isFailure)
    }
}
