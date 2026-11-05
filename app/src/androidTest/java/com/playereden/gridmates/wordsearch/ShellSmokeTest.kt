package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ShellSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun appHasGridMatesIdentityAndAWorkingCollectionTab() {
        compose.onNodeWithText("Word Search").assertExists()
        compose.onNodeWithText("Good puzzles. Good company.").assertExists()
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Collection").assertExists()
        compose.onNodeWithText(
            "Our original Woodland portraits and other GridMates styles will be added from the verified Sudoku art collection."
        ).assertExists()
    }
}
