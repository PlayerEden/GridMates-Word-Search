package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/** WS-16C: home play, Profile, Menu and settings replace footer navigation. */
class PaperbackAppPolishTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun homeShowsMainPlayModesWithNoPermanentBottomNavigation() {
        compose.onNodeWithTag("ws16-home-header").assertExists()
        compose.onNodeWithContentDescription("Open Play").assertDoesNotExist()
        compose.onNodeWithTag("ws16-home-books").assertExists()
        compose.onNodeWithTag("ws16-resume-solo").performScrollTo().assertExists()
        compose.onNodeWithText("Daily Puzzle").assertExists()
        compose.onNodeWithText("Together").assertExists()
        compose.onNodeWithText("Custom").performScrollTo().assertExists()
    }

    @Test fun nearbyAndJournalAndSettingsRemainReachable() {
        compose.onNodeWithContentDescription("Open Together on local Wi-Fi")
            .performScrollTo().performClick()
        compose.onNodeWithText("Play Together").assertExists()
        compose.onNodeWithText("← Back to Play").performClick()
        compose.onNodeWithContentDescription("Open player profile").performClick()
        compose.onNodeWithTag("ws16c-profile-records").performScrollTo().performClick()
        compose.onNodeWithText("Your puzzle journal").assertExists()
        compose.onNodeWithContentDescription("Back to Profile").performClick()
        compose.onNodeWithContentDescription("Back to Home").performClick()
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Settings & backup").assertExists()
        compose.onNodeWithText("About Word Search").assertExists()
    }

    @Test fun collectionKeepsProfileAndFontUnlockEntry() {
        compose.onNodeWithContentDescription("Open player profile").performClick()
        compose.onNodeWithTag("ws16c-profile-collection").performScrollTo().performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Fonts").performClick()
        compose.onNodeWithText("board fonts unlocked", substring = true).assertExists()
    }
}
