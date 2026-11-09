package com.playereden.gridmates.wordsearch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * One actual source for the illustrated cover pager in Home and Full Shelf.
 * Neighbor cover tap: focus; current cover tap: invoke caller's intent.
 * Never writes a bookmark or puzzle completion.
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
        val coverWidth = (width * .59f).coerceIn(136.dp, 186.dp)
        val peek = ((width - coverWidth) / 2).coerceAtLeast(0.dp)
        Column {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(coverWidth),
                contentPadding = PaddingValues(horizontal = peek),
                pageSpacing = 10.dp,
                key = { index -> books[index].id },
                modifier = Modifier.fillMaxWidth().height(234.dp)
                    .testTag("ws14d-cover-pager")
            ) { page ->
                val book = books[page]
                val focused = pagerState.currentPage == page
                val lift by animateFloatAsState(
                    targetValue = if (focused) 1.025f else .92f,
                    animationSpec = tween(durationMillis = 220),
                    label = "ws16-focused-cover"
                )
                Box(
                    Modifier.fillMaxWidth().height(234.dp)
                        .graphicsLayer { scaleX = lift; scaleY = lift }
                        .semantics {
                            contentDescription =
                                (if (focused) "Play " else "Select ") +
                                book.title.removeSuffix(" · TEST BOOK")
                        }
                        .clickable(role = Role.Button) {
                            onCoverTap(page, focused)
                        }
                        .testTag("ws14d-cover-" + book.id)
                        .testTag("ws16-cover-" + book.id),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    BookArt(book.theme, BookArtKind.COVER,
                        Modifier.width(coverWidth - 8.dp).height(218.dp),
                        fieldNotes = fieldNotesArt)
                }
            }
            Canvas(Modifier.fillMaxWidth().height(13.dp)
                .testTag("ws14d-shelf-edge")) {
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(
                        Color(0xFFE2CFAC), Color(0xFFAE906C), Color(0xFF8E7354))),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    size = Size(size.width, size.height)
                )
                drawLine(Color(0xFFF6E9CE),
                    Offset(5.dp.toPx(), 2.dp.toPx()),
                    Offset(size.width - 5.dp.toPx(), 2.dp.toPx()),
                    strokeWidth = 1.dp.toPx())
            }
        }
    }
}
