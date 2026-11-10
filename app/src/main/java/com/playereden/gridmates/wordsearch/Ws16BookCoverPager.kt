package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import kotlin.math.absoluteValue

/**
 * WS-16Q — ONE full-width swipeable bookshelf, with real neighboring books
 * extending toward the physical viewport edge. No frozen miniature copies,
 * duplicate book visuals, arrows, decorative masks, or offscreen overlays.
 *
 * The outer Home column has an 18dp horizontal gutter. Only the pager bleeds
 * into that gutter; the walnut shelf and all Home text retain their layout.
 * Actual books are bounded naturally by the pager's viewport at the screen
 * edge rather than being clipped by the old narrow center page.
 *
 * Book size, book IDs, selected progress, tap-to-open, and saved focus are
 * unchanged. Fullscreen and Standard share the same carousel logic.
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
        val contentWidth = maxWidth
        // Matching GridMatesShell's existing 18dp Home page gutter.
        // The pager fills the actual horizontal viewport without stretching
        // the shelf or allowing labels/cards to slip under the phone edges.
        val edgeBleed = 18.dp
        val viewportWidth = contentWidth + edgeBleed * 2
        val coverWidth = (contentWidth * .59f).coerceIn(136.dp, 186.dp)
            .coerceAtMost((contentWidth - 80.dp).coerceAtLeast(120.dp))
        val sidePadding = ((viewportWidth - coverWidth) / 2).coerceAtLeast(0.dp)

        Box(Modifier.fillMaxWidth().height(252.dp)) {
            Ws16EStorybookShelf(
                Modifier.align(Alignment.BottomCenter).offset(y = (-6).dp)
            )

            // One real Pager draws the center AND the adjacent pages and moves
            // them together. Fixed book-sized pages + symmetrical padding
            // center the focused volume and reveal partial neighbors.
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(coverWidth),
                contentPadding = PaddingValues(horizontal = sidePadding),
                pageSpacing = 10.dp,
                beyondViewportPageCount = 1,
                key = { page -> books[page].id },
                modifier = Modifier.align(Alignment.TopCenter)
                    .requiredWidth(viewportWidth)
                    .height(226.dp)
                    .testTag("ws14d-cover-pager")
            ) { page ->
                val book = books[page]
                val focused = pagerState.settledPage == page
                Box(
                    modifier = Modifier.fillMaxWidth().height(226.dp)
                        .graphicsLayer {
                            // As one book moves toward the edge, it subtly
                            // recedes; the incoming book grows as it centers.
                            // Origin at bottom keeps every book resting on the
                            // shelf even while scaled or midway through a swipe.
                            val distance = (
                                (pagerState.currentPage - page) +
                                    pagerState.currentPageOffsetFraction
                            ).absoluteValue.coerceIn(0f, 1f)
                            val scale = 1f - .12f * distance
                            scaleX = scale
                            scaleY = scale
                            transformOrigin = TransformOrigin(.5f, 1f)
                        }
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
                    BookArt(
                        theme = book.theme,
                        kind = BookArtKind.COVER,
                        modifier = Modifier.width(coverWidth - 8.dp).height(218.dp),
                        fieldNotes = fieldNotesArt
                    )
                }
            }
        }
    }
}
