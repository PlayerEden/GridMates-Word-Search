package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/**
 * Screen reachability, understandable labels and navigation regressions.
 * These checks don't substitute for actual narrow-phone screenshots.
 */
class PaperbackAppPolishTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun homeShowsDistinctPlayModesAndCurrentProgress() {
        compose.onNodeWithText("Word Search").assertExists()
        compose.onNodeWithText("Good puzzles. Good company.").assertExists()
        compose.onNodeWithText("01 · YOUR CURRENT PUZZLE").assertExists()
        compose.onNodeWithText("02 · A LITTLE EVERY DAY").assertExists()
        compose.onNodeWithText("03 · TOGETHER IS BETTER").assertExists()
        compose.onNodeWithContentDescription("Open Nearby co-op").assertExists()
    }

    @Test fun nearbyAndJournalAndSettingsAreReachableWithoutDebugCopy() {
        compose.onNodeWithContentDescription("Open Nearby co-op")
            .performScrollTo().performClick()
        compose.onNodeWithText("Play Together").assertExists()
        compose.onNodeWithText("← Back to Play").performClick()
        compose.onNodeWithText("Continue Solo").assertExists()
        compose.onNodeWithContentDescription("Open Records").performClick()
        compose.onNodeWithText("Your puzzle journal").assertExists()
        compose.onNodeWithContentDescription("Open Settings").performClick()
        compose.onNodeWithText("Settings & backup").assertExists()
        compose.onNodeWithText("About Word Search").assertExists()
    }

    @Test fun collectionKeepsProfileAndFontUnlockEntry() {
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Fonts").performClick()
        compose.onNodeWithText("board fonts unlocked", substring = true).assertExists()
    }
}
