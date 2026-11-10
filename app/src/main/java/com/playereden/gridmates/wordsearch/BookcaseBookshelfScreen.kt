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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS-13A. Actual book objects standing on wooden shelves, rather than cards.
 * Rows stay lazy for a growing catalog. No invented empty/locked books.
 * Opt-in QA-only until content and artwork have been approved.
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
        var previewToolsOpen by remember { mutableStateOf(false) }
        val columns = when {
            maxWidth < 310.dp || fontScale >= 1.65f -> 1
            maxWidth >= 700.dp && fontScale <= 1.25f -> 3
            else -> 2
        }
        val rows = catalog.books.chunked(columns)

        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("bookcase-lazy-shelves"),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
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
            itemsIndexed(rows, key = { _, row -> "shelf:" + row.first().id }) { index, row ->
                BookcaseShelfRow(index, row, columns, catalog, progress,
                    onOpenBook, fieldNotesArt)
            }
            item(key = "bookshelf-footer") {
                Text(
                    catalog.books.size.toString() +
                        (if (catalog.books.size == 1) " book" else " books") +
                        " in your library",
                    style = MaterialTheme.typography.labelMedium, color = Muted,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
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

@Composable
private fun BookcaseShelfRow(
    shelfIndex: Int,
    books: List<PuzzleBook>,
    columns: Int,
    catalog: BookCatalog,
    progress: BookLibraryProgress,
    onOpenBook: (String) -> Unit,
    fieldNotesArt: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth().testTag("bookcase-shelf-$shelfIndex"),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text("SHELF " + (shelfIndex + 1),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold, color = Muted,
            modifier = Modifier.padding(start = 7.dp, bottom = 4.dp))
        // WS-16F: one real bookcase segment per catalog row. The warm
        // backboard and uprights stay behind the interactive book objects.
        Box(Modifier.fillMaxWidth().height(184.dp)) {
            Canvas(Modifier.matchParentSize()
                .testTag("ws16f-bookcase-backboard-$shelfIndex")) {
                val u = 1.dp.toPx()
                val w = size.width
                val h = size.height
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(
                        Color(0xFFEEDFBF), Color(0xFFE7D2AC),
                        Color(0xFFDAC093))),
                    topLeft = Offset(5*u,0f),
                    size = Size(w-10*u,h-8*u),
                    cornerRadius = CornerRadius(5*u),
                    alpha = .65f
                )
                val upright = Brush.verticalGradient(listOf(
                    Color(0xFFCE8B48), Color(0xFF975A2D),
                    Color(0xFF683C22)))
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
            // Same reusable carved shelf as Home, composited behind the
            // covers rather than consuming an extra isolated vertical row.
            WoodenShelfPlank(shelfIndex, Modifier.align(Alignment.BottomCenter))
            Row(Modifier.fillMaxWidth().align(Alignment.TopCenter)
                    .padding(horizontal = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom) {
                books.forEach { book ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                        BookStandingArt(book, fieldNotesArt) { onOpenBook(book.id) }
                    }
                }
                repeat(columns - books.size) { Box(Modifier.weight(1f)) }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top) {
            books.forEach { book ->
                val completed = BookProgressRules.completedCount(catalog, progress, book.id)
                val total = catalog.pages(book.id).size
                Column(
                    modifier = Modifier.weight(1f).testTag("shelf-label-" + book.id),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(book.title.removeSuffix(" · TEST BOOK"),
                        color = Forest, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center, maxLines = 2)
                    if (book.title.endsWith(" · TEST BOOK")) {
                        Text("TEST BOOK · " + book.chapters.size +
                                (if (book.chapters.size == 1) " chapter" else " chapters"),
                            style = MaterialTheme.typography.labelSmall,
                            color = Muted, textAlign = TextAlign.Center)
                    }
                    Text("$completed of $total pages completed", color = Ink,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center)
                    TextButton(onClick = { onOpenBook(book.id) },
                        modifier = Modifier.heightIn(min = 48.dp)
                            .testTag("shelf-open-" + book.id)) {
                        Text(if (completed == total) "Read again"
                             else if (completed > 0) "Continue book" else "Open book")
                    }
                }
            }
            repeat(columns - books.size) { Box(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun BookStandingArt(
    book: PuzzleBook,
    fieldNotesArt: Boolean,
    onOpen: () -> Unit
) {
    Box(
        modifier = Modifier.height(164.dp)
            .semantics { contentDescription = "Open " + book.title.removeSuffix(" · TEST BOOK") }
            .clickable(role = Role.Button, onClick = onOpen)
            .testTag("shelf-cover-select-" + book.id),
        contentAlignment = Alignment.BottomCenter
    ) {
        // The illustrated cover bitmap already includes the book's left spine.
        // Drawing an extra separate spine beside it makes a misleading second
        // miniature book. Keep the real clickable cover and its semantics.
        BookArt(book.theme, BookArtKind.COVER,
            Modifier.width(108.dp).height(148.dp).testTag("book-cover-" + book.id),
            fieldNotes = fieldNotesArt)
    }
}

/** Every real Library shelf uses the same modular wood art as Home. */
@Composable
private fun WoodenShelfPlank(index: Int, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().testTag("wooden-shelf-plank-$index")) {
        Ws16EStorybookShelf()
    }
}
