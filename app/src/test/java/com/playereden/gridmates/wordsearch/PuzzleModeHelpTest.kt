package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleModeHelpTest {
    @Test fun largeBoardsExplainMoveWithoutConfusingItWithWordSelection() {
        val guide = PuzzleModeHelp.opening(20)
        assertTrue(guide.contains("Move"))
        assertTrue(guide.contains("Find"))
        assertFalse(guide.contains("Drag a word"))
    }

    @Test fun findAndMoveDirectionsActuallyDescribeTheirCurrentMode() {
        val move = PuzzleModeHelp.selected(true)
        val find = PuzzleModeHelp.selected(false)
        assertTrue(move.startsWith("Move:"))
        assertTrue(move.contains("pan"))
        assertTrue(move.contains("zoom"))
        assertTrue(move.contains("Tap Find"))
        assertTrue(find.startsWith("Find:"))
        assertTrue(find.contains("first and last"))
        assertFalse(find.contains("pinch"))
    }

    @Test fun smallerBoardsKeepSimpleFindGuidance() {
        assertTrue(PuzzleModeHelp.opening(12).contains("first and last"))
    }
}
