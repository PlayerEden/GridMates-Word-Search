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
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.fillMaxSize().testTag("real-bookcase-screen")) {
        val fontScale = LocalDensity.current.fontScale
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Back to Play")
                    }
                    PaperEyebrow("GRIDMATES · PUZZLE LIBRARY")
                    Text("Puzzle Books", color = Forest,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Your bookshelf · Choose a book to open.", color = Muted,
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Prototype artwork · final illustrations can change",
                        color = Muted, style = MaterialTheme.typography.labelSmall)
                    Text(if (fieldNotesArt) "Illustrated study B · Field Notes"
                         else "Original study A · Quiet Paperbacks",
                         color = Forest, style = MaterialTheme.typography.labelMedium)
                    if (onToggleArt != null) {
                        OutlinedButton(onClick = onToggleArt,
                            modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(if (fieldNotesArt) "Show original art (A)"
                                 else "Try more illustrated art (B)")
                        }
                    }
                }
            }
            itemsIndexed(rows, key = { _, row -> "shelf:" + row.first().id }) { index, row ->
                BookcaseShelfRow(index, row, columns, catalog, progress,
                    onOpenBook, fieldNotesArt)
            }
            item(key = "bookshelf-footer") {
                Text(
                    catalog.books.size.toString() +
                        (if (catalog.books.size == 1) " book" else " books") +
                        " on your shelf",
                    style = MaterialTheme.typography.labelMedium, color = Muted,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom) {
            books.forEach { book ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                    BookStandingArt(book, fieldNotesArt) { onOpenBook(book.id) }
                }
            }
            repeat(columns - books.size) { Box(Modifier.weight(1f)) }
        }
        WoodenShelfPlank(shelfIndex)
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
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom) {
            BookArt(book.theme, BookArtKind.COVER,
                Modifier.width(108.dp).height(148.dp).testTag("book-cover-" + book.id),
                fieldNotes = fieldNotesArt)
            BookArt(book.theme, BookArtKind.SPINE,
                Modifier.width(13.dp).height(130.dp).testTag("book-spine-" + book.id),
                fieldNotes = fieldNotesArt)
        }
    }
}

/** Restrained, native-vector woodgrain and a subtle lip for physical shelf depth. */
@Composable
private fun WoodenShelfPlank(index: Int) {
    Canvas(Modifier.fillMaxWidth().height(23.dp).testTag("wooden-shelf-plank-$index")) {
        val faceHeight = size.height * 0.77f
        val radius = 3.dp.toPx()
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(
                Color(0xFFE8D5B1), Color(0xFFB99B75), Color(0xFF927450)
            )),
            cornerRadius = CornerRadius(radius, radius),
            size = Size(size.width, faceHeight)
        )
        drawLine(Color(0xFFF7E9CB),
            Offset(3.dp.toPx(), 3.dp.toPx()),
            Offset(size.width - 3.dp.toPx(), 3.dp.toPx()),
            strokeWidth = 1.5.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(
                Color(0xFF927450), Color(0xFF6F573E)
            )),
            topLeft = Offset(0f, faceHeight),
            size = Size(size.width, size.height - faceHeight),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawLine(Color(0xFF806748).copy(alpha = 0.37f),
            Offset(size.width * 0.18f, size.height * 0.44f),
            Offset(size.width * 0.39f, size.height * 0.44f),
            strokeWidth = 0.8.dp.toPx())
        drawLine(Color(0xFF806748).copy(alpha = 0.28f),
            Offset(size.width * 0.67f, size.height * 0.53f),
            Offset(size.width * 0.83f, size.height * 0.53f),
            strokeWidth = 0.8.dp.toPx())
    }
}
