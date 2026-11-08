package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaperbackPageLayoutTest {
    @Test fun paperbackMarginsPreserveSpaceAtPhoneAndTabletWidths() {
        val widths = listOf(280, 320, 359, 360, 390, 419, 420, 600, 700, 850)
        val expected = listOf(16, 16, 16, 18, 18, 18, 20, 20, 24, 24)
        assertEquals(expected, widths.map(PaperbackPageLayout::sideMarginDp))
        widths.forEach { width ->
            assertTrue("Gutters must never swallow most of a small board",
                PaperbackPageLayout.sideMarginDp(width) * 2 < width / 3)
        }
    }

    @Test fun fitAndZoomReachAllCornerCellsWithNoCornerCutout() {
        for (side in listOf(280f, 288f, 320f, 360f))
            for (cells in listOf(8, 12, 15, 20)) {
                val fit = BoardViewportTransform(1f)
                val at = side - 0.1f
                assertEquals(GridPoint(0, 0), fit.cellAt(0.1f, 0.1f, side, cells))
                assertEquals(GridPoint(cells - 1, cells - 1),
                    fit.cellAt(at, at, side, cells))
                assertEquals(GridPoint(0, cells - 1),
                    fit.cellAt(at, 0.1f, side, cells))
                assertEquals(GridPoint(cells - 1, 0),
                    fit.cellAt(0.1f, at, side, cells))
                for (zoom in listOf(1.25f, 1.5f, 2f, 2.5f)) {
                    val topLeft = BoardViewportTransform(1f).zoomedAt(
                        zoom, side, 0f, 0f).panned(100000f, 100000f, side)
                    assertEquals(GridPoint(0, 0),
                        topLeft.cellAt(0.1f, 0.1f, side, cells))
                    val bottomRight = topLeft.panned(-100000f, -100000f, side)
                    assertEquals(GridPoint(cells - 1, cells - 1),
                        bottomRight.cellAt(at, at, side, cells))
                }
            }
    }
}
