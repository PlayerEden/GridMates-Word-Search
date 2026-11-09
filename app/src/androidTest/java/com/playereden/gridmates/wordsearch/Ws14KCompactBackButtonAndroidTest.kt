package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Source-compiled by CI; physical screen acceptance still belongs to owner. */
class Ws14KCompactBackButtonAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun backArrowKeepsAnAccessibleNativeAction() {
        var hits = 0
        compose.setContent {
            GridMatesTheme { BookPageBackButton(onBack = { hits++ }) }
        }
        compose.onNodeWithContentDescription("Back to book").assertExists()
        compose.onNodeWithTag("ws14k-back-to-book").performClick()
        compose.runOnIdle { assertEquals(1, hits) }
    }
}
