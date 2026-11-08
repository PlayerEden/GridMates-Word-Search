package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BookCatalogTest {
    private fun puzzle(seed: Long) = WordSearchGeneratorV2.generate(
        WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, seed
    )

    private fun page(id: String, puzzle: WordSearchPuzzle): BookPage = BookPage(
        id = id,
        puzzleId = puzzle.id,
        generatorVersion = puzzle.contentVersion,
        lexiconVersion = "fixture-en-v1",
        boardSize = PuzzleBoardSize.BOOK_SHORT,
        challenge = PuzzleChallenge.CLASSIC,
        seed = puzzle.seed,
        boardSha256 = BookBoardDigest.sha256(puzzle)
    )

    private fun fixture(): Pair<BookCatalog, List<WordSearchPuzzle>> {
        // Synthetic fixtures deliberately never enter the production catalog.
        val puzzles = listOf(puzzle(17L), puzzle(21L), puzzle(27L))
        val book = PuzzleBook(
            id = "woodland-volume-one",
            title = "Woodland Volume I",
            volume = 1,
            theme = WordTheme.WOODLAND,
            chapters = listOf(
                BookChapter("woodland-chapter-one", "Under the Pines", listOf(
                    page("woodland-page-one", puzzles[0]), page("woodland-page-two", puzzles[1])
                )),
                BookChapter("woodland-chapter-two", "Beside the Creek", listOf(
                    page("woodland-page-three", puzzles[2])
                ))
            )
        )
        return BookCatalog(listOf(book)) to puzzles
    }

    private inline fun reject(block: () -> Unit) {
        try {
            block()
            fail("Expected a rejected catalog/operation")
        } catch (_: IllegalArgumentException) {
            // Good: fail closed rather than accepting a corrupt manifest/save.
        }
    }

    @Test fun emptyCatalogNeverInventsPlayableVolumes() {
        val catalog = BookCatalog()
        assertTrue(catalog.books.isEmpty())
        assertNull(catalog.page("woodland-volume-one", "woodland-page-one"))
        BookProgressRules.validate(catalog, BookLibraryProgress())
    }

    @Test fun correctFixedPuzzleMatchesExactPageDigest() {
        val (catalog, puzzles) = fixture()
        assertEquals(1, BookBoardDigest.VERSION)
        assertEquals(64, BookBoardDigest.sha256(puzzles[0]).length)
        assertTrue(catalog.matchesPublishedPuzzle(
            "woodland-volume-one", "woodland-page-one", puzzles[0]
        ))
        assertFalse(catalog.matchesPublishedPuzzle(
            "woodland-volume-one", "woodland-page-two", puzzles[0]
        ))
    }

    @Test fun sameSeedOrIdDoesNotAllowModifiedBoardOrAnswerPaths() {
        val (catalog, puzzles) = fixture()
        val correct = puzzles[0]
        val changedRow = correct.rows[0].reversed()
        val mutatedBoard = correct.copy(rows = listOf(changedRow) + correct.rows.drop(1))
        val target = correct.targets.first()
        val swappedPath = target.positions.reversed()
        val alteredAnswers = correct.copy(targets = listOf(
            target.copy(positions = swappedPath)
        ) + correct.targets.drop(1))
        assertFalse(catalog.matchesPublishedPuzzle(
            "woodland-volume-one", "woodland-page-one", mutatedBoard
        ))
        assertFalse(catalog.matchesPublishedPuzzle(
            "woodland-volume-one", "woodland-page-one", alteredAnswers
        ))
        assertNotEquals(BookBoardDigest.sha256(correct), BookBoardDigest.sha256(mutatedBoard))
    }

    @Test fun cannotDuplicateBookPagePuzzleOrChapterIdentities() {
        val (catalog, _) = fixture()
        val old = catalog.books.single()
        reject { BookCatalog(listOf(old, old)) }
        reject { BookCatalog(listOf(old.copy(chapters = listOf(
            old.chapters[0], old.chapters[0]
        )))) }
        reject { BookCatalog(listOf(old.copy(chapters = listOf(
            old.chapters[0].copy(
                pages = listOf(old.chapters[0].pages[0], old.chapters[0].pages[0])
            )
        )))) }
        val duplicatePuzzleId = old.chapters[0].pages[1].copy(
            puzzleId = old.chapters[0].pages[0].puzzleId
        )
        reject { BookCatalog(listOf(old.copy(chapters = listOf(
            old.chapters[0].copy(pages = listOf(
                old.chapters[0].pages[0], duplicatePuzzleId
            ))
        )))) }
    }

    @Test fun rejectsPlaceholderPreviewUnversionedOrUnhashedContent() {
        val (catalog, _) = fixture()
        val book = catalog.books.single()
        fun withFirstPage(update: (BookPage) -> BookPage): BookCatalog {
            val chapter = book.chapters.first()
            val modifiedChapter = chapter.copy(
                pages = listOf(update(chapter.pages.first())) + chapter.pages.drop(1)
            )
            return BookCatalog(listOf(book.copy(
                chapters = listOf(modifiedChapter) + book.chapters.drop(1)
            )))
        }
        reject { withFirstPage { it.copy(puzzleId = "wordsearch:v4:PREVIEW:WOODLAND:17") } }
        reject { withFirstPage { it.copy(boardSha256 = "0".repeat(64)) } }
        reject { withFirstPage { it.copy(boardSha256 = "XYZ") } }
        reject { withFirstPage { it.copy(lexiconVersion = "unapproved_VERSION") } }
        reject { withFirstPage { it.copy(boardSize = PuzzleBoardSize.COMPACT) } }
        reject { withFirstPage { it.copy(generatorVersion = 0) } }
    }

    @Test fun upgradesPreserveStableChapterAndPageContent() {
        val (old, _) = fixture()
        val same = BookCatalog(old.books.map { book ->
            book.copy(title = "Woodland — Updated Display Label")
        })
        old.requireCompatibleUpgrade(same)

        val first = old.books.first()
        reject { old.requireCompatibleUpgrade(BookCatalog()) }
        val edited = first.copy(chapters = first.chapters.mapIndexed { index, chapter ->
            if (index == 0) chapter.copy(pages = listOf(
                chapter.pages[0].copy(seed = 9999L)
            ) + chapter.pages.drop(1)) else chapter
        })
        reject { old.requireCompatibleUpgrade(BookCatalog(listOf(edited))) }
        val reordered = first.copy(chapters = first.chapters.reversed())
        reject { old.requireCompatibleUpgrade(BookCatalog(listOf(reordered))) }
    }

    @Test fun completeRequiresAllFoundIdsAndExactRegisteredBoard() {
        val (catalog, puzzles) = fixture()
        var saved = BookLibraryProgress()
        val id = "woodland-volume-one"
        assertEquals("woodland-page-one", BookProgressRules.continuePage(catalog, saved, id)?.id)
        assertEquals(0, BookProgressRules.completedCount(catalog, saved, id))
        assertFalse(BookProgressRules.isComplete(catalog, saved, id))
        reject {
            BookProgressRules.recordVerifiedCompletion(
                catalog, saved, id, "woodland-page-one", puzzles[0], emptySet()
            )
        }
        reject {
            BookProgressRules.recordVerifiedCompletion(
                catalog, saved, id, "woodland-page-one", puzzles[1],
                puzzles[1].targets.map { it.id }.toSet()
            )
        }
        saved = BookProgressRules.recordVerifiedCompletion(
            catalog, saved, id, "woodland-page-one", puzzles[0],
            puzzles[0].targets.map { it.id }.toSet()
        )
        assertEquals(1, BookProgressRules.completedCount(catalog, saved, id))
        assertEquals("woodland-page-two", BookProgressRules.continuePage(catalog, saved, id)?.id)
        // Duplicate completion is idempotent, never produces another award.
        val identical = BookProgressRules.recordVerifiedCompletion(
            catalog, saved, id, "woodland-page-one", puzzles[0],
            puzzles[0].targets.map { it.id }.toSet()
        )
        assertEquals(saved, identical)
        for ((index, puzzle) in puzzles.withIndex()) {
            if (index == 0) continue
            val pageId = if (index == 1) "woodland-page-two" else "woodland-page-three"
            saved = BookProgressRules.recordVerifiedCompletion(
                catalog, saved, id, pageId, puzzle,
                puzzle.targets.map { it.id }.toSet()
            )
        }
        assertTrue(BookProgressRules.isComplete(catalog, saved, id))
        assertEquals(3, BookProgressRules.completedCount(catalog, saved, id))
        assertNull(BookProgressRules.continuePage(catalog, saved, id))
    }

    @Test fun bookmarksAndRecoveryNeverForgetOrReassignPages() {
        val (catalog, puzzles) = fixture()
        val id = "woodland-volume-one"
        var saved = BookProgressRules.bookmark(
            catalog, BookLibraryProgress(), id, "woodland-page-three"
        )
        assertEquals("woodland-page-three", BookProgressRules.continuePage(catalog, saved, id)?.id)
        saved = BookProgressRules.recordVerifiedCompletion(
            catalog, saved, id, "woodland-page-one", puzzles[0],
            puzzles[0].targets.map { it.id }.toSet()
        )
        assertEquals("woodland-page-three", BookProgressRules.continuePage(catalog, saved, id)?.id)
        reject { BookProgressRules.bookmark(catalog, saved, id, "woodland-page-one") }
        // Simulated decoded snapshot: stable, independent of object identity.
        val rehydrated = BookLibraryProgress(saved.volumes.mapValues { (_, state) ->
            state.copy(completedPageIds = state.completedPageIds.toSet())
        })
        BookProgressRules.validate(catalog, rehydrated)
        assertEquals(saved, rehydrated)
        reject { BookProgressRules.validate(BookCatalog(), rehydrated) }
        reject { BookProgressRules.validate(catalog, BookLibraryProgress(
            mapOf(id to BookVolumeProgress(id, setOf("unknown-page")))
        )) }
    }

    @Test fun bookProgressDoesNotMutateIndependentSoloGeneratorIdentities() {
        val (catalog, puzzles) = fixture()
        val freePlayId = WordSearchGeneratorV2.puzzleId(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.CLASSIC, 17L
        )
        val oldSha = BookBoardDigest.sha256(puzzles[0])
        val saved = BookProgressRules.bookmark(
            catalog, BookLibraryProgress(), "woodland-volume-one", "woodland-page-two"
        )
        assertEquals("woodland-page-two", saved.volumes["woodland-volume-one"]?.bookmarkedPageId)
        assertEquals(freePlayId, WordSearchGeneratorV2.puzzleId(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.CLASSIC, 17L
        ))
        assertEquals(oldSha, BookBoardDigest.sha256(puzzle(17L)))
    }
}
