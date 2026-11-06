package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Actual Android emulator/device UI tests, not browser approximations.
 * Includes a completed real seed-generated game and result/review navigation.
 */
class WordSearchUiSmokeTest {
    @get:Rule val rule = createComposeRule()

    @Test fun completedPuzzleShowsHonestResultsAndCanReviewWithoutReset() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04d_b4_screen_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            for (target in puzzle.targets) {
                store.claim(target.positions.first(), target.positions.last())
            }
            assertEquals(puzzle.targets.size, store.profile.active.foundIds.size)
            rule.setContent {
                GridMatesTheme {
                    WordSearchScreen(store = store, onBack = {}, onRecords = {})
                }
            }
            rule.onNodeWithText("Puzzle complete!", substring = true).assertExists()
            rule.onNodeWithText("Unassisted solve", substring = true).assertExists()
            rule.onNodeWithText("Review completed board").performScrollTo().performClick()
            rule.onNodeWithText("Results").assertExists()
            rule.onNodeWithText("Results").performClick()
            rule.onNodeWithText("Play another puzzle").assertExists()
            assertEquals(puzzle.targets.size, store.profile.active.foundIds.size)
            assertEquals(1, store.profile.completions)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
