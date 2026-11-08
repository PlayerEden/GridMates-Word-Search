package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LargeBoardBookmarkUiAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun giantBoardRestoresPreviousSectionAndWritesNewZoomOnFitAndMap() {
        val puzzle = WordSearchGeneratorV3.generate(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_GIANT, PuzzleChallenge.CLASSIC, 42L)
        val side = 280f
        val bookmark = LargeBoardBookmark.capture(puzzle.id,
            LargeBoardViewport().jumpToRegion(2, 2, 30, side), side)
        var recorded: LargeBoardBookmark? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(620.dp)) {
                    ZoomableWordGrid(
                        puzzle = puzzle, found = emptySet(), hintPaths = emptyList(),
                        viewport = 280.dp, onSelect = { _, _ -> }, onStep = {},
                        boardFontId = "atkinson", bookmark = bookmark,
                        onBookmark = { recorded = it }
                    )
                }
            }
        }
        compose.onNodeWithText("3.0×", substring = true).assertExists()
        compose.onNodeWithContentDescription("Fit entire puzzle board").performClick()
        compose.onNodeWithText("Rows 1–30", substring = true).assertExists()
        compose.waitForIdle()
        assertEquals(1f, recorded?.zoom ?: -1f, 0.0001f)
        assertEquals(puzzle.id, recorded?.puzzleId)
        compose.onNodeWithContentDescription("Open board region map").performClick()
        compose.onNodeWithText("Row C").performClick()
        compose.onNodeWithText("Column 3").performClick()
        compose.onNodeWithText("Jump to section").performClick()
        compose.onNodeWithText("3.0×", substring = true).assertExists()
        compose.waitForIdle()
        assertEquals(3f, recorded?.zoom ?: -1f, 0.0001f)
        assertEquals(puzzle.id, recorded?.puzzleId)
    }
}
