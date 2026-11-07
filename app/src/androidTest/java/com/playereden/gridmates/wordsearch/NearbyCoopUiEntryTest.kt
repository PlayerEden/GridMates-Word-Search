package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** UI reachability does not require a second phone or a successful Wi-Fi host. */
class NearbyCoopUiEntryTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun nearbyRoomControlsAreReachableWithoutStartingAConnection() {
        compose.onNodeWithContentDescription("Open Nearby co-op")
            .performScrollTo().performClick()
        compose.onNodeWithText("Play Together").assertExists()
        compose.onNodeWithText("Co-op board appearance").assertExists()
        compose.onNodeWithText("Linen Sketch").performScrollTo().performClick()
        compose.onNodeWithText("Blueprint Study").performScrollTo().performClick()
        compose.onNodeWithText("Host Co-op game").assertExists()
        compose.onNodeWithText("Join nearby game").assertExists()
        compose.onNodeWithText("Require a room code").assertExists()
        compose.onNodeWithText("Shared Board Race").performScrollTo().performClick()
        compose.onNodeWithText("Host Shared Board Race").assertExists()
        compose.onNodeWithText("Speed Duel").performScrollTo().performClick()
        compose.onNodeWithText("Host Speed Duel").assertExists()
        compose.onNodeWithText("private boards", substring = true).assertExists()
        compose.onNodeWithText("Co-op").performScrollTo().performClick()
        compose.onNodeWithText("Host Co-op game").assertExists()
        compose.onNodeWithText("← Back to Play").performClick()
        compose.onNodeWithText("Continue Solo").assertExists()
    }
}
