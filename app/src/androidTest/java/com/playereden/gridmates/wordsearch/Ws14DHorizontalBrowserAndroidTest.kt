package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

/**
 * WS-14D Android instrumentation-source tests. CI compiles these; a separate
 * emulator/device run is required before claiming interactive QA is passed.
 */
class Ws14DHorizontalBrowserAndroidTest {
    @get:Rule val compose = createComposeRule()

    private fun fixture(theme: WordTheme, name: String, seed: Long): PuzzleBook {
        val slug = name.lowercase()
        val page = BookPage(
            id = slug + "-page-one",
            puzzleId = "wordsearch:v2:en:" + theme.name + ":BOOK_SHORT:CLASSIC:" + seed,
            generatorVersion = 2, lexiconVersion = "fixture-en-v1",
            boardSize = PuzzleBoardSize.BOOK_SHORT, challenge = PuzzleChallenge.CLASSIC,
            seed = seed, boardSha256 = "a".repeat(64)
        )
        return PuzzleBook(
            id = slug + "-volume-one", title = name + " · TEST BOOK",
            volume = 1, theme = theme,
            chapters = listOf(BookChapter(slug + "-chapter", "Getting Started", listOf(page)))
        )
    }

    private val books = listOf(
        fixture(WordTheme.WOODLAND, "Woodland", 1001L),
        fixture(WordTheme.SPACE, "Space", 1002L),
        fixture(WordTheme.GARDEN, "Garden", 1003L)
    )
    private val catalog = BookCatalog(books)

    @Test fun selectedCoverArrowNavigationAndOpenUseRealStableBookId() {
        var opened: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(720.dp)) {
                    PuzzleBookshelfScreen(
                        catalog = catalog, progress = BookLibraryProgress(),
                        onBack = {}, onOpenBook = { opened = it },
                        showPrototypeArt = true, fieldNotesArt = true,
                        showHorizontalBrowser = true, onToggleLayout = {}
                    )
                }
            }
        }
        compose.onNodeWithTag("ws14d-horizontal-browser").assertExists()
        compose.onNodeWithText("1 of 3 books").assertExists()
        compose.onAllNodesWithText("Open book").assertCountEquals(1)
        compose.onNodeWithTag("ws14d-next").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("2 of 3 books").assertExists()
        compose.onNodeWithTag("ws14d-selected-name").assertExists()
        compose.onNodeWithTag("ws14d-open-selected").performClick()
        compose.runOnIdle { assertEquals(books[1].id, opened) }
        compose.onNodeWithTag("ws14d-prev").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("1 of 3 books").assertExists()
        compose.onNodeWithTag("ws14d-open-selected").performClick()
        compose.runOnIdle { assertEquals(books[0].id, opened) }
    }

    @Test fun userCanReturnToClassicShelvesWithoutTouchingBookProgress() {
        var swipe by mutableStateOf(true)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(720.dp)) {
                    PuzzleBookshelfScreen(
                        catalog = catalog, progress = BookLibraryProgress(),
                        onBack = {}, onOpenBook = {},
                        showPrototypeArt = true, fieldNotesArt = true,
                        showHorizontalBrowser = swipe,
                        onToggleLayout = { swipe = !swipe }
                    )
                }
            }
        }
        compose.onNodeWithText("Classic shelves").performClick()
        compose.onNodeWithTag("real-bookcase-screen").assertExists()
        compose.onNodeWithText("Try swipe library").performClick()
        compose.onNodeWithTag("ws14d-horizontal-browser").assertExists()
        compose.onNodeWithText("1 of 3 books").assertExists()
    }

    @Test fun narrowLargeFontHasArrowsAndSingleNativeOpenAction() {
        compose.setContent {
            GridMatesTheme {
                CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                    Box(Modifier.width(320.dp).height(780.dp)) {
                        PuzzleBookshelfScreen(
                            catalog = catalog, progress = BookLibraryProgress(),
                            onBack = {}, onOpenBook = {},
                            showPrototypeArt = true, fieldNotesArt = true,
                            showHorizontalBrowser = true
                        )
                    }
                }
            }
        }
        compose.onNodeWithTag("ws14d-cover-pager").assertExists()
        compose.onNodeWithTag("ws14d-next").assertExists()
        compose.onAllNodesWithText("Open book").assertCountEquals(1)
        compose.onNodeWithTag("ws14d-open-selected").performScrollTo().assertIsDisplayed()
    }
}
