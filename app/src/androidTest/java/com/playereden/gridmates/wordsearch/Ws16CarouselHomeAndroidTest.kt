package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertHasClickAction
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
        compose.onNodeWithTag("ws16-family-mark").assertExists()
        compose.onNodeWithTag("ws16-word-search-mark").assertExists()
        compose.onNodeWithTag("ws16i-home-divider").assertExists()
        compose.onNodeWithText("Word Search").assertExists()
        compose.onNodeWithContentDescription("Open menu").assertExists()
        compose.onNodeWithTag("gm-shared-parchment-background").assertExists()
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
        // WS-16Q: adjacent real pages peek in at the screen edge.
        // No disconnected thumbnail/chevron component exists at rest.
        compose.onNodeWithTag("ws14d-cover-pager").assertExists()
        compose.onNodeWithTag("ws16-carousel-next").assertDoesNotExist()
        compose.onNodeWithTag("ws16-carousel-prev").assertDoesNotExist()
        compose.onNodeWithContentDescription("Select Space").assertExists()
        compose.onNodeWithContentDescription("Play Woodland", substring = true)
            .performClick()
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Back to book").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("ws16-home-header").assertExists()
    }

    @Test fun fullShelfAndSoloShortcutAreStillReachable() {
        compose.onNodeWithText("Full Library").performScrollTo().assertExists()
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithText("Your Library").assertExists()
        compose.onNodeWithTag("ws15-preview-tools").performClick()
        compose.onNodeWithText("Try swipe library").assertExists()
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithTag("ws15-back").performClick()
        compose.onNodeWithTag("ws16-continue-solo")
            .performScrollTo().assertHasClickAction()
        compose.onNodeWithTag("ws16-resume-solo", useUnmergedTree = true).assertExists()
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

    @Test fun sharedParchmentFramesKeepAllNativeActionsDiscoverable() {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().assertExists()
        compose.onNodeWithTag("ws16d-home-collection").assertExists()
        compose.onNodeWithTag("ws16-continue-solo")
            .performScrollTo().assertHasClickAction()
        compose.onNodeWithTag("ws16-resume-solo", useUnmergedTree = true).assertExists()
        compose.onNodeWithTag("ws16-resume-solo", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Open Daily Puzzle")
            .performScrollTo().assertExists()
        compose.onNodeWithContentDescription("Open Together on local Wi-Fi")
            .performScrollTo().assertExists()
        compose.onNodeWithTag("ws16b-quick-play-preview").performScrollTo().assertExists()
        compose.onNodeWithText("Mixed-word boards · coming soon").assertExists()
        compose.onNodeWithTag("ws16-new-solo").performScrollTo().assertExists()
        compose.onNodeWithTag("ws16-my-puzzles").assertExists()
    }

}
