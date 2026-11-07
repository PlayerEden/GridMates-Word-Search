package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.FileInputStream

/**
 * Verify player-facing navigation and collect real on-device screenshots.
 * No network connection, in-app payment, or backup replacement is performed.
 */
class CollectionSettingsPolishAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun capture(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        Thread.sleep(1_300L)
        val pipe = instrumentation.uiAutomation.executeShellCommand(
            "screencap -p /sdcard/Download/" + name
        )
        try {
            FileInputStream(pipe.fileDescriptor).use { it.readBytes() }
        } finally { pipe.close() }
    }

    @Test fun profileAndEarnedCollectionsUseRealLabelsAndReadableCards() {
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Edit display name").assertExists()
        compose.onNodeWithText("Collection progress").assertExists()
        capture("ws05h-profile.png")

        compose.onNodeWithText("Fonts").performScrollTo().performClick()
        compose.onNodeWithText("Readable Print").performScrollTo().assertExists()
        compose.onNodeWithText("Classic Typewriter").assertExists()
        capture("ws05h-fonts.png")

        compose.onNodeWithText("Achievements").performScrollTo().performClick()
        compose.onNodeWithText("First Page").performScrollTo().assertExists()
        compose.onNodeWithText("Complete your first puzzle.").assertExists()
        capture("ws05h-achievements.png")
    }

    @Test fun switchingFromDeepCollectionScrollStartsSettingsAtTop() {
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Achievements").performScrollTo().performClick()
        compose.onNodeWithText("Three Chapters").performScrollTo().assertExists()
        compose.onNodeWithContentDescription("Open Settings").performClick()
        compose.onNodeWithText("Settings & backup").assertIsDisplayed()
        compose.onNodeWithText("Export backup").assertExists()
        compose.onNodeWithText("Restore backup").assertExists()
        capture("ws05h-settings.png")
        compose.onNodeWithText("About Word Search").performScrollTo().assertExists()
    }

    @Test fun renameDialogMakesNearbyPrivacyExplicitAndIsCancellable() {
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Edit display name").performClick()
        compose.onNodeWithText("Your local profile").assertExists()
        compose.onNodeWithText("shown to a partner", substring = true).assertExists()
        compose.onNodeWithText("Cancel").performClick()
        compose.onNodeWithText("Your Collection").assertExists()
    }
}
