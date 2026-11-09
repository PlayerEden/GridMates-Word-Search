package com.playereden.gridmates.wordsearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import com.playereden.gridmates.wordsearch.core.WordTheme
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import kotlinx.coroutines.delay

/**
 * WS-14G QA layout specimen: use the existing theme's miniature emblem
 * without changing word-grid touch geometry, page content or save data.
 * When accessibility text is enlarged or width is tight, native text wins.
 * Completed and bookmarked pages already have a themed status illustration.
 */
internal fun shouldShowBookPageHeaderEmblem(
    qaArtworkEnabled: Boolean,
    completed: Boolean,
    bookmarked: Boolean,
    viewportWidthDp: Float,
    fontScale: Float
): Boolean = qaArtworkEnabled && !completed && !bookmarked &&
    viewportWidthDp >= 320f && fontScale < 1.5f

/**
 * WS-11D2B — a real book-page playing screen, independent of SoloStore.
 *
 * Shares the EXACT ZoomableWordGrid, precise-selection and WordList components
 * with Free Play/Daily; no duplicate gesture algorithm and no fake Solo route.
 * Real catalogs and isolated book stores are injected by an eventual release
 * host. MainActivity and GridMatesShell intentionally DO NOT instantiate this
 * screen while the first publisher-approved books are still unavailable.
 *
 * Completed pages can be replayed only in practice mode, in memory, without
 * modifying GMWB book completion or the GMWS partial-session file.
 */
