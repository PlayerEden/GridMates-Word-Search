package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Assertions reflect currently playable Home, Solo and Collection screens.
 * Old prototype-only texts must not become false launch regressions.
 */
class ShellSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun soloBoardOpensAndCurrentOptionsAreDiscoverable() {
        compose.onNodeWithText("Continue Solo").performClick()
        compose.onNodeWithText("Words to find", substring = true).assertExists()
        compose.onNodeWithText("Hint (", substring = true).assertExists()
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithText("Continue Solo").assertExists()
        compose.onNodeWithText("Continue Solo").performClick()
        compose.onNodeWithText("Options").performClick()
        compose.onNodeWithText("Customize puzzle").assertExists()
        compose.onNodeWithText("New puzzle").assertExists()
        compose.onNodeWithText("Feedback settings").assertExists()
    }

    @Test fun collectionTabContainsRealWoodlandProfilesNotPrototypeCopy() {
        compose.onNodeWithText("Word Search").assertExists()
        compose.onNodeWithText("Good puzzles. Good company.").assertExists()
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Portraits").assertExists()
        compose.onNodeWithText("Achievements").assertExists()
    }
    @Test fun fontCollectionTabShowsFreeEarnedAndDefaultStyles() {
        compose.onNodeWithContentDescription("Open Collection").performClick()
        compose.onNodeWithText("Fonts").performClick()
        compose.onNodeWithText("board fonts unlocked", substring = true).assertExists()
        compose.onNodeWithText("Readable Print").assertExists()
        compose.onNodeWithText("Classic Typewriter").assertExists()
        compose.onNodeWithText("Pencil Notes").assertExists()
    }

}
