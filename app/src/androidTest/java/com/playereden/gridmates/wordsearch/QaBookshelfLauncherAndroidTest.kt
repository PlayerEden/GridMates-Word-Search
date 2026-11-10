package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
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
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Your Library").assertExists()
        // The native bookcase displays the actual spine; tap to pull out
        // the illustrated cover, then route into the existing chapters.
        compose.onNodeWithTag("library-spine-woodland-volume-one").assertExists()
        compose.onNodeWithTag("library-spine-woodland-volume-one").performClick()
        compose.onNodeWithText("Woodland").assertExists()
        compose.onNodeWithTag("library-view-pages").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Getting Started").assertExists()
        compose.onNodeWithText("Start solving").assertExists()
        compose.onNodeWithTag("ws15-back").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Your Library").assertExists()
    }
}
