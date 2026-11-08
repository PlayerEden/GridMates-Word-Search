package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Device/emulator test source: Android AtomicFile writes, not just pure Kotlin. */
class BookPageSessionStoreAndroidTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun fixture(): Pair<BookCatalog, com.playereden.gridmates.wordsearch.core.WordSearchPuzzle> {
        val puzzle = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 17L
        )
        return BookCatalog(listOf(
            PuzzleBook(
                "woodland-volume-one", "Woodland Volume I", 1, WordTheme.WOODLAND,
                listOf(BookChapter("woodland-chapter-one", "The Trees", listOf(
                    BookPage("woodland-page-one", puzzle.id, puzzle.contentVersion,
                        "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
                        PuzzleChallenge.CLASSIC, puzzle.seed, BookBoardDigest.sha256(puzzle))
                )))
            )
        )) to puzzle
    }

    private fun delete(name: String) {
        listOf("", ".bak", ".new").forEach { suffix ->
            File(context.filesDir, name + suffix).delete()
        }
    }

    @Test fun partialWordAndHintsSurviveRestartWithoutTouchingSoloProfile() {
        val name = "ws11d2a_session_" + System.nanoTime() + ".dat"
        val soloName = "ws11d2a_legacy_" + System.nanoTime() + ".json"
        val (catalog, board) = fixture()
        val book = "woodland-volume-one"
        val page = "woodland-page-one"
        try {
            val solo = SoloStore(context, soloName)
            solo.flush() // commit a stable existing profile before reopening it
            val oldProfile = solo.profile
            val store = BookPageSessionStore(context, catalog, name)
            assertTrue(store.open(book, page))
            val target = board.targets.first()
            assertEquals(ClaimStatus.FOUND, store.claim(
                book, page, target.positions.first(), target.positions.last()
            ).status)
            assertNotNull(store.hint(book, page))
            assertTrue(store.tick(book, page, 13))
            val reopened = BookPageSessionStore(context, catalog, name)
            assertFalse(reopened.blocked)
            val partial = requireNotNull(reopened.session(book, page))
            assertTrue(target.id in partial.foundIds)
            assertEquals(13L, partial.elapsedSeconds)
            assertEquals(1, partial.hints.values.sum())
            assertEquals(oldProfile, SoloStore(context, soloName).profile)
        } finally {
            delete(name)
            delete(soloName)
        }
    }

    @Test fun recoveredSolvedSidecarCompletesBookJournalExactlyOnce() {
        val sidecar = "ws11d2a_sidecar_" + System.nanoTime() + ".dat"
        val committedName = "ws11d2a_book_" + System.nanoTime() + ".dat"
        val (catalog, board) = fixture()
        val book = "woodland-volume-one"
        val page = "woodland-page-one"
        try {
            val store = BookPageSessionStore(context, catalog, sidecar)
            assertTrue(store.open(book, page))
            for (target in board.targets) {
                assertEquals(ClaimStatus.FOUND, store.claim(
                    book, page, target.positions.first(), target.positions.last()
                ).status)
            }
            val bookStore = BookLibraryStore(context, catalog, committedName)
            assertEquals(0, BookProgressRules.completedCount(catalog, bookStore.progress, book))
            // Stop process before completion was awarded; a full page is saved.
            val restartedSession = BookPageSessionStore(context, catalog, sidecar)
            assertTrue(BookPageSessionRules.complete(
                catalog, requireNotNull(restartedSession.session(book, page))
            ))
            assertTrue(restartedSession.recoverSolvedPages(bookStore))
            assertEquals(1, BookProgressRules.completedCount(catalog,
                BookLibraryStore(context, catalog, committedName).progress, book))
            assertNull(BookPageSessionStore(context, catalog, sidecar).session(book, page))
            assertTrue(restartedSession.recoverSolvedPages(bookStore))
            assertEquals(1, BookProgressRules.completedCount(catalog, bookStore.progress, book))
        } finally {
            delete(sidecar)
            delete(committedName)
        }
    }

    @Test fun corruptOrDowngradedSessionFileIsPreservedAndWritesAreBlocked() {
        val name = "ws11d2a_corrupt_" + System.nanoTime() + ".dat"
        val (catalog, _) = fixture()
        try {
            val file = File(context.filesDir, name)
            val bytes = byteArrayOf(2, 9, 3, 0, 7)
            file.writeBytes(bytes)
            val unreadable = BookPageSessionStore(context, catalog, name)
            assertTrue(unreadable.blocked)
            assertNotNull(unreadable.warning)
            assertFalse(unreadable.open("woodland-volume-one", "woodland-page-one"))
            assertArrayEquals(bytes, file.readBytes())

            // Even valid bytes from a newer game must not be reinterpreted
            // by a downgrade temporarily missing that published book.
            file.delete()
            val valid = BookPageSessionStore(context, catalog, name)
            assertTrue(valid.open("woodland-volume-one", "woodland-page-one"))
            val original = file.readBytes()
            val oldCatalog = BookPageSessionStore(context, BookCatalog(), name)
            assertTrue(oldCatalog.blocked)
            assertFalse(oldCatalog.open("woodland-volume-one", "woodland-page-one"))
            assertArrayEquals(original, file.readBytes())
        } finally { delete(name) }
    }
}
