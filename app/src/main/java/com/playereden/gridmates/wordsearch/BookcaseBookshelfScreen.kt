package com.playereden.gridmates.wordsearch

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS-16H1: A real, expandable spine-out bookcase instead of another cover gallery.
 * No synthetic books or fake locked volume placeholders. Additional rows appear
 * only as the real catalog grows; one additional empty shelf is physical scenery.
 * Selection previews the existing cover and then routes through real book details.
 */
@Composable
internal fun BookcaseBookshelfScreen(
    catalog: BookCatalog,
    progress: BookLibraryProgress,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    fieldNotesArt: Boolean,
    onToggleArt: (() -> Unit)?,
    onToggleLayout: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.fillMaxSize().testTag("real-bookcase-screen")) {
        val fontScale = LocalDensity.current.fontScale
        val slotWidth = if (fontScale >= 1.65f) 54.dp else 48.dp
        val slotsPerShelf = ((maxWidth - 46.dp).value / slotWidth.value)
            .toInt().coerceIn(1, 16)
        val capacity = slotsPerShelf * 2
        val pageCount = maxOf(1, (catalog.books.size + capacity - 1) / capacity)
        val pager = rememberPagerState(pageCount = { pageCount })
        val scope = rememberCoroutineScope()
        var selectedBookId by rememberSaveable { mutableStateOf<String?>(null) }
        var previewToolsOpen by remember { mutableStateOf(false) }
        // QA-only empty-background inspection; regular books are one tap away.
        var emptyCabinetPreview by rememberSaveable { mutableStateOf(false) }
        // Keep the working WS16AI bookcase as a manually selectable QA rollback.
        var immersiveBooksPreview by rememberSaveable { mutableStateOf(true) }
        var modularPreview by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        // A new art path is never selectable until all five real shell images
        // are packaged. No placeholder or flattened proof art is substituted.
        val modularAssetsAvailable = remember(context) {
            hasWs16atCabinetArt(context)
        }

        if (fieldNotesArt && modularPreview && modularAssetsAvailable) {
            WS16ATModularLibraryScene(
                books = catalog.books,
                onBack = onBack,
                onBookSelected = { selectedBookId = it },
                onPreviousArt = { modularPreview = false },
                modifier = Modifier.fillMaxSize()
            )
        } else if (fieldNotesArt && immersiveBooksPreview) {
            WS16AMInteractiveBookcaseScene(
                books = catalog.books,
                onBack = onBack,
                onBookSelected = { selectedBookId = it },
                onInspectEmpty = {
                    immersiveBooksPreview = false
                    emptyCabinetPreview = true
                },
                onOldCase = {
                    immersiveBooksPreview = false
                    emptyCabinetPreview = false
                },
                onModularCase = if (modularAssetsAvailable) {
                    { modularPreview = true }
                } else null,
                modifier = Modifier.fillMaxSize()
            )
        } else if (fieldNotesArt && emptyCabinetPreview) {
            WS16ALImmersiveEmptyLibraryScene(
                bookCount = catalog.books.size,
                onBack = onBack,
                onShowBooks = {
                    immersiveBooksPreview = true
                    emptyCabinetPreview = false
                },
                onPreviewOptions = if (onToggleLayout != null || onToggleArt != null) {
                    { previewToolsOpen = true }
                } else null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
        Column(Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 7.dp)) {
            Ws15BackHeader(
                title = "Your Library",
                subtitle = "Browse your puzzle books",
                onBack = onBack,
                backLabel = "Back to Play",
                onPreviewOptions = if (onToggleLayout != null || onToggleArt != null) {
                    { previewToolsOpen = true }
                } else null
            )
            if (fieldNotesArt) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(if (emptyCabinetPreview) "Inspect the empty bookcase artwork"
                        else "Tap a spine to pull out a book.",
                        style = MaterialTheme.typography.bodyMedium, color = Muted,
                        modifier = Modifier.weight(1f).padding(start = 12.dp))
                    TextButton(
                        onClick = { emptyCabinetPreview = !emptyCabinetPreview },
                        modifier = Modifier.heightIn(min = 48.dp)
                            .testTag("bookcase-empty-background-preview")
                    ) { Text(if (emptyCabinetPreview) "Show books" else "Empty art") }
                    TextButton(
                        onClick = {
                            immersiveBooksPreview = true
                            emptyCabinetPreview = false
                        },
                        modifier = Modifier.testTag("ws16am-return-to-immersive")
                    ) { Text("New case") }
                }
            } else {
                Text("Tap a spine to pull out a book.",
                    style = MaterialTheme.typography.bodyMedium, color = Muted,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp))
            }
            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .testTag("bookcase-lazy-shelves")
            ) { page ->
                val first = page * capacity
                val pageBooks = catalog.books.drop(first).take(capacity)
                BoxWithConstraints(Modifier.fillMaxSize()
                    .testTag("bookcase-page-$page")) {
                    if (fieldNotesArt && emptyCabinetPreview) {
                        // WS16AK: six source-exact wood modules, no baked book art or
                        // clickable catalog objects in this controlled visual study.
                        WS16AKEmptyBookcaseBackground(Modifier.fillMaxSize())
                    } else {
                    BookcaseWoodParts(Modifier.fillMaxSize(), fieldNotesArt)
                    val tierHeight = (maxHeight - 65.dp).coerceAtLeast(100.dp) / 2
                    Column(Modifier.fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp, top = 43.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween) {
                        SpineOutShelfRow(page * 2, pageBooks.take(slotsPerShelf),
                            slotsPerShelf, slotWidth, tierHeight, fieldNotesArt,
                            onSelect = { selectedBookId = it })
                        SpineOutShelfRow(page * 2 + 1, pageBooks.drop(slotsPerShelf),
                            slotsPerShelf, slotWidth, tierHeight, fieldNotesArt,
                            onSelect = { selectedBookId = it })
                    }
                    }
                }
            }
            if (pageCount > 1) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    TextButton(enabled = pager.currentPage > 0,
                        onClick = { scope.launch {
                            pager.animateScrollToPage(pager.currentPage - 1)
                        } }, modifier = Modifier.testTag("bookcase-previous-page")) {
                        Text("Previous")
                    }
                    Text("${pager.currentPage + 1} of $pageCount",
                        style = MaterialTheme.typography.labelMedium, color = Forest,
                        modifier = Modifier.padding(horizontal = 12.dp)
                            .testTag("bookcase-page-count"))
                    TextButton(enabled = pager.currentPage < pageCount - 1,
                        onClick = { scope.launch {
                            pager.animateScrollToPage(pager.currentPage + 1)
                        } }, modifier = Modifier.testTag("bookcase-next-page")) {
                        Text("Next")
                    }
                }
            }
            Text("${catalog.books.size}" +
                    (if (catalog.books.size == 1) " book" else " books") +
                    " in your library",
                style = MaterialTheme.typography.labelMedium, color = Muted,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                textAlign = TextAlign.Center)
        }
        } // end WS16AL QA scene vs existing interactive bookcase

        val chosen = selectedBookId?.let { catalog.book(it) }
        if (chosen != null) {
            val completed = BookProgressRules.completedCount(catalog, progress, chosen.id)
            val total = catalog.pages(chosen.id).size
            AlertDialog(
                onDismissRequest = { selectedBookId = null },
                modifier = Modifier.testTag("library-pulled-book-dialog"),
                title = {
                    Text(chosen.title.removeSuffix(" · TEST BOOK"),
                        color = Forest, style = MaterialTheme.typography.titleLarge)
                },
                text = {
                    Column(Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        BookArt(chosen.theme, BookArtKind.COVER,
                            Modifier.width(144.dp).height(192.dp)
                                .testTag("library-pulled-cover-" + chosen.id),
                            fieldNotes = fieldNotesArt)
                        Text(chosen.chapters.size.toString() +
                            (if (chosen.chapters.size == 1) " chapter" else " chapters") +
                            " · " + completed + " of " + total + " pages completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink, textAlign = TextAlign.Center)
                        if (chosen.title.endsWith(" · TEST BOOK")) {
                            Text("TEST BOOK", style = MaterialTheme.typography.labelSmall,
                                color = Muted)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val id = chosen.id
                        selectedBookId = null
                        onOpenBook(id)
                    }, modifier = Modifier.heightIn(min = 48.dp)
                        .testTag("library-view-pages")) {
                        Text("View pages")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedBookId = null },
                        modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Back to shelf")
                    }
                }
            )
        }
        if (previewToolsOpen) {
            Ws15LibraryPreviewDialog(
                fieldNotesArt = fieldNotesArt,
                onToggleArt = onToggleArt,
                onToggleLayout = onToggleLayout,
                onDismiss = { previewToolsOpen = false },
                layoutActionLabel = "Try swipe library"
            )
        }
    }
}

