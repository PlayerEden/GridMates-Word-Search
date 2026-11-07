package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class RaceJournalTest {
    private fun race(seed: Long = 2061117L) = NearbyCoopSession(
        SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, seed),
        "ws06b-room-$seed", NearbyMatch.SHARED_BOARD_RACE
    )

    private fun finish(session: NearbyCoopSession, guestClaims: Int = 5): CoopSnapshot {
        session.admitOrRejoinGuest()
        session.puzzle.targets.forEachIndexed { index, target ->
            val seat = if (index < guestClaims) CoopSeat.GUEST else CoopSeat.HOST
            assertEquals(CoopClaimStatus.FOUND,
                session.submit(seat, session.roomId, "find-$index",
                    target.positions.first(), target.positions.last()).status)
        }
        return session.snapshot()
    }

    @Test fun onlyCompleteHostVerifiedRacesBecomeResults() {
        val room = race()
        val partial = room.snapshot()
        assertTrue(runCatching {
            RaceResult.fromVerifiedSnapshot(partial, CoopSeat.HOST, 1234)
        }.isFailure)
        val complete = finish(room)
        val host = RaceResult.fromVerifiedSnapshot(complete, CoopSeat.HOST, 1234)
        val guest = RaceResult.fromVerifiedSnapshot(complete, CoopSeat.GUEST, 1235)
        assertEquals(7, host.ownWords)
        assertEquals(5, host.partnerWords)
        assertEquals(RaceOutcome.WIN, host.outcome)
        assertEquals(RaceOutcome.LOSS, guest.outcome)
        assertEquals(host.roomId, guest.roomId)
        assertEquals(12, host.totalWords)
        assertEquals(2, host.lead)
    }

    @Test fun oneRoomIsRecordedOnceAcrossReconnectOrReplayEvenIfTimestampDiffers() {
        val room = race()
        val snapshot = finish(room)
        val a = RaceResult.fromVerifiedSnapshot(snapshot, CoopSeat.HOST, 1000)
        val saved = RaceJournalRules.addOnce(emptyList(), a)
        room.guestDisconnected()
        room.admitOrRejoinGuest()
        val replay = RaceResult.fromVerifiedSnapshot(room.snapshot(), CoopSeat.HOST, 9000)
        val again = RaceJournalRules.addOnce(saved, replay)
        assertEquals(saved, again)
        assertEquals(1000, again.single().completedAtMs)
        assertEquals(1, RaceSummary.of(again).played)
    }

    @Test fun tiesAndMultipleRoomsAggregateWithoutChangingSoloProfile() {
        val a = race(27)
        val b = race(28)
        val tied = RaceResult.fromVerifiedSnapshot(finish(a, 6), CoopSeat.HOST, 1200)
        val won = RaceResult.fromVerifiedSnapshot(finish(b, 3), CoopSeat.HOST, 1400)
        assertEquals(RaceOutcome.TIE, tied.outcome)
        val records = RaceJournalRules.addOnce(
            RaceJournalRules.addOnce(emptyList(), tied), won)
        val summary = RaceSummary.of(records)
        assertEquals(2, summary.played)
        assertEquals(1, summary.wins)
        assertEquals(0, summary.losses)
        assertEquals(1, summary.ties)
        val solo = SoloProfile(SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.RELAXED, 999))
        val newProfile = solo.copy(raceResults = records)
        assertEquals(solo.active, newProfile.active)
        assertEquals(0, newProfile.completions)
        assertEquals(0, newProfile.dailyCompletions)
        assertEquals(2, newProfile.raceResults.size)
    }

    @Test fun invalidImportedRecordsAndDuplicateRoomIdsFailClosed() {
        val a = RaceResult.fromVerifiedSnapshot(finish(race()), CoopSeat.GUEST, 1000)
        for (bad in listOf(
            a.copy(roomId = "a"),
            a.copy(boardFingerprint = "invalid"),
            a.copy(ownWords = -1),
            a.copy(partnerWords = a.partnerWords + 2),
            a.copy(completedAtMs = -1),
            a.copy(puzzleId = "other")
        )) {
            assertTrue(runCatching { bad.validate() }.isFailure)
        }
        assertTrue(runCatching {
            RaceJournalRules.validateImported(listOf(a, a.copy(completedAtMs = 2222)))
        }.isFailure)
        assertEquals(listOf(a), RaceJournalRules.validateImported(listOf(a)))
    }

    @Test fun coOpCannotBeAwardedAsRace() {
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 42)
        val co = NearbyCoopSession(run, "ws06b-coop-only")
        val snap = finish(co)
        assertTrue(runCatching {
            RaceResult.fromVerifiedSnapshot(snap, CoopSeat.HOST, 1000)
        }.isFailure)
    }
}
