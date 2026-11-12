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

internal fun hasWs16baCabinetArt(context: Context): Boolean = runCatching {
    val available = context.assets.list("ws16ba/shell")?.toSet() ?: emptySet()
    ws16atShell.all { "$it.png" in available }
}.getOrDefault(false)

private fun ws16atLoad(context: Context, name: String, highRes: Boolean): Bitmap? = runCatching {
    val folder = if (highRes) "ws16ba/shell" else "ws16as/shell"
    context.assets.open("$folder/$name.png").use { BitmapFactory.decodeStream(it) }
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
    BoxWithConstraints(modifier.testTag(if (foreground)
        "ws16at-front-woodwork" else "ws16at-five-part-shell")) {
        // New 1024×2298 source downscales cleanly on portrait phones;
        // retain the original art for landscape or missing QA candidates.
        val highRes = hasWs16baCabinetArt(context) &&
            maxHeight >= maxWidth * 1.7f
        val artwork = remember(context, highRes) {
            ws16atShell.associateWith { ws16atLoad(context, it, highRes) }
        }
        val middleScale = if (highRes) ContentScale.FillBounds else ContentScale.Crop
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
                    contentScale = middleScale,
                    modifier = Modifier.align(Alignment.TopStart)
                        .offset(x = leftWidth, y = crownHeight)
                        .width(backWidth).height(middleHeight))
            }
        } else {
            artwork["left_pilaster"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = middleScale,
                    modifier = Modifier.align(Alignment.TopStart)
                        .offset(y = crownHeight)
                        .width(leftWidth).height(middleHeight))
            }
            artwork["right_pilaster"]?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = null,
                    contentScale = middleScale,
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
        // WS16BD: a sparse portrait collection should showcase the REAL
        // books across both shelves, not give half the cabinet to fake volumes.
        // Once there are more than one full row, return to catalog-order paging.
        val balancedSparseLayout = tierCount == 2 &&
            maxHeight >= maxWidth * 1.7f &&
            pages == 1 && books.size in 2..slots
        val pager = rememberPagerState(pageCount = { pages })
        val scope = rememberCoroutineScope()
        val top = (maxWidth * (198f / 602f)).coerceAtMost(maxHeight * 0.25f)
        val bottom = (maxWidth * (108f / 602f)).coerceAtMost(maxHeight * 0.15f)
        val available = (maxHeight - top - bottom).coerceAtLeast(1.dp)
        val cabinetPortrait = hasWs16baCabinetArt(LocalContext.current) &&
            maxHeight >= maxWidth * 1.7f
        // WS16BC: measure the painted shelf's actual extension beneath its
        // book-contact plane. A bottom-anchored count used to cover the carving
        // on some aspect ratios, even when the wood looked fine in screenshots.
        val shelfContactDepth = 42.dp
        val shelfOverhang = if (cabinetPortrait) {
            ((maxWidth - leftWidth - rightWidth).coerceAtLeast(1.dp) *
                (125f / 586f)).coerceIn(42.dp, 72.dp) - shelfContactDepth
        } else 0.dp
        val countGap = if (density.fontScale >= 1.65f) 8.dp else 12.dp
        val countPlaqueBudget = if (density.fontScale >= 1.65f) 52.dp else 38.dp
        val countBayHeight = shelfOverhang + countGap + countPlaqueBudget
        // Reserve only the space that the shelf/carved apron and native plaque
        // require. Use the remaining height for real books, not dead wood space.
        val shelfTierSpace = (available - countBayHeight).coerceAtLeast(1.dp)
        val tierHeights = if (balancedSparseLayout) listOf(
            shelfTierSpace * 0.50f, shelfTierSpace * 0.50f
        ) else if (tierCount == 2) listOf(
            shelfTierSpace * 0.54f, shelfTierSpace * 0.46f
        ) else List(tierCount) { available / tierCount }

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
                    val shelfBooks = if (balancedSparseLayout) {
                        val topBooks = (pageBooks.size + 1) / 2
                        if (tier == 0) pageBooks.take(topBooks) else pageBooks.drop(topBooks)
                    } else pageBooks.drop(tier * slots).take(slots)
                    val tierHeight = tierHeights[tier]
                    Box(Modifier.fillMaxWidth().height(tierHeight)
                        .testTag("ws16at-movable-shelf-$shelfIndex")) {
                        WS16AMSpineBay(
                            books = shelfBooks,
                            shelfIndex = shelfIndex,
                            slots = if (balancedSparseLayout) shelfBooks.size else slots,
                            slot = if (balancedSparseLayout) 60.dp else hitSlot,
                            // Keep the BOTTOM of the bay at the shelf-contact
                            // plane; shrink only the image above that point.
                            height = (tierHeight - shelfContactDepth)
                                .coerceAtLeast(60.dp),
                            headClearance = if (balancedSparseLayout) 34.dp else 10.dp,
                            futureTreatment = true,
                            showFutureVolumes = false,
                            // WS16BE: use the clean ORIGINAL single-binding
                            // sprite. WS16AO wider shoulder panels visually
                            // duplicate each book even with no extra UI image.
                            useCleanSpines = cabinetPortrait,
                            drawContactShadow = !cabinetPortrait,
                            onBookSelected = onBookSelected
                        )
                        Box(Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .testTag("ws16at-home-shelf-$shelfIndex")) {
                            if (cabinetPortrait) {
                                Ws16BBMatchedCabinetShelf()
                            } else {
                                Ws16EStorybookShelf()
                            }
                        }
                        // An entirely empty catalog may show an upcoming
                        // section; never compete with a real player's books.
                        if (tier > 0 && pageBooks.isEmpty()) {
                            Ws16AZCabinetPlaque(
                                label = "Coming soon", title = false,
                                modifier = Modifier.align(Alignment.TopCenter)
                                    .padding(top = 14.dp)
                                    .testTag("ws16bb-upcoming-label")
                            )
                        }
                    }
                }
                // The live count belongs directly beneath the LAST painted
                // shelf, after its full carved overhang; it is not a loose
                // overlay positioned from the bottom of the screen.
                if (tierCount == 2) {
                    Spacer(Modifier.height(shelfOverhang + countGap))
                    Box(
                        Modifier.fillMaxWidth().height(countPlaqueBudget)
                            .testTag("ws16bc-count-bay"),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Ws16AZCabinetPlaque(
                            label = "${books.size} ${if (books.size == 1) "book" else "books"}",
                            title = false,
                            modifier = Modifier.testTag("ws16at-real-count")
                        )
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
        if (tierCount != 2) {
            // Compact/overflow tier modes keep the existing bottom placard.
            Ws16AZCabinetPlaque(
                label = "${books.size} ${if (books.size == 1) "book" else "books"}",
                title = false,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .testTag("ws16at-real-count")
            )
        }
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
