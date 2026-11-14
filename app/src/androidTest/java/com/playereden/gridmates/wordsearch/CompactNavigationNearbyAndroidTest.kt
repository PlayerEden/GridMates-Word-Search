package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** 200% Compose text and compact viewport, without changing device settings. */
class CompactNavigationNearbyAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun profileAndMenuReplaceBottomTabsAtLargeFontScale() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = "ws05i_nav_" + System.nanoTime() + ".json"
        val store = SoloStore(ctx, file)
        val rooms = NearbyRoomViewModel()
        try {
            rule.setContent {
                val base = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(base.density, 2f)) {
                    GridMatesTheme {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithContentDescription("Open Play").assertDoesNotExist()
            rule.onNodeWithContentDescription("Open player profile").performClick()
            rule.onNodeWithText("Player profile").assertExists()
            rule.onNodeWithTag("ws16c-profile-collection").performScrollTo().performClick()
            rule.onNodeWithText("Your Collection").assertExists()
            rule.onNodeWithContentDescription("Back to Profile").performScrollTo().performClick()
            rule.onNodeWithContentDescription("Back to Home").performClick()
            rule.onNodeWithContentDescription("Open menu").performClick()
            rule.onNodeWithText("Settings & backup").performClick()
            rule.onNodeWithText("Settings & backup").assertExists()
            rule.onNodeWithContentDescription("Back to Home").performClick()
        } finally {
            rooms.leaveRoom()
            File(ctx.filesDir, file).delete()
        }
    }

    @Test fun nearbyHostAndJoinControlsAreReachableInCompactLargeText() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = "ws05i_nearby_" + System.nanoTime() + ".json"
        val store = SoloStore(ctx, file)
        val rooms = NearbyRoomViewModel()
        try {
            rule.setContent {
                val base = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(base.density, 2f)) {
                    GridMatesTheme {
                        Box(Modifier.width(320.dp).height(640.dp)) {
                            NearbyCoopScreen(store, rooms, onBack = {})
                        }
                    }
                }
            }
            rule.onNodeWithText("Play Together").assertExists()
            rule.onNodeWithText("Host Co-op game").performScrollTo().assertIsDisplayed()
            rule.onNodeWithText("Join nearby game").performScrollTo().assertIsDisplayed()
        } finally {
            rooms.leaveRoom()
            File(ctx.filesDir, file).delete()
        }
    }
}
