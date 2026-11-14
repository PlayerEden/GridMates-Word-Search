package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Reproduces a real phone-sized Compose viewport rather than assuming large
 * emulator screen dimensions. Verifies primary actions are visible without
 * asking people to scroll past a decorative cover or advanced room settings.
 */
class CompactHomeNearbyEntryAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun homeQuickActionsRemainReachableAt360By720() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws07b_home_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(context, filename)
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithText("Word Search").assertIsDisplayed()
            rule.onNodeWithText("Continue").assertIsDisplayed()
            rule.onNodeWithContentDescription("Open Daily Puzzle").performScrollTo().assertIsDisplayed()
            rule.onNodeWithContentDescription("Open Together on local Wi-Fi").performScrollTo().assertIsDisplayed()
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }

    @Test fun hostAndJoinAppearBeforeOptionalRoomCustomization() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws07b_setup_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(context, filename)
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        NearbyCoopScreen(store, rooms, onBack = {})
                    }
                }
            }
            rule.onNodeWithText("Host Co-op game").assertIsDisplayed()
            rule.onNodeWithText("Join nearby game").assertIsDisplayed()
            rule.onNodeWithText("Co-op board appearance").assertDoesNotExist()
            rule.onNodeWithText("Nearby games").assertDoesNotExist()
            rule.onNodeWithText("Join nearby game").performClick()
            rule.onNodeWithText("Nearby games").assertIsDisplayed()
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
