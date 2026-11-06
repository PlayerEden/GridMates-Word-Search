package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BoardViewportTransformTest {
    private val view = 360f

    @Test fun noZoomHasNoPanAndMapsAllEdges() {
        val base = BoardViewportTransform(1f).panned(-50f, -120f, view)
        assertEquals(0f, base.panX, 0.001f)
        assertEquals(0f, base.panY, 0.001f)
        assertEquals(GridPoint(0, 0), base.cellAt(0f, 0f, view, 20))
        assertEquals(GridPoint(19, 19), base.cellAt(359f, 359f, view, 20))
        assertNull(base.cellAt(360f, 0f, view, 20))
    }

    @Test fun screenshotsMagnificationsScaleCellsAndKeepPanBounds() {
        for (zoom in listOf(1f, 1.25f, 1.45f, 1.5f, 1.85f, 2f, 2.5f)) {
            val t = BoardViewportTransform(1f).zoomed(zoom, view)
            val moved = t.panned(-10000f, -10000f, view)
            assertEquals(-view * (zoom - 1f), moved.panX, 0.005f)
            assertEquals(-view * (zoom - 1f), moved.panY, 0.005f)
            val expandedCellWidth = view / 20f * zoom
            assertTrue(expandedCellWidth >= view / 20f)
            val left = BoardViewportTransform(zoom).cellAt(
                expandedCellWidth * 0.45f, expandedCellWidth * 0.45f, view, 20)
            assertEquals(GridPoint(0, 0), left)
        }
    }

    @Test fun zoomPreservesViewportCenterWithinBoundsAndResetsAt100() {
        val t = BoardViewportTransform(1.5f, -90f, -90f)
        val beforeX = (view / 2f - t.panX) / t.zoom
        val after = t.zoomed(2f, view)
        val afterX = (view / 2f - after.panX) / after.zoom
        assertEquals(beforeX, afterX, 0.001f)
        val reset = after.zoomed(1f, view)
        assertEquals(0f, reset.panX, 0.001f)
        assertEquals(0f, reset.panY, 0.001f)
    }

    @Test fun findAndMoveShareSameCoordinatesWhenBoardIsTranslated() {
        val t = BoardViewportTransform(2f, -180f, -180f)
        assertEquals(GridPoint(10, 10), t.cellAt(198f, 198f, view, 20))
        assertEquals(GridPoint(5, 5), t.cellAt(18f, 18f, view, 20))
        assertNull(t.cellAt(-1f, 200f, view, 20))
    }
}
