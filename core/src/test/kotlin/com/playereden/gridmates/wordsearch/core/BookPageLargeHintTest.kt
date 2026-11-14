package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

/** Regression for owner WS-14H 20×20 bulb screenshot showing no hints at 0/24. */
class BookPageLargeHintTest {
    private val bookId = "woodland-hints-volume-one"
    private val pageId = "woodland-hints-page-one"

    private fun fixture(): Pair<BookCatalog, WordSearchPuzzle> {
        val board = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC, 20261109L
        )
        val page = BookPage(
            pageId, board.id, board.contentVersion, "fixture-en-v1",
            PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC, board.seed,
            BookBoardDigest.sha256(board)
        )
        return BookCatalog(listOf(PuzzleBook(
            bookId, "Woodland Hints", 1, WordTheme.WOODLAND,
            listOf(BookChapter("woodland-hints-chapter-one", "A Forest Walk", listOf(page)))
        ))) to board
    }

    @Test fun twentyByTwentyKeepsHelpingPastFourTapsWithoutAutoFinding() {
        val (catalog, board) = fixture()
        assertEquals(20, board.size)
        assertEquals(24, board.targets.size)
        var journal = BookPageSessionRules.open(catalog, BookPageSessionJournal(), bookId, pageId)
        val ids = mutableListOf<String>()
        repeat(7) {
            val (next, clue) = BookPageSessionRules.hint(catalog, journal, bookId, pageId)
            assertNotNull("There are still unhinted words after four taps", clue)
            ids.add(requireNotNull(clue).id)
            journal = next
        }
        assertEquals(ids[0], ids[1])
        assertEquals(ids[1], ids[2])
        assertNotEquals(ids[2], ids[3])
        val session = requireNotNull(BookPageSessionRules.session(journal, bookId, pageId))
        assertEquals(7, session.hints.values.sum())
        assertTrue(session.foundIds.isEmpty())
        val restored = BookPageSessionCodec.decode(catalog, BookPageSessionCodec.encode(catalog, journal))
        assertEquals(journal, restored)
        val word = board.targets.first()
        val (afterFind, claim) = BookPageSessionRules.find(
            catalog, restored, bookId, pageId, word.positions.first(), word.positions.last()
        )
        assertEquals(ClaimStatus.FOUND, claim.status)
        assertEquals(setOf(word.id), afterFind.sessions.single().foundIds)
        assertEquals(session.hints, afterFind.sessions.single().hints)
    }

    @Test fun fullThreeStageCluesForEveryWordAreBoundedAndDoNotChangeAnswers() {
        val (catalog, board) = fixture()
        var journal = BookPageSessionRules.open(catalog, BookPageSessionJournal(), bookId, pageId)
        repeat(board.targets.size * 3) {
            val (next, clue) = BookPageSessionRules.hint(catalog, journal, bookId, pageId)
            assertNotNull(clue)
            journal = next
        }
        assertEquals(board.targets.size, journal.sessions.single().hints.size)
        assertTrue(journal.sessions.single().hints.values.all { it == 3 })
        val (same, noMore) = BookPageSessionRules.hint(catalog, journal, bookId, pageId)
        assertNull(noMore)
        assertEquals(journal, same)
        assertTrue(journal.sessions.single().foundIds.isEmpty())
    }
}
