package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV3
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Rule
import org.junit.Test

/** One continuous grid, navigation only; saved found-word state remains owned by store. */
class LargeBoardRegionNavigationAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun giantBoardCanJumpToFarRegionAndReturnToFitOn320dp() {
        val puzzle=WordSearchGeneratorV3.generate(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.CLASSIC,42L)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(600.dp)) {
                    ZoomableWordGrid(
                        puzzle = puzzle, found = emptySet(), hintPaths = emptyList(),
                        viewport = 280.dp, onSelect = { _, _ -> }, onStep = {},
                        boardFontId = "atkinson")
                }
            }
        }
        compose.onNodeWithTag("word-grid-touch-area").assertExists()
        compose.onNodeWithText("Rows 1–30",substring=true).assertExists()
        compose.onNodeWithContentDescription("Open board region map").performClick()
        compose.onNodeWithText("Board map").assertExists()
        compose.onNodeWithTag("large-board-region-map").assertExists()
        compose.onNodeWithText("Row A").performClick()
        compose.onNodeWithText("Row C").performClick()
        compose.onNodeWithText("Column 1").performClick()
        compose.onNodeWithText("Column 3").performClick()
        compose.onNodeWithText("Jump to section").performClick()
        compose.onNodeWithText("Board map").assertDoesNotExist()
        compose.onNodeWithText("3.0×",substring=true).assertExists()
        compose.onNodeWithContentDescription("Fit entire puzzle board").performClick()
        compose.onNodeWithText("Rows 1–30",substring=true).assertExists()
        compose.onNodeWithText("1.0×",substring=true).assertExists()
        compose.onNodeWithTag("word-grid-touch-area").assertExists()
    }

    @Test fun classicFifteenByFifteenKeepsOriginalControlsWithoutExtraMap() {
        val puzzle=WordSearchGeneratorV2.generate(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_CLASSIC,PuzzleChallenge.CLASSIC,42L)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(580.dp)) {
                    ZoomableWordGrid(
                        puzzle=puzzle,found=emptySet(),hintPaths=emptyList(),
                        viewport=280.dp,onSelect={ _, _ -> },onStep={},
                        boardFontId="atkinson")
                }
            }
        }
        compose.onNodeWithTag("word-grid-touch-area").assertExists()
        compose.onNodeWithContentDescription("Open board region map").assertDoesNotExist()
        compose.onNodeWithContentDescription("Zoom in").performClick()
        compose.onNodeWithContentDescription("Fit entire puzzle board").performClick()
        compose.onNodeWithTag("word-grid-touch-area").assertExists()
    }
}
