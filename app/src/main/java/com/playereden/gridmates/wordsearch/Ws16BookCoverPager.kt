package com.playereden.gridmates.wordsearch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS-16P: full-width moving book pages plus contained resting mini-neighbors.
 * WS-16O introduced small neighbor volumes and accessible directional
 * arrows. The large center volume remains swipeable and playable; side
 * thumbnails are clickable and never hang outside the device viewport.
 * No changes to saved focus/book/page progress.
 */
@Composable
internal fun Ws16BookCoverPager(
    books: List<PuzzleBook>,
    pagerState: PagerState,
    fieldNotesArt: Boolean,
    onCoverTap: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (books.isEmpty()) return
    BoxWithConstraints(modifier.fillMaxWidth().testTag("ws16-shared-cover-carousel")) {
        val width = maxWidth
        // Keep the hero book exactly as large as the accepted WS-16N build.
        val coverWidth = (width * .59f).coerceIn(136.dp, 186.dp)
            .coerceAtMost((width - 80.dp).coerceAtLeast(120.dp))
        val sideWidth = (width - coverWidth) / 2
        val index = pagerState.settledPage.coerceIn(0, books.lastIndex)

        Box(Modifier.fillMaxWidth().height(252.dp)) {
            // One continuous wood rail; covers overlap its upper edge.
            Ws16EStorybookShelf(
                Modifier.align(Alignment.BottomCenter).offset(y = (-6).dp)
            )

            // The pager spans the ENTIRE carousel width so the complete
            // books pass across the available page during a drag. Previously
            // the pager was only coverWidth wide, abruptly cropping both
            // full-size pages at the middle while the minis stayed frozen.
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                key = { page -> books[page].id },
                modifier = Modifier.align(Alignment.TopCenter)
                    .fillMaxWidth().height(226.dp)
                    .testTag("ws14d-cover-pager")
            ) { page ->
                val book = books[page]
                val focused = pagerState.settledPage == page
                val lift by animateFloatAsState(
                    targetValue = if (focused) 1.025f else .92f,
                    animationSpec = tween(durationMillis = 220),
                    label = "ws16-focused-cover"
                )
                // Outer page fills the available swipe lane. Only the book
                // itself has clickable semantics, never transparent side air.
                Box(Modifier.fillMaxWidth().height(226.dp),
                    contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier.width(coverWidth).height(226.dp)
                            .graphicsLayer { scaleX = lift; scaleY = lift }
                            .semantics {
                                contentDescription =
                                    (if (focused) "Play " else "Select ") +
                                        book.title.removeSuffix(" · TEST BOOK")
                            }
                            .clickable(role = Role.Button) {
                                if (!pagerState.isScrollInProgress) {
                                    onCoverTap(page, focused)
                                }
                            }
                            .testTag("ws14d-cover-" + book.id),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        BookArt(book.theme, BookArtKind.COVER,
                            Modifier.width(coverWidth - 8.dp).height(218.dp),
                            fieldNotes = fieldNotesArt)
                    }
                }
            }

            // Fully contained mini books are REST-STATE hints, not extra
            // sliding book pages. Hide them for the few moments of a swipe,
            // so no duplicate/frozen books overlap the moving full covers.
            // Rendering AFTER the pager also keeps their tap regions active.
            if (!pagerState.isScrollInProgress && sideWidth >= 42.dp) {
                if (index > 0) {
                    Ws16NeighborBook(
                        book = books[index - 1],
                        sideWidth = sideWidth,
                        fieldNotesArt = fieldNotesArt,
                        previous = true,
                        onClick = { onCoverTap(index - 1, false) },
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                if (index < books.lastIndex) {
                    Ws16NeighborBook(
                        book = books[index + 1],
                        sideWidth = sideWidth,
                        fieldNotesArt = fieldNotesArt,
                        previous = false,
                        onClick = { onCoverTap(index + 1, false) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

/** Compact complete book, not a clipped piece of a full-size next page. */
@Composable
private fun Ws16NeighborBook(
    book: PuzzleBook,
    sideWidth: Dp,
    fieldNotesArt: Boolean,
    previous: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = book.title.removeSuffix(" · TEST BOOK")
    val previewWidth = (sideWidth - 7.dp).coerceIn(32.dp, 74.dp)
    Column(
        modifier = modifier.width(sideWidth).height(216.dp)
            .semantics {
                contentDescription =
                    (if (previous) "Previous book: " else "Next book: ") + title
            }
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(if (previous) "ws16-carousel-prev" else "ws16-carousel-next"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        BookArt(
            book.theme, BookArtKind.COVER,
            Modifier.width(previewWidth).height(previewWidth * 1.38f),
            fieldNotes = fieldNotesArt
        )
        Canvas(Modifier.size(19.dp)) {
            val c = Color(0xFF2C6146)
            val sign = if (previous) -1f else 1f
            val sx = size.width * .5f
            drawLine(
                c, Offset(sx - sign * size.width * .17f, size.height * .20f),
                Offset(sx + sign * size.width * .14f, size.height * .50f),
                strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round
            )
            drawLine(
                c, Offset(sx + sign * size.width * .14f, size.height * .50f),
                Offset(sx - sign * size.width * .17f, size.height * .80f),
                strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round
            )
        }
    }
}
