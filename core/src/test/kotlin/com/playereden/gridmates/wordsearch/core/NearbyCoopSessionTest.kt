package com.playereden.gridmates.wordsearch.core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test

class NearbyCoopSessionTest {
    private fun fresh(seed: Long = 12006L) = SoloProgressRules.freshV2(
        WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, seed
    )
    private fun room(seed: Long = 12006L) = NearbyCoopSession(fresh(seed), "ws05-room-2026")

    private fun expectRejected(block: () -> Unit) {
        try { block(); fail("Expected snapshot validation failure") }
        catch (_: IllegalArgumentException) { /* expected */ }
    }

    @Test fun hostWaitsForGuestButIdleNeverTimesOutOrFinishes() {
        val co = room()
        val p = co.puzzle.targets.first()
        val waiting = co.submit(CoopSeat.HOST, co.roomId, "one", p.positions.first(), p.positions.last())
        assertEquals(CoopClaimStatus.NOT_READY, waiting.status)
        assertEquals(0L, waiting.snapshot.revision)
        assertFalse(waiting.snapshot.finished)
        val ready = co.admitOrRejoinGuest()
        assertTrue(ready.guestReserved)
        assertTrue(ready.guestConnected)
        assertFalse(ready.finished)
        repeat(20) {
            assertEquals(0L, co.snapshot().revision)
            assertFalse(co.snapshot().finished)
        }
        val first = co.submit(CoopSeat.HOST, co.roomId, "one", p.positions.first(), p.positions.last())
        assertEquals(CoopClaimStatus.FOUND, first.status)
        assertEquals(1L, first.snapshot.revision)
    }

    @Test fun duplicateRetransmissionIsIdempotentAndCannotStealOwnership() {
        val co = room()
        co.admitOrRejoinGuest()
        val target = co.puzzle.targets.first()
        val first = co.submit(CoopSeat.GUEST, co.roomId, "req_1", target.positions.last(), target.positions.first())
        assertEquals(CoopClaimStatus.FOUND, first.status)
        assertEquals(CoopSeat.GUEST, first.snapshot.claims.single().seat)
        val resend = co.submit(CoopSeat.GUEST, co.roomId, "req_1", target.positions.last(), target.positions.first())
        assertEquals(CoopClaimStatus.FOUND, resend.status)
        assertTrue(resend.retransmitted)
        assertEquals(1L, resend.snapshot.revision)
        val host = co.submit(CoopSeat.HOST, co.roomId, "req_1", target.positions.first(), target.positions.last())
        assertEquals(CoopClaimStatus.ALREADY_FOUND, host.status)
        assertEquals(CoopSeat.GUEST, host.snapshot.claims.single().seat)
        assertFalse(host.retransmitted)
    }

    @Test fun reconnectPreservesClaimsAndDoesNotStartNewPuzzle() {
        val co = room()
        co.admitOrRejoinGuest()
        val p = co.puzzle.targets.first()
        co.submit(CoopSeat.GUEST, co.roomId, "start", p.positions.first(), p.positions.last())
        val before = co.snapshot()
        val offline = co.guestDisconnected()
        assertTrue(offline.guestReserved)
        assertFalse(offline.guestConnected)
        val blocked = co.submit(CoopSeat.HOST, co.roomId, "wait",
            co.puzzle.targets[1].positions.first(), co.puzzle.targets[1].positions.last())
        assertEquals(CoopClaimStatus.NOT_READY, blocked.status)
        val back = co.admitOrRejoinGuest()
        assertEquals(before.spec.puzzleId, back.spec.puzzleId)
        assertEquals(before.claims, back.claims)
        assertEquals(1L, back.revision)
        assertEquals(CoopClaimStatus.FOUND, co.submit(CoopSeat.HOST, co.roomId, "wait",
            co.puzzle.targets[1].positions.first(), co.puzzle.targets[1].positions.last()).status)
    }

