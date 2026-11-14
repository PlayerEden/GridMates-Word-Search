package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/** Real Activity routes with no permanent footer; not a physical LAN proof. */
class IntegratedNavigationAndroidTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun playProfileMenuDailyRecordsSettingsAndNearbyReturnToHome() {
        rule.onNodeWithContentDescription("Open Daily Puzzle").performScrollTo().performClick()
        rule.onNodeWithText("Daily Word Search").assertExists()
        rule.onNodeWithText("Back to Play").performScrollTo().performClick()

        rule.onNodeWithContentDescription("Open player profile").performClick()
        rule.onNodeWithText("Player profile").assertExists()
        rule.onNodeWithTag("ws16c-profile-records").performScrollTo().performClick()
        rule.onNodeWithText("Your puzzle journal").assertExists()
        rule.onNodeWithContentDescription("Back to Profile").performClick()
        rule.onNodeWithTag("ws16c-profile-collection").performScrollTo().performClick()
        rule.onNodeWithText("Your Collection").assertExists()
        rule.onNodeWithContentDescription("Back to Profile").performClick()
        rule.onNodeWithContentDescription("Back to Home").performClick()

        rule.onNodeWithContentDescription("Open menu").performClick()
        rule.onNodeWithText("Settings & backup").performClick()
        rule.onNodeWithText("Settings & backup").assertExists()
        rule.onNodeWithText("Export backup").assertExists()
        rule.onNodeWithText("Restore backup").assertExists()
        rule.onNodeWithContentDescription("Back to Home").performClick()

        rule.onNodeWithContentDescription("Open Together on local Wi-Fi")
            .performScrollTo().performClick()
        rule.onNodeWithText("Host Co-op game").assertExists()
        rule.onNodeWithText("Join nearby game").assertExists()
        rule.onNodeWithText("← Back to Play").performClick()
        rule.onNodeWithTag("ws16-home-header").assertExists()
        rule.onNodeWithContentDescription("Open Play").assertDoesNotExist()
    }
}
