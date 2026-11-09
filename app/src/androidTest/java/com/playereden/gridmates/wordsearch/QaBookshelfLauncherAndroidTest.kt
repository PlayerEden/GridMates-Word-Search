package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
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
        compose.onNodeWithContentDescription("Explore books").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Puzzle Books").assertExists()
        // Preserve the old A/B bookcase regression path as an explicit option.
        compose.onNodeWithTag("ws15-preview-tools").performClick()
        compose.onNodeWithText("Classic shelves").performClick()
        compose.onNodeWithText("Woodland").assertExists()
        compose.onAllNodesWithText("TEST BOOK · 2 chapters")[0].assertExists()
        compose.onAllNodesWithText("Open book")[0].performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Getting Started").assertExists()
        compose.onNodeWithText("Start reading").assertExists()
        compose.onNodeWithTag("ws15-back").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Puzzle Books").assertExists()
    }
}
