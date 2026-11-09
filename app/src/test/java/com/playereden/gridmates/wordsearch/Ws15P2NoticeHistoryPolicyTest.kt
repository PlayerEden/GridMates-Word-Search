package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Ws15P2NoticeHistoryPolicyTest {
    @Test fun meaningfulHintsAndSavedResultsStayAvailable() {
        assertTrue(shouldRememberBookPageNotice("Hint: MOSS is highlighted on the grid."))
        assertTrue(shouldRememberBookPageNotice("✓ FOX found!"))
        assertTrue(shouldRememberBookPageNotice("The selection could not be saved."))
    }
    @Test fun missedTracesAndAlreadyFoundWarningsAreNotArchived() {
        assertFalse(shouldRememberBookPageNotice("No matching word in that selection."))
        assertFalse(shouldRememberBookPageNotice("You've already found that word."))
        assertFalse(shouldRememberBookPageNotice(""))
    }
}
