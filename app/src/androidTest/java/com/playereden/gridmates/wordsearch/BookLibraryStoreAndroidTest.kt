package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Test on an Android emulator/device: real AtomicFile, not a fake in-memory store. */
class BookLibraryStoreAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun fixture(): Pair<BookCatalog, WordSearchPuzzle> {
        val puzzle = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 1198L
        )
        val page = BookPage(
            "woodland-page-one", puzzle.id, puzzle.contentVersion, "fixture-en-v1",
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED,
            puzzle.seed, BookBoardDigest.sha256(puzzle)
        )
        return BookCatalog(listOf(PuzzleBook(
            "woodland-volume-one", "Woodland I", 1, WordTheme.WOODLAND,
            listOf(BookChapter("woodland-chapter-one", "The Trees", listOf(page)))
        ))) to puzzle
    }

    private fun cleanup(name: String) {
        for (suffix in listOf("", ".bak", ".new")) {
            File(ctx.filesDir, name + suffix).delete()
        }
    }

    @Test fun bookmarkAndCompletionSurviveRealAtomicFileReopen() {
        val name = "ws11c_book_" + System.nanoTime() + ".dat"
        val (catalog, puzzle) = fixture()
        try {
            val store = BookLibraryStore(ctx, catalog, name)
            assertFalse(store.blocked)
            assertTrue(store.bookmark("woodland-volume-one", "woodland-page-one"))
            assertEquals("woodland-page-one", BookProgressRules.continuePage(
                catalog, BookLibraryStore(ctx, catalog, name).progress,
                "woodland-volume-one"
            )?.id)

            val found = puzzle.targets.map { it.id }.toSet()
            assertTrue(store.recordCompletion(
                "woodland-volume-one", "woodland-page-one", puzzle, found
            ))
            val afterRestart = BookLibraryStore(ctx, catalog, name)
            assertFalse(afterRestart.blocked)
            assertTrue(BookProgressRules.isComplete(
                catalog, afterRestart.progress, "woodland-volume-one"
            ))
            assertEquals(1, BookProgressRules.completedCount(
                catalog, afterRestart.progress, "woodland-volume-one"
            ))
            assertNull(BookProgressRules.continuePage(
                catalog, afterRestart.progress, "woodland-volume-one"
            ))
        } finally { cleanup(name) }
    }

    @Test fun manualBackupIsExactAndInvalidImportNeverDamagesTheDisk() {
        val sourceName = "ws11c_backup_" + System.nanoTime() + ".dat"
        val restoredName = "ws11c_restore_" + System.nanoTime() + ".dat"
        val (catalog, _) = fixture()
        try {
            val first = BookLibraryStore(ctx, catalog, sourceName)
            assertTrue(first.bookmark("woodland-volume-one", "woodland-page-one"))
            val originalBytes = File(ctx.filesDir, sourceName).readBytes()
            val backup = requireNotNull(first.exportForManualBackup())
            assertArrayEquals(originalBytes, backup)
            val broken = backup.clone().also { it[10] = 0xff.toByte() }
            assertFalse(first.importManualBackup(broken))
            assertArrayEquals(originalBytes, File(ctx.filesDir, sourceName).readBytes())

            val second = BookLibraryStore(ctx, catalog, restoredName)
            assertTrue(second.importManualBackup(backup))
            assertEquals(first.progress, BookLibraryStore(ctx, catalog, restoredName).progress)
            assertArrayEquals(backup, File(ctx.filesDir, restoredName).readBytes())
        } finally {
            cleanup(sourceName)
            cleanup(restoredName)
        }
    }

    @Test fun corruptedOrUnknownCatalogFileIsPreservedAndBlocksWrites() {
        val corruptName = "ws11c_corrupt_" + System.nanoTime() + ".dat"
        val rollbackName = "ws11c_rollback_" + System.nanoTime() + ".dat"
        val (catalog, _) = fixture()
        try {
            val bytes = "future-or-corrupt-book-data".toByteArray(Charsets.UTF_8)
            val corruptFile = File(ctx.filesDir, corruptName)
            corruptFile.writeBytes(bytes)
            val corrupted = BookLibraryStore(ctx, catalog, corruptName)
            assertTrue(corrupted.blocked)
            assertNotNull(corrupted.warning)
            assertNull(corrupted.exportForManualBackup())
            assertFalse(corrupted.bookmark("woodland-volume-one", "woodland-page-one"))
            assertArrayEquals(bytes, corruptFile.readBytes())

            val valid = BookLibraryStore(ctx, catalog, rollbackName)
            assertTrue(valid.bookmark("woodland-volume-one", "woodland-page-one"))
            val before = File(ctx.filesDir, rollbackName).readBytes()
            // A downgrade/temporary missing catalog cannot delete unknown book
            // progress or overwrite it with an empty "clean" profile.
            val missingCatalog = BookLibraryStore(ctx, BookCatalog(), rollbackName)
            assertTrue(missingCatalog.blocked)
            assertFalse(missingCatalog.bookmark("woodland-volume-one", "woodland-page-one"))
            assertArrayEquals(before, File(ctx.filesDir, rollbackName).readBytes())
            assertFalse(BookLibraryStore(ctx, catalog, rollbackName).blocked)
        } finally {
            cleanup(corruptName)
            cleanup(rollbackName)
        }
    }
}
