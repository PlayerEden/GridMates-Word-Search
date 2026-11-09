package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/** WS-16 real-activity navigation compilation. Physical-device execution remains owner QA. */
class Ws16CarouselHomeAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun sharedBrandAndHeaderNavigateToWorkingLocalDestinations() {
        compose.onNodeWithTag("ws16-home-header").assertExists()
        compose.onNodeWithContentDescription("Open player profile").performClick()
        compose.onNodeWithText("Player profile").assertExists()
        compose.onNodeWithTag("ws16c-profile-collection").performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithContentDescription("Back to Profile").performClick()
        compose.onNodeWithTag("ws16c-profile-records").performClick()
        compose.onNodeWithText("Your puzzle journal").assertExists()
        compose.onNodeWithContentDescription("Back to Profile").performClick()
        compose.onNodeWithContentDescription("Back to Home").performClick()
        compose.onNodeWithTag("ws16-home-header").assertExists()
        compose.onNodeWithContentDescription("Open Play").assertDoesNotExist()
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Settings & backup").assertExists()
        compose.onNodeWithContentDescription("Back to Home").performClick()
        compose.onNodeWithTag("ws16-home-header").assertExists()
    }

    @Test fun focusedCoverDirectlyEntersPageAndBackReturnsHome() {
        compose.onNodeWithTag("ws16-home-books").assertExists()
        compose.onNodeWithTag("ws16e-storybook-shelf").assertExists()
        compose.onNodeWithContentDescription("Play Woodland", substring = true)
            .performClick()
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Back to book").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("ws16-home-header").assertExists()
    }

    @Test fun fullShelfAndSoloShortcutAreStillReachable() {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithText("Puzzle Books").assertExists()
        compose.onNodeWithTag("ws15-preview-tools").performClick()
        compose.onNodeWithText("Try swipe library").assertExists()
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithTag("ws15-back").performClick()
        compose.onNodeWithTag("ws16-resume-solo").performScrollTo().assertExists()
    }
    @Test fun asymmetricAssetsAndQuickPlayPlaceholderHaveHonestRouting() {
        compose.onNodeWithTag("ws16-action-daily").performScrollTo().assertExists()
        compose.onNodeWithText("Daily Puzzle").assertExists()
        compose.onNodeWithTag("ws16-action-together").performScrollTo().assertExists()
        compose.onNodeWithText("Together").assertExists()
        compose.onNodeWithTag("ws16b-quick-play-preview").performScrollTo().assertExists()
        compose.onNodeWithText("Custom").assertExists()
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithTag("ws16b-compact-menu").assertExists()
    }

    @Test fun collectionHasDedicatedHomeEntryAndReturnsWithoutFooter() {
        compose.onNodeWithTag("ws16d-home-collection")
            .performScrollTo().performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Portraits").assertExists()
        compose.onNodeWithContentDescription("Back to Home").performClick()
        compose.onNodeWithTag("ws16-home-header").assertExists()
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().assertExists()
        compose.onNodeWithContentDescription("Open Play").assertDoesNotExist()
    }

}
