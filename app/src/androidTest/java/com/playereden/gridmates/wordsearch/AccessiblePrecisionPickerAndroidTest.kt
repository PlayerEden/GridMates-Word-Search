package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

/**
 * Accessible alternate input is a real end-to-end Compose action path.
 * Selecting a pair of endpoints calls the SAME claim callback as finger
 * tracing. No automatic target lookup or success is awarded in the picker.
 */
class AccessiblePrecisionPickerAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun spokenRowColumnControlsCanMarkARealWordWithoutDragging() {
        val puzzle = SoloProgressRules.puzzle(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 90128L
        ))
        val target = puzzle.targets.first()
        val chosen = AtomicReference<Pair<GridPoint, GridPoint>?>(null)
        compose.setContent {
            GridMatesTheme {
                PreciseWordPicker(puzzle,
                    onSelect = { first, last -> chosen.set(first to last) },
                    onDismiss = {})
            }
        }
        val first = target.positions.first()
        val last = target.positions.last()
        repeat(first.row) {
            compose.onNodeWithContentDescription("Next row").performClick()
        }
        repeat(first.col) {
            compose.onNodeWithContentDescription("Next column").performClick()
        }
        compose.onNodeWithText("Set start").performClick()
        compose.onNodeWithText("Mark word").assertIsNotEnabled()
        repeat(kotlin.math.abs(last.row - first.row)) {
            compose.onNodeWithContentDescription(
                if (last.row > first.row) "Next row" else "Previous row"
            ).performClick()
        }
        repeat(kotlin.math.abs(last.col - first.col)) {
            compose.onNodeWithContentDescription(
                if (last.col > first.col) "Next column" else "Previous column"
            ).performClick()
        }
        compose.onNodeWithText("Mark word").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(first to last, chosen.get()) }
    }

    @Test fun nonStraightSelectionsCannotBeSubmittedAccidentally() {
        val puzzle = SoloProgressRules.puzzle(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 90128L
        ))
        val chosen = AtomicReference<Pair<GridPoint, GridPoint>?>(null)
        compose.setContent {
            GridMatesTheme {
                PreciseWordPicker(puzzle,
                    onSelect = { first, last -> chosen.set(first to last) },
                    onDismiss = {})
            }
        }
        compose.onNodeWithText("Set start").performClick()
        compose.onNodeWithContentDescription("Next row").performClick()
        compose.onNodeWithContentDescription("Next column").performClick()
        compose.onNodeWithContentDescription("Next column").performClick()
        compose.onNodeWithText("Mark word").assertIsNotEnabled()
        compose.onNodeWithText("Move the endpoint", substring = true).assertExists()
        compose.runOnIdle { assertTrue(chosen.get() == null) }
    }

    @Test fun denseBoardOffersOneAccessibleEntryInsteadOfFourHundredTinyCells() {
        val puzzle = SoloProgressRules.puzzle(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.CLASSIC, 90128L
        ))
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.size(360.dp)) {
                    ZoomableWordGrid(puzzle, emptySet(), emptyList(), 320.dp,
                        onSelect = { _, _ -> },
                        onStep = {},
                        boardFontId = "atkinson")
                }
            }
        }
        compose.onNodeWithContentDescription("Word-search grid", substring = true)
            .assertExists()
        compose.onAllNodesWithContentDescription("Row ", substring = true)
            .assertCountEquals(0)
        compose.onNodeWithContentDescription("Select a word without dragging")
            .assertIsDisplayed().performClick()
        compose.onNodeWithText("Select letters precisely").assertIsDisplayed()
        compose.onNodeWithContentDescription("Next row").assertIsDisplayed()
        compose.onNodeWithContentDescription("Next column").assertIsDisplayed()
    }
}
