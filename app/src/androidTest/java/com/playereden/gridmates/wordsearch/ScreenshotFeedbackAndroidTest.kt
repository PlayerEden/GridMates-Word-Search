package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** On-screen touch directions agree with actual one/two-finger controls. */
class ScreenshotFeedbackAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun largeBoardStartsFittedAndExplainsTraceAndPinchWithoutModeToggle() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws07a_mode_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, filename)
            rule.setContent {
                GridMatesTheme {
                    WordSearchScreen(store = store, onBack = {}, onRecords = {})
                }
            }
            rule.onNodeWithText(
                "Trace a word with one finger. Pinch with two fingers to zoom, or tap its first and last letters."
            ).assertExists()
            rule.onNodeWithText("Trace words · pinch to zoom").assertExists()
            rule.onNodeWithContentDescription("Fit entire puzzle board").assertExists()
            rule.onNodeWithContentDescription("Zoom in").assertExists()
            rule.onNodeWithText("Mode: Move").assertDoesNotExist()
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
