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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    val context = LocalContext.current
    val hasNewArtwork = remember(context, books) {
        val bundled = runCatching {
            context.assets.list("ws14/books").orEmpty().toSet()
        }.getOrDefault(emptySet())
        books.any { book ->
            ws14QaCoverPath(book.theme, BookArtKind.COVER)
                ?.substringAfterLast('/') in bundled
        }
    }

    BoxWithConstraints(modifier.fillMaxSize().testTag("ws14d-horizontal-browser")) {
        val availableWidth = maxWidth - 36.dp
        val coverPageWidth = (availableWidth * 0.59f).coerceIn(136.dp, 186.dp)
        val sidePeek = ((availableWidth - coverPageWidth) / 2).coerceAtLeast(0.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "ws14d-title") {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Back to Play")
                    }
                    StorybookHeading(
                        eyebrow = "GridMates · Puzzle Library",
                        title = "Puzzle Books",
                        subtitle = "Choose a book · Swipe or use the arrows."
                    )
                    Text(
                        if (hasNewArtwork) "Illustrated cover preview · release review pending"
                        else "Prototype artwork · final illustrations can change",
                        color = Muted, style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        if (!fieldNotesArt) "Original study A · Quiet Paperbacks"
                        else if (hasNewArtwork) "WS-14 covers · Field Notes supporting art"
                        else "Illustrated study B · Field Notes",
                        color = Forest, style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onToggleLayout != null) {
                            TextButton(onClick = onToggleLayout,
                                modifier = Modifier.heightIn(min = 48.dp)
                                    .testTag("ws14d-classic-layout")) {
                                Text("Classic shelves")
                            }
                        }
                        if (onToggleArt != null) {
                            TextButton(onClick = onToggleArt,
                                modifier = Modifier.heightIn(min = 48.dp)
                                    .testTag("ws14d-art-toggle")) {
                                Text(if (fieldNotesArt) "Original art (A)"
                                     else "Illustrated art (B)")
                            }
                        }
                    }
                }
            }

            item(key = "ws14d-books") {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    HorizontalPager(
                        state = pagerState,
                        pageSize = PageSize.Fixed(coverPageWidth),
                        contentPadding = PaddingValues(horizontal = sidePeek),
                        pageSpacing = 10.dp,
                        key = { index -> books[index].id },
                        modifier = Modifier.fillMaxWidth().height(234.dp)
                            .testTag("ws14d-cover-pager")
                    ) { page ->
                        val book = books[page]
                        val focused = pagerState.currentPage == page
                        // Native touch feedback, no continuous idle animation.
                        // Compose observes the system's animator duration scale.
                        val lift by animateFloatAsState(
                            targetValue = if (focused) 1.025f else 0.92f,
                            animationSpec = tween(durationMillis = 220),
                            label = "ws14d-book-focus"
                        )
                        Box(
                            Modifier.fillMaxSize()
                                .graphicsLayer { scaleX = lift; scaleY = lift }
                                .semantics { contentDescription = "Select " +
                                    book.title.removeSuffix(" · TEST BOOK") }
                                .clickable(role = Role.Button) {
                                    if (page != pagerState.currentPage) {
                                        scope.launch { pagerState.animateScrollToPage(page) }
                                    }
                                }
                                .testTag("ws14d-cover-" + book.id),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            BookArt(
                                theme = book.theme, kind = BookArtKind.COVER,
                                modifier = Modifier.width(coverPageWidth - 8.dp)
                                    .height(218.dp),
                                fieldNotes = fieldNotesArt
                            )
                        }
                    }
                    // Light physical shelf accent, subordinate to the actual art.
                    Canvas(
                        Modifier.fillMaxWidth().height(13.dp)
                            .testTag("ws14d-shelf-edge")
                    ) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(
                                Color(0xFFE2CFAC), Color(0xFFAE906C),
                                Color(0xFF8E7354)
                            )),
                            cornerRadius = CornerRadius(3.dp.toPx()),
                            size = Size(size.width, size.height)
                        )
                        drawLine(
                            Color(0xFFF6E9CE),
                            Offset(5.dp.toPx(), 2.dp.toPx()),
                            Offset(size.width - 5.dp.toPx(), 2.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
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
    }
}
