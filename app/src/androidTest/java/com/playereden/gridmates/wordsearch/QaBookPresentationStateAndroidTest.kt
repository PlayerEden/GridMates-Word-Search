package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.BookVolumeProgress
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Source-compiled Android QA for *actual* book artwork and chapter transitions.
 * Stable book/page IDs are fixtures; no production catalog is published.
 * Running this on a device/emulator remains a separate gate from compiling it.
 */
class QaBookPresentationStateAndroidTest {
    @get:Rule val compose = createComposeRule()

    private fun page(suffix: String, seed: Long, digit: Char) = BookPage(
        id = "woodland-page-" + suffix,
        puzzleId = "wordsearch:v2:en:WOODLAND:BOOK_SHORT:CLASSIC:" + seed,
        generatorVersion = 2,
        lexiconVersion = "qa-fixture-v2",
        boardSize = PuzzleBoardSize.BOOK_SHORT,
        challenge = PuzzleChallenge.CLASSIC,
        seed = seed,
        boardSha256 = digit.toString().repeat(64)
    )
    private val first = page("one", 1201L, 'a')
    private val second = page("two", 1202L, 'b')
    private val third = page("three", 1203L, 'c')
    private val book = PuzzleBook(
        id = "woodland-test-volume",
        title = "Woodland · TEST BOOK",
        volume = 1,
        theme = WordTheme.WOODLAND,
        chapters = listOf(
            BookChapter("woodland-chapter-one", "First Chapter", listOf(first, second)),
            BookChapter("woodland-chapter-two", "Second Chapter", listOf(third))
        )
    )
    private val catalog = BookCatalog(listOf(book))

    @Test fun bookmarkOnChapterTwoKeepsGlobalPageThreeAndNoFalseCompletionSeal() {
        var selectedPage: String? = null
        val saved = BookLibraryProgress(mapOf(book.id to BookVolumeProgress(
            bookId = book.id,
            completedPageIds = setOf(first.id),
            bookmarkedPageId = third.id
        )))
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(760.dp)) {
                    PuzzleBookDetailScreen(
                        catalog = catalog, progress = saved, bookId = book.id,
                        onBack = {},
                        onPlayPage = { _, selected -> selectedPage = selected },
                        onBookmarkPage = { _, _ -> error("Already saved bookmark") },
                        showPrototypeArt = true
                    )
                }
            }
        }
        compose.onNodeWithText("Continue reading").assertExists().performClick()
        compose.runOnIdle { assertEquals(third.id, selectedPage) }
        compose.onNodeWithTag("book-volume-complete-" + book.id).assertDoesNotExist()

        compose.onNodeWithTag("book-detail-list")
            .performScrollToNode(hasText("Page 3 · Next page"))
        compose.onNodeWithText("Page 3 · Next page").assertExists()
        compose.onNodeWithText("Play page 3").assertExists()
        compose.onNodeWithText("Bookmark saved").assertExists()
        compose.onNodeWithTag("book-bookmark-" + third.id).assertExists()
        compose.onNodeWithTag("book-page-complete-" + third.id).assertDoesNotExist()

        compose.onNodeWithTag("book-detail-list")
            .performScrollToNode(hasText("Page 1 · Completed"))
        compose.onNodeWithText("Page 1 · Completed").assertExists()
        compose.onNodeWithTag("book-page-complete-" + first.id).assertExists()
        compose.onNodeWithTag("book-bookmark-" + first.id).assertDoesNotExist()
    }

    @Test fun fullBookCompletionShowsSealAndNoBookmarkActionOrFakeNextPage() {
        val saved = BookLibraryProgress(mapOf(book.id to BookVolumeProgress(
            bookId = book.id,
            completedPageIds = setOf(first.id, second.id, third.id)
        )))
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp).height(700.dp)) {
                    PuzzleBookDetailScreen(
                        catalog = catalog, progress = saved, bookId = book.id,
                        onBack = {},
                        onPlayPage = { _, _ -> },
                        onBookmarkPage = { _, _ -> error("No completed bookmarks") },
                        showPrototypeArt = true
                    )
                }
            }
        }
        compose.onNodeWithTag("book-volume-complete-" + book.id).assertExists()
        compose.onNodeWithText("Book complete · You can revisit any page.").assertExists()
        compose.onNodeWithText("Continue reading").assertDoesNotExist()
        compose.onNodeWithTag("book-detail-list")
            .performScrollToNode(hasText("Page 3 · Completed"))
        compose.onNodeWithText("Page 3 · Completed").assertExists()
        compose.onNodeWithText("Replay page 3").assertExists()
        compose.onNodeWithText("Bookmark page 3").assertDoesNotExist()
        compose.onNodeWithTag("book-page-complete-" + third.id).assertExists()
    }
}
