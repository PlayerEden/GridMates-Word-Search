package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class WordDragSnapTest {
    @Test fun horizontalSelectionSnapsWithoutPerfectFingerAlignment() {
        val start = GridPoint(3, 3)
        val snap = WordDragSnap.update(start, 3.2f, 0.32f, 10)
        assertEquals(GridPoint(3, 6), snap.end)
        assertEquals(DragDirection(0, 1), snap.direction)
        assertEquals(4, snap.length)
    }

    @Test fun diagonalSelectionStaysInDirectionAfterFingerWobble() {
        val first = WordDragSnap.update(GridPoint(3, 3), 2.4f, 2.2f, 10)
        assertEquals(DragDirection(1, 1), first.direction)
        val wandering = WordDragSnap.update(GridPoint(3, 3), 5.1f, 3.4f, 10, first.direction)
        assertEquals(DragDirection(1, 1), wandering.direction)
        assertEquals(GridPoint(6, 6), wandering.end)
    }

    @Test fun allEightDirectionsAndBoardEdges() {
        val start = GridPoint(4, 4)
        for (dr in -1..1) for (dc in -1..1) {
            if (dr == 0 && dc == 0) continue
            val dx = if (dc == 0) 0f else dc * 5.0f
            val dy = if (dr == 0) 0f else dr * 5.0f
            val snap = WordDragSnap.update(start, dx, dy, 8)
            val direction = DragDirection(dr, dc)
            assertEquals(direction, snap.direction)
            assertTrue(snap.end.row in 0..7 && snap.end.col in 0..7)
            val actual = WordSearchRules.line(start, snap.end, 8)
            assertEquals(snap.length, actual.size)
        }
    }

    @Test fun initialMovementWaitsAndReturningNearStartReleasesLock() {
        val p = GridPoint(2, 2)
        assertNull(WordDragSnap.update(p, 0.3f, 0.1f, 8).direction)
        val locked = WordDragSnap.update(p, 1.6f, 0.1f, 8)
        assertEquals(DragDirection(0, 1), locked.direction)
        val returned = WordDragSnap.update(p, 0.1f, 0.1f, 8, locked.direction)
        assertNull(returned.direction)
        assertEquals(p, returned.end)
    }

    @Test fun edgeDragNeverOverflowsAndRejectsInvalidStart() {
        val p = GridPoint(0, 0)
        assertEquals(p, WordDragSnap.update(p, -6f, -6f, 8).end)
        assertEquals(GridPoint(0, 7), WordDragSnap.update(p, 99f, 0f, 8).end)
        assertEquals(GridPoint(7, 7), WordDragSnap.update(p, 99f, 99f, 8).end)
        assertNull(WordDragSnap.update(GridPoint(-1, 0), 4f, 4f, 8).direction)
    }

    @Test fun recomputingDirectionFromFullSwipeCorrectsEarlyDiagonalWobble() {
        val start = GridPoint(6, 5)
        val wobble = WordDragSnap.update(start, 0.62f, 0.59f, 15)
        assertEquals(DragDirection(1, 1), wobble.direction)
        val fullSwipe = WordDragSnap.update(start, 7f, 0.16f, 15)
        assertEquals(DragDirection(0, 1), fullSwipe.direction)
        assertEquals(GridPoint(6, 12), fullSwipe.end)
    }

    @Test fun noNaNCanGenerateAClaimPath() {
        val p = GridPoint(3, 3)
        val result = WordDragSnap.update(p, Float.NaN, 1f, 10)
        assertEquals(p, result.end)
        assertNull(result.direction)
    }
}
