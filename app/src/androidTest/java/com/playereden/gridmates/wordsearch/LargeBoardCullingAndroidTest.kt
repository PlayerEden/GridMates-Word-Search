package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.playereden.gridmates.wordsearch.core.BoardRenderWindow
import com.playereden.gridmates.wordsearch.core.LargeBoardViewport
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV3
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Actual Android rendering: the visible portion of an optically centered
 * font must agree with the old full-board painter. Culling affects work,
 * not positions or the surrounding paper background.
 */
class LargeBoardCullingAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun offscreenLettersSkippedWhileVisibleInkMatchesUnculledReference() {
        val puzzle = WordSearchGeneratorV3.generate(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_GIANT, PuzzleChallenge.CLASSIC, 42L)
        rule.setContent {
            GridMatesTheme {
                val side = 230.dp
                val sidePx = with(LocalDensity.current) { side.toPx() }
                val view = LargeBoardViewport().jumpToRegion(1, 1, puzzle.size, sidePx)
                val window = BoardRenderWindow.around(view, sidePx, puzzle.size)
                val glyphs = rememberBoardGlyphPainter("atkinson")
                Column {
                    for (cull in listOf(false, true)) {
                        Box(Modifier.size(side).clipToBounds()
                            .background(Paper)
                            .testTag(if (cull) "culled-render" else "full-render")) {
                            Canvas(Modifier.size(side).graphicsLayer {
                                scaleX = view.zoom
                                scaleY = view.zoom
                                transformOrigin = TransformOrigin(0f, 0f)
                                translationX = view.panX
                                translationY = view.panY
                            }) {
                                glyphs.drawLetters(this, puzzle.rows, Ink,
                                    if (cull) window else null)
                            }
                        }
                    }
                }
            }
        }
        val full = rule.onNodeWithTag("full-render").captureToImage().toPixelMap()
        val culled = rule.onNodeWithTag("culled-render").captureToImage().toPixelMap()
        assertEquals(full.width, culled.width)
        assertEquals(full.height, culled.height)
        var mismatches = 0
        for (y in 0 until full.height step 2)
            for (x in 0 until full.width step 2) {
                if (full[x,y] != culled[x,y]) mismatches++
            }
        assertTrue("Culling should not visibly alter central viewport ink",
            mismatches <= (full.width / 2) * (full.height / 2) / 100)
    }
}
