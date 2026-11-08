package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Rule
import org.junit.Test

/** 105-word future board UI test, with a fabricated puzzle and NO v4 game modes. */
class GiantWordListIndexAndroidTest {
    @get:Rule val compose = createComposeRule()

    private fun puzzle(count: Int): WordSearchPuzzle {
        val targets = (0 until count).map { index ->
            val initial = 'A' + (index / 5)
            val suffix = 'A' + (index % 5)
            WordTarget("ID_$index", "$initial${suffix}PAPER", emptyList())
        }
        return WordSearchPuzzle(
            id = "wordsearch:ui-only:index:$count",
            seed = 1L,
            contentVersion = 4,
            theme = WordTheme.GARDEN,
            difficulty = WordDifficulty.HARD,
            rows = List(50) { "A".repeat(50) },
            targets = targets
        )
    }

    @Test fun hundredFiveWordsJumpFromAtoTAndNavigateAt300dp() {
        val sample = puzzle(105)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(300.dp).height(680.dp)) {
                    WordList(sample, emptySet(), false, Modifier.fillMaxSize())
                }
            }
        }
        compose.onNodeWithTag("word-list-range").assertTextContains("Words 1–5 of 105")
        compose.onNodeWithTag("word-list-az-index").performClick()
        compose.onNodeWithText("Find words by letter").assertExists()
        compose.onNodeWithTag("word-list-letter-T").performScrollTo().performClick()
        compose.onNodeWithTag("word-list-range").assertTextContains("Words 96–100 of 105")
        compose.onNodeWithText("Page 20 of 21").assertExists()
        compose.onNodeWithTag("word-list-az-index").performClick()
        compose.onNodeWithTag("word-list-letter-U").performScrollTo().performClick()
        compose.onNodeWithTag("word-list-range").assertTextContains("Words 101–105 of 105")
        compose.onNodeWithText("Page 21 of 21").assertExists()
    }

    @Test fun remainingOnlyOmitsCompletedLettersAndNeverChangesThePuzzle() {
        val sample = puzzle(105)
        val before = sample.targets.map { it.id to it.word }
        val found = sample.targets.take(10).map { it.id }.toSet() // A and B.
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(300.dp).height(680.dp)) {
                    WordList(sample, found, false, Modifier.fillMaxSize())
                }
            }
        }
        compose.onNodeWithText("Remaining").performClick()
        compose.onNodeWithTag("word-list-range").assertTextContains("Words 1–5 of 95")
        compose.onNodeWithTag("word-list-az-index").performClick()
        compose.onNodeWithTag("word-list-letter-A").assertDoesNotExist()
        compose.onNodeWithTag("word-list-letter-B").assertDoesNotExist()
        compose.onNodeWithTag("word-list-letter-C").assertExists()
        compose.onNodeWithTag("word-list-letter-U").performScrollTo().performClick()
        compose.onNodeWithTag("word-list-range").assertTextContains("Words 91–95 of 95")
        org.junit.Assert.assertEquals(before, sample.targets.map { it.id to it.word })
    }

    @Test fun currentFortyFourWordGameDoesNotShowFutureIndexControls() {
        val sample = puzzle(44)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(300.dp).height(680.dp)) {
                    WordList(sample, emptySet(), false, Modifier.fillMaxSize())
                }
            }
        }
        compose.onNodeWithTag("word-list-az-index").assertDoesNotExist()
        compose.onNodeWithTag("word-list-range").assertDoesNotExist()
        compose.onNodeWithText("Page 1 of 9").assertExists()
    }
}
