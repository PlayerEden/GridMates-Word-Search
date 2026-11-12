package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS16BI: real bindings FIRST in catalog order, then a few explicitly
 * decorative, totally unlettered/unclickable future volumes.
 *
 * Visual width and each 48dp minimum hit cell are separate. No overlapping
 * hit targets between two neighboring real books; large catalogs paginate.
 * Single rounded bindings have an ambient shadow projected onto backboard
 * plus a tight shelf-contact shadow, never another fake book backing.
 */
@Composable
internal fun WS16BHDenseSpineBay(
    books: List<PuzzleBook>,
    shelfIndex: Int,
    slot: Dp,
    height: Dp,
    onBookSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSlots: Int = 12,
    showDecorativeBlanks: Boolean = false
) {
    BoxWithConstraints(
        modifier.fillMaxWidth().height(height)
            .testTag("ws16bh-packed-shelf-$shelfIndex")
    ) {
        val bindingRatio = 104f / 542f
        val bindingWidth = minOf(
            slot - 3.dp,
            (maxHeight - 4.dp).coerceAtLeast(24.dp) * bindingRatio
        ).coerceAtLeast(12.dp)
        val bindingHeight = bindingWidth / bindingRatio
        // Environment decoration must not consume book IDs, capacity or taps.
        val blanks = if (showDecorativeBlanks) {
            (maxSlots - books.size).coerceAtLeast(0)
                .coerceAtMost(if (books.isEmpty()) 4 else 2)
        } else 0
        val displayCount = books.size + blanks
        if (displayCount > 0) {
            Row(
                Modifier.align(Alignment.BottomCenter)
                    .width(slot * displayCount)
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
                        // Soft cast on the backboard, clipped *behind* the real
                        // rounded book; no hard-edged duplicate leather panel.
                        Box(
                            Modifier.align(Alignment.BottomCenter)
                                .offset(x = 2.dp, y = (-4).dp)
                                .width(bindingWidth * .91f)
                                .height((bindingHeight - 7.dp).coerceAtLeast(12.dp))
                                .blur(5.dp)
                                .background(
                                    Color(0x72130904),
                                    RoundedCornerShape(8.dp)
                                )
                                .testTag("ws16bi-real-shadow-" + book.id)
                        )
                        Box(
                            Modifier.align(Alignment.BottomCenter)
                                .width(bindingWidth * 0.86f).height(3.dp)
                                .background(
                                    Color(0xA0110704),
                                    RoundedCornerShape(50)
                                )
                        )
                        BookcaseSpineArt(
                            theme = book.theme,
                            fieldNotesArt = true,
                            modifier = Modifier.width(bindingWidth)
                                .height(bindingHeight)
                                .graphicsLayer { scaleX = 1.07f }
                                .testTag("ws16bh-art-" + book.id),
                            profile = "curved"
                        )
                    }
                }
                repeat(blanks) { i ->
                    Box(
                        Modifier.width(slot).height(bindingHeight + 4.dp)
                            .testTag("ws16bi-blank-cell-$shelfIndex-$i"),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            Modifier.align(Alignment.BottomCenter)
                                .width(bindingWidth * .8f).height(3.dp)
                                .background(
                                    Color(0x70110805), RoundedCornerShape(50)
                                )
                        )
                        WS16BIUnletteredBlankSpine(
                            index = shelfIndex * 5 + i,
                            modifier = Modifier.width(bindingWidth)
                                .height(bindingHeight)
                        )
                    }
                }
            }
        }
    }
}
