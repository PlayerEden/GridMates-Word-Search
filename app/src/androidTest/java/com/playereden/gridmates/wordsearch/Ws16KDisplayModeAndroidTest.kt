package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * UI contract compilation. CI compiles instrumentation sources, but does NOT
 * run a device/emulator: final system-bar navigation needs an owner phone test.
 */
class Ws16KDisplayModeAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun canSwitchFullscreenOnAndBackOffFromHomeMenu() {
        // Normalize preference from any previous test/installation.
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Standard").performClick()
        compose.onNodeWithContentDescription("Back to Home").performClick()

        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Display: Standard · Switch to Fullscreen").performClick()
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Display: Fullscreen · Switch to Standard").performClick()

        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Current mode: Standard").assertExists()
    }

    @Test fun preferencesDisplayModeIsAvailableFromSettings() {
        compose.onNodeWithContentDescription("Open menu").performClick()
        compose.onNodeWithText("Settings & backup").performClick()
        compose.onNodeWithText("Fullscreen").assertExists()
        compose.onNodeWithText("Standard").assertExists()
    }
}