/** Two complete physical shelf tiers inside each modular cabinet page. */
@Composable
private fun SpineOutShelfRow(
    index: Int, books: List<PuzzleBook>, slotsPerRow: Int,
    slotWidth: Dp, tierHeight: Dp, fieldNotesArt: Boolean,
    onSelect: (String) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(tierHeight)
        .testTag("bookcase-shelf-$index")) {
        // Tightly cropped 104x542 original spine sources: visible wood bindings
        // nearly fill each 48dp hit zone without nonuniform image distortion.
        val spineHeight = (maxHeight - 49.dp).coerceIn(75.dp, 235.dp)
        val spineWidth = (spineHeight * (104f / 542f))
            .coerceAtMost(slotWidth - 2.dp)
        val rowInset = ((maxWidth - (slotWidth * slotsPerRow)) / 2)
            .coerceAtLeast(0.dp)
        Row(Modifier.align(Alignment.BottomStart)
            .padding(start = rowInset, bottom = 35.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.Bottom) {
            for (slot in 0 until slotsPerRow) {
                val book = books.getOrNull(slot)
                if (book != null) {
                    Box(Modifier.width(slotWidth).height(spineHeight + 4.dp)
                        .semantics { contentDescription = "Pull out " +
                            book.title.removeSuffix(" · TEST BOOK") }
                        .clickable(role = Role.Button) { onSelect(book.id) }
                        .testTag("library-spine-" + book.id),
                        contentAlignment = Alignment.BottomCenter) {
                        BookcaseSpineArt(book.theme, fieldNotesArt,
                            Modifier.width(spineWidth).height(spineHeight)
                                .testTag("book-spine-" + book.id))
                    }
                } else {
                    val style = listOf("linen", "night", "burgundy")[
                        (index * slotsPerRow + slot) % 3]
                    Box(Modifier.width(slotWidth).height(spineHeight + 4.dp)
                        .semantics { contentDescription = "Future volume, coming soon" }
                        .testTag("library-future-volume-$index-$slot"),
                        contentAlignment = Alignment.BottomCenter) {
                        if (fieldNotesArt) {
                            BookcaseFutureSpineArt(style,
                                Modifier.width(spineWidth).height(spineHeight))
                        }
                    }
                }
            }
        }
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .testTag("wooden-shelf-plank-$index")) {
            Ws16EStorybookShelf()
        }
    }
}
