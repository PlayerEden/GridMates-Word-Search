package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.BookVolumeProgress
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Synthetic page metadata is used ONLY in Android tests. Actual app catalog
 * remains empty until editorially approved/published immutable pages exist.
 */
class PuzzleBookshelfScreenAndroidTest {
    @get:Rule val compose = createComposeRule()

    private val first = BookPage(
        "woodland-page-one",
        "wordsearch:v2:en:WOODLAND:BOOK_SHORT:CLASSIC:1001",
        2, "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
        PuzzleChallenge.CLASSIC, 1001L, "a".repeat(64)
    )
    private val second = BookPage(
        "woodland-page-two",
        "wordsearch:v2:en:WOODLAND:BOOK_SHORT:CLASSIC:1002",
        2, "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
        PuzzleChallenge.CLASSIC, 1002L, "b".repeat(64)
    )
    private val book = PuzzleBook(
        "woodland-volume-one", "Woodland Volume I", 1,
        WordTheme.WOODLAND, listOf(
            BookChapter("woodland-chapter-one", "The Trees", listOf(first, second))
        )
    )
    private val catalog = BookCatalog(listOf(book))

    @Test fun emptyUnpublishedCatalogDoesNotShowFakePlayableBooks() {
        var returned = false
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(640.dp)) {
                    PuzzleBookshelfScreen(
                        BookCatalog(), BookLibraryProgress(),
                        onBack = { returned = true },
                        onOpenBook = { error("Unpublished book selected") }
                    )
                }
            }
        }
        compose.onNodeWithText("Puzzle Books").assertIsDisplayed()
        compose.onNodeWithText("Our first puzzle books are being prepared").assertIsDisplayed()
        compose.onNodeWithText("Open book").assertDoesNotExist()
        compose.onNodeWithText("Back to Play").performClick()
        compose.runOnIdle { assertEquals(true, returned) }
    }

    @Test fun publishedFixtureBookShowsProgressAndOpensByStableId() {
        var opened: String? = null
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(640.dp)) {
                    PuzzleBookshelfScreen(
                        catalog, BookLibraryProgress(),
                        onBack = {}, onOpenBook = { opened = it }
                    )
                }
            }
        }
        compose.onNodeWithText("Woodland Volume I").assertIsDisplayed()
        compose.onNodeWithText("0 of 2 pages completed").assertIsDisplayed()
        compose.onNodeWithText("Open book").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("woodland-volume-one", opened) }
    }

    @Test fun bookmarkDeterminesContinueRatherThanRestartingAtPageOne() {
        val saved = BookProgressRules.bookmark(
            catalog, BookLibraryProgress(), book.id, second.id
        )
        var selected: String? = null
        compose.setContent {
            GridMatesTheme {
                PuzzleBookDetailScreen(
                    catalog, saved, book.id, onBack = {},
                    onPlayPage = { _, pageId -> selected = pageId },
                    onBookmarkPage = { _, _ -> }
                )
            }
        }
        compose.onNodeWithText("The Trees").assertExists()
        compose.onNodeWithText("Start reading").assertExists().performClick()
        compose.runOnIdle { assertEquals(second.id, selected) }
        compose.onNodeWithText("Bookmark saved").assertExists()
    }

    @Test fun completedBookOffersReviewWithoutAnotherCompletionCounter() {
        val saved = BookLibraryProgress(mapOf(book.id to BookVolumeProgress(
            book.id, setOf(first.id, second.id)
        )))
        var selected: String? = null
        compose.setContent {
            GridMatesTheme {
                PuzzleBookDetailScreen(
                    catalog, saved, book.id, onBack = {},
                    onPlayPage = { _, pageId -> selected = pageId },
                    onBookmarkPage = { _, _ -> error("Cannot bookmark completed page") }
                )
            }
        }
        compose.onNodeWithText("Book complete · You can revisit any page.").assertExists()
        compose.onNodeWithText("Start reading").assertDoesNotExist()
        compose.onNodeWithText("Bookmark page 1").assertDoesNotExist()
        compose.onNodeWithText("Replay page 1").assertExists().performClick()
        compose.runOnIdle { assertEquals(first.id, selected) }
    }

    @Test fun missingGameHandlerDoesNotOfferDeadPlayButtons() {
        compose.setContent {
            GridMatesTheme {
                PuzzleBookDetailScreen(catalog, BookLibraryProgress(), book.id, onBack = {})
            }
        }
        compose.onNodeWithText("Book pages will be playable after content approval.").assertExists()
        compose.onNodeWithText("Start reading").assertDoesNotExist()
        compose.onNodeWithText("Play page 1").assertDoesNotExist()
    }

    @Test fun incompatibleSavedBookProgressIsNotDisplayedAsFreshProgress() {
        val unknown = BookLibraryProgress(mapOf(
            "other-book" to BookVolumeProgress("other-book", setOf("other-page"))
        ))
        compose.setContent {
            GridMatesTheme {
                PuzzleBookshelfScreen(catalog, unknown,
                    onBack = {}, onOpenBook = { error("Must block unusable saves") })
            }
        }
        compose.onNodeWithText("Your saved book progress could not be opened.").assertExists()
        compose.onNodeWithText("Open book").assertDoesNotExist()
    }
}