    @Test fun invalidOutOfBoundsNonwordAndWrongRoomNeverAdvance() {
        val co = room()
        co.admitOrRejoinGuest()
        val target = co.puzzle.targets.first()
        assertEquals(CoopClaimStatus.WRONG_ROOM, co.submit(CoopSeat.GUEST,
            "wrong", "r1", target.positions.first(), target.positions.last()).status)
        assertEquals(CoopClaimStatus.INVALID_REQUEST, co.submit(CoopSeat.GUEST,
            co.roomId, "bad request", target.positions.first(), target.positions.last()).status)
        assertEquals(CoopClaimStatus.INVALID_PATH, co.submit(CoopSeat.GUEST,
            co.roomId, "r2", GridPoint(-1, 0), GridPoint(500, 500)).status)
        assertEquals(CoopClaimStatus.INVALID_PATH, co.submit(CoopSeat.GUEST,
            co.roomId, "r3", GridPoint(0, 0), GridPoint(1, 2)).status)
        assertEquals(0L, co.snapshot().revision)
    }

    @Test fun simultaneousSameWordHasExactlyOneFirstOwner() {
        val co = room()
        co.admitOrRejoinGuest()
        val p = co.puzzle.targets.first()
        val latch = CountDownLatch(1)
        val done = CountDownLatch(2)
        val results = java.util.Collections.synchronizedList(mutableListOf<CoopClaimDecision>())
        val pool = Executors.newFixedThreadPool(2)
        try {
            for (seat in listOf(CoopSeat.HOST, CoopSeat.GUEST)) pool.execute {
                latch.await()
                results.add(co.submit(seat, co.roomId, "request-" + seat.name,
                    p.positions.first(), p.positions.last()))
                done.countDown()
            }
            latch.countDown()
            assertTrue(done.await(10, TimeUnit.SECONDS))
            assertEquals(1, results.count { it.status == CoopClaimStatus.FOUND })
            assertEquals(1, results.count { it.status == CoopClaimStatus.ALREADY_FOUND })
            assertEquals(1L, co.snapshot().revision)
            assertEquals(1, co.snapshot().claims.size)
        } finally { pool.shutdownNow() }
    }

    @Test fun completeOnlyAfterAllRealTargetsAndNeverFromInactivity() {
        val co = room()
        co.admitOrRejoinGuest()
        co.puzzle.targets.forEachIndexed { i, word ->
            val decision = co.submit(if (i % 2 == 0) CoopSeat.HOST else CoopSeat.GUEST,
                co.roomId, "find-" + i, word.positions.first(), word.positions.last())
            assertEquals(CoopClaimStatus.FOUND, decision.status)
            assertEquals(i + 1L, decision.snapshot.revision)
            assertEquals(i == co.puzzle.targets.lastIndex, decision.snapshot.finished)
        }
        assertTrue(co.snapshot().finished)
        assertEquals(co.puzzle.targets.size, co.snapshot().hostWords + co.snapshot().guestWords)
        NearbyCoopSession.validateSnapshot(co.snapshot())
    }

    @Test fun specRejectsAnyPuzzleDriftAndSnapshotRejectsFakeClaims() {
        val co = room()
        val s = co.snapshot()
        assertEquals(co.puzzle.id, s.spec.regenerate().id)
        assertEquals(64, s.spec.boardFingerprint.length)
        expectRejected { s.spec.copy(boardFingerprint = "0".repeat(64)).regenerate() }
        expectRejected { s.spec.copy(seed = 991L).regenerate() }
        expectRejected { NearbyCoopSession.validateSnapshot(s.copy(revision = -1L)) }
        expectRejected { NearbyCoopSession.validateSnapshot(s.copy(finished = true)) }
        expectRejected { NearbyCoopSession.validateSnapshot(s.copy(claims = listOf(
            CoopWordClaim("invented", CoopSeat.GUEST, 1L)), revision = 1L)) }
    }

    @Test fun puzzleAndExistingSoloProfileRemainUnmodified() {
        val saved = fresh()
        val solo = SoloProfile(saved)
        val id = solo.active.puzzleId
        val co = NearbyCoopSession(solo.active, "ws05-room-solo")
        co.admitOrRejoinGuest()
        val word = co.puzzle.targets.first()
        co.submit(CoopSeat.HOST, co.roomId, "r1", word.positions.first(), word.positions.last())
        assertEquals(id, solo.active.puzzleId)
        assertTrue(solo.active.foundIds.isEmpty())
        assertTrue(solo.results.isEmpty())
        assertEquals(0, solo.completions)
    }
}
