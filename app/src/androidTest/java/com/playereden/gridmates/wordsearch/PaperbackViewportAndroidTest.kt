package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Actual Android narrow-board geometry and physical pointer mapping. */
class PaperbackViewportAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun narrowBookGridHasSquareBoundsAndSideGuttersWithFitAndZoomControls() {
        val context = androidx.test.platform.app.InstrumentationRegistry
            .getInstrumentation().targetContext
        val filename = "ws09d_margins_" + System.nanoTime() + ".json"
        val store = SoloStore(context, filename)
        try {
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(690.dp)) {
                        WordSearchScreen(store = store, onBack = {}, onRecords = {})
                    }
                }
            }
        val grid = rule.onNodeWithTag("word-grid-touch-area")
        grid.assertExists()
        val bounds = grid.getUnclippedBoundsInRoot()
        assertTrue("left book gutter should be at least 16dp", bounds.left >= 15.dp)
        assertTrue("right book gutter should be at least 16dp", bounds.right <= 305.dp)
        assertEquals("Grid should stay truly square",
            (bounds.right - bounds.left).value,
            (bounds.bottom - bounds.top).value, 1.5f)
        rule.onNodeWithContentDescription("Zoom in").performClick()
        rule.onNodeWithContentDescription("Fit entire puzzle board").performClick()
        val after = grid.getUnclippedBoundsInRoot()
        assertEquals((bounds.right - bounds.left).value,
            (after.right - after.left).value, 1.5f)
        assertEquals((bounds.bottom - bounds.top).value,
            (after.bottom - after.top).value, 1.5f)
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                java.io.File(context.filesDir, filename + suffix).delete()
            }
        }
    }

    @Test fun zoomableBoardUsesEntireUnroundedSquareForRealWordSwipe() {
        val puzzle = WordSearchGeneratorV2.generate(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC, 42L)
        val word = puzzle.targets.first()
        var selected: Pair<GridPoint, GridPoint>? = null
        rule.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(540.dp)) {
                    ZoomableWordGrid(
                        puzzle = puzzle, found = emptySet(), hintPaths = emptyList(),
                        viewport = 284.dp,
                        onSelect = { first, last -> selected = first to last },
                        onStep = {}, boardFontId = "atkinson")
                }
            }
        }
        val grid = rule.onNodeWithTag("word-grid-touch-area")
        grid.assertExists()
        // performTouchInput operates in local pixels. Convert the measured
        // square in root dp to native Android display pixels first.
        val bounds = grid.getUnclippedBoundsInRoot()
        val density = androidx.test.platform.app.InstrumentationRegistry
            .getInstrumentation().targetContext.resources.displayMetrics.density
        val sidePx = (bounds.right - bounds.left).value * density
        fun cellCenter(p: GridPoint): Offset = Offset(
            (p.col + 0.5f) * sidePx / puzzle.size,
            (p.row + 0.5f) * sidePx / puzzle.size
        )
        grid.performTouchInput {
            swipe(start = cellCenter(word.positions.first()),
                end = cellCenter(word.positions.last()), durationMillis = 480)
        }
        rule.waitForIdle()
        assertEquals("Swiping a real printed word should keep exact cell mapping",
            word.positions.first() to word.positions.last(), selected)
    }
}
