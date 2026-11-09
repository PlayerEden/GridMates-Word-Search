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
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithContentDescription("Open Play").performClick()
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Settings & backup").assertExists()
    }

    @Test fun focusedCoverDirectlyEntersPageAndBackReturnsHome() {
        compose.onNodeWithTag("ws16-home-books").assertExists()
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
        compose.onNodeWithTag("ws15-back").performClick()
        compose.onNodeWithTag("ws16-resume-solo").performScrollTo().assertExists()
    }
}
