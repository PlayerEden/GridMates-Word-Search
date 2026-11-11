package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import kotlinx.coroutines.launch

/**
 * WS16AT: real catalog + independent home shelves within an independent five-piece
 * cabinet. Zero permanent shelves, plaques, books or labels in shell artwork.
 * Experimental renderer is not enabled until all actual ws16as shell bytes exist.
 * Reuses WS16AMSpineBay for actual accessible real-book/blank hit slots.
 */
private val ws16atShell = listOf(
    "top_crown", "left_pilaster", "recessed_backboard",
    "right_pilaster", "bottom_base"
)

internal fun hasWs16atCabinetArt(context: Context): Boolean = runCatching {
    val available = context.assets.list("ws16as/shell")?.toSet() ?: emptySet()
    ws16atShell.all { "$it.png" in available }
}.getOrDefault(false)

private fun ws16atLoad(context: Context, name: String): Bitmap? = runCatching {
    context.assets.open("ws16as/shell/$name.png").use {
        BitmapFactory.decodeStream(it)
    }
}.getOrNull()

/**
 * Architectural paint is split into TWO compositing passes.
 * The dark planks are behind the real shelves and book spines; carved columns,
 * crown and base are in FRONT so shelves never appear pasted over pilasters.
 * Middle layers use aspect-preserving crops on tall phones, instead of elongating carvings, ivy and wood grain.
 * Each raster is an independent, source-accurate piece; no UI or shelf pixels
 * have been painted into the cabinet assets.
 */
@Composable
private fun WS16ATCabinetLayers(foreground: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val artwork = remember(context) {
        ws16atShell.associateWith { ws16atLoad(context, it) }
    }
    BoxWithConstraints(modifier.testTag(if (foreground)
        "ws16at-front-woodwork" else "ws16at-five-part-shell")) {
        val crownHeight = (maxWidth * (198f / 602f))
            .coerceAtMost(maxHeight * 0.25f)
        val baseHeight = (maxWidth * (108f / 602f))
            .coerceAtMost(maxHeight * 0.15f)
        val leftWidth = maxWidth * (126f / 602f)
        val rightWidth = maxWidth * (120f / 602f)
        val backWidth = (maxWidth - leftWidth - rightWidth).coerceAtLeast(1.dp)
        val middleHeight = (maxHeight - crownHeight - baseHeight).coerceAtLeast(1.dp)

        if (!foreground) {
            Canvas(Modifier.fillMaxSize()) { drawRect(Color(0xFF753A1A)) }
            artwork["recessed_backboard"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.align(Alignment.TopStart)
                        .offset(x = leftWidth, y = crownHeight)
                        .width(backWidth).height(middleHeight))
            }
        } else {
            artwork["left_pilaster"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.align(Alignment.TopStart)
                        .offset(y = crownHeight)
                        .width(leftWidth).height(middleHeight))
            }
            artwork["right_pilaster"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .offset(y = crownHeight)
                        .width(rightWidth).height(middleHeight))
            }
            artwork["top_crown"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth().height(crownHeight))
            }
            artwork["bottom_base"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth().height(baseHeight))
            }
        }
    }
}

