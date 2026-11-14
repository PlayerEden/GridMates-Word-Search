package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
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
    onCollection: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onRecords: () -> Unit,
    onHelp: () -> Unit,
    onSolo: () -> Unit,
    onMyPuzzles: () -> Unit,
    onNewSolo: () -> Unit,
    onDaily: () -> Unit,
    onNearby: () -> Unit,
    showQuickPlayPreview: Boolean = false,
    displayMode: DisplayMode = DisplayMode.FULLSCREEN,
    onDisplayModeChange: (DisplayMode) -> Unit = {},
    compactHome: Boolean = false
) {
    var menuOpen by remember { mutableStateOf(false) }
    val books = catalog?.books.orEmpty()
    val firstIndex = remember(catalog, initialFocusedBookId) {
        books.indexOfFirst { it.id == initialFocusedBookId }.coerceAtLeast(0)
    }
    val pager = rememberPagerState(initialPage = firstIndex, pageCount = { books.size })
    val scope = rememberCoroutineScope()
    val selected = books.getOrNull(pager.settledPage)
    val active = store.profile.active
    val soloPuzzle = remember(active.puzzleId) { SoloProgressRules.puzzle(active) }
    val soloComplete = WordSearchRules.complete(soloPuzzle, active.foundIds)
    val soloSize = active.boardSize?.cells ?: active.difficulty.gridSize

    // Fullscreen lends a little more room to the illustrated page-top border.
    // Standard keeps the existing compact fit on smaller three-button phones.
    if (displayMode == DisplayMode.FULLSCREEN)
        Spacer(Modifier.height(if (compactHome) 7.dp else 14.dp))
    Ws16HomeHeader(displayMode = displayMode,
        onProfile = onProfile, onMenu = { menuOpen = true })
    // An independent antique-gold botanical rule, no words baked in art.
    Ws16IHeaderDivider()

    store.warning?.let { message ->
        PaperPanel(Modifier.fillMaxWidth()) {
            Text(message, color = Gold, style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (selected != null && catalog != null && bookProgress != null) {
        LaunchedEffect(pager.settledPage, books) {
            books.getOrNull(pager.settledPage)?.let { onFocusedBookChanged(it.id) }
        }
        Column(Modifier.fillMaxWidth().qaTrackBounds("Books + progress").testTag("ws16-home-books"),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your puzzle books", color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text("${pager.settledPage + 1} of ${books.size}",
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

    // Puzzle volumes and their unlockable art deserve sibling Home entry points.
    // Collection remains visible even while production books await approval.
    Ws16DLibraryShortcuts(
        fullShelfAvailable = selected != null && catalog != null && bookProgress != null,
        onFullShelf = onFullShelf,
        onCollection = onCollection
    )

    // The entire paper card is one accessible action. The green Play pill
    // is a visual affordance, not a second overlapping tap target.
    val soloAction: () -> Unit = if (soloComplete) onNewSolo else onSolo
    GridMatesIllustratedFrame(
        modifier = Modifier.fillMaxWidth().heightIn(min = 68.dp)
            .qaTrackBounds("Continue Solo")
            .testTag("ws16-continue-solo")
            .semantics { contentDescription = if (soloComplete)
                "Start a new solo word search" else "Continue saved solo word search" },
        variant = GridMatesFrameVariant.FEATURE,
        onClick = soloAction
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 68.dp)
            .padding(horizontal = 11.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Ws16BActionArt(Ws16BArt.CONTINUE, iconSize = 42.dp)
            // Separate bookmark art from the actual saved Solo progress.
            // The whole card responds to taps, including the Play pill.
            Spacer(Modifier.width(1.dp).height(31.dp)
                .background(Color(0x77B78F47)))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(if (soloComplete) "New Solo" else "Continue Solo",
                    color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
                // Keep progress explicit and on one line at normal phone widths.
                // Grid dimensions remain visible in Custom and the active puzzle.
                Text((if (soloComplete) "" else "Saved: ") + active.theme.label +
                    if (soloComplete) " · Finished" else
                        " · ${active.foundIds.size}/${soloPuzzle.targets.size} words",
                    color = Muted, style = MaterialTheme.typography.labelSmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            GridMatesIllustratedPill(
                label = if (soloComplete) "New" else "Play",
                onClick = null,
                modifier = Modifier.testTag("ws16-resume-solo")
            )
        }
    }
    Ws16BHomeActions(
        onDaily = onDaily,
        onNearby = onNearby,
        onCustom = onNewSolo,
        onMyPuzzles = onMyPuzzles,
        showQuickPlayPreview = showQuickPlayPreview,
        compactHome = compactHome
    )

    if (menuOpen) {
        Ws16BCompactMenu(
            options = listOfNotNull(
                "Display: " + displayMode.label + " · Switch to " +
                    displayMode.alternate().label to {
                        onDisplayModeChange(displayMode.alternate())
                    },
                "Settings & backup" to onSettings,
                "How to play" to onHelp,
                "Records" to onRecords,
                "My Puzzles" to onMyPuzzles,
                if (catalog != null && bookProgress != null &&
                    books.isNotEmpty()) "Full Library" to onFullShelf else null,
                "Custom" to onNewSolo
            ),
            onDismiss = { menuOpen = false }
        )
    }
}
