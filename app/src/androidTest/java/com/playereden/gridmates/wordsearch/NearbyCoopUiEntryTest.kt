package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/** Primary Host/Join is easy; advanced customization is available on demand. */
class NearbyCoopUiEntryTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun nearbyRoomControlsAreReachableWithoutStartingAConnection() {
        compose.onNodeWithContentDescription("Open Nearby co-op")
            .performScrollTo().performClick()
        compose.onNodeWithText("Play Together").assertExists()
        compose.onNodeWithText("Host Co-op game").assertExists()
        compose.onNodeWithText("Join nearby game").assertExists()
        compose.onNodeWithText("Board appearance", substring = true)
            .performScrollTo().performClick()
        compose.onNodeWithText("Co-op board appearance").assertExists()
        compose.onNodeWithText("Linen Sketch").performScrollTo().performClick()
        compose.onNodeWithText("Blueprint Study").performScrollTo().performClick()
        compose.onNodeWithText("Puzzle & room options", substring = true)
            .performScrollTo().performClick()
        compose.onNodeWithText("Require a room code").assertExists()
        compose.onNodeWithText("Race").performScrollTo().performClick()
        compose.onNodeWithText("Host Shared Board Race").assertExists()
        compose.onNodeWithText("Duel").performScrollTo().performClick()
        compose.onNodeWithText("Host Speed Duel").assertExists()
        compose.onNodeWithText("private boards", substring = true).assertExists()
        compose.onNodeWithText("Co-op").performScrollTo().performClick()
        compose.onNodeWithText("Host Co-op game").assertExists()
        compose.onNodeWithText("← Back to Play").performScrollTo().performClick()
        compose.onNodeWithText("Continue Solo").assertExists()
    }
}
