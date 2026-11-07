package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/**
 * End-to-end, offline navigation through the actual Activity. This checks that
 * the integrated routes remain reachable without opening sockets, replacing
 * puzzles or accepting a restore dialog. It isn't physical-device LAN proof.
 */
class IntegratedNavigationAndroidTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun homeDailyRecordsCollectionsSettingsAndNearbyReturnToPlay() {
        rule.onNodeWithText("Open Daily").performScrollTo().performClick()
        rule.onNodeWithText("The Daily Puzzle").assertExists()
        rule.onNodeWithText("Back to Play").performScrollTo().performClick()

        rule.onNodeWithContentDescription("Open Records").performClick()
        rule.onNodeWithContentDescription("Open Collection").performClick()
        rule.onNodeWithText("Your Collection").assertExists()

        rule.onNodeWithContentDescription("Open Settings").performClick()
        rule.onNodeWithText("Settings & backup").assertExists()
        rule.onNodeWithText("Export backup").assertExists()
        rule.onNodeWithText("Restore backup").assertExists()

        rule.onNodeWithContentDescription("Open Play").performClick()
        rule.onNodeWithContentDescription("Open Nearby co-op")
            .performScrollTo().performClick()
        rule.onNodeWithText("Board appearance", substring = true).assertExists()
        rule.onNodeWithText("Host Co-op game").assertExists()
        rule.onNodeWithText("← Back to Play").performClick()

        rule.onNodeWithText("Good puzzles. Good company.").assertExists()
        rule.onNodeWithText("Open Daily").assertExists()
    }
}
