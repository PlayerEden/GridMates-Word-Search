package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * WS-14I: real Compose pointer gesture (not a call to core.claim).
 * CI compiles these tests; actual emulator/device execution is still required.
 * A 20×20 book page at Fit must persist a found word across a new store instance.
 */
class Ws14IRealTwentyCellWordTraceAndroidTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun delete(vararg names: String) {
        names.forEach { name -> listOf("", ".bak", ".new").forEach { suffix ->
            File(context.filesDir, name + suffix).delete()
        } }
    }

    @Test fun fittedTwentyCellWordDragClaimsAndSavesWhileHintsRemainUsable() {
        val board = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.CLASSIC, 20261109L
        )
        val bookId = "woodland-twenty-volume"
        val pageId = "woodland-twenty-page"
        val catalog = BookCatalog(listOf(PuzzleBook(
            bookId, "Woodland", 1, WordTheme.WOODLAND,
            listOf(BookChapter("woodland-twenty-chapter", "Forest Trails", listOf(
                BookPage(pageId, board.id, board.contentVersion, "fixture-en-v1",
                    PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC,
                    board.seed, BookBoardDigest.sha256(board))
            )))
        )))
        val sessionName = "ws14i_drag_" + System.nanoTime() + ".dat"
        val awardName = "ws14i_award_" + System.nanoTime() + ".dat"
        try {
            val store = BookPageSessionStore(context, catalog, sessionName)
            val award = BookLibraryStore(context, catalog, awardName)
            assertTrue(store.open(bookId, pageId))
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(820.dp)) {
                        BookPageGameplayScreen(catalog, bookId, pageId,
                            store, award, "classic", onBack = {}, showPrototypeArt = true)
                    }
                }
            }
            compose.waitForIdle()
            // The bulb remains useful after the previous arbitrary four-tap quota.
            repeat(5) {
                compose.onNodeWithContentDescription("Hint").performClick()
            }
            compose.onNodeWithText("No hints available", substring = true).assertDoesNotExist()
            compose.runOnIdle {
                assertEquals(5, store.session(bookId, pageId)?.hints?.values?.sum())
            }
            val target = board.targets.first()
            val start = target.positions.first()
            val end = target.positions.last()
            // Real finger trace at grid cell centers in *local* bounds of the
            // actual zoomable grid node; board starts fitted at scale 1.
            compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
                val cellX = size.width.toFloat() / board.size
                val cellY = size.height.toFloat() / board.size
                swipe(
                    start = Offset((start.col + 0.5f) * cellX, (start.row + 0.5f) * cellY),
                    end = Offset((end.col + 0.5f) * cellX, (end.row + 0.5f) * cellY),
                    durationMillis = 400L
                )
            }
            compose.waitForIdle()
            compose.onNodeWithText("1 of 24 words found", substring = true).assertExists()
            val reopened = BookPageSessionStore(context, catalog, sessionName)
            assertEquals(setOf(target.id), reopened.session(bookId, pageId)?.foundIds)
            assertEquals(5, reopened.session(bookId, pageId)?.hints?.values?.sum())
        } finally { delete(sessionName, awardName) }
    }
}
