package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Host-controlled race rules; no Android dependencies or timers. */
class SharedBoardRaceTest {
    private fun race() = NearbyCoopSession(
        SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 20261107L),
        "ws06-race-room", NearbyMatch.SHARED_BOARD_RACE
    )

    private fun claim(r: NearbyCoopSession, index: Int, seat: CoopSeat, id: String) =
        r.puzzle.targets[index].let {
            r.submit(seat, r.roomId, id, it.positions.first(), it.positions.last())
        }

    @Test fun raceWaitsForBothAndHasNoInactivityOrTimeLimitFinish() {
        val r = race()
        assertEquals(NearbyMatch.SHARED_BOARD_RACE, r.snapshot().match)
        assertNull(r.snapshot().raceStanding)
        assertEquals(CoopClaimStatus.NOT_READY, claim(r, 0, CoopSeat.HOST, "p1").status)
        r.admitOrRejoinGuest()
        repeat(100) {
            assertNull(r.snapshot().raceStanding)
            assertEquals(0L, r.snapshot().revision)
        }
        assertEquals(CoopClaimStatus.FOUND, claim(r, 0, CoopSeat.HOST, "p1").status)
        assertNull(r.snapshot().raceStanding)
    }

    @Test fun twelveClaimsYieldOnePointEachAndFinishWithWinner() {
        val r = race()
        r.admitOrRejoinGuest()
        assertEquals(12, r.puzzle.targets.size)
        repeat(12) { i ->
            val seat = if (i < 8) CoopSeat.HOST else CoopSeat.GUEST
            val step = claim(r, i, seat, "claim-$i")
            assertEquals(CoopClaimStatus.FOUND, step.status)
            assertEquals(i + 1L, step.snapshot.revision)
            assertEquals(i == 11, step.snapshot.finished)
        }
        val final = r.snapshot()
        assertEquals(8, final.hostWords)
        assertEquals(4, final.guestWords)
        assertEquals(CoopSeat.HOST, final.raceStanding?.winner)
        assertFalse(requireNotNull(final.raceStanding).tied)
        NearbyCoopSession.validateSnapshot(final)
        assertEquals(CoopClaimStatus.ALREADY_FOUND,
            claim(r, 0, CoopSeat.GUEST, "too-late").status)
        assertEquals(final, r.snapshot())
    }

    @Test fun evenlySplitRaceIsAnActualTieNotAnArbitraryHostWin() {
        val r = race()
        r.admitOrRejoinGuest()
        repeat(12) { i ->
            assertEquals(CoopClaimStatus.FOUND,
                claim(r, i, if (i % 2 == 0) CoopSeat.GUEST else CoopSeat.HOST,
                    "claim-$i").status)
        }
        val standing = requireNotNull(r.snapshot().raceStanding)
        assertTrue(standing.tied)
        assertNull(standing.winner)
        assertEquals(6, standing.hostPoints)
        assertEquals(6, standing.guestPoints)
    }

    @Test fun retransmitOtherSeatAndDisconnectNeverDoubleScore() {
        val r = race()
        r.admitOrRejoinGuest()
        val result = claim(r, 0, CoopSeat.GUEST, "guest-1")
        assertEquals(CoopClaimStatus.FOUND, result.status)
        val retry = claim(r, 0, CoopSeat.GUEST, "guest-1")
        assertTrue(retry.retransmitted)
        assertEquals(CoopClaimStatus.FOUND, retry.status)
        assertEquals(CoopClaimStatus.ALREADY_FOUND,
            claim(r, 0, CoopSeat.HOST, "guest-1").status)
        assertEquals(1, r.snapshot().guestWords)
        r.guestDisconnected()
        assertEquals(CoopClaimStatus.NOT_READY,
            claim(r, 1, CoopSeat.HOST, "after-disconnect").status)
        r.admitOrRejoinGuest()
        assertEquals(CoopClaimStatus.FOUND,
            claim(r, 1, CoopSeat.HOST, "after-disconnect").status)
        assertEquals(2L, r.snapshot().revision)
        assertEquals(1, r.snapshot().hostWords)
        assertEquals(1, r.snapshot().guestWords)
    }

    @Test fun twoNearSimultaneousClaimsReceiveExactlyOnePoint() {
        val r = race()
        r.admitOrRejoinGuest()
        val ready = CountDownLatch(1)
        val done = CountDownLatch(2)
        val results = java.util.Collections.synchronizedList(mutableListOf<CoopClaimStatus>())
        val pool = Executors.newFixedThreadPool(2)
        try {
            listOf(CoopSeat.HOST, CoopSeat.GUEST).forEach { seat ->
                pool.execute {
                    try {
                        ready.await()
                        results.add(claim(r, 0, seat, "same-$seat").status)
                    } finally { done.countDown() }
                }
            }
            ready.countDown()
            assertTrue(done.await(10, TimeUnit.SECONDS))
            assertEquals(1, results.count { it == CoopClaimStatus.FOUND })
            assertEquals(1, results.count { it == CoopClaimStatus.ALREADY_FOUND })
            assertEquals(1, r.snapshot().hostWords + r.snapshot().guestWords)
            assertEquals(1L, r.snapshot().revision)
        } finally { pool.shutdownNow() }
    }

    @Test fun coOpAlwaysKeepsItsNonCompetitiveIdentity() {
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 42L)
        val co = NearbyCoopSession(run, "ws06-coop")
        co.admitOrRejoinGuest()
        co.puzzle.targets.forEachIndexed { i, word ->
            co.submit(CoopSeat.HOST, co.roomId, "finish-$i",
                word.positions.first(), word.positions.last())
        }
        assertTrue(co.snapshot().finished)
        assertEquals(NearbyMatch.COOP, co.snapshot().match)
        assertNull(co.snapshot().raceStanding)
    }
}
