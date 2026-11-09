package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import kotlinx.coroutines.launch

/** WS-16: real carousel-first Home, with native buttons and preserved local saves.
 * Books remain QA-only until the existing editorial/release gates approve them.
 */
@Composable
internal fun Ws16HomeScreen(
    store: SoloStore,
    catalog: BookCatalog?,
    bookProgress: BookLibraryProgress?,
    initialFocusedBookId: String?,
    onFocusedBookChanged: (String) -> Unit,
    onPlayBook: (String) -> Unit,
    onFullShelf: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onRecords: () -> Unit,
    onHelp: () -> Unit,
    onSolo: () -> Unit,
    onMyPuzzles: () -> Unit,
    onNewSolo: () -> Unit,
    onDaily: () -> Unit,
    onNearby: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val books = catalog?.books.orEmpty()
    val firstIndex = remember(catalog, initialFocusedBookId) {
        books.indexOfFirst { it.id == initialFocusedBookId }.coerceAtLeast(0)
    }
    val pager = rememberPagerState(initialPage = firstIndex, pageCount = { books.size })
    val scope = rememberCoroutineScope()
    val selected = books.getOrNull(pager.currentPage)
    val active = store.profile.active
    val soloPuzzle = remember(active.puzzleId) { SoloProgressRules.puzzle(active) }
    val soloComplete = WordSearchRules.complete(soloPuzzle, active.foundIds)
    val soloSize = active.boardSize?.cells ?: active.difficulty.gridSize

    Ws16HomeHeader(onProfile = onProfile, onMenu = { menuOpen = true })

    store.warning?.let { message ->
        PaperPanel(Modifier.fillMaxWidth()) {
            Text(message, color = Gold, style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (selected != null && catalog != null && bookProgress != null) {
        LaunchedEffect(pager.settledPage, books) {
            books.getOrNull(pager.settledPage)?.let { onFocusedBookChanged(it.id) }
        }
        Column(Modifier.fillMaxWidth().testTag("ws16-home-books"),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your puzzle books", color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text("${pager.currentPage + 1} of ${books.size}",
                    color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            Ws16BookCoverPager(
                books = books,
                pagerState = pager,
                fieldNotesArt = true,
                onCoverTap = { index, focused ->
                    if (focused) onPlayBook(books[index].id)
                    else scope.launch { pager.animateScrollToPage(index) }
                }
            )
            val count = remember(catalog, bookProgress, selected.id) {
                runCatching {
                    BookProgressRules.completedCount(catalog, bookProgress, selected.id)
                }.getOrDefault(0)
            }
            val pages = catalog.pages(selected.id).size
            Text(selected.title.removeSuffix(" · TEST BOOK") +
                    " · $count/$pages pages complete",
                color = Forest, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().testTag("ws16-selected-book"))
            TextButton(onClick = onFullShelf,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .heightIn(min = 48.dp).testTag("ws16-full-shelf")) {
                Ws15ModeEmblem(Ws15ModeGlyph.BOOKS)
                Text("Full shelf", modifier = Modifier.padding(start = 6.dp))
            }
        }
    } else {
        // A production build without editorially approved books must never
        // fabricate QA covers, pages or a playable book.
        PaperPanel(Modifier.fillMaxWidth()) {
            Text("Puzzle books", color = Forest,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text("Illustrated puzzle books are being prepared. You can play Solo or Daily now.",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }

    PaperPanel(Modifier.fillMaxWidth().testTag("ws16-continue-solo")) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Ws15ModeEmblem(Ws15ModeGlyph.SOLO)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(if (soloComplete) "New Solo" else "Continue Solo",
                    color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
                Text(active.theme.label + " · ${soloSize}×$soloSize" +
                    if (soloComplete) " · Finished" else
                        " · ${active.foundIds.size}/${soloPuzzle.targets.size} found",
                    color = Muted, style = MaterialTheme.typography.labelSmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(onClick = if (soloComplete) onNewSolo else onSolo,
                modifier = Modifier.heightIn(min = 48.dp)
                    .testTag("ws16-resume-solo")) {
                Text(if (soloComplete) "New" else "Play")
            }
        }
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < 328.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Ws16ActionButton("Daily", Ws15ModeGlyph.DAILY, onDaily,
                    Modifier.fillMaxWidth())
                Ws16ActionButton("Together", Ws15ModeGlyph.TOGETHER, onNearby,
                    Modifier.fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Ws16ActionButton("Daily", Ws15ModeGlyph.DAILY, onDaily,
                    Modifier.weight(1f))
                Ws16ActionButton("Together", Ws15ModeGlyph.TOGETHER, onNearby,
                    Modifier.weight(1f))
            }
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onNewSolo,
            modifier = Modifier.heightIn(min = 48.dp).testTag("ws16-new-solo")) {
            Text("New Solo")
        }
        TextButton(onClick = onMyPuzzles,
            modifier = Modifier.heightIn(min = 48.dp).testTag("ws16-my-puzzles")) {
            Text("My Puzzles")
        }
    }

    if (menuOpen) {
        AlertDialog(
            onDismissRequest = { menuOpen = false },
            title = { Text("Word Search menu", color = Forest) },
            text = {
                Column(Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    val options = listOf(
                        "Settings & backup" to onSettings,
                        "How to play" to onHelp,
                        "Records" to onRecords,
                        "My Puzzles" to onMyPuzzles,
                        "Full shelf" to onFullShelf,
                        "New Solo" to onNewSolo
                    )
                    options.forEach { (label, action) ->
                        TextButton(onClick = { menuOpen = false; action() },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { menuOpen = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun Ws16ActionButton(
    label: String,
    glyph: Ws15ModeGlyph,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(onClick = onClick,
        modifier = modifier.heightIn(min = 64.dp).testTag("ws16-action-" + label.lowercase())
            .semantics { contentDescription = "Open $label" }) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Ws15ModeEmblem(glyph)
            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
