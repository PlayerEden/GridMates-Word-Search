package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * A single synthetic test-only catalog exercises the real Library -> Detail ->
 * Page -> Detail route without adding any unapproved book to production.
 */
class BookLibraryFlowAndroidTest {
    @get:Rule val rule = createComposeRule()
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun realLibraryChapterAndBookPlayerCanOpenPauseAndResumeWithoutSolo() {
        val sessionName = "ws11d2b_e2e_" + System.nanoTime() + ".dat"
        val awardName = "ws11d2b_progress_" + System.nanoTime() + ".dat"
        val board = WordSearchGeneratorV2.generate(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 83L
        )
        val bookId = "garden-volume-one"
        val pageId = "garden-page-one"
        val catalog = BookCatalog(listOf(
            PuzzleBook(
                bookId, "Garden Volume I", 1, WordTheme.GARDEN,
                listOf(BookChapter("garden-chapter-one", "Flowers", listOf(
                    BookPage(pageId, board.id, board.contentVersion,
                        "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
                        PuzzleChallenge.CLASSIC, board.seed, BookBoardDigest.sha256(board))
                )))
            )
        ))
        try {
            val sessions = BookPageSessionStore(ctx, catalog, sessionName)
            val progress = BookLibraryStore(ctx, catalog, awardName)
            rule.setContent {
                GridMatesTheme {
                    BookLibraryFlow(catalog, progress, sessions, "classic", onExit = {})
                }
            }
            rule.waitForIdle()
            rule.onNodeWithText("Open book").performClick()
            rule.waitForIdle()
            rule.onNodeWithText("Flowers").assertExists()
            rule.onNodeWithText("Bookmark page 1").performClick()
            rule.waitForIdle()
            assertEquals(pageId, progress.progress.volumes[bookId]?.bookmarkedPageId)
            rule.onNodeWithText("Play page 1").performClick()
            rule.waitForIdle()
            rule.onNodeWithContentDescription("Select a word without dragging").assertExists()
            assertEquals(1, sessions.journal.sessions.size)
            rule.onNodeWithText("Back to book").performClick()
            rule.waitForIdle()
            rule.onNodeWithText("Bookmark saved").assertExists()
            assertTrue(BookPageSessionStore(ctx, catalog, sessionName)
                .session(bookId, pageId) != null)
        } finally {
            for (name in listOf(sessionName, awardName)) {
                for (suffix in listOf("", ".bak", ".new"))
                    File(ctx.filesDir, name + suffix).delete()
            }
        }
    }
}
