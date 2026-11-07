package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** The actual gameplay mode must agree with what the on-screen hint says. */
class ScreenshotFeedbackAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun movingBoardExplainsPanningThenFindExplainsWordSelection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws05j_mode_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, filename)
            rule.setContent {
                GridMatesTheme {
                    WordSearchScreen(store = store, onBack = {}, onRecords = {})
                }
            }
            rule.onNodeWithText("Large board: tap Move to explore, then Find to select words.")
                .assertExists()
            rule.onNodeWithText("Move").performClick()
            rule.onNodeWithText(
                "Move: drag to pan or pinch to zoom. Tap Find to select words."
            ).assertExists()
            rule.onNodeWithText("Find").performClick()
            rule.onNodeWithText(
                "Find: drag a word, or tap its first and last letters."
            ).assertExists()
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
