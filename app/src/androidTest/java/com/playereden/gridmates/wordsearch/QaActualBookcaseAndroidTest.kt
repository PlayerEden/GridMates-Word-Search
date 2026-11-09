package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** QA source tests: real bookcase browsing, no fabricated shelf inventory. */
class QaActualBookcaseAndroidTest {
    @get:Rule val compose = createComposeRule()

    private fun fixture(theme: WordTheme, tag: String, digest: Char): PuzzleBook {
        val page = BookPage(
            id = tag + "-page-one",
            puzzleId = "wordsearch:v2:en:" + theme.name + ":BOOK_SHORT:CLASSIC:1001",
            generatorVersion = 2,
            lexiconVersion = "fixture-en-v1",
            boardSize = PuzzleBoardSize.BOOK_SHORT,
            challenge = PuzzleChallenge.CLASSIC,
            seed = 1001L,
            boardSha256 = digest.toString().repeat(64)
        )
        return PuzzleBook(
            id = tag + "-volume-one",
            title = tag.replaceFirstChar { it.uppercase() } + " · TEST BOOK",
            volume = 1,
            theme = theme,
            chapters = listOf(BookChapter(tag + "-chapter-one", "Getting Started", listOf(page)))
        )
    }

    private val woodland = fixture(WordTheme.WOODLAND, "woodland", 'a')
    private val space = fixture(WordTheme.SPACE, "space", 'b')
    private val garden = fixture(WordTheme.GARDEN, "garden", 'c')
    private val catalog = BookCatalog(listOf(woodland, space, garden))

    @Test fun bookcaseShowsRealStandingBooksAndTapCoverUsesStableBookId() {
        var selected: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(730.dp)) {
                    PuzzleBookshelfScreen(
                        catalog = catalog, progress = BookLibraryProgress(),
                        onBack = {}, onOpenBook = { selected = it },
                        showPrototypeArt = true, fieldNotesArt = true,
                        onToggleArt = {}
                    )
                }
            }
        }
        compose.onNodeWithTag("real-bookcase-screen").assertExists()
        compose.onNodeWithTag("wooden-shelf-plank-0").assertExists()
        compose.onNodeWithTag("book-cover-" + woodland.id).assertExists()
        compose.onNodeWithTag("book-spine-" + woodland.id).assertExists()
        compose.onNodeWithText("Woodland").assertExists()
        compose.onAllNodesWithText("TEST BOOK · 1 chapter")[0].assertExists()
        compose.onNodeWithTag("shelf-cover-select-" + woodland.id).performClick()
        compose.runOnIdle { assertEquals(woodland.id, selected) }
        compose.onNodeWithText("Illustrated study B · Field Notes").assertExists()

        // Three actual published/QA catalog entries require two shelf rows
        // at ordinary phone width. There are no fake locked future books.
        compose.onNodeWithTag("bookcase-lazy-shelves")
            .performScrollToNode(hasTestTag("bookcase-shelf-1"))
        compose.onNodeWithTag("wooden-shelf-plank-1").assertExists()
        compose.onNodeWithTag("shelf-open-" + garden.id).performClick()
        compose.runOnIdle { assertEquals(garden.id, selected) }
    }

    @Test fun largeFontCreatesOneBookPerShelfWithoutChangingCatalog() {
        compose.setContent {
            GridMatesTheme {
                CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                    Box(Modifier.width(320.dp).height(700.dp)) {
                        PuzzleBookshelfScreen(
                            catalog, BookLibraryProgress(),
                            onBack = {}, onOpenBook = {},
                            showPrototypeArt = true, fieldNotesArt = false
                        )
                    }
                }
            }
        }
        compose.onNodeWithTag("real-bookcase-screen").assertExists()
        compose.onNodeWithTag("bookcase-lazy-shelves")
            .performScrollToNode(hasTestTag("bookcase-shelf-2"))
        compose.onNodeWithTag("wooden-shelf-plank-2").assertExists()
    }

    @Test fun oneBookDoesNotInventMoreVolumesOrLockedPlaceholders() {
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(700.dp)) {
                    PuzzleBookshelfScreen(
                        BookCatalog(listOf(woodland)), BookLibraryProgress(),
                        onBack = {}, onOpenBook = {},
                        showPrototypeArt = true, fieldNotesArt = true
                    )
                }
            }
        }
        compose.onNodeWithText("1 book on your shelf").assertExists()
        compose.onNodeWithText("SHELF 2").assertDoesNotExist()
        compose.onNodeWithText("Locked").assertDoesNotExist()
    }
}