@Composable
internal fun BookPageGameplayScreen(
    catalog: BookCatalog,
    bookId: String,
    pageId: String,
    sessionStore: BookPageSessionStore,
    progressStore: BookLibraryStore,
    boardFontId: String,
    onBack: () -> Unit,
    onNextPage: ((bookId: String, pageId: String) -> Unit)? = null,
    showPrototypeArt: Boolean = false,
    fieldNotesArt: Boolean = false
) {
    BackHandler(onBack = onBack)
    val book = catalog.book(bookId)
    val page = catalog.page(bookId, pageId)
    val board = remember(catalog, bookId, pageId) {
        runCatching { BookPageSessionRules.puzzle(catalog, bookId, pageId) }.getOrNull()
    }

    var initialized by remember(catalog, bookId, pageId) { mutableStateOf(false) }
    var error by remember(catalog, bookId, pageId) { mutableStateOf<String?>(null) }
    var revision by remember(catalog, bookId, pageId) { mutableIntStateOf(0) }
    // Do not reserve screen height for a generic instruction. Show only meaningful feedback.
    var status by remember(catalog, bookId, pageId) { mutableStateOf("") }
    var practice by remember(catalog, bookId, pageId) { mutableStateOf(false) }
    var practiceFound by remember(catalog, bookId, pageId) { mutableStateOf(emptySet<String>()) }

    // Session construction and solved-sidecar recovery happen outside Compose
    // rendering; an invalid publisher manifest leaves previous files untouched.
    LaunchedEffect(catalog, bookId, pageId, sessionStore, progressStore) {
        initialized = false
        when {
            board == null || book == null || page == null -> {
                error = "This book page is unavailable or has changed. Your saves were preserved."
            }
            sessionStore.blocked || progressStore.blocked -> {
                error = sessionStore.warning ?: progressStore.warning
                    ?: "Your book progress is protected and cannot currently be opened."
            }
            !sessionStore.recoverSolvedPages(progressStore) -> {
                error = sessionStore.warning ?: progressStore.warning
                    ?: "A saved book page needs recovery before you continue."
            }
            else -> {
                val alreadyCompleted = pageId in
                    progressStore.progress.volumes[bookId]?.completedPageIds.orEmpty()
                if (alreadyCompleted || sessionStore.open(bookId, pageId)) {
                    error = null
                    initialized = true
                    revision++
                } else {
                    error = sessionStore.warning
                        ?: "This book page could not be saved. Previous progress was preserved."
                }
            }
        }
    }

    // A five-second foreground checkpoint avoids writing every animation frame.
    // Word claims and hints are written IMMEDIATELY, before success is shown.
    val committed = remember(revision, initialized, bookId, pageId) {
        pageId in progressStore.progress.volumes[bookId]?.completedPageIds.orEmpty()
    }
    val active = remember(revision, initialized, bookId, pageId) {
        sessionStore.session(bookId, pageId)
    }
    val wordsFound = when {
        board == null -> emptySet()
        practice && committed -> practiceFound
        committed -> board.targets.map { it.id }.toSet()
        else -> active?.foundIds.orEmpty()
    }
    val finished = board != null && WordSearchRules.complete(board, wordsFound)
    val canEdit = initialized && !committed && !practice && !sessionStore.blocked &&
        !progressStore.blocked && active != null

    LaunchedEffect(initialized, canEdit, finished, bookId, pageId, sessionStore) {
        if (canEdit && !finished) {
            while (true) {
                delay(5_000L)
                if (sessionStore.tick(bookId, pageId, 5L)) revision++
                else {
                    error = sessionStore.warning ?: "This page could not be saved."
                    break
                }
            }
        }
    }

    fun select(start: GridPoint, end: GridPoint) {
        if (board == null || !initialized || error != null) return
        val claim = when {
            practice && committed -> WordSearchRules.claim(board, practiceFound, start, end)
            canEdit -> sessionStore.claim(bookId, pageId, start, end)
            else -> return
        }
        when (claim.status) {
            ClaimStatus.FOUND -> {
                val word = requireNotNull(claim.target)
                if (practice) {
                    practiceFound = practiceFound + word.id
                    status = if (WordSearchRules.complete(board, practiceFound))
                        "Practice complete! Your book results were not changed."
                    else "✓ " + word.word + " found in practice."
                } else {
                    // The word was already durably written to GMWS by claim().
                    val current = sessionStore.session(bookId, pageId)
                    if (current != null && BookPageSessionRules.complete(catalog, current)) {
                        val finalized = sessionStore.finalizePage(progressStore, bookId, pageId)
                        revision++
                        val awardCommitted = pageId in
                            progressStore.progress.volumes[bookId]?.completedPageIds.orEmpty()
                        status = if (awardCommitted) {
                            if (finalized) "Book page complete! Continue when you're ready."
                            else "Page completed and saved. Cleanup will retry safely."
                        } else "Page solved and saved. The completion record needs recovery."
                    } else {
                        revision++
                        status = "✓ " + word.word + " found!"
                    }
                }
            }
            ClaimStatus.ALREADY_FOUND -> status = "You've already found that word."
            ClaimStatus.INVALID -> status = if (sessionStore.warning != null)
                "The selection could not be saved. Your earlier progress was preserved."
                else "Try selecting a word from the list."
        }
    }

    if (!initialized || board == null || book == null || page == null || error != null) {
        Column(
            Modifier.fillMaxSize().background(Cream).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Back to book")
            }
            PaperEyebrow("GridMates · Puzzle Books")
            Text(if (error != null) "Page unavailable" else "Opening book page",
                color = Forest, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text(error ?: "Checking the exact puzzle and saved progress…",
                color = Ink, style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val hintPaths = if (committed || practice) emptyList()
        else board.targets.filter {
            it.id !in wordsFound && (active?.hints?.get(it.id) ?: 0) >= 3
        }.map { it.positions }

    val next = if (committed) {
        runCatching {
            BookProgressRules.continuePage(catalog, progressStore.progress, bookId)
        }.getOrNull()
    } else null

    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(if (showPrototypeArt) bookPageThemePaperTint(book.theme) else Cream)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Resolve once in this BoxWithConstraints scope: nested Row scopes cannot
        // access the implicit maxWidth receiver (previous WS-14G compile error).
        val contentWidthDp = maxWidth.value
        val landscape = maxWidth > maxHeight * 1.2f
        val enlarged = LocalDensity.current.fontScale >= 1.5f
        val viewport = if (landscape) {
            minOf(maxWidth * 0.50f, maxHeight * if (enlarged) 0.62f else 0.74f)
                .coerceAtLeast(130.dp)
        } else {
            minOf(maxWidth, maxHeight * if (enlarged) 0.39f else 0.49f)
                .coerceAtLeast(130.dp)
        }
        val choose: (GridPoint, GridPoint) -> Unit = { start, end -> select(start, end) }
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Back to book")
                }
                Text(
                    if (practice) "Practice · unsaved" else
                        if (committed) "Completed page" else "Progress saved on device",
                    color = Muted, style = MaterialTheme.typography.labelMedium
                )
            }
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                val bookmarked = pageId ==
                    progressStore.progress.volumes[bookId]?.bookmarkedPageId
                if (showPrototypeArt && committed) {
                    BookArt(book.theme, BookArtKind.COMPLETION, Modifier.width(39.dp).height(39.dp),
                        fieldNotes = fieldNotesArt)
                } else if (showPrototypeArt && bookmarked) {
                    BookArt(book.theme, BookArtKind.BOOKMARK, Modifier.width(17.dp).height(49.dp),
                        fieldNotes = fieldNotesArt)
                } else if (shouldShowBookPageHeaderEmblem(
                        showPrototypeArt, committed, bookmarked,
                        contentWidthDp, LocalDensity.current.fontScale
                    )) {
                    // Source-backed WS-12 emblem, matched by book.theme.
                    // This is a QA art-slot test, NOT a new generated sprite.
                    BookArt(
                        book.theme, BookArtKind.EMBLEM,
                        Modifier.size(32.dp).testTag("ws14g-book-page-theme-emblem"),
                        fieldNotes = fieldNotesArt
                    )
                }
                Text(book.title.removeSuffix(" · TEST BOOK") + " · Page " + (
                    catalog.pages(bookId).indexOfFirst { it.id == pageId } + 1),
                    color = Forest, fontWeight = FontWeight.Bold,
                    style = if (enlarged) MaterialTheme.typography.titleMedium
                        else MaterialTheme.typography.titleLarge,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f))
                if (!practice && !committed) {
                    // Icon-only hint, with a full 48dp touch target and an
                    // accessibility description. Same durable hint policy.
                    BookPageHintButton(enabled = canEdit, onClick = {
                        val hint = sessionStore.hint(bookId, pageId)
                        if (hint == null) {
                            status = "No hints available. Keep looking!"
                        } else {
                            revision++
                            val step = sessionStore.session(bookId, pageId)
                                ?.hints?.get(hint.id) ?: 1
                            status = when (step) {
                                1 -> "Hint: look for a " + hint.word.length + "-letter word."
                                2 -> "Hint: " + hint.word + " starts in row " +
                                    (hint.positions.first().row + 1) + "."
                                else -> "Hint: " + hint.word + " is highlighted on the grid."
                            }
                        }
                    })
                }
            }
            Text(
                wordsFound.size.toString() + " of " + board.targets.size +
                    " words found · " +
                    ((active?.elapsedSeconds ?: 0L) / 60L).toString() + "m " +
                    ((active?.elapsedSeconds ?: 0L) % 60L).toString() + "s",
                color = Ink, style = MaterialTheme.typography.bodyMedium
            )
            PaperProgress(wordsFound.size, board.targets.size)
            if (committed && !practice) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            practice = true
                            practiceFound = emptySet()
                            status = "Practice replay will not change your book progress."
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) { Text("Practice again") }
                    if (next != null && onNextPage != null) {
                        Button(
                            onClick = { onNextPage(bookId, next.id) },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) { Text("Next page") }
                    }
                }
            }
            if (practice) {
                OutlinedButton(
                    onClick = { practice = false; practiceFound = emptySet() },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) { Text("Finish practice") }
            }
            // An instruction is not a status; show feedback only after a real
            // hint, claim, error or mode change.
            if (status.isNotBlank()) {
                Text(status, style = MaterialTheme.typography.bodyMedium,
                    color = Forest, maxLines = if (enlarged) 3 else 2)
            }
            if (landscape) {
                Row(Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.width(viewport), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ZoomableWordGrid(board, wordsFound, hintPaths, viewport,
                            choose, onStep = {}, boardFontId = boardFontId)
                    }
                    WordList(board, wordsFound, daily = false,
                        modifier = Modifier.weight(1f).fillMaxHeight(), scrollAll = true)
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ZoomableWordGrid(board, wordsFound, hintPaths, viewport,
                        choose, onStep = {}, boardFontId = boardFontId)
                }
                WordList(board, wordsFound, daily = false,
                    modifier = Modifier.fillMaxWidth().weight(1f), scrollAll = true)
            }
        }
    }
}
