package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SpeedDuelSessionTest {
    private fun duel() = SpeedDuelSession(SoloProgressRules.puzzle(
        SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 1171L)))

    private fun find(session: SpeedDuelSession, seat: CoopSeat, index: Int,
                     request: String = "claim-$seat-$index"): DuelDecision {
        val t = session.puzzle.targets[index]
        return session.submit(seat, request, t.positions.first(), t.positions.last(), true)
    }

    @Test fun bothMustBeReadyAndBoardIsUnplayableBeforeGo() {
        val d = duel()
        assertFalse(d.view(CoopSeat.HOST).started)
        assertFalse(d.view(CoopSeat.GUEST).started)
        assertEquals(DuelCommandStatus.NOT_CONNECTED, d.ready(CoopSeat.HOST, false).status)
        assertEquals(DuelCommandStatus.NOT_STARTED, find(d, CoopSeat.HOST, 0).status)
        assertEquals(DuelCommandStatus.READY, d.ready(CoopSeat.GUEST, true).status)
        assertFalse(d.view(CoopSeat.HOST).started)
        assertEquals(DuelCommandStatus.STARTED, d.ready(CoopSeat.HOST, true).status)
        assertTrue(d.view(CoopSeat.GUEST).started)
        assertTrue(d.view(CoopSeat.HOST).ownReady)
        assertTrue(d.view(CoopSeat.GUEST).ownReady)
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.HOST, 0).status)
    }

    @Test fun privateLedgersLetBothPlayersFindSameWordWithoutStealing() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.HOST, 0).status)
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.GUEST, 0).status)
        val host = d.view(CoopSeat.HOST)
        val guest = d.view(CoopSeat.GUEST)
        assertEquals(1, host.hostCount)
        assertEquals(1, guest.guestCount)
        assertEquals(1, host.ownFoundIds.size)
        assertEquals(1, guest.ownFoundIds.size)
        assertEquals(DuelCommandStatus.ALREADY_FOUND, find(d, CoopSeat.HOST, 0, "new-id").status)
        assertEquals(1, d.view(CoopSeat.HOST).hostCount)
        host.validate(d.puzzle, true)
        guest.validate(d.puzzle, true)
    }

    @Test fun hostAndGuestNeverReceiveOpponentsPrivateWordIds() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        find(d, CoopSeat.HOST, 0)
        find(d, CoopSeat.GUEST, 1)
        val host = d.view(CoopSeat.HOST)
        val guest = d.view(CoopSeat.GUEST)
        val first = d.puzzle.targets[0].id
        val second = d.puzzle.targets[1].id
        assertEquals(setOf(first), host.ownFoundIds)
        assertEquals(setOf(second), guest.ownFoundIds)
        assertFalse(host.ownFoundIds.contains(second))
        assertFalse(guest.ownFoundIds.contains(first))
        assertEquals(1, host.opponentCount)
        assertEquals(1, guest.opponentCount)
    }

    @Test fun firstSerializedCompleteBoardWinsAndLateFinishesDoNotTie() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        repeat(d.puzzle.targets.size - 1) { i -> assertEquals(DuelCommandStatus.FOUND,
            find(d, CoopSeat.HOST, i).status) }
        repeat(d.puzzle.targets.size - 1) { i -> assertEquals(DuelCommandStatus.FOUND,
            find(d, CoopSeat.GUEST, i).status) }
        val last = d.puzzle.targets.lastIndex
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.GUEST, last).status)
        assertEquals(CoopSeat.GUEST, d.view(CoopSeat.HOST).winner)
        assertTrue(d.view(CoopSeat.GUEST).finished)
        assertEquals(DuelCommandStatus.FINISHED, find(d, CoopSeat.HOST, last).status)
        assertEquals(d.puzzle.targets.size - 1, d.view(CoopSeat.HOST).hostCount)
        assertEquals(d.puzzle.targets.size, d.view(CoopSeat.GUEST).guestCount)
    }

    @Test fun disconnectedGuestMustReReadyOnlyBeforeStart() {
        val d = duel()
        d.ready(CoopSeat.GUEST, true)
        d.onGuestDisconnected()
        assertFalse(d.view(CoopSeat.GUEST).guestReady)
        d.ready(CoopSeat.HOST, true)
        assertFalse(d.view(CoopSeat.HOST).started)
        d.ready(CoopSeat.GUEST, true)
        assertTrue(d.view(CoopSeat.HOST).started)
        find(d, CoopSeat.GUEST, 0)
        d.onGuestDisconnected()
        assertTrue(d.view(CoopSeat.GUEST).guestReady)
        assertEquals(DuelCommandStatus.NOT_CONNECTED, d.submit(CoopSeat.GUEST,
            "offline", d.puzzle.targets[1].positions.first(),
            d.puzzle.targets[1].positions.last(), false).status)
        assertEquals(1, d.view(CoopSeat.GUEST).guestCount)
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.GUEST, 1).status)
    }

    @Test fun sameSeatCommandReplayIsIdempotent() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        val first = find(d, CoopSeat.GUEST, 0, "retryable-1")
        assertEquals(DuelCommandStatus.FOUND, first.status)
        val replay = find(d, CoopSeat.GUEST, 0, "retryable-1")
        assertTrue(replay.retransmitted)
        assertEquals(DuelCommandStatus.FOUND, replay.status)
        assertEquals(1, d.view(CoopSeat.GUEST).guestCount)
        assertEquals(DuelCommandStatus.FOUND, find(d, CoopSeat.HOST, 0,
            "retryable-1").status)
    }

    @Test fun invalidViewsAndLegacySharedClaimsRejected() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        val frame = d.view(CoopSeat.GUEST)
        assertTrue(runCatching { frame.copy(hostCount = 1).validate(d.puzzle, true) }.isSuccess)
        assertTrue(runCatching { frame.copy(guestCount = 1).validate(d.puzzle, true) }.isFailure)
        assertTrue(runCatching { frame.copy(finished = true).validate(d.puzzle, true) }.isFailure)
        assertTrue(runCatching { frame.copy(started = false).validate(d.puzzle, true) }.isFailure)
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 42L)
        val legacy = NearbyCoopSession(run, "ws06c-separate", NearbyMatch.SPEED_DUEL)
        legacy.admitOrRejoinGuest()
        val t = legacy.puzzle.targets[0]
        assertEquals(CoopClaimStatus.INVALID_REQUEST,
            legacy.submit(CoopSeat.HOST, legacy.roomId, "legacy",
                t.positions.first(), t.positions.last()).status)
    }

    @Test fun concurrentNearCompleteRaceHasExactlyOneWinner() {
        val d = duel()
        d.ready(CoopSeat.HOST, true)
        d.ready(CoopSeat.GUEST, true)
        repeat(d.puzzle.targets.lastIndex) {
            find(d, CoopSeat.HOST, it)
            find(d, CoopSeat.GUEST, it)
        }
        val ready = CountDownLatch(1)
        val done = CountDownLatch(2)
        val outcomes = java.util.Collections.synchronizedList(mutableListOf<DuelCommandStatus>())
        val pool = Executors.newFixedThreadPool(2)
        try {
            for (seat in CoopSeat.entries) pool.execute {
                try {
                    ready.await()
                    outcomes.add(find(d, seat, d.puzzle.targets.lastIndex).status)
                } finally { done.countDown() }
            }
            ready.countDown()
            assertTrue(done.await(10, TimeUnit.SECONDS))
            assertEquals(1, outcomes.count { it == DuelCommandStatus.FOUND })
            assertEquals(1, outcomes.count { it == DuelCommandStatus.FINISHED })
            assertNotNull(d.view(CoopSeat.HOST).winner)
        } finally { pool.shutdownNow() }
    }
}
