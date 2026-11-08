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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * WS-11D2B actual Compose screen-source tests; test-only editorial fixtures
 * are NOT present in the production catalog or public GridMatesShell.
 * CI compiles these; execution on a device is a separate release gate.
 */
class BookPageGameplayScreenAndroidTest {
    @get:Rule val compose = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun fixture(): Pair<BookCatalog, com.playereden.gridmates.wordsearch.core.WordSearchPuzzle> {
        val board = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 17L
        )
        val page = BookPage(
            "woodland-page-one", board.id, board.contentVersion,
            "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.CLASSIC, board.seed, BookBoardDigest.sha256(board)
        )
        return BookCatalog(listOf(PuzzleBook(
            "woodland-volume-one", "Woodland Volume I", 1,
            WordTheme.WOODLAND, listOf(BookChapter(
                "woodland-chapter-one", "The Trees", listOf(page)
            ))
        ))) to board
    }

    private fun clear(vararg names: String) {
        names.forEach { name -> listOf("", ".bak", ".new").forEach { suffix ->
            File(context.filesDir, name + suffix).delete()
        } }
    }

    @Test fun usesSameRealZoomSelectBoardAndSavedProgressOnNarrowPhone() {
        val name = "ws11d2b_play_" + System.nanoTime() + ".dat"
        val complete = "ws11d2b_award_" + System.nanoTime() + ".dat"
        val (catalog, puzzle) = fixture()
        try {
            val sessions = BookPageSessionStore(context, catalog, name)
            val awards = BookLibraryStore(context, catalog, complete)
            val target = puzzle.targets.first()
            assertTrue(sessions.open("woodland-volume-one", "woodland-page-one"))
            assertEquals(
                com.playereden.gridmates.wordsearch.core.ClaimStatus.FOUND,
                sessions.claim("woodland-volume-one", "woodland-page-one",
                    target.positions.first(), target.positions.last()).status
            )
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(720.dp)) {
                        BookPageGameplayScreen(
                            catalog, "woodland-volume-one", "woodland-page-one",
                            sessions, awards, "classic", onBack = {}
                        )
                    }
                }
            }
            compose.waitForIdle()
            compose.onNodeWithText("Woodland Volume I · Page 1").assertExists()
            compose.onNodeWithText("1 of 12 words found", substring = true).assertExists()
            // Reuses the same zoom/precise gesture primitive as ordinary Solo.
            compose.onNodeWithContentDescription("Select a word without dragging")
                .assertExists()
            // A book exposes all targets in one scrollable list, instead of
            // showing only two targets plus a six-page word-list pager.
            compose.onNodeWithText("○ " + puzzle.targets.last().word).assertExists()
            compose.onNodeWithText("Page 1 of 6").assertDoesNotExist()
            assertEquals(1, sessions.session("woodland-volume-one", "woodland-page-one")
                ?.foundIds?.size)
        } finally { clear(name, complete) }
    }

    @Test fun alreadyFinishedBookReplayDoesNotWriteAnotherAward() {
        val name = "ws11d2b_practice_" + System.nanoTime() + ".dat"
        val committed = "ws11d2b_committed_" + System.nanoTime() + ".dat"
        val (catalog, puzzle) = fixture()
        val bookId = "woodland-volume-one"
        val pageId = "woodland-page-one"
        try {
            val sessions = BookPageSessionStore(context, catalog, name)
            val awards = BookLibraryStore(context, catalog, committed)
            assertTrue(awards.recordCompletion(
                bookId, pageId, puzzle, puzzle.targets.map { it.id }.toSet()
            ))
            val before = File(context.filesDir, committed).readBytes()
            compose.setContent {
                GridMatesTheme {
                    BookPageGameplayScreen(catalog, bookId, pageId,
                        sessions, awards, "classic", onBack = {})
                }
            }
            compose.waitForIdle()
            compose.onNodeWithText("Practice again").assertExists().performClick()
            compose.waitForIdle()
            compose.onNodeWithText("Practice · unsaved").assertExists()
            compose.onNodeWithText("Finish practice").assertExists().performClick()
            compose.waitForIdle()
            assertTrue(sessions.journal.sessions.isEmpty())
            assertTrue(before.contentEquals(File(context.filesDir, committed).readBytes()))
        } finally { clear(name, committed) }
    }

    @Test fun badPublisherCatalogNeverCreatesAnySessionOrFakeBoard() {
        val name = "ws11d2b_bad_" + System.nanoTime() + ".dat"
        val committed = "ws11d2b_bad_done_" + System.nanoTime() + ".dat"
        try {
            val empty = BookCatalog()
            val sessions = BookPageSessionStore(context, empty, name)
            val awards = BookLibraryStore(context, empty, committed)
            compose.setContent {
                GridMatesTheme {
                    BookPageGameplayScreen(
                        empty, "woodland-volume-one", "woodland-page-one",
                        sessions, awards, "classic", onBack = {})
                }
            }
            compose.waitForIdle()
            compose.onNodeWithText("Page unavailable").assertExists()
            compose.onNodeWithText("Practice again").assertDoesNotExist()
            compose.onNodeWithText("Hint").assertDoesNotExist()
            assertTrue(sessions.journal.sessions.isEmpty())
            assertFalse(File(context.filesDir, name).exists())
        } finally { clear(name, committed) }
    }
}
