package com.playereden.gridmates.wordsearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Compiles in Android QA; running emulator instrumentation is a separate gate. */
class GridMatesDeveloperOverlayAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun controlsRemainOptionalInDebug() {
        assertTrue(GridMatesDevTools.available)
        var flags by mutableStateOf(DevOverlayFlags())
        compose.setContent {
            GridMatesTheme {
                GridMatesDevTools.Controls(flags) { flags = it }
            }
        }
        compose.onNodeWithTag("gm-dev-panel-toggle").performClick()
        compose.onNodeWithTag("gm-dev-enable").assertExists()
        compose.onNodeWithTag("gm-dev-grid").assertExists()
        compose.onNodeWithTag("gm-dev-bounds").assertExists()
        compose.onNodeWithTag("gm-dev-safe").assertExists()
        compose.onNodeWithTag("gm-dev-touch").assertExists()
    }
}
