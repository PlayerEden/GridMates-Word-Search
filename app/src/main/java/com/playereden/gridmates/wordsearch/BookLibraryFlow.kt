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
import com.playereden.gridmates.wordsearch.core.BookProgressRules

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
    onExit: () -> Unit,
    showPrototypeArt: Boolean = false,
    /** Home may send an existing catalog book straight into the next safe page. */
    initialBookId: String? = null
) {
    val initialBook = initialBookId?.takeIf { catalog.book(it) != null }
    val directFromHome = initialBook != null
    // Reuse published book and local progress validation; a fully solved book
    // enters details for replay/page choice instead of silently resetting.
    val initialPage = remember(catalog, initialBook, progressStore) {
        initialBook?.takeUnless { progressStore.blocked || sessionStore.blocked }
            ?.let { book ->
                runCatching {
                    BookProgressRules.continuePage(catalog, progressStore.progress, book)
                }.getOrNull()?.id
            }
    }
    var bookId by remember(catalog, initialBook) { mutableStateOf(initialBook) }
    var pageId by remember(catalog, initialBook) { mutableStateOf(initialPage) }
    var update by remember(catalog) { mutableIntStateOf(0) }
    var feedback by remember(catalog) { mutableStateOf<String?>(null) }
    // QA-only in-memory style choice: does NOT affect book progress or saves.
    // The owner's current preference is the more detailed Study B, while
    // keeping Study A available as an immediate, lossless visual rollback.
    var fieldNotesArt by remember(catalog) { mutableStateOf(showPrototypeArt) }
    // WS-16: Home is the swipe carousel; Full Shelf defaults to the
    // multi-book physical bookcase instead of duplicating the Home showcase.
    // Preview options still allow the older swipe view for QA comparison.
    var horizontalLibrary by remember(catalog) { mutableStateOf(false) }
    var focusedBookId by remember(catalog) {
        mutableStateOf(catalog.books.firstOrNull()?.id)
    }

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
                showPrototypeArt = showPrototypeArt,
                fieldNotesArt = fieldNotesArt,
                onBack = {
                    if (directFromHome) onExit()
                    else {
                        pageId = null
                        update++
                    }
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
            BackHandler {
                if (directFromHome) onExit() else bookId = null
            }
            val currentBook = requireNotNull(bookId)
            Column(Modifier.fillMaxSize()) {
                if (feedback != null) {
                    Text(feedback.orEmpty(), modifier = Modifier.padding(12.dp))
                }
                PuzzleBookDetailScreen(
                    catalog = catalog,
                    progress = snapshot,
                    bookId = currentBook,
                    onBack = {
                        if (directFromHome) onExit()
                        else { bookId = null; feedback = null }
                    },
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
                    modifier = Modifier.weight(1f),
                    showPrototypeArt = showPrototypeArt,
                    fieldNotesArt = fieldNotesArt
                )
            }
        }
        else -> {
            BackHandler(onBack = onExit)
            PuzzleBookshelfScreen(
                catalog = catalog,
                progress = snapshot,
                showPrototypeArt = showPrototypeArt,
                fieldNotesArt = fieldNotesArt,
                onToggleArt = if (showPrototypeArt) {
                    { fieldNotesArt = !fieldNotesArt }
                } else null,
                showHorizontalBrowser = horizontalLibrary,
                onToggleLayout = if (showPrototypeArt) {
                    { horizontalLibrary = !horizontalLibrary }
                } else null,
                initialFocusedBookId = focusedBookId,
                onFocusedBookChanged = { focusedBookId = it },
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
