package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
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
import org.junit.Assert.assertTrue
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

    @Test fun bookcaseShowsSpinesAndPullOutPreviewUsesStableBookId() {
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
        compose.onNodeWithTag("ws16ai-painted-bookcase").assertExists()
        compose.onNodeWithTag("library-spine-" + woodland.id).assertExists()
        compose.onNodeWithTag("library-spine-" + space.id).assertExists()
        compose.onNodeWithTag("library-spine-" + garden.id).assertExists()
        compose.onNodeWithTag("book-cover-" + woodland.id).assertDoesNotExist()
        compose.onNodeWithTag("library-spine-" + woodland.id).performClick()
        compose.onNodeWithTag("library-pulled-book-dialog").assertExists()
        compose.onNodeWithTag("library-pulled-cover-" + woodland.id).assertExists()
        compose.onNodeWithText("Woodland").assertExists()
        compose.onNodeWithTag("library-view-pages").performClick()
        compose.runOnIdle { assertEquals(woodland.id, selected) }
        compose.onNodeWithText("Browse your puzzle books").assertExists()

        // Second tier shares a painted cabinet and decorative anonymous bindings.
        compose.onNodeWithTag("bookcase-shelf-1").assertExists()
        compose.onNodeWithTag("wooden-shelf-plank-1").assertExists()
        compose.onNodeWithText("Locked").assertDoesNotExist()
    }

    @Test fun largeFontShowsRealSpinesWithoutChangingCatalog() {
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
        compose.onNodeWithTag("library-spine-" + woodland.id).assertExists()
        compose.onNodeWithTag("library-spine-" + garden.id).assertExists()
        compose.onNodeWithTag("library-spine-" + garden.id).performClick()
        compose.onNodeWithTag("library-view-pages").assertExists()
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
        compose.onNodeWithText("1 book in your library").assertExists()
        compose.onNodeWithTag("library-spine-" + woodland.id).assertExists()
        compose.onNodeWithTag("library-spine-" + space.id).assertDoesNotExist()
        compose.onNodeWithTag("library-spine-" + garden.id).assertDoesNotExist()
        compose.onNodeWithText("Locked").assertDoesNotExist()
    }

    @Test fun modularPortraitPacksThreeRealSpinesSideBySide() {
        var selected: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(760.dp)) {
                    WS16ATModularLibraryScene(
                        books = catalog.books,
                        onBack = {},
                        onBookSelected = { selected = it },
                        onPreviousArt = {}
                    )
                }
            }
        }
        compose.onNodeWithTag("ws16at-modular-library").assertExists()
        compose.onNodeWithTag("ws16at-movable-shelf-0").assertExists()
        compose.onNodeWithTag("ws16at-movable-shelf-1").assertExists()
        compose.onNodeWithTag("ws16at-movable-shelf-2").assertDoesNotExist()
        compose.onNodeWithTag("ws16bh-packed-shelf-0").assertExists()
        compose.onNodeWithTag("ws16bh-packed-shelf-1").assertExists()
        compose.onNodeWithTag("ws16bh-spine-" + woodland.id).assertExists()
        compose.onNodeWithTag("ws16bh-spine-" + space.id).assertExists()
        compose.onNodeWithTag("ws16bh-spine-" + garden.id).assertExists()
        compose.onNodeWithTag("ws16bi-blank-cell-0-0").assertExists()
        compose.onNodeWithTag("ws16bi-blank-cell-1-0").assertExists()
        compose.onNodeWithTag("ws16bi-mounted-shelf").assertExists()
        compose.onNodeWithTag("ws16bi-real-shadow-" + woodland.id).assertExists()
        compose.onNodeWithTag("ws16bg-cover-" + woodland.id).assertDoesNotExist()
        compose.onNodeWithTag("ws16bb-upcoming-label").assertDoesNotExist()
        compose.onNodeWithTag("ws16bc-visible-future-0-3").assertDoesNotExist()
        compose.onNodeWithText("3 books").assertExists()

        val woodlandBounds = compose.onNodeWithTag("ws16bh-spine-" + woodland.id)
            .fetchSemanticsNode().boundsInRoot
        val spaceBounds = compose.onNodeWithTag("ws16bh-spine-" + space.id)
            .fetchSemanticsNode().boundsInRoot
        val gardenBounds = compose.onNodeWithTag("ws16bh-spine-" + garden.id)
            .fetchSemanticsNode().boundsInRoot
        assertTrue("All owned volumes must be on the same first shelf",
            kotlin.math.abs(woodlandBounds.top - gardenBounds.top) < 2f)
        assertTrue("Spines pack side-by-side, not spaced hero items",
            kotlin.math.abs(woodlandBounds.right - spaceBounds.left) < 4f)

        val titleBounds = compose.onNodeWithTag("ws16at-library-title")
            .fetchSemanticsNode().boundsInRoot
        val topArt = compose.onNodeWithTag("ws16bh-art-" + woodland.id)
            .fetchSemanticsNode().boundsInRoot
        assertTrue("Spines clear the title plaque", topArt.top >= titleBounds.bottom)
        val lowerShelf = compose.onNodeWithTag("ws16at-movable-shelf-1")
            .fetchSemanticsNode().boundsInRoot
        val count = compose.onNodeWithTag("ws16at-real-count")
            .fetchSemanticsNode().boundsInRoot
        assertTrue("Count clears lower shelf", count.top > lowerShelf.bottom)

        compose.onNodeWithTag("ws16bh-spine-" + garden.id).performClick()
        compose.runOnIdle { assertEquals(garden.id, selected) }
    }

    @Test fun tallPortraitExpandsToThreeRealShelvesWithLargeCatalog() {
        val many = (0 until 10).map { i ->
            fixture(
                when (i % 3) {
                    0 -> WordTheme.WOODLAND
                    1 -> WordTheme.SPACE
                    else -> WordTheme.GARDEN
                },
                "volume-$i",
                'a'
            )
        }
        var selected: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(980.dp)) {
                    WS16ATModularLibraryScene(
                        books = many,
                        onBack = {},
                        onBookSelected = { selected = it },
                        onPreviousArt = {}
                    )
                }
            }
        }
        compose.onNodeWithTag("ws16bh-packed-shelf-0").assertExists()
        compose.onNodeWithTag("ws16bh-packed-shelf-1").assertExists()
        compose.onNodeWithTag("ws16bh-packed-shelf-2").assertExists()
        compose.onNodeWithTag("ws16bh-spine-" + many.first().id).assertExists()
        compose.onNodeWithTag("ws16bh-spine-" + many.last().id).assertExists()
        compose.onNodeWithText("10 books").assertExists()
        compose.onNodeWithTag("ws16bb-upcoming-label").assertDoesNotExist()

        val lastBinding = compose.onNodeWithTag("ws16bh-spine-" + many.last().id)
            .fetchSemanticsNode().boundsInRoot
        val firstBinding = compose.onNodeWithTag("ws16bh-spine-" + many.first().id)
            .fetchSemanticsNode().boundsInRoot
        assertTrue("Overflow goes onto lower real shelf",
            lastBinding.top > firstBinding.bottom)

        compose.onNodeWithTag("ws16bh-spine-" + many.last().id).performClick()
        compose.runOnIdle { assertEquals(many.last().id, selected) }
    }
}