@Composable
internal fun WS16ATModularLibraryScene(
    books: List<PuzzleBook>,
    onBack: () -> Unit,
    onBookSelected: (String) -> Unit,
    onPreviousArt: () -> Unit,
    safeContentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier.fillMaxSize().testTag("ws16at-modular-library")) {
        val hitSlot = if (density.fontScale >= 1.65f) 54.dp else 48.dp
        val leftWidth = maxWidth * (126f / 602f)
        val rightWidth = maxWidth * (120f / 602f)
        val slots = ((maxWidth - leftWidth - rightWidth).value / hitSlot.value)
            .toInt().coerceIn(1, 12)
        val tierCount = when {
            maxHeight < 480.dp -> 1
            maxHeight >= 830.dp && books.size > 2 * slots -> 3
            else -> 2
        }
        val capacity = slots * tierCount
        val pages = maxOf(1, (books.size + capacity - 1) / capacity)
        val pager = rememberPagerState(pageCount = { pages })
        val scope = rememberCoroutineScope()
        val top = (maxWidth * (198f / 602f)).coerceAtMost(maxHeight * 0.25f)
        val bottom = (maxWidth * (108f / 602f)).coerceAtMost(maxHeight * 0.15f)
        val available = (maxHeight - top - bottom).coerceAtLeast(1.dp)
        val tierHeight = available / tierCount
        // The approved Home shelf draws a 50dp image offset 8dp downward;
        // its actual book-contact plane is 42dp above the row's lower edge.
        // Reserve that entire plane so the spine bottoms meet the shelf top,
        // rather than being partly hidden by the wooden apron.
        val shelfContactDepth = 42.dp

        // Backdrop and real shelves/books are independent; hide nothing in a bitmap.
        WS16ATCabinetLayers(foreground = false, modifier = Modifier.fillMaxSize())
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxSize()
                .padding(start = leftWidth, end = rightWidth, top = top, bottom = bottom)
                .testTag("ws16at-bookcase-pages")
        ) { page ->
            val pageBooks = books.drop(page * capacity).take(capacity)
            Column(Modifier.fillMaxSize().testTag("ws16at-page-$page")) {
                repeat(tierCount) { tier ->
                    val shelfIndex = page * tierCount + tier
                    Box(Modifier.fillMaxWidth().height(tierHeight)
                        .testTag("ws16at-movable-shelf-$shelfIndex")) {
                        WS16AMSpineBay(
                            books = pageBooks.drop(tier * slots).take(slots),
                            shelfIndex = shelfIndex, slots = slots,
                            slot = hitSlot,
                            height = (tierHeight - shelfContactDepth).coerceAtLeast(60.dp),
                            onBookSelected = onBookSelected
                        )
                        Box(Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .testTag("ws16at-home-shelf-$shelfIndex")) {
                            Ws16EStorybookShelf()
                        }
                    }
                }
            }
        }
        // Carved woodwork overlays the working shelf sprites at the real edges.
        WS16ATCabinetLayers(foreground = true, modifier = Modifier.fillMaxSize())
        // Only controls respect Android safe insets. The cabinet wood and live
        // shelf/book stage remain full-bleed behind visible or gesture bars.
        Box(Modifier.fillMaxSize().padding(safeContentPadding)
            .testTag("ws16aw-safe-overlays")) {
        Ws16AZCabinetPlaque(
            label = "Your Library", title = true,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(start = 62.dp, end = 62.dp,
                    top = (top - 58.dp).coerceAtLeast(38.dp))
                .testTag("ws16at-library-title")
        )
        Ws16AZCabinetControl(
            glyph = Ws16AZCabinetGlyph.BACK,
            description = "Back to Play", onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 9.dp, top = 10.dp)
                .testTag("ws16at-back")
        )
        Ws16AZCabinetPlaque(
            label = "${books.size} ${if (books.size == 1) "book" else "books"}",
            title = false,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .testTag("ws16at-real-count")
        )
        if (pages > 1) {
            Row(Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = (bottom - 9.dp).coerceAtLeast(46.dp))
                    .testTag("ws16at-pagination"),
                verticalAlignment = Alignment.CenterVertically) {
                Ws16AZCabinetControl(
                    glyph = Ws16AZCabinetGlyph.PREVIOUS,
                    description = "Previous bookcase",
                    enabled = pager.currentPage > 0,
                    onClick = {
                        scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
                    },
                    modifier = Modifier.testTag("ws16at-prev")
                )
                Ws16AZCabinetPlaque(
                    label = "${pager.currentPage + 1} / $pages",
                    title = false,
                    modifier = Modifier.testTag("ws16at-page-label")
                )
                Ws16AZCabinetControl(
                    glyph = Ws16AZCabinetGlyph.NEXT,
                    description = "Next bookcase",
                    enabled = pager.currentPage < pages - 1,
                    onClick = {
                        scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    },
                    modifier = Modifier.testTag("ws16at-next")
                )
            }
        }
        // Keep the old Library renderer one tap away for visual comparison.
        Ws16AZCabinetControl(
            glyph = Ws16AZCabinetGlyph.ART_OPTIONS,
            description = "Previous Library artwork",
            onClick = onPreviousArt,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 9.dp, top = 10.dp)
                .testTag("ws16at-previous-art")
        )
        } // inset overlay only; original shell fills the outer Box
    }
}
