package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertEquals
import org.junit.Test

/** Visible guidance and tap-step rules only; no changes to word-claim geometry. */
class Ws14FGridZoomGuidanceTest {
    @Test fun denseBoardsHaveUsefulInitialZoomTapWhileSmallBoardsStayUnchanged() {
        assertEquals(0.25f, gridZoomTapIncrement(12), 0.001f)
        assertEquals(0.25f, gridZoomTapIncrement(15), 0.001f)
        assertEquals(0.5f, gridZoomTapIncrement(20), 0.001f)
        assertEquals(0.5f, gridZoomTapIncrement(25), 0.001f)
    }

    @Test fun denseBoardGuidanceIsSpecificButNormalHintIsPreserved() {
        assertEquals("Trace words · pinch to zoom", gridZoomInstruction(12))
        assertEquals("Dense board · use + or pinch", gridZoomInstruction(20))
    }
}
