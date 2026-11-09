package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Debug APK navigation proof, NOT a substitute for user phone QA.
 * Runs against the real activity, its .qa package and QA-only book catalog.
 */
class QaBookshelfLauncherAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun homeHasClearlyLabeledTestBookLauncherAndCanOpenAnActualVolume() {
        compose.onNodeWithText("Test Puzzle Books").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Puzzle Books").assertExists()
        compose.onNodeWithText("Woodland").assertExists()
        compose.onNodeWithText("TEST BOOK · 2 chapters").assertExists()
        compose.onNodeWithText("Open book").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Getting Started · TEST").assertExists()
        compose.onNodeWithText("Start reading").assertExists()
        compose.onNodeWithText("Back to Puzzle Books").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Puzzle Books").assertExists()
    }
}
