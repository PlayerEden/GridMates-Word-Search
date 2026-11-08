package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * testDebug source only: prevents distributing a QA build whose seeded pages
 * fail their own immutable book hash/target/route checks.
 */
class QaPilotCatalogTest {
    @Test fun allThreeTestVolumesAndNinePagesAreActuallyPlayable() {
        assertTrue(BookQaPilot.enabled)
        val catalog = BookQaPilot.catalog()
        assertEquals(3, catalog.books.size)
        assertEquals(
            setOf("Woodland · TEST BOOK", "Space · TEST BOOK", "Garden · TEST BOOK"),
            catalog.books.map { it.title }.toSet()
        )
        assertEquals(9, catalog.books.sumOf { book ->
            catalog.pages(book.id).size
        })
        val puzzleIds = hashSetOf<String>()
        for (book in catalog.books) {
            assertEquals(2, book.chapters.size)
            assertEquals(3, catalog.pages(book.id).size)
            assertTrue(book.id.contains("qa"))
            assertTrue(book.title.contains("TEST"))
            for (chapter in book.chapters) {
                assertTrue(chapter.title.contains("TEST"))
                for (page in chapter.pages) {
                    assertTrue(page.id.contains("qa"))
                    val puzzle = BookPageSessionRules.puzzle(catalog, book.id, page.id)
                    assertTrue(puzzleIds.add(puzzle.id))
                    assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, puzzle))
                    assertEquals(page.boardSize.targets, puzzle.targets.size)
                    assertTrue(puzzle.targets.isNotEmpty())
                }
            }
            assertEquals(0, BookProgressRules.completedCount(
                catalog, com.playereden.gridmates.wordsearch.core.BookLibraryProgress(), book.id
            ))
        }
    }
}
