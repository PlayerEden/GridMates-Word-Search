package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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

/** Native QA visuals; CI compiles these; device/emulator execution remains a separate gate. */
class Ws14FBookDetailPolishAndroidTest {
    @get:Rule val compose = createComposeRule()

    private val page = BookPage(
        "woodland-page-one", "wordsearch:v2:en:WOODLAND:BOOK_SHORT:CLASSIC:101",
        2, "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
        PuzzleChallenge.CLASSIC, 101L, "a".repeat(64)
    )
    private val book = PuzzleBook(
        "woodland-volume-one", "Woodland · TEST BOOK", 1, WordTheme.WOODLAND,
        listOf(BookChapter("woodland-chapter-one", "Getting Started · TEST", listOf(page)))
    )
    private val catalog = BookCatalog(listOf(book))

    @Test fun qaBookHasShortVisibleActionsAndCompleteAccessibilityDescriptions() {
        var played: String? = null
        var bookmarked: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(760.dp)) {
                    PuzzleBookDetailScreen(
                        catalog, BookLibraryProgress(), book.id, onBack = {},
                        onPlayPage = { _, id -> played = id },
                        onBookmarkPage = { _, id -> bookmarked = id },
                        showPrototypeArt = true, fieldNotesArt = true
                    )
                }
            }
        }
        compose.onNodeWithText("Reading progress").assertExists()
        compose.onNodeWithText("Getting Started").assertExists()
        compose.onNodeWithText("Getting Started · TEST").assertDoesNotExist()
        compose.onNodeWithText("TEST BOOK").assertDoesNotExist()
        compose.onNodeWithTag("book-detail-list")
            .performScrollToNode(hasText("Page 1 · Next page"))
        compose.onNodeWithText("Play").assertExists()
        compose.onNodeWithText("Bookmark").assertExists()
        compose.onNodeWithContentDescription("Play page 1").performClick()
        compose.onNodeWithContentDescription("Bookmark page 1").performClick()
        compose.runOnIdle {
            assertEquals(page.id, played)
            assertEquals(page.id, bookmarked)
        }
    }

    @Test fun enlargedTextStacksPageControlsWithoutLosingActions() {
        compose.setContent {
            GridMatesTheme {
                CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                    Box(Modifier.width(320.dp).height(780.dp)) {
                        PuzzleBookDetailScreen(
                            catalog, BookLibraryProgress(), book.id, onBack = {},
                            onPlayPage = { _, _ -> },
                            onBookmarkPage = { _, _ -> },
                            showPrototypeArt = true
                        )
                    }
                }
            }
        }
        compose.onNodeWithTag("book-detail-list")
            .performScrollToNode(hasText("Page 1 · Next page"))
        compose.onNodeWithContentDescription("Play page 1").assertExists()
        compose.onNodeWithContentDescription("Bookmark page 1").assertExists()
    }
}
