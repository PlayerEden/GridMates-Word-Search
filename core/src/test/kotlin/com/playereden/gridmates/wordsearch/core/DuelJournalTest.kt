package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class DuelJournalTest {
    private fun create(seed: Long = 1617L): Pair<NearbyCoopSession, SpeedDuelSession> {
        val shared = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, seed),
            "ws06d-duel-room-" + seed, NearbyMatch.SPEED_DUEL)
        val duel = SpeedDuelSession(shared.puzzle)
        shared.admitOrRejoinGuest()
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        return shared to duel
    }

    private fun finished(seed: Long = 1617L, winner: CoopSeat = CoopSeat.HOST):
            Pair<CoopSnapshot, CoopSnapshot> {
        val (room, duel) = create(seed)
        val loser = if (winner == CoopSeat.HOST) CoopSeat.GUEST else CoopSeat.HOST
        room.puzzle.targets.take(4).forEachIndexed { i, target ->
            assertEquals(DuelCommandStatus.FOUND,
                duel.submit(loser, "loser-" + i, target.positions.first(),
                    target.positions.last(), true).status)
        }
        room.puzzle.targets.forEachIndexed { i, target ->
            assertEquals(DuelCommandStatus.FOUND,
                duel.submit(winner, "winner-" + i, target.positions.first(),
                    target.positions.last(), true).status)
        }
        val snapshot = room.snapshot()
        return snapshot.copy(duel = duel.view(CoopSeat.HOST), finished = true) to
            snapshot.copy(duel = duel.view(CoopSeat.GUEST), finished = true)
    }

    @Test fun verifiedFinishedDuelGivesOppositePlayerRelativeOutcomes() {
        val (host, guest) = finished()
        val home = DuelResult.fromVerifiedSnapshot(host, CoopSeat.HOST, 1000L)
        val away = DuelResult.fromVerifiedSnapshot(guest, CoopSeat.GUEST, 1001L)
        assertTrue(home.won)
        assertFalse(away.won)
        assertEquals(CoopSeat.HOST, home.winner)
        assertEquals(12, home.ownWords)
        assertEquals(4, home.partnerWords)
        assertEquals(4, away.ownWords)
        assertEquals(12, away.partnerWords)
        assertEquals(home.roomId, away.roomId)
        assertEquals(8, away.wordsRemaining)
        assertEquals(1, DuelSummary.of(listOf(home)).wins)
        assertEquals(1, DuelSummary.of(listOf(away)).losses)
    }

    @Test fun incompleteUnadmittedAndWrongRecipientCannotBeAwarded() {
        val (room, duel) = create()
        val partial = room.snapshot().copy(duel = duel.view(CoopSeat.HOST))
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(partial, CoopSeat.HOST, 1000)
        }.isFailure)
        val (host, guest) = finished()
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(host, CoopSeat.GUEST, 1000)
        }.isFailure)
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(guest, CoopSeat.HOST, 1000)
        }.isFailure)
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(host.copy(guestReserved = false),
                CoopSeat.HOST, 1000)
        }.isFailure)
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(host.copy(match = NearbyMatch.SHARED_BOARD_RACE),
                CoopSeat.HOST, 1000)
        }.isFailure)
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(host.copy(duel = null),
                CoopSeat.HOST, 1000)
        }.isFailure)
        assertTrue(runCatching {
            DuelResult.fromVerifiedSnapshot(host.copy(finished = false),
                CoopSeat.HOST, 1000)
        }.isFailure)
    }

    @Test fun sameRoomReplayAndReconnectCannotAwardDuplicateWin() {
        val (host, _) = finished()
        val first = DuelResult.fromVerifiedSnapshot(host, CoopSeat.HOST, 1000)
        val ledger = DuelJournalRules.addOnce(emptyList(), first)
        val replay = DuelResult.fromVerifiedSnapshot(host, CoopSeat.HOST, 8000)
        assertEquals(ledger, DuelJournalRules.addOnce(ledger, replay))
        assertEquals(1000, DuelJournalRules.addOnce(ledger, replay).single().completedAtMs)
        assertEquals(1, DuelSummary.of(ledger).played)
    }

    @Test fun noTiesAndImpossibleImportedResultsFailClosed() {
        val (host, _) = finished()
        val valid = DuelResult.fromVerifiedSnapshot(host, CoopSeat.HOST, 1000)
        for (bad in listOf(
            valid.copy(roomId = "x"),
            valid.copy(puzzleId = "wordsearch:v1:bad"),
            valid.copy(boardFingerprint = "not-a-digest"),
            valid.copy(ownWords = 11),
            valid.copy(partnerWords = 12),
            valid.copy(winner = CoopSeat.GUEST),
            valid.copy(completedAtMs = 0L)
        )) assertTrue(runCatching { bad.validate() }.isFailure)
        assertTrue(runCatching {
            DuelJournalRules.validateImported(listOf(valid, valid.copy(completedAtMs = 2000)))
        }.isFailure)
        assertEquals(listOf(valid), DuelJournalRules.validateImported(listOf(valid)))
    }

    @Test fun separateJournalsPreserveSoloDailyAndRaceTotals() {
        val (first, _) = finished(401L, CoopSeat.HOST)
        val (second, _) = finished(402L, CoopSeat.GUEST)
        val a = DuelResult.fromVerifiedSnapshot(first, CoopSeat.HOST, 1000)
        val b = DuelResult.fromVerifiedSnapshot(second, CoopSeat.HOST, 2000)
        val saved = DuelJournalRules.addOnce(DuelJournalRules.addOnce(emptyList(), a), b)
        val summary = DuelSummary.of(saved)
        assertEquals(2, summary.played)
        assertEquals(1, summary.wins)
        assertEquals(1, summary.losses)
        assertEquals(16, summary.totalWordsFound)
        val solo = SoloProfile(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 999))
        val updated = solo.copy(duelResults = saved)
        assertEquals(solo.active, updated.active)
        assertEquals(0, updated.completions)
        assertEquals(0, updated.dailyCompletions)
        assertEquals(0, updated.raceSummary.played)
        assertEquals(2, updated.duelSummary.played)
    }
}
