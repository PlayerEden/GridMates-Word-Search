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
import androidx.compose.foundation.lazy.LazyColumn
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
        val slotsPerShelf = ((maxWidth - 38.dp).value / slotWidth.value)
            .toInt().coerceIn(1, 16)
        val actualRows = remember(catalog, slotsPerShelf) {
            catalog.books.chunked(slotsPerShelf)
        }
        val shelfCount = maxOf(2, actualRows.size)
        var selectedBookId by rememberSaveable { mutableStateOf<String?>(null) }
        var previewToolsOpen by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("bookcase-lazy-shelves"),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            item(key = "bookshelf-title") {
                Ws15BackHeader(
                    title = "Your Library",
                    subtitle = "Browse your puzzle books",
                    onBack = onBack,
                    backLabel = "Back to Play",
                    onPreviewOptions = if (onToggleLayout != null || onToggleArt != null) {
                        { previewToolsOpen = true }
                    } else null
                )
            }
            item(key = "shelf-instruction") {
                Text("Tap a spine to pull out a book.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    modifier = Modifier.padding(start = 5.dp, bottom = 2.dp))
            }
            items(shelfCount, key = { "shelf:" + it }) { index ->
                SpineOutShelfRow(index, actualRows.getOrNull(index).orEmpty(),
                    fieldNotesArt, slotWidth) { selectedBookId = it }
            }
            item(key = "bookshelf-footer") {
                Text(catalog.books.size.toString() +
                    (if (catalog.books.size == 1) " book" else " books") +
                    " in your library",
                    style = MaterialTheme.typography.labelMedium,
                    color = Muted,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center)
            }
        }

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

/** Quiet native wood fallback + the exact source-rendered modular QA shelf. */
@Composable
private fun SpineOutShelfRow(
    index: Int, books: List<PuzzleBook>,
    fieldNotesArt: Boolean, slotWidth: Dp, onSelect: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().testTag("bookcase-shelf-" + index)) {
        Box(Modifier.fillMaxWidth().height(198.dp)) {
            Canvas(Modifier.matchParentSize()
                .testTag("ws16f-bookcase-backboard-" + index)) {
                val u = 1.dp.toPx()
                val w = size.width
                val h = size.height
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(
                        Color(0xFFEEDFBF), Color(0xFFE7D2AC), Color(0xFFDAC093))),
                    topLeft = Offset(5*u, 0f),
                    size = Size(w-10*u, h-8*u),
                    cornerRadius = CornerRadius(5*u), alpha = .65f)
                val upright = Brush.verticalGradient(listOf(
                    Color(0xFFCE8B48), Color(0xFF975A2D), Color(0xFF683C22)))
                drawRoundRect(upright, Offset(2*u,1*u),
                    Size(9*u,h-10*u),CornerRadius(2*u))
                drawRoundRect(upright, Offset(w-11*u,1*u),
                    Size(9*u,h-10*u),CornerRadius(2*u))
                drawLine(Color(0xFFEFC38A).copy(alpha=.77f),
                    Offset(7*u,8*u),Offset(7*u,h-18*u),.8f*u)
                drawLine(Color(0xFFEFC38A).copy(alpha=.77f),
                    Offset(w-7*u,8*u),Offset(w-7*u,h-18*u),.8f*u)
            }
            Ws16GBookcaseBackboard(Modifier.matchParentSize())
            WoodenShelfPlank(index, Modifier.align(Alignment.BottomCenter))
            Row(Modifier.align(Alignment.BottomStart)
                    .padding(start = 15.dp, end = 15.dp, bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.Bottom) {
                books.forEach { book ->
                    // Small controlled height classes; uniformly scale the actual spine.
                    // Keep the independent 48dp/54dp touch target regardless of artwork.
                    val printedTitle = book.title.removeSuffix(" · TEST BOOK")
                    val letters = printedTitle.substringBefore(" ").count { it.isLetter() }
                    val tall = letters in 8..11
                    val spineHeight = if (tall) 160.dp else 154.dp
                    val spineWidth = if (tall) 32.dp else 31.dp
                    Box(Modifier.width(slotWidth).height(170.dp)
                            .semantics {
                                contentDescription = "Pull out " +
                                    book.title.removeSuffix(" · TEST BOOK")
                            }
                            .clickable(role = Role.Button) { onSelect(book.id) }
                            .testTag("library-spine-" + book.id),
                        contentAlignment = Alignment.BottomCenter) {
                        // The binding artwork is a real existing spine,
                        // not a duplicate cover or invented future volume.
                        BookArt(book.theme, BookArtKind.SPINE,
                            Modifier.width(spineWidth).height(spineHeight)
                                .testTag("book-spine-" + book.id),
                            fieldNotes = fieldNotesArt)
                    }
                }

                // QA-only physical filler books: no PuzzleBook, no ID, no tap,
                // no fake chapter count or progress. Each is a distinct source PNG.
                if (fieldNotesArt && books.isEmpty() && index == 1) {
                    listOf("linen", "night", "burgundy").forEach { style ->
                        Box(Modifier.width(slotWidth).height(170.dp)
                                .semantics {
                                    contentDescription = "Upcoming puzzle book, coming soon"
                                }
                                .testTag("library-future-volume-$style"),
                            contentAlignment = Alignment.BottomCenter) {
                            BookFactoryBlankSpine(style,
                                Modifier.width(31.dp).height(154.dp))
                        }
                    }
                }
            }
            if (fieldNotesArt && books.isEmpty() && index == 1) {
                Text("More books coming soon",
                    modifier = Modifier.align(Alignment.TopCenter)
                        .padding(top = 9.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFE9C0))
            }
        }
    }
}

@Composable
private fun WoodenShelfPlank(index: Int, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().testTag("wooden-shelf-plank-" + index)) {
        Ws16EStorybookShelf()
    }
}
