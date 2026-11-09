package com.playereden.gridmates.wordsearch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import kotlinx.coroutines.launch

/**
 * WS-14D QA experiment: one selected, swipeable illustrated book at a time.
 *
 * Existing immutable book IDs and read-only progress are unchanged. This view
 * only exists behind the QA book flow; it never changes production saves.
 * The cover already contains its left binding -- no detached spine is drawn.
 */
@Composable
internal fun HorizontalBookBrowserScreen(
    catalog: BookCatalog,
    progress: BookLibraryProgress,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    fieldNotesArt: Boolean,
    onToggleArt: (() -> Unit)?,
    onToggleLayout: (() -> Unit)?,
    initialFocusedBookId: String?,
    onFocusedBookChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val books = catalog.books
    if (books.isEmpty()) return // validated by PuzzleBookshelfScreen
    val initialPage = remember(catalog, initialFocusedBookId) {
        books.indexOfFirst { it.id == initialFocusedBookId }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { books.size })
    val scope = rememberCoroutineScope()
    val selectedPage = pagerState.currentPage.coerceIn(0, books.lastIndex)
    val selectedBook = books[selectedPage]
    val completed = BookProgressRules.completedCount(catalog, progress, selectedBook.id)
    val total = catalog.pages(selectedBook.id).size

    // Retain selected book in *memory* when opening a volume and returning.
    LaunchedEffect(pagerState.settledPage, books) {
        books.getOrNull(pagerState.settledPage)?.let { onFocusedBookChanged(it.id) }
    }

    var previewToolsOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier.fillMaxSize().testTag("ws14d-horizontal-browser")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "ws14d-title") {
                Ws15BackHeader(
                    title = "Puzzle Books",
                    subtitle = "Swipe to explore",
                    onBack = onBack,
                    backLabel = "Back to Play",
                    onPreviewOptions = if (onToggleLayout != null || onToggleArt != null) {
                        { previewToolsOpen = true }
                    } else null
                )
            }

            item(key = "ws14d-books") {
                Ws16BookCoverPager(
                    books = books,
                    pagerState = pagerState,
                    fieldNotesArt = fieldNotesArt,
                    onCoverTap = { page, focused ->
                        if (focused) onOpenBook(books[page].id)
                        else scope.launch { pagerState.animateScrollToPage(page) }
                    }
                )
            }

            item(key = "ws14d-navigation") {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { scope.launch {
                            pagerState.animateScrollToPage((selectedPage - 1).coerceAtLeast(0))
                        } },
                        enabled = selectedPage > 0,
                        modifier = Modifier.heightIn(min = 48.dp)
                            .semantics { contentDescription = "Previous book" }
                            .testTag("ws14d-prev")
                    ) {
                        Text("‹")
                    }
                    Text(
                        "${selectedPage + 1} of ${books.size} books",
                        color = Muted,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("ws14d-position")
                    )
                    OutlinedButton(
                        onClick = { scope.launch {
                            pagerState.animateScrollToPage(
                                (selectedPage + 1).coerceAtMost(books.lastIndex)
                            )
                        } },
                        enabled = selectedPage < books.lastIndex,
                        modifier = Modifier.heightIn(min = 48.dp)
                            .semantics { contentDescription = "Next book" }
                            .testTag("ws14d-next")
                    ) {
                        Text("›")
                    }
                }
            }

            item(key = "ws14d-selected-book") {
                Column(
                    Modifier.fillMaxWidth().testTag("ws14d-selected-detail"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        selectedBook.title.removeSuffix(" · TEST BOOK"),
                        color = Forest,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("ws14d-selected-name")
                    )
                    Text(
                        "${selectedBook.chapters.size} chapters · $completed of $total pages completed",
                        color = Muted, style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    PaperProgress(completed, total)
                    Button(
                        onClick = { onOpenBook(selectedBook.id) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                            .testTag("ws14d-open-selected")
                    ) {
                        Text(
                            if (completed == total) "Read again"
                            else if (completed > 0) "Continue book"
                            else "Open book"
                        )
                    }
                }
            }
        }
        if (previewToolsOpen) {
            Ws15LibraryPreviewDialog(
                fieldNotesArt = fieldNotesArt,
                onToggleArt = onToggleArt,
                onToggleLayout = onToggleLayout,
                onDismiss = { previewToolsOpen = false }
            )
        }
    }
}

/** Keeps QA comparison controls legible at 320dp / 200% font scale. */
@Composable
private fun LibraryReviewControls(
    stacked: Boolean,
    fieldNotesArt: Boolean,
    onToggleLayout: (() -> Unit)?,
    onToggleArt: (() -> Unit)?
) {
    val controls: @Composable () -> Unit = {
        val sizeModifier = if (stacked) Modifier.fillMaxWidth() else Modifier
        if (onToggleLayout != null) {
            TextButton(onClick = onToggleLayout,
                modifier = sizeModifier.heightIn(min = 48.dp)
                    .testTag("ws14d-classic-layout")) {
                Text("Classic shelves")
            }
        }
        if (onToggleArt != null) {
            TextButton(onClick = onToggleArt,
                modifier = sizeModifier.heightIn(min = 48.dp)
                    .testTag("ws14d-art-toggle")) {
                Text(if (fieldNotesArt) "Original art (A)" else "Illustrated art (B)")
            }
        }
    }
    if (stacked) {
        Column(Modifier.fillMaxWidth()) { controls() }
    } else {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            controls()
        }
    }
}
