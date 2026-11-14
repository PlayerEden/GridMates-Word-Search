package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Source compiled by CI; instrumentation execution requires emulator/phone. */
class Ws14HHintIconAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun bulbIsLabeledAndFiresOnlyWhenEnabled() {
        var hits = 0
        compose.setContent {
            GridMatesTheme {
                BookPageHintButton(enabled = true, onClick = { hits++ })
            }
        }
        compose.onNodeWithContentDescription("Hint").assertIsDisplayed().performClick()
        compose.onNodeWithTag("ws14h-hint-lightbulb").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, hits) }
    }

    @Test fun bulbCanBeDisabledWhileStoreInitializes() {
        compose.setContent {
            GridMatesTheme { BookPageHintButton(enabled = false, onClick = {}) }
        }
        compose.onNodeWithContentDescription("Hint").assertIsNotEnabled()
    }
}
