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

@Composable
private fun WS16ATShell(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val artwork = remember(context) {
        ws16atShell.associateWith { ws16atLoad(context, it) }
    }
    BoxWithConstraints(modifier.testTag("ws16at-five-part-shell")) {
        val crownHeight = (maxWidth * (198f / 602f)).coerceAtMost(maxHeight * 0.28f)
        val baseHeight = (maxWidth * (108f / 602f)).coerceAtMost(maxHeight * 0.18f)
        val sideWidth = (maxWidth * 0.105f).coerceAtMost(46.dp)
        val middleHeight = (maxHeight - crownHeight - baseHeight).coerceAtLeast(1.dp)
        // The rear planks may flex vertically; the carved crown/base retain aspect.
        Canvas(Modifier.fillMaxSize()) { drawRect(Color(0xFF753A1A)) }
        artwork["recessed_backboard"]?.let { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxWidth()
                    .height(middleHeight)
                    .align(Alignment.TopCenter)
                    .padding(top = crownHeight * 0.5f))
        }
        // The separately movable book shelves render over the backboard in
        // the scene, with these rails placed last as the front architectural trim.
        artwork["left_pilaster"]?.let { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.align(Alignment.CenterStart)
                    .width(sideWidth).height(middleHeight))
        }
        artwork["right_pilaster"]?.let { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.align(Alignment.CenterEnd)
                    .width(sideWidth).height(middleHeight))
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

@Composable
internal fun WS16ATModularLibraryScene(
    books: List<PuzzleBook>,
    onBack: () -> Unit,
    onBookSelected: (String) -> Unit,
    onPreviousArt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier.fillMaxSize().testTag("ws16at-modular-library")) {
        val hitSlot = if (density.fontScale >= 1.65f) 54.dp else 48.dp
        val sideInset = (maxWidth * 0.09f).coerceIn(26.dp, 50.dp)
        val slots = ((maxWidth - 2 * sideInset).value / hitSlot.value)
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
        val top = (maxWidth * (198f / 602f)).coerceAtLeast(98.dp)
        val bottom = (maxWidth * (108f / 602f)).coerceAtLeast(52.dp)
        val available = (maxHeight - top - bottom).coerceAtLeast(1.dp)
        val tierHeight = available / tierCount

        // Backdrop and real shelves/books are independent; hide nothing in a bitmap.
        WS16ATShell(Modifier.fillMaxSize())
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxSize()
                .padding(top = top, bottom = bottom)
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
                            slot = hitSlot, height = tierHeight,
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
        // Native text, never printed into the illustration or decorative plaque.
        Text("Your Library", color = Color(0xFFFFE6B5),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = (top - 58.dp).coerceAtLeast(38.dp))
                .testTag("ws16at-library-title"))
        Text("‹", style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFFFE6B5),
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 9.dp, top = 10.dp)
                .size(48.dp)
                .semantics { contentDescription = "Back to Play" }
                .clickable(role = Role.Button, onClick = onBack)
                .testTag("ws16at-back"))
        Text("${books.size} ${if (books.size == 1) "book" else "books"}",
            color = Color(0xFFFFE6B5),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .testTag("ws16at-real-count"))
        if (pages > 1) {
            Row(Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = bottom - 9.dp)
                    .testTag("ws16at-pagination"),
                verticalAlignment = Alignment.CenterVertically) {
                Text("◀", modifier = Modifier.size(48.dp)
                    .semantics { contentDescription = "Previous bookcase" }
                    .clickable(enabled = pager.currentPage > 0,
                        role = Role.Button) {
                        scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
                    }.testTag("ws16at-prev"),
                    color = Color(0xFFFFE6B5))
                Text("${pager.currentPage + 1} / $pages",
                    color = Color(0xFFFFE6B5),
                    style = MaterialTheme.typography.labelMedium)
                Text("▶", modifier = Modifier.size(48.dp)
                    .semantics { contentDescription = "Next bookcase" }
                    .clickable(enabled = pager.currentPage < pages - 1,
                        role = Role.Button) {
                        scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    }.testTag("ws16at-next"),
                    color = Color(0xFFFFE6B5))
            }
        }
        // QA escape route — does not change saved books or global art setting.
        Text("⋯", color = Color(0xFFFFE6B5),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 9.dp, top = 10.dp)
                .size(48.dp)
                .semantics { contentDescription = "Previous Library artwork" }
                .clickable(role = Role.Button, onClick = onPreviousArt)
                .testTag("ws16at-previous-art"))
    }
}
