package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS16BH: scalable, tightly packed real book bindings for the physical Library.
 *
 * Each visible binding is a real PuzzleBook with a stable click ID, not a
 * decorative placeholder. Existing WS16BF 104x542 single-rounded spine art
 * is reused without new panels, scaling tricks or image-generation changes.
 * Covers remain reserved for the existing pull-out/book preview.
 */
@Composable
internal fun WS16BHDenseSpineBay(
    books: List<PuzzleBook>,
    shelfIndex: Int,
    slot: Dp,
    height: Dp,
    onBookSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier.fillMaxWidth().height(height)
            .testTag("ws16bh-packed-shelf-$shelfIndex")
    ) {
        val bindingRatio = 104f / 542f
        val bindingWidth = minOf(
            slot - 2.dp,
            (maxHeight - 12.dp).coerceAtLeast(24.dp) * bindingRatio
        ).coerceAtLeast(12.dp)
        val bindingHeight = bindingWidth / bindingRatio
        if (books.isNotEmpty()) {
            // Small collections make a tidy group centered on the shelf.
            // With many books, the same group grows continuously, edge-to-edge.
            Row(
                Modifier.align(Alignment.BottomCenter)
                    .width(slot * books.size)
                    .padding(bottom = 2.dp)
                    .testTag("ws16bh-real-book-row-$shelfIndex"),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                books.forEach { book ->
                    Box(
                        Modifier.width(slot).height(bindingHeight + 4.dp)
                            .semantics {
                                contentDescription = "Open " +
                                    book.title.removeSuffix(" · TEST BOOK")
                            }
                            .clickable(role = Role.Button) { onBookSelected(book.id) }
                            .testTag("ws16bh-spine-" + book.id),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // One subdued contact strip; the artwork itself already
                        // provides leather curvature. No second backing shape.
                        Box(
                            Modifier.align(Alignment.BottomCenter)
                                .width(bindingWidth * 0.78f).height(2.dp)
                                .background(
                                    Color(0x550E0906),
                                    RoundedCornerShape(50)
                                )
                        )
                        BookcaseSpineArt(
                            theme = book.theme,
                            fieldNotesArt = true,
                            modifier = Modifier.width(bindingWidth)
                                .height(bindingHeight)
                                .testTag("ws16bh-art-" + book.id),
                            profile = "curved"
                        )
                    }
                }
            }
        }
    }
}
