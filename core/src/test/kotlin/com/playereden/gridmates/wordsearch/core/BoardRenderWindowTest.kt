package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BoardRenderWindowTest {
    @Test fun oneContinuousGridUsesAtMostVisibleGlyphBudgetWhenZoomed() {
        for (cells in listOf(25, 30, 40, 50, 75, 100)) {
            for (side in listOf(280f, 320f, 480f)) {
                val viewport = LargeBoardViewport().jumpToRegion(
                    0, 0, cells, side
                )
                val window = BoardRenderWindow.around(viewport, side, cells)
                assertTrue(window.glyphBudget <= 196)
                assertTrue(window.glyphBudget < cells * cells)
                assertTrue(0 in window.rows && 0 in window.columns)
                val last = LargeBoardViewport().jumpToRegion(
                    LargeBoardViewport.regionCount(cells) - 1,
                    LargeBoardViewport.regionCount(cells) - 1,
                    cells, side
                )
                val lastWindow = BoardRenderWindow.around(last, side, cells)
                assertTrue(cells - 1 in lastWindow.rows)
                assertTrue(cells - 1 in lastWindow.columns)
                assertTrue(lastWindow.glyphBudget <= 196)
            }
        }
        val zoomed = LargeBoardViewport().jumpToRegion(5, 5, 100, 320f)
        val window = BoardRenderWindow.around(zoomed, 320f, 100)
        assertTrue("100×100 should draw fewer than 2% of letters at 10×",
            window.glyphBudget < 200)
    }

    @Test fun selectionLinesCrossRegionBoundariesWithoutBeingDiscarded() {
        val window = BoardRenderWindow(
            40..50, 40..50, 100
        )
        val crosses = (0..99).map { GridPoint(45, it) }
        assertTrue(window.mayIntersect(crosses))
        assertTrue(window.mayIntersect(listOf(
            GridPoint(0, 0), GridPoint(99, 99)
        )))
        assertFalse(window.mayIntersect(listOf(
            GridPoint(0, 99), GridPoint(9, 90)
        )))
        assertFalse(window.mayIntersect(emptyList()))
    }

    @Test fun fullFitNeverDiscardsAnyLetterAndOverscanIsBounded() {
        for (cells in listOf(12, 15, 20, 25, 30, 40, 50, 75, 100)) {
            val all = BoardRenderWindow.all(cells)
            assertEquals(cells * cells, all.glyphBudget)
            val atFit = BoardRenderWindow.around(LargeBoardViewport(), 320f, cells)
            assertEquals(all, atFit)
            val leftEdge = BoardRenderWindow.around(
                LargeBoardViewport(12f), 320f, cells)
            assertEquals(0, leftEdge.rows.first)
            assertEquals(0, leftEdge.columns.first)
            assertTrue(leftEdge.rows.last < cells)
        }
    }
}
