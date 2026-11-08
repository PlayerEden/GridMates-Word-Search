package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class LargeBoardViewportTest {
    @Test fun regionsCoverContinuousBoardsIncludingPartialLastRowColumn() {
        for ((cells, count) in listOf(25 to 3, 30 to 3, 40 to 4,
            50 to 5, 75 to 8, 100 to 10)) {
            assertEquals(count, LargeBoardViewport.regionCount(cells))
            val bottom = LargeBoardViewport.regionCenter(cells, count - 1, count - 1)
            assertTrue(bottom.row in (count - 1) * 10 until cells)
            assertTrue(bottom.col in (count - 1) * 10 until cells)
            assertEquals(0 to 0, LargeBoardViewport.tappedRegion(
                0.1f, 0.1f, 300f, cells))
            assertEquals((count - 1) to (count - 1),
                LargeBoardViewport.tappedRegion(299.9f, 299.9f, 300f, cells))
            assertNull(LargeBoardViewport.tappedRegion(300f, 200f, 300f, cells))
        }
    }

    @Test fun fitAndJumpMaintainExactCornersAndViewportBoundaries() {
        for (cells in listOf(25, 30, 40, 50, 75, 100))
            for (side in listOf(280f, 320f, 480f)) {
                val fit = LargeBoardViewport()
                assertEquals(GridPoint(0, 0), fit.cellAt(0.1f, 0.1f, side, cells))
                assertEquals(GridPoint(cells-1, cells-1),
                    fit.cellAt(side-0.1f, side-0.1f, side, cells))
                val last = LargeBoardViewport().jumpToRegion(
                    LargeBoardViewport.regionCount(cells)-1,
                    LargeBoardViewport.regionCount(cells)-1, cells, side)
                val far = last.cellAt(side-0.1f, side-0.1f, side, cells)
                assertEquals(GridPoint(cells-1, cells-1), far)
                assertTrue(last.zoom in 2.5f..LargeBoardViewport.MAX_ZOOM)
                assertTrue(cells-1 in last.visibleRows(side,cells))
                assertTrue(cells-1 in last.visibleColumns(side,cells))
                val overview = last.overview(side)
                assertTrue(overview.right > 0.90f && overview.right <= 1f)
                assertTrue(overview.bottom > 0.90f && overview.bottom <= 1f)
                assertNull(last.cellAt(-1f, 0f, side, cells))
                assertNull(last.cellAt(side, side, side, cells))
            }
    }

    @Test fun centeringAndZoomAreStableAndNeverPanOutsideBoard() {
        val side = 320f
        for (cells in listOf(25, 30, 40, 50, 75, 100)) {
            val center = GridPoint(cells / 2, cells / 2)
            val jumped = LargeBoardViewport().jumpTo(center,cells,side)
            assertEquals(center,jumped.cellAt(side/2f,side/2f,side,cells))
            assertTrue(center.row in jumped.visibleRows(side,cells))
            assertTrue(center.col in jumped.visibleColumns(side,cells))
            val extreme = jumped.panned(1e6f,1e6f,side)
            assertEquals(0f,extreme.panX,0f)
            assertEquals(0f,extreme.panY,0f)
            val other = jumped.panned(-1e6f,-1e6f,side)
            assertEquals(-side*(other.zoom-1f),other.panX,0.01f)
            assertEquals(-side*(other.zoom-1f),other.panY,0.01f)
            assertEquals(LargeBoardViewport(),jumped.zoomed(1f,side))
        }
    }

    @Test fun longZoomReachesTenCellsWithoutShrinkingThePuzzle() {
        val at100 = LargeBoardViewport().jumpToRegion(5, 5, 100, 300f)
        assertEquals(10f,at100.zoom,0.001f)
        assertTrue(at100.visibleRows(300f,100).count() in 10..11)
        val at30 = LargeBoardViewport().jumpToRegion(1,1,30,300f)
        assertEquals(3f,at30.zoom,0.001f)
        assertTrue(at30.visibleRows(300f,30).count() in 10..11)
        val zoomed=at30.zoomedAt(12f,300f,150f,150f)
        assertEquals(12f,zoomed.zoom,0.001f)
        assertEquals(at30.cellAt(150f,150f,300f,30),
            zoomed.cellAt(150f,150f,300f,30))
    }
}
