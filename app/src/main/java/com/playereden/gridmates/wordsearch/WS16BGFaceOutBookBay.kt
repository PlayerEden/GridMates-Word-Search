package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS16BG: real cover-first display for the carved portrait cabinet.
 *
 * Artwork is the exact existing WS16AE painted COVER used on the Home
 * carousel. The only additional book "depth" is a 5dp visible page edge;
 * no flattened full-book duplicate or fake future books are drawn.
 *
 * Stable PuzzleBook IDs remain the only clickable objects.
 */
@Composable
internal fun WS16BGFaceOutBookBay(
    books: List<PuzzleBook>,
    height: Dp,
    onBookSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.fillMaxWidth().height(height)
        .testTag("ws16bg-face-out-bay")) {
        val maxPerShelf = 2
        val visibleBooks = books.take(maxPerShelf)
        if (visibleBooks.isEmpty()) return@BoxWithConstraints

        // Use actual cabinet interior width. The 480x640 cover's aspect
        // ratio is preserved and the artwork stays out of the title plaque.
        val availableWidth = (maxWidth - 12.dp).coerceAtLeast(60.dp)
        val unitWidth = if (visibleBooks.size == 1) {
            minOf(111.dp, availableWidth)
        } else {
            minOf(104.dp, availableWidth / 2f)
        }
        val usableHeight = (maxHeight - 24.dp).coerceAtLeast(70.dp)
        // The actual painted-book bounds of the existing 480x640
        // WS16AE artwork are x=12..468, y=65..574. Tight 460x513
        // crops keep feet ON the shelf; no imaginary 65px empty margin.
        val physicalRatio = 460f / 513f
        val coverWidth = minOf(unitWidth - 10.dp, usableHeight * physicalRatio)
            .coerceAtLeast(36.dp)
        val coverHeight = coverWidth / physicalRatio

        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
                .testTag("ws16bg-real-covers"),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            visibleBooks.forEachIndexed { index, book ->
                val lean = if (visibleBooks.size == 1) 0f else
                    if (index == 0) -2f else 2f
                Box(
                    Modifier.width(coverWidth + 10.dp).height(coverHeight + 10.dp)
                        .semantics {
                            contentDescription = "Open " +
                                book.title.removeSuffix(" · TEST BOOK")
                        }
                        .clickable(role = Role.Button) { onBookSelected(book.id) }
                        .testTag("ws16bg-cover-" + book.id),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Contact shadow is beneath the book, not a second book.
                    Box(
                        Modifier.align(Alignment.BottomCenter)
                            .width(coverWidth + 5.dp).height(4.dp)
                            .background(Color(0x770E0704), RoundedCornerShape(50))
                    )
                    Box(
                        Modifier.align(Alignment.BottomCenter)
                            .offset(y = (-3).dp)
                            .width(coverWidth + 6.dp).height(coverHeight)
                            .graphicsLayer {
                                rotationZ = lean
                                rotationY = if (index == 0) -5f else 5f
                                cameraDistance = 16f * density
                            }
                    ) {
                        // Only a narrow exposed page block to the RIGHT of
                        // the front illustration gives genuine hardcover depth.
                        Box(
                            Modifier.align(Alignment.CenterEnd)
                                .width(6.dp).fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFB99A70),
                                            Color(0xFFF7E8C5),
                                            Color(0xFF785337)
                                        )
                                    ),
                                    RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp)
                                )
                        )
                        WS16BGTrimmedCoverArt(
                            book.theme,
                            modifier = Modifier.align(Alignment.CenterStart)
                                .width(coverWidth).fillMaxHeight()
                                .shadow(3.dp, RoundedCornerShape(3.dp))
                                .testTag("ws16bg-art-" + book.id)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reuse precisely the existing illustrated Home-cover pixels. We crop only
 * known transparent WS16AE margins (2px safety around physical page artwork).
 * The original PNG, manifest, design master and SHA stay untouched. On a
 * missing/unavailable debug-only cover we fall back to existing BookArt.
 */
@Composable
private fun WS16BGTrimmedCoverArt(
    theme: WordTheme,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    val bitmap = remember(context, slug) {
        runCatching {
            context.assets.open("ws16ae/covers/$slug.png").use { stream ->
                BitmapFactory.decodeStream(stream)
            }?.let { original ->
                if (original.width == 480 && original.height == 640) {
                    // Source alpha bounds: (12,65)..(468,574). There must
                    // be no transparent 65px book-raising bottom margin.
                    Bitmap.createBitmap(original, 10, 63, 460, 513)
                } else original
            }
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
        )
    } else {
        BookArt(
            theme = theme,
            kind = BookArtKind.COVER,
            modifier = modifier,
            fieldNotes = true
        )
    }
}
