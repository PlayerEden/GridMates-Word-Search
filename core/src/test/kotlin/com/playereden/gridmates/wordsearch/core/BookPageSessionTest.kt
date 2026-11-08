package com.playereden.gridmates.wordsearch.core

import java.nio.ByteBuffer
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class BookPageSessionTest {
    private data class Fixture(
        val catalog: BookCatalog, val woodland: WordSearchPuzzle,
        val space: WordSearchPuzzle
    )
    private fun fixture(): Fixture {
        // Existing frozen V2 vocabulary; solely synthetic test manifests.
        val woodland = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 17L
        )
        val space = WordSearchGeneratorV2.generate(
            WordTheme.SPACE, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 19L
        )
        fun book(id: String, chapter: String, page: String, puzzle: WordSearchPuzzle) =
            PuzzleBook(id, id, 1, puzzle.theme, listOf(BookChapter(
                chapter, chapter, listOf(BookPage(
                    page, puzzle.id, puzzle.contentVersion, "fixture-en-v1",
                    PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC,
                    puzzle.seed, BookBoardDigest.sha256(puzzle)
                ))
            )))
        return Fixture(BookCatalog(listOf(
            book("woodland-volume-one", "woodland-chapter-one", "woodland-page-one", woodland),
            book("space-volume-one", "space-chapter-one", "space-page-one", space)
        )), woodland, space)
    }
    private inline fun reject(block: () -> Unit) {
        try {
            block()
            fail("Expected a rejected mismatched/corrupt session")
        } catch (_: IllegalArgumentException) { }
    }
    private fun allFound(board: WordSearchPuzzle) = board.targets.map { it.id }.toSet()

    @Test fun onlyExactFrozenContentCanBecomeAnActiveBookPage() {
        val f = fixture()
        val empty = BookPageSessionJournal()
        val a = BookPageSessionRules.open(
            f.catalog, empty, "woodland-volume-one", "woodland-page-one"
        )
        val saved = requireNotNull(BookPageSessionRules.session(
            a, "woodland-volume-one", "woodland-page-one"
        ))
        assertEquals(f.woodland.id, saved.puzzleId)
        assertEquals(BookBoardDigest.sha256(f.woodland), saved.boardSha256)
        assertEquals(a, BookPageSessionRules.open(
            f.catalog, a, "woodland-volume-one", "woodland-page-one"
        ))
        reject { BookPageSessionRules.open(f.catalog, a, "unpublished-book", "unpublished-page") }
        reject { BookPageSessionRules.open(f.catalog, a, "woodland-volume-one", "wrong-page") }
        val original = f.catalog.books.first()
        val changed = original.copy(chapters = original.chapters.map { chapter ->
            chapter.copy(pages = chapter.pages.map { page ->
                page.copy(boardSha256 = "b".repeat(64))
            })
        })
        val corruptCatalog = BookCatalog(listOf(changed, f.catalog.books[1]))
        reject { BookPageSessionRules.open(
            corruptCatalog, empty, "woodland-volume-one", "woodland-page-one"
        ) }
    }

    @Test fun realSelectionPathsAndDuplicateClaimsAreEnforced() {
        val f = fixture()
        val firstWord = f.woodland.targets.first()
        val start = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "woodland-volume-one", "woodland-page-one"
        )
        val (noChange, invalid) = BookPageSessionRules.find(
            f.catalog, start, "woodland-volume-one", "woodland-page-one",
            GridPoint(-1, -1), GridPoint(-1, 4)
        )
        assertEquals(ClaimStatus.INVALID, invalid.status)
        assertEquals(start, noChange)
        val (found, success) = BookPageSessionRules.find(
            f.catalog, start, "woodland-volume-one", "woodland-page-one",
            firstWord.positions.last(), firstWord.positions.first()
        )
        assertEquals(ClaimStatus.FOUND, success.status)
        assertEquals(setOf(firstWord.id), BookPageSessionRules.session(
            found, "woodland-volume-one", "woodland-page-one"
        )?.foundIds)
        val (duplicate, repeat) = BookPageSessionRules.find(
            f.catalog, found, "woodland-volume-one", "woodland-page-one",
            firstWord.positions.first(), firstWord.positions.last()
        )
        assertEquals(ClaimStatus.ALREADY_FOUND, repeat.status)
        assertEquals(found, duplicate)
    }

    @Test fun hintsTimeAndProgressAreIndependentAndBounded() {
        val f = fixture()
        val opened = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "woodland-volume-one", "woodland-page-one"
        )
        var journal = BookPageSessionRules.tick(
            f.catalog, opened, "woodland-volume-one", "woodland-page-one", 10
        )
        val old = BookPageSessionRules.session(
            journal, "woodland-volume-one", "woodland-page-one"
        )
        assertEquals(10L, old?.elapsedSeconds)
        reject { BookPageSessionRules.tick(
            f.catalog, journal, "woodland-volume-one", "woodland-page-one", 0
        ) }
        repeat(2) {
            val (next, target) = BookPageSessionRules.hint(
                f.catalog, journal, "woodland-volume-one", "woodland-page-one"
            )
            assertNotNull(target)
            journal = next
        }
        val session = requireNotNull(BookPageSessionRules.session(
            journal, "woodland-volume-one", "woodland-page-one"
        ))
        assertEquals(2, session.hints.values.sum())
        assertTrue(session.foundIds.isEmpty()) // hints do NOT silently solve
        val (same, noExtra) = BookPageSessionRules.hint(
            f.catalog, journal, "woodland-volume-one", "woodland-page-one"
        )
        assertNull(noExtra)
        assertEquals(journal, same)
    }

    @Test fun partialFileCodecRoundTripAndCanonicalBytes() {
        val f = fixture()
        var sessions = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "space-volume-one", "space-page-one"
        )
        sessions = BookPageSessionRules.open(
            f.catalog, sessions, "woodland-volume-one", "woodland-page-one"
        )
        val first = f.woodland.targets.first()
        sessions = BookPageSessionRules.find(
            f.catalog, sessions, "woodland-volume-one", "woodland-page-one",
            first.positions.first(), first.positions.last()
        ).first
        sessions = BookPageSessionRules.tick(
            f.catalog, sessions, "woodland-volume-one", "woodland-page-one", 9L
        )
        val raw = BookPageSessionCodec.encode(f.catalog, sessions)
        assertEquals("GMWS", String(raw.copyOfRange(0, 4), Charsets.US_ASCII))
        assertEquals(1, ByteBuffer.wrap(raw, 4, 4).int)
        assertEquals(sessions, BookPageSessionCodec.decode(f.catalog, raw))
        assertArrayEquals(raw, BookPageSessionCodec.encode(
            f.catalog, sessions.copy(sessions = sessions.sessions.reversed())
        ))
        assertEquals(1, BookPageSessionRules.session(
            BookPageSessionCodec.decode(f.catalog, raw),
            "woodland-volume-one", "woodland-page-one"
        )?.foundIds?.size)
    }

    @Test fun badChecksumTruncationAndUnknownFutureVersionAreRejected() {
        val f = fixture()
        val open = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "woodland-volume-one", "woodland-page-one"
        )
        val original = BookPageSessionCodec.encode(f.catalog, open)
        val corrupted = original.clone()
        corrupted[15] = (corrupted[15].toInt() xor 1).toByte()
        reject { BookPageSessionCodec.decode(f.catalog, corrupted) }
        reject { BookPageSessionCodec.decode(f.catalog, original.copyOf(original.size - 1)) }
        reject { BookPageSessionCodec.decode(f.catalog, ByteArray(BookPageSessionCodec.MAX_BYTES + 1)) }
        val futurePayload = original.copyOf(original.size - 32)
        ByteBuffer.wrap(futurePayload).putInt(4, 2)
        val future = futurePayload +
            MessageDigest.getInstance("SHA-256").digest(futurePayload)
        reject { BookPageSessionCodec.decode(f.catalog, future) }
        reject { BookPageSessionCodec.decode(BookCatalog(), original) }
        assertEquals(open, BookPageSessionCodec.decode(f.catalog, original))
    }

    @Test fun noDuplicateSessionsUnknownTargetsOrSilentRecoveryTrimming() {
        val f = fixture()
        val j = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "woodland-volume-one", "woodland-page-one"
        )
        val first = j.sessions.single()
        reject { BookPageSessionRules.validate(
            f.catalog, BookPageSessionJournal(listOf(first, first))
        ) }
        reject { BookPageSessionRules.validate(
            f.catalog, BookPageSessionJournal(listOf(first.copy(foundIds = setOf("FAKEWORD"))))
        ) }
        reject { BookPageSessionRules.validate(
            f.catalog, BookPageSessionJournal(listOf(first.copy(
                hints = mapOf(f.woodland.targets.first().id to 4)
            )))
        ) }
        reject { BookPageSessionRules.validate(
            f.catalog, BookPageSessionJournal(listOf(first.copy(
                boardSha256 = "c".repeat(64)
            )))
        ) }
    }

    @Test fun completionUsesSavedFindsAndCanRecoverAcrossTwoSeparateFiles() {
        val f = fixture()
        val id = "woodland-volume-one"
        val page = "woodland-page-one"
        var journal = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), id, page
        )
        reject { BookPageSessionRules.verifiedCompletion(
            f.catalog, journal, BookLibraryProgress(), id, page
        ) }
        reject { BookPageSessionRules.clearCommitted(
            f.catalog, journal, BookLibraryProgress(), id, page
        ) }
        for (target in f.woodland.targets) {
            val (updated, success) = BookPageSessionRules.find(
                f.catalog, journal, id, page,
                target.positions.first(), target.positions.last()
            )
            assertEquals(ClaimStatus.FOUND, success.status)
            journal = updated
        }
        assertTrue(BookPageSessionRules.complete(f.catalog, journal.sessions.single()))
        assertEquals(journal, BookPageSessionCodec.decode(
            f.catalog, BookPageSessionCodec.encode(f.catalog, journal)
        ))
        // Crash at this point: only the GMWS session file is durable.
        val completed = BookPageSessionRules.verifiedCompletion(
            f.catalog, journal, BookLibraryProgress(), id, page
        )
        assertEquals(1, BookProgressRules.completedCount(f.catalog, completed, id))
        assertEquals(completed, BookPageSessionRules.verifiedCompletion(
            f.catalog, journal, completed, id, page
        ))
        val cleaned = BookPageSessionRules.clearCommitted(
            f.catalog, journal, completed, id, page
        )
        assertTrue(cleaned.sessions.isEmpty())
        assertEquals(cleaned, BookPageSessionRules.clearCommitted(
            f.catalog, cleaned, completed, id, page
        ))
        assertTrue(BookProgressRules.isComplete(f.catalog, completed, id))
        assertEquals(f.space.id, f.space.id) // the other book remains independent
    }

    @Test fun legacySoloAndDailyGenerationAreNotChanged() {
        val oldId = WordSearchGeneratorV2.puzzleId(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.CLASSIC, 17L
        )
        val f = fixture()
        val start = BookPageSessionRules.open(
            f.catalog, BookPageSessionJournal(), "woodland-volume-one", "woodland-page-one"
        )
        assertTrue(start.sessions.single().foundIds.isEmpty())
        assertEquals(oldId, WordSearchGeneratorV2.puzzleId(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.CLASSIC, 17L
        ))
        assertEquals(BookPageSessionCodec.VERSION, 1)
        assertEquals(BookProgressCodec.VERSION, 1)
    }
}
