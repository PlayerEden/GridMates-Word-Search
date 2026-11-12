package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS16AM: interactive book layers above the actual WS16AK painted cabinet.
 * Real catalog books keep their IDs; blanks are purely decorative.
 * The physical shelf boundaries use the same six-band math as WS16AL.
 */
@Composable
internal fun WS16AMInteractiveBookcaseScene(
    books: List<PuzzleBook>,
    onBack: () -> Unit,
    onBookSelected: (String) -> Unit,
    onInspectEmpty: () -> Unit,
    onOldCase: () -> Unit,
    onModularCase: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fontScale = LocalDensity.current.fontScale
    val hitSlot = if (fontScale >= 1.65f) 54.dp else 48.dp
    var qaMenuOpen by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().padding(horizontal = 3.dp, vertical = 2.dp)
        .testTag("ws16am-interactive-library")) {
        Ws15BackHeader(
            title = "Your Library", onBack = onBack, backLabel = "Back to Play",
            onPreviewOptions = { qaMenuOpen = true },
            modifier = Modifier.padding(horizontal = 6.dp)
        )
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)
            .padding(horizontal = 2.dp).testTag("ws16am-cabinet-stage")) {
            val slots = ((maxWidth - 94.dp).value / hitSlot.value)
                .toInt().coerceIn(1, 12)
            val capacity = slots * 2
            val pages = maxOf(1, (books.size + capacity - 1) / capacity)
            val pager = rememberPagerState(pageCount = { pages })
            // Match the source-painted background's bands exactly.
            val scale = minOf(maxWidth.value / 1024f, maxHeight.value / 1150f)
            val fixed = 722f * scale
            val flexible = (maxHeight.value - fixed).coerceAtLeast(0f)
            val upper = flexible * (450f / 814f)
            val lower = flexible - upper
            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxSize().testTag("ws16am-bookcase-pages")
            ) { page ->
                val pageBooks = books.drop(page * capacity).take(capacity)
                Box(Modifier.fillMaxSize().testTag("ws16am-bookcase-page-$"+"page")) {
                    WS16ALResponsiveBookcaseBackground(Modifier.fillMaxSize())
                    Column(Modifier.fillMaxSize()) {
                        Spacer(Modifier.height((246f * scale).dp))
                        WS16AMSpineBay(
                            books = pageBooks.take(slots),
                            shelfIndex = page * 2, slots = slots, slot = hitSlot,
                            height = upper.dp, onBookSelected = onBookSelected
                        )
                        Spacer(Modifier.height((128f * scale).dp))
                        WS16AMSpineBay(
                            books = pageBooks.drop(slots),
                            shelfIndex = page * 2 + 1, slots = slots, slot = hitSlot,
                            height = lower.dp, onBookSelected = onBookSelected
                        )
                    }
                }
            }
            // Keep only a small real-book label on the left of the base.
            // QA switches belong behind the header overflow, not on woodwork.
            Box(Modifier.align(Alignment.BottomStart)
                .padding(start = 58.dp, bottom = 12.dp)
                .testTag("ws16am-book-count")) {
                WS16AMBookCountPlaque(books.size)
            }
            if (pages > 1) {
                Text(
                    (pager.currentPage + 1).toString() + " of " + pages,
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(end = 42.dp, bottom = 21.dp)
                        .testTag("ws16am-page-count"),
                    color = Color(0xFFFFE9BD),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
    if (qaMenuOpen) {
        AlertDialog(
            onDismissRequest = { qaMenuOpen = false },
            title = { Text("Library visual test") },
            text = {
                Column {
                    Text("Compare the illustrated cabinet with and without books. Your saved puzzles won't change.")
                    if (onModularCase != null) {
                        TextButton(
                            onClick = { qaMenuOpen = false; onModularCase() },
                            modifier = Modifier.testTag("ws16at-open-modular")
                        ) { Text("Try modular Library") }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { qaMenuOpen = false; onInspectEmpty() },
                    modifier = Modifier.testTag("ws16am-inspect-empty")
                ) { Text("Empty woodwork") }
            },
            dismissButton = {
                TextButton(
                    onClick = { qaMenuOpen = false; onOldCase() },
                    modifier = Modifier.testTag("ws16am-old-case")
                ) { Text("Previous bookcase") }
            }
        )
    }
}

@Composable
internal fun WS16AMBookCountPlaque(count: Int) {
    // The carved base already contains a dark-green inlaid strip. A floating
    // filled badge obscured its gold border and looked like a glued-on button.
    Text(
        count.toString() + if (count == 1) " book" else " books",
        color = Color(0xFFFFE9BD),
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.testTag("ws16an-inlaid-book-count")
    )
}

@Composable
internal fun WS16AMSpineBay(
    books: List<PuzzleBook>,
    shelfIndex: Int,
    slots: Int,
    slot: Dp,
    height: Dp,
    onBookSelected: (String) -> Unit,
    futureTreatment: Boolean = false,
    showFutureVolumes: Boolean = true,
    useCleanSpines: Boolean = false,
    drawContactShadow: Boolean = true
) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(height)
        .testTag("ws16am-spine-shelf-" + shelfIndex)) {
        // Publisher geometry is true pixel aspect, not a stretched cover.
        // Tall upper volumes and standard lower volumes share visible width,
        // while each keeps its unscaled typography and 48/54dp tap region.
        // WS16BE: the WS16AO wide sprite contains a second square leather
        // silhouette BEHIND the original rounded binding. This is baked into
        // the PNG, not a second Compose image. For the modular cabinet, show
        // the preserved single-binding WS16AI source instead; no re-stretching
        // of the lettering or replacement of existing pinned images.
        val profile = if (useCleanSpines) "clean" else
            if (shelfIndex % 2 == 0) "tall" else "standard"
        val bindingRatio = when (profile) {
            "clean" -> 104f / 542f
            "tall" -> 166f / 662f
            else -> 166f / 542f
        }
        val artWidth = minOf(slot - 2.dp,
            (maxHeight - 10.dp).coerceAtLeast(24.dp) * bindingRatio)
        val artHeight = artWidth / bindingRatio
        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
                .width(slot * slots).padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            for (i in 0 until slots) {
                val book = books.getOrNull(i)
                val isFutureVisible = showFutureVolumes && (
                    !futureTreatment || if (books.isEmpty()) {
                        i == (slots - 1) / 2 || i == slots / 2
                    } else i == books.size
                )
                Box(
                    Modifier.width(slot).height(artHeight + 4.dp)
                        .then(if (book != null) Modifier
                            .semantics {
                                contentDescription = "Pull out " +
                                    book.title.removeSuffix(" · TEST BOOK")
                            }
                            .clickable(role = Role.Button) { onBookSelected(book.id) }
                            .testTag("ws16am-spine-" + book.id)
                        else Modifier.testTag("ws16am-future-" + shelfIndex + "-" + i)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (drawContactShadow && (book != null || isFutureVisible)) {
                        Box(Modifier.align(Alignment.BottomCenter)
                            .width(artWidth).height(4.dp)
                            .background(Color(0x990F0806), RoundedCornerShape(50)))
                    }
                    if (book != null) {
                        BookcaseSpineArt(
                            book.theme, true,
                            Modifier.width(artWidth).height(artHeight)
                                .testTag("ws16am-art-" + book.id),
                            profile = profile
                        )
                    } else if (isFutureVisible) {
                        val style = listOf("linen", "night", "burgundy")[
                            (shelfIndex * slots + i) % 3
                        ]
                        BookcaseFutureSpineArt(
                            style, Modifier.width(artWidth).height(artHeight)
                                .alpha(if (futureTreatment) .34f else 1f)
                                .then(if (futureTreatment) Modifier.testTag(
                                    "ws16bc-visible-future-$shelfIndex-$i"
                                ) else Modifier),
                            profile = profile
                        )
                    }
                }
            }
        }
    }
}
