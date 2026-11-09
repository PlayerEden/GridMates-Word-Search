package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertExists
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Source-compiled by CI; a physical tap/gesture test is still required. */
class Ws14K2GameChromeAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun toolsAndNoticeHistoryAreActualAccessibleActions() {
        var tools = 0
        var history = 0
        compose.setContent {
            GridMatesTheme {
                Row {
                    BookPageToolsButton(onClick = { tools++ })
                    BookPageNoticeHistoryButton(hasNotices = true,
                        onClick = { history++ })
                }
            }
        }
        compose.onNodeWithContentDescription("Puzzle tools").performClick()
        compose.onNodeWithContentDescription("Notice history").performClick()
        compose.runOnIdle {
            assertEquals(1, tools)
            assertEquals(1, history)
        }
    }
    @Test fun noticeHistoryRetainsPassedMessages() {
        compose.setContent {
            GridMatesTheme {
                BookPageNoticeHistoryDialog(
                    notices = listOf("Hint: look for a five-letter word.",
                        "FOX found!"),
                    onDismiss = {})
            }
        }
        compose.onNodeWithText("Game notices").assertExists()
        compose.onNodeWithText("Hint: look for a five-letter word.").assertExists()
    }
}
