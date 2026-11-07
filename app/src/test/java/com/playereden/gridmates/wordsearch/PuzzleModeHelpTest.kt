package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleModeHelpTest {
    @Test fun largeBoardsExplainBothGestureTypesWithoutForcedModes() {
        val guide = PuzzleModeHelp.opening(20)
        assertTrue(guide.contains("one finger"))
        assertTrue(guide.contains("Pinch"))
        assertTrue(guide.contains("first and last"))
        assertFalse(guide.contains("tap Move"))
    }

    @Test fun guidanceMakesOneAndTwoFingerResponsibilitiesClear() {
        val guide = PuzzleModeHelp.selected(false)
        assertTrue(guide.contains("One finger"))
        assertTrue(guide.contains("Two fingers"))
        assertFalse(guide.contains("Tap Find"))
    }

    @Test fun smallerBoardsKeepSimpleFindGuidance() {
        assertTrue(PuzzleModeHelp.opening(12).contains("first and last"))
    }
}
