package com.playereden.gridmates.wordsearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog

/**
 * WS-11D2B: actual internal route from Library -> Book -> Page -> Book.
 *
 * The public Home screen does not instantiate this route before editorially
 * approved pages, source hashes, rights and device QA pass. The same host may
 * be exercised with synthetic pages in Android instrumentation tests.
 *
 * No duplicate book data, navigation libraries or hidden SoloStore routing.
 */
@Composable
internal fun BookLibraryFlow(
    catalog: BookCatalog,
    progressStore: BookLibraryStore,
    sessionStore: BookPageSessionStore,
    boardFontId: String,
    onExit: () -> Unit
) {
    var bookId by remember(catalog) { mutableStateOf<String?>(null) }
    var pageId by remember(catalog) { mutableStateOf<String?>(null) }
    var update by remember(catalog) { mutableIntStateOf(0) }
    var feedback by remember(catalog) { mutableStateOf<String?>(null) }

    val unavailable = progressStore.blocked || sessionStore.blocked
    val snapshot = remember(update, pageId, bookId) { progressStore.progress }

    if (unavailable) {
        BackHandler(onBack = onExit)
        Column(Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = onExit) { Text("Back to Play") }
            PaperEyebrow("Puzzle Books")
            Text(progressStore.warning ?: sessionStore.warning
                ?: "Book progress cannot be opened safely. Original files were preserved.")
        }
        return
    }

    when {
        pageId != null && bookId != null -> {
            val activeBook = requireNotNull(bookId)
            val activePage = requireNotNull(pageId)
            BookPageGameplayScreen(
                catalog = catalog,
                bookId = activeBook,
                pageId = activePage,
                sessionStore = sessionStore,
                progressStore = progressStore,
                boardFontId = boardFontId,
                onBack = {
                    pageId = null
                    update++
                },
                onNextPage = { nextBook, nextPage ->
                    if (nextBook == activeBook &&
                        catalog.page(nextBook, nextPage) != null) {
                        pageId = nextPage
                        update++
                    }
                }
            )
        }
        bookId != null -> {
            BackHandler { bookId = null }
            val currentBook = requireNotNull(bookId)
            Column(Modifier.fillMaxSize()) {
                if (feedback != null) {
                    Text(feedback.orEmpty(), modifier = Modifier.padding(12.dp))
                }
                PuzzleBookDetailScreen(
                    catalog = catalog,
                    progress = snapshot,
                    bookId = currentBook,
                    onBack = { bookId = null; feedback = null },
                    onPlayPage = { selectedBook, selectedPage ->
                        if (selectedBook == currentBook &&
                            catalog.page(selectedBook, selectedPage) != null) {
                            pageId = selectedPage
                            feedback = null
                        }
                    },
                    onBookmarkPage = { selectedBook, selectedPage ->
                        if (selectedBook == currentBook &&
                            progressStore.bookmark(selectedBook, selectedPage)) {
                            update++
                            feedback = "Bookmark saved for this book."
                        } else {
                            feedback = progressStore.warning
                                ?: "The bookmark couldn't be saved. Your last progress was kept."
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        else -> {
            BackHandler(onBack = onExit)
            PuzzleBookshelfScreen(
                catalog = catalog,
                progress = snapshot,
                onBack = onExit,
                onOpenBook = { selectedBook ->
                    if (catalog.book(selectedBook) != null) {
                        bookId = selectedBook
                        feedback = null
                    }
                }
            )
        }
    }
}
