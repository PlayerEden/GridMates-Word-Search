package com.playereden.gridmates.wordsearch

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BoardViewportTransform
import com.playereden.gridmates.wordsearch.core.LargeBoardViewport
import com.playereden.gridmates.wordsearch.core.LargeBoardBookmark
import com.playereden.gridmates.wordsearch.core.BoardRenderWindow
import com.playereden.gridmates.wordsearch.core.PaperbackPageLayout
import com.playereden.gridmates.wordsearch.core.PaperbackWordIndex
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.DragDirection
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordHintPolicy
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordDragSnap
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import com.playereden.gridmates.wordsearch.core.WordTheme
import kotlin.math.roundToInt

private val FoundMarker = Color(0xFF9FC5AA)
private val PartnerMarker = Color(0xFF91B7DF)
private val ActiveMarker = Color(0xFFE4B957)
private val GridRule = Color(0xFFD8DFD2)

@Composable
internal fun WordSearchScreen(
    store: SoloStore,
    daily: Boolean = false,
    onBack: () -> Unit,
    onRecords: () -> Unit,
    /** A route to separate pre-game setup; never changes a puzzle in-game. */
    onNewPuzzle: (() -> Unit)? = null
) {
    val dailyEntry = if (daily) store.selectedDaily else null
    val run = if (daily) dailyEntry?.run else store.profile.active
    if (run == null) {
        Text("No Daily puzzle selected.", color = Muted)
        TextButton(onClick = onBack) { Text("Back") }
        return
    }
    val theme = run.theme
    val difficulty = run.difficulty
    val puzzle = remember(run.puzzleId) { SoloProgressRules.puzzle(run) }
    val challengeLabel = run.challenge?.label ?: difficulty.label
    val sizeLabel = run.boardSize?.let { "${it.cells}×${it.cells}" } ?:
        "${difficulty.gridSize}×${difficulty.gridSize}"
    val boardFontId = store.effectiveBoardFontId
    val found = run.foundIds
    LaunchedEffect(daily, run.puzzleId) {
        while (true) {
            delay(1000L)
            if (daily) store.tickDaily() else store.tick()
        }
    }
    var status by remember(puzzle.id) {
        mutableStateOf(PuzzleModeHelp.opening(puzzle.size))
    }
    var feelOpen by rememberSaveable { mutableStateOf(false) }
    var cluesOpen by rememberSaveable { mutableStateOf(false) }
    var optionsOpen by rememberSaveable { mutableStateOf(false) }
    var reviewingCompletedBoard by remember(puzzle.id) { mutableStateOf(false) }
    val hapticsEnabled = store.hapticsEnabled
    val soundEnabled = store.soundEnabled
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val hintsRemaining = WordHintPolicy.remaining(run, daily)
    val usedHintSteps = WordHintPolicy.used(run)
    val finished = WordSearchRules.complete(puzzle, found)
    val hintPaths = puzzle.targets.filter { target ->
        target.id !in found && (run.hints[target.id] ?: 0) >= 3
    }.map { it.positions }
    val currentResult = if (daily) null else
        store.profile.results.firstOrNull { it.puzzleId == puzzle.id }
    LaunchedEffect(status, puzzle.id) {
        if (status.startsWith("✓")) {
            delay(2200L)
            status = "Keep going — the next word is waiting."
        }
    }

    if (feelOpen) {
        AlertDialog(
            onDismissRequest = { feelOpen = false },
            title = { Text("Puzzle feedback") },
            text = {
                Column {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Gentle vibration")
                        Switch(checked = hapticsEnabled, onCheckedChange = store::updateHapticsPreference)
                    }
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Word-found click")
                        Switch(checked = soundEnabled, onCheckedChange = store::updateSoundPreference)
                    }
                    Text("Your feedback preferences stay set on this device.",
                        style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            },
            confirmButton = { TextButton(onClick = { feelOpen = false }) { Text("Done") } }
        )
    }

    if (cluesOpen) {
        SavedCluesDialog(puzzle = puzzle, run = run,
            onDismiss = { cluesOpen = false })
    }

    // All screen sizes share the same two-row HUD. The separate word panel
    // consumes remaining measured space rather than being pushed offscreen.
    // Keep a real paperback margin around the puzzle rather than pressing
    // its outer letters against the phone bezel. Dimensions remain based on
    // the square content viewport, not the unpadded screen.
    val pageMargin = PaperbackPageLayout.sideMarginDp(
        LocalConfiguration.current.screenWidthDp
    ).dp
    BoxWithConstraints(Modifier.fillMaxSize().padding(
        horizontal = pageMargin, vertical = 5.dp
    )) {
        val landscape = maxWidth > maxHeight * 1.25f
        val compactHud = maxWidth < 360.dp || LocalDensity.current.fontScale >= 1.5f
        val boardViewport = if (landscape) {
            minOf(maxHeight - when {
                    compactHud -> 248.dp
                    finished && !reviewingCompletedBoard -> 236.dp
                    else -> 212.dp
                }, maxWidth * 0.51f).coerceAtLeast(106.dp)
        } else {
            minOf(maxWidth, maxHeight * when {
                    compactHud -> 0.40f
                    finished && !reviewingCompletedBoard -> 0.43f
                    else -> 0.54f
                })
                .coerceAtLeast(145.dp)
        }

        val select: (GridPoint, GridPoint) -> Unit = { start, end ->
            val result = if (daily) store.dailyClaim(start, end)
                else store.claim(start, end)
            when (result.status) {
                ClaimStatus.FOUND -> {
                    val target = requireNotNull(result.target)
                    status = "✓ " + target.word + " found! · " +
                        (found.size + 1) + " of " + puzzle.targets.size
                    if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                }
                ClaimStatus.ALREADY_FOUND -> status = "Already found. Nice work!"
                ClaimStatus.INVALID -> status = "Try a word from the list."
            }
        }
        val onStep: (Int) -> Unit = { count ->
            if (hapticsEnabled && count > 1 && count % 2 == 0) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Compact/high-font screens must not pack the timer, count and
            // full hint button into one overflowing horizontal row.
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { store.flush(); onBack() }) { Text("Home") }
                Column(Modifier.weight(1f).padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(theme.label, style = MaterialTheme.typography.titleSmall,
                        color = Forest, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!compactHud) {
                        Text((if (daily) "Daily · " else "") +
                            sizeLabel + " · " + challengeLabel,
                            style = MaterialTheme.typography.labelSmall, color = Muted,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Box {
                    TextButton(onClick = { optionsOpen = true }) { Text("Options") }
                    DropdownMenu(expanded = optionsOpen,
                        onDismissRequest = { optionsOpen = false }) {
                        if (!daily && onNewPuzzle != null) {
                            // Return to pre-game setup without replacing the
                            // current saved board until Start is confirmed.
                            DropdownMenuItem(text = { Text("New puzzle") },
                                onClick = {
                                    optionsOpen = false
                                    onNewPuzzle()
                                })
                        }
                        if (usedHintSteps > 0) {
                            DropdownMenuItem(
                                text = { Text("Review saved clues ($usedHintSteps)") },
                                onClick = {
                                    optionsOpen = false
                                    cluesOpen = true
                                })
                        }
                        DropdownMenuItem(text = { Text("Feedback settings") },
                            onClick = {
                                optionsOpen = false
                                feelOpen = true
                            })
                    }
                }
            }
            if (compactHud) {
                Text((if (daily) "Daily · " else "") + sizeLabel + " · " + challengeLabel,
                    style = MaterialTheme.typography.labelSmall, color = Muted,
                    maxLines = 2)
            }
            val timeLabel = (run.elapsedSeconds / 60L).toString() + ":" +
                (run.elapsedSeconds % 60L).toString().padStart(2, '0')
            val hintAction: @Composable () -> Unit = {
                TextButton(onClick = {
                    if (hintsRemaining == 0 && usedHintSteps > 0) {
                        cluesOpen = true
                    } else {
                    val (next, target) = if (daily) store.dailyHint() else {
                        val (updated, word) = store.hint()
                        updated to word
                    }
                    status = if (target == null || next == null) "All words found!" else {
                        when (next.hints[target.id] ?: 0) {
                            1 -> "Try " + target.word + " near row " +
                                (target.positions.first().row + 1) + "."
                            2 -> target.word + " starts at row " +
                                (target.positions.first().row + 1) + ", col " +
                                (target.positions.first().col + 1) + "."
                            else -> "Follow the faint gold guide for " + target.word + "."
                        }
                    }
                    }
                }, enabled = !finished && (hintsRemaining > 0 || usedHintSteps > 0),
                    modifier = Modifier.semantics {
                        contentDescription = if (hintsRemaining == 0 && usedHintSteps > 0)
                            "Review your saved clues. No hints spent."
                        else "Hint. $hintsRemaining of " +
                            WordHintPolicy.allowance(run, daily) + " left for this puzzle."
                    }
                ) {
                    Text(if (hintsRemaining == 0 && usedHintSteps > 0)
                        "Review clues" else "Hint ($hintsRemaining)")
                }
            }
            if (compactHud) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(found.size.toString() + " / " + puzzle.targets.size + " found",
                        style = MaterialTheme.typography.titleSmall,
                        color = Forest, fontWeight = FontWeight.SemiBold)
                    hintAction()
                }
                Text("Elapsed " + timeLabel, color = Muted,
                    style = MaterialTheme.typography.labelSmall)
            } else {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(found.size.toString() + " / " + puzzle.targets.size + " found",
                        style = MaterialTheme.typography.titleSmall,
                        color = Forest, fontWeight = FontWeight.SemiBold)
                    Text(timeLabel, color = Muted,
                        style = MaterialTheme.typography.bodyMedium)
                    hintAction()
                }
            }
            PaperProgress(found.size, puzzle.targets.size)

            // A finished puzzle should open its journal first, not bury
            // Play another / Share below a large (zoomed) board.
            // The board remains available with Review completed board.
            if (finished && !reviewingCompletedBoard) {
                CompletionPanel(
                    puzzle = puzzle, run = run, daily = daily,
                    completedSeconds = currentResult?.elapsedSeconds ?: run.elapsedSeconds,
                    onReview = { reviewingCompletedBoard = true },
                    onRecords = { store.flush(); onRecords() },
                    onHome = { store.flush(); onBack() },
                    onAnother = if (daily) null else onNewPuzzle,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else if (landscape) {
                Row(Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.width(boardViewport),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ZoomableWordGrid(puzzle, found, hintPaths,
                            boardViewport, select, onStep, boardFontId,
                            bookmark = if (daily) null else run.viewportBookmark,
                            onBookmark = { if (!daily) store.saveLargeBoardBookmark(it) },
                            onModeChanged = { moving ->
                                status = PuzzleModeHelp.selected(moving)
                            })
                        PuzzleStatus(status, compact = true)
                    }
                    WordList(puzzle, found, daily,
                        Modifier.weight(1f).fillMaxSize(),
                        onShowResults = if (finished) { { reviewingCompletedBoard = false } }
                            else null)
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ZoomableWordGrid(puzzle, found, hintPaths,
                        boardViewport, select, onStep, boardFontId,
                        bookmark = if (daily) null else run.viewportBookmark,
                        onBookmark = { if (!daily) store.saveLargeBoardBookmark(it) },
                        onModeChanged = { moving ->
                            status = PuzzleModeHelp.selected(moving)
                        })
                }
                PuzzleStatus(status, compact = false)
                WordList(puzzle, found, daily,
                    Modifier.weight(1f).fillMaxWidth(),
                    onShowResults = if (finished) { { reviewingCompletedBoard = false } }
                        else null)
            }
        }
    }
}

/** A small, calm result/hint announcement that does not obstruct the board. */
@Composable
private fun PuzzleStatus(message: String, compact: Boolean) {
    val positive = message.startsWith("✓")
    val hint = message.startsWith("Try ") || message.contains("starts at row") ||
        message.startsWith("Follow the faint")
    Box(
        Modifier.fillMaxWidth()
            .background(
                if (positive) Color(0xFFE5EFE6)
                else if (hint) Color(0xFFF5EDD8)
                else Paper,
                RoundedCornerShape(9.dp)
            )
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(message, color = if (positive) Forest else Ink,
            fontWeight = if (positive) FontWeight.SemiBold else FontWeight.Normal,
            style = MaterialTheme.typography.labelMedium,
            maxLines = if (compact) 1 else 2,
            overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CompletionPanel(
    puzzle: WordSearchPuzzle,
    run: com.playereden.gridmates.wordsearch.core.SoloRun,
    daily: Boolean,
    completedSeconds: Long,
    onReview: () -> Unit,
    onRecords: () -> Unit,
    onHome: () -> Unit,
    onAnother: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var shareText by remember(puzzle.id) { mutableStateOf<String?>(null) }
    val usedHints = run.hints.values.sum()
    val time = (completedSeconds.coerceAtLeast(0L) / 60L).toString() + ":" +
        (completedSeconds.coerceAtLeast(0L) % 60L).toString().padStart(2, '0')

    Column(modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PaperPanel(Modifier.fillMaxWidth()) {
            PaperEyebrow(if (daily) "Daily journal · Completed"
                else "Puzzle journal · Completed")
            Text(if (daily) "Daily complete!" else "Puzzle complete!",
                style = MaterialTheme.typography.headlineMedium,
                color = Forest, fontWeight = FontWeight.Bold)
            Text("Every word is found. Nicely done.",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Text(puzzle.theme.label + " · " + puzzle.size + "×" + puzzle.size +
                " · " + (run.challenge?.label ?: run.difficulty.label),
                color = Gold, style = MaterialTheme.typography.labelLarge)
            PaperProgress(puzzle.targets.size, puzzle.targets.size)
            HorizontalDivider(color = GridRule)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf(
                    "Words" to (puzzle.targets.size.toString() + "/" + puzzle.targets.size),
                    "Time" to time,
                    "Hints" to usedHints.toString()
                ).forEach { (label, amount) ->
                    Column(Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(amount, color = Forest,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(label, color = Muted,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Text(
                if (usedHints == 0) "Unassisted solve · No hints used"
                else "Assisted solve · $usedHints hint " +
                    (if (usedHints == 1) "step" else "steps"),
                color = Muted, style = MaterialTheme.typography.bodyMedium
            )
            Text(if (daily) "Saved in your Daily Journal."
                else "Saved in your puzzle journal.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }

        if (onAnother != null) {
            Button(onClick = onAnother,
                modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Play another puzzle")
            }
        }

        OutlinedButton(onClick = {
            shareText = PuzzleResultShare.text(puzzle, run, completedSeconds, daily)
        }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Share results")
        }

        OutlinedButton(onClick = onReview,
            modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Review completed board")
        }
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onRecords, modifier = Modifier.weight(1f)) {
                Text("Records")
            }
            TextButton(onClick = onHome, modifier = Modifier.weight(1f)) {
                Text("Home")
            }
        }
    }
    shareText?.let { summary ->
        ShareResultPreview(summary, onDismiss = { shareText = null })
    }
}

@Composable
internal fun WordList(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    daily: Boolean,
    modifier: Modifier = Modifier,
    onShowResults: (() -> Unit)? = null
) {
    var remainingOnly by remember(puzzle.id) { mutableStateOf(false) }
    var page by remember(puzzle.id, remainingOnly) { mutableStateOf(0) }
    var indexOpen by remember(puzzle.id) { mutableStateOf(false) }
    val giantWordList = PaperbackWordIndex.isGiant(puzzle.targets.size)
    // Keep the existing placement-order presentation for all released boards.
    // Giant boards only: alphabetize a display copy, never saved target order.
    val targets = if (giantWordList)
        PaperbackWordIndex.visibleWords(puzzle.targets, found, remainingOnly)
    else if (remainingOnly) puzzle.targets.filter { it.id !in found }
    else puzzle.targets

    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Words to find · " + (puzzle.targets.size - found.size) + " left",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold, color = Forest)
            if (onShowResults != null) {
                TextButton(onClick = onShowResults) { Text("Results") }
            } else {
                FilterChip(selected = remainingOnly,
                    onClick = { remainingOnly = !remainingOnly },
                    label = { Text("Remaining") })
            }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val enlarged = LocalDensity.current.fontScale >= 1.3f
            val columns = if (maxWidth < 320.dp || enlarged) 1 else 2
            // The list is paged instead of scrollable. Keep all visible
            // targets within the space left below the fixed puzzle board.
            // Two-line entries must never spill under the pager. Keep more
            // vertical space than the old single-line pill layout allowed.
            val rowBudget = if (enlarged) 86.dp else 54.dp
            val rowsPerPage = ((maxHeight - 40.dp) / rowBudget)
                .toInt().coerceIn(1, 5)
            val perPage = columns * rowsPerPage
            val pageCount = ((targets.size + perPage - 1) / perPage).coerceAtLeast(1)
            val visiblePage = page.coerceIn(0, pageCount - 1)
            val visibleTargets = targets.drop(visiblePage * perPage).take(perPage)
            val visibleRange = PaperbackWordIndex.visibleRange(targets.size, visiblePage, perPage)
            Column(Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (giantWordList) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        val rangeLabel = if (targets.isEmpty()) "No words remaining"
                            else "Words ${visibleRange.first}–${visibleRange.last} of ${targets.size}"
                        Text(rangeLabel, modifier = Modifier.weight(1f)
                            .testTag("word-list-range"),
                            color = Muted, style = MaterialTheme.typography.labelSmall,
                            maxLines = 2)
                        TextButton(onClick = { indexOpen = true },
                            modifier = Modifier.testTag("word-list-az-index")) {
                            Text("A–Z index")
                        }
                    }
                }
                if (targets.isEmpty()) {
                    Text("All words found!", color = Forest,
                        style = MaterialTheme.typography.bodyMedium)
                } else {
                    visibleTargets.chunked(columns).forEach { pair ->
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pair.forEach { target ->
                                val solved = target.id in found
                                // Printed book-style square entries, not
                                // rounded pills which visually trim letters.
                                Box(Modifier.weight(1f)
                                    .background(if (solved) FoundMarker.copy(alpha = 0.28f)
                                        else Paper)
                                    .border(1.dp,
                                        if (solved) Color(0xFFC5D9C8) else GridRule)
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text((if (solved) "✓ " else "○ ") + target.word,
                                        color = if (solved) Forest else Ink,
                                        fontWeight = if (solved) FontWeight.Normal
                                            else FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textDecoration = if (solved)
                                            TextDecoration.LineThrough else null,
                                        maxLines = 2,
                                        overflow = TextOverflow.Clip)
                                }
                            }
                            if (pair.size < columns) Box(Modifier.weight(1f))
                        }
                    }
                }
                if (pageCount > 1) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { page = visiblePage - 1 },
                            enabled = visiblePage > 0) { Text("Previous") }
                        Text("Page " + (visiblePage + 1) + " of " + pageCount,
                            color = Muted, style = MaterialTheme.typography.labelSmall)
                        TextButton(onClick = { page = visiblePage + 1 },
                            enabled = visiblePage < pageCount - 1) { Text("Next") }
                    }
                }
            }
            if (giantWordList && indexOpen) {
                val letters = PaperbackWordIndex.availableLetters(targets)
                AlertDialog(
                    onDismissRequest = { indexOpen = false },
                    title = { Text("Find words by letter", color = Forest) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Jump to the first word beginning with a letter. " +
                                "Only letters on this list are shown.",
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                            if (letters.isEmpty()) {
                                Text("All words found!", color = Forest)
                            } else {
                                Column(Modifier.heightIn(max = 360.dp)
                                    .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    letters.chunked(4).forEach { rowLetters ->
                                        Row(Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            rowLetters.forEach { letter ->
                                                TextButton(
                                                    onClick = {
                                                        page = PaperbackWordIndex.pageForLetter(
                                                            targets, letter, perPage) ?: visiblePage
                                                        indexOpen = false
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                        .heightIn(min = 48.dp)
                                                        .testTag("word-list-letter-$letter")
                                                ) { Text(letter.toString()) }
                                            }
                                            repeat(4 - rowLetters.size) {
                                                Box(Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { indexOpen = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Touch-first board. The whole puzzle starts fitted, then users can pinch
 * with two fingers to zoom/pan while one finger always selects words.
 * On-screen zoom buttons remain for switch access / people unable to pinch.
 * The grid itself owns taps and drags; no transparent click targets steal
 * physical word-selection gestures.
 */
@Composable
internal fun ZoomableWordGrid(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    hintPaths: List<List<GridPoint>>,
    viewport: Dp,
    onSelect: (GridPoint, GridPoint) -> Unit,
    onStep: (Int) -> Unit,
    boardFontId: String,
    partnerFoundIds: Set<String> = emptySet(),
    onModeChanged: (Boolean) -> Unit = {},
    boardLookId: String = "classic",
    bookmark: LargeBoardBookmark? = null,
    onBookmark: (LargeBoardBookmark) -> Unit = {}
) {
    // Allocate a truly square board using the height actually available AFTER
    // the toolbar. A requested 320dp square previously got squeezed to a shorter
    // rectangle on compact surfaces, disagreeing with letter and swipe geometry.
    var preciseOpen by remember(puzzle.id) { mutableStateOf(false) }
    val largeBoard = puzzle.size >= 25
    BoxWithConstraints(Modifier.width(viewport)) {
        // Large boards get one extra map/position line below the toolbar.
        val toolbarSpace = if (largeBoard) 94.dp else 56.dp
        val side = if (maxHeight == Dp.Infinity) viewport
            else minOf(viewport, (maxHeight - toolbarSpace).coerceAtLeast(96.dp))
        val sidePx = with(LocalDensity.current) { side.toPx() }
        // Keep the original 2.5x transform untouched for legacy boards.
        var transform by remember(puzzle.id, sidePx) {
            mutableStateOf(BoardViewportTransform(1f))
        }
        var largeTransform by remember(puzzle.id, sidePx) {
            mutableStateOf(bookmark?.restoreFor(puzzle.id, sidePx)
                ?: LargeBoardViewport())
        }
        // Commit only at the end of a gesture or on a deliberate zoom/map
        // action. Never write to disk for every finger-motion frame.
        fun rememberView() {
            if (largeBoard) onBookmark(
                LargeBoardBookmark.capture(puzzle.id, largeTransform, sidePx)
            )
        }
        var regionOpen by remember(puzzle.id) { mutableStateOf(false) }
        val currentZoom = if (largeBoard) largeTransform.zoom else transform.zoom
        var multiTouch by remember(puzzle.id) { mutableStateOf(false) }

        Column(Modifier.width(viewport),
            verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Trace words · pinch to zoom",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = Muted, maxLines = 2)
                TextButton(
                    onClick = { preciseOpen = true },
                    modifier = Modifier.semantics {
                        contentDescription = "Select a word without dragging"
                    }
                ) { Text("Select", maxLines = 1) }
                IconButton(onClick = {
                    if (largeBoard) {
                        largeTransform = largeTransform.zoomed(
                            largeTransform.zoom - 0.75f, sidePx)
                        rememberView()
                    } else {
                        transform = transform.zoomed(transform.zoom - 0.25f, sidePx)
                    }
                }, enabled = currentZoom > 1f,
                    modifier = Modifier.size(48.dp).semantics {
                        contentDescription = "Zoom out"
                    }) { Text("−", style = MaterialTheme.typography.titleMedium) }
                IconButton(onClick = {
                    if (largeBoard) {
                        largeTransform = LargeBoardViewport()
                        rememberView()
                    } else transform = BoardViewportTransform(1f)
                }, modifier = Modifier.size(48.dp).semantics {
                    contentDescription = "Fit entire puzzle board"
                }) { Text("Fit") }
                IconButton(onClick = {
                    if (largeBoard) {
                        largeTransform = largeTransform.zoomed(
                            largeTransform.zoom + 0.75f, sidePx)
                        rememberView()
                    } else {
                        transform = transform.zoomed(transform.zoom + 0.25f, sidePx)
                    }
                }, enabled = currentZoom <
                    (if (largeBoard) LargeBoardViewport.MAX_ZOOM else 2.5f),
                    modifier = Modifier.size(48.dp).semantics {
                        contentDescription = "Zoom in"
                    }) { Text("+", style = MaterialTheme.typography.titleMedium) }
            }
            if (largeBoard) {
                // The map is a navigation aid, not a partition of the puzzle.
                // Moving via the map does not touch any found-word state.
                val rows = largeTransform.visibleRows(sidePx, puzzle.size)
                val cols = largeTransform.visibleColumns(sidePx, puzzle.size)
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Rows ${rows.first+1}–${rows.last+1} · " +
                        "Cols ${cols.first+1}–${cols.last+1} · " +
                        "${String.format(java.util.Locale.US, "%.1f", largeTransform.zoom)}×",
                        color = Muted, style = MaterialTheme.typography.labelSmall,
                        maxLines = 1, modifier = Modifier.weight(1f))
                    TextButton(onClick = { regionOpen = true },
                        modifier = Modifier.semantics {
                            contentDescription = "Open board region map"
                        }) { Text("Map") }
                }
            }
            Box(Modifier.size(side).align(Alignment.CenterHorizontally)
                .clipToBounds()
                .pointerInput(puzzle.id, sidePx) {
                    awaitEachGesture {
                        var sawMulti = false
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.changes.count { it.pressed } >= 2) {
                                sawMulti = true
                                multiTouch = true
                                val scale = event.calculateZoom()
                                val pan = event.calculatePan()
                                val center = event.calculateCentroid(useCurrent = false)
                                if (center != Offset.Unspecified) {
                                    if (largeBoard) {
                                        largeTransform = largeTransform.zoomedAt(
                                            largeTransform.zoom * scale, sidePx,
                                            center.x, center.y
                                        ).panned(pan.x, pan.y, sidePx)
                                    } else {
                                        transform = transform.zoomedAt(
                                            transform.zoom * scale, sidePx,
                                            center.x, center.y
                                        ).panned(pan.x, pan.y, sidePx)
                                    }
                                }
                                event.changes.forEach { it.consume() }
                            } else if (sawMulti) {
                                event.changes.forEach { it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                        if (sawMulti && largeBoard) rememberView()
                        multiTouch = false
                    }
                }
            ) {
                // At Fit draw every cell as before. Once zoomed, only
                // visible/one-cell-overscan ink and rules are sent to Canvas.
                // Hit testing still works in full puzzle coordinates.
                val drawWindow = if (largeBoard && currentZoom > 1f)
                    BoardRenderWindow.around(largeTransform, sidePx, puzzle.size)
                    else null
                WordGrid(
                    puzzle, found, hintPaths,
                    modifier = Modifier.size(side).graphicsLayer {
                        scaleX = currentZoom
                        scaleY = currentZoom
                        transformOrigin = TransformOrigin(0f, 0f)
                        translationX = if (largeBoard) largeTransform.panX else transform.panX
                        translationY = if (largeBoard) largeTransform.panY else transform.panY
                    },
                    onSelect = onSelect, onStep = onStep,
                    boardFontId = boardFontId,
                    partnerFoundIds = partnerFoundIds,
                    boardLookId = boardLookId,
                    renderWindow = drawWindow,
                    multiTouchActive = multiTouch,
                    onPreciseRequest = { preciseOpen = true }
                )
            }
        }
        if (largeBoard && regionOpen) {
            LargeBoardRegionDialog(
                cells = puzzle.size, viewport = largeTransform, sidePx = sidePx,
                onJumpToRegion = { rowRegion, colRegion ->
                    largeTransform = largeTransform.jumpToRegion(
                        rowRegion, colRegion, puzzle.size, sidePx)
                    rememberView()
                    regionOpen = false
                },
                onDismiss = { regionOpen = false }
            )
        }
    }
    if (preciseOpen) {
        PreciseWordPicker(puzzle, onSelect = onSelect,
            onDismiss = { preciseOpen = false })
    }

}

@Composable
private fun WordGrid(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    hintPaths: List<List<GridPoint>>,
    modifier: Modifier = Modifier,
    onSelect: (GridPoint, GridPoint) -> Unit,
    onStep: (Int) -> Unit,
    boardFontId: String,
    partnerFoundIds: Set<String> = emptySet(),
    boardLookId: String = "classic",
    renderWindow: BoardRenderWindow? = null,
    multiTouchActive: Boolean = false,
    onPreciseRequest: () -> Unit = {}
) {
    val palette = BoardLookPalettes.forId(boardLookId)
    val glyphPainter = rememberBoardGlyphPainter(boardFontId)
    var sizePx by remember(puzzle.id) { mutableStateOf(IntSize.Zero) }
    var dragStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var dragEnd by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var tapStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    val latestOnSelect by rememberUpdatedState(onSelect)
    val latestOnStep by rememberUpdatedState(onStep)
    val latestMultiTouch by rememberUpdatedState(multiTouchActive)

    fun selectCell(cell: GridPoint) {
        if (latestMultiTouch) return
        val previous = tapStart
        if (previous == null || previous == cell) tapStart = if (previous == cell) null else cell
        else {
            latestOnSelect(previous, cell)
            tapStart = null
        }
    }

    fun point(offset: Offset): GridPoint? {
        if (sizePx.width <= 0 || sizePx.height <= 0 || offset.x < 0f || offset.y < 0f ||
            offset.x >= sizePx.width || offset.y >= sizePx.height) return null
        return GridPoint(
            (offset.y * puzzle.size / sizePx.height).toInt().coerceIn(0, puzzle.size - 1),
            (offset.x * puzzle.size / sizePx.width).toInt().coerceIn(0, puzzle.size - 1)
        )
    }

    val confirmed = remember(puzzle.id, found, partnerFoundIds) {
        puzzle.targets.filter { it.id in found }.map { target ->
            target.positions to (target.id in partnerFoundIds)
        }
    }
    val active = remember(puzzle.id, dragStart, dragEnd, tapStart) {
        when {
            dragStart != null && dragEnd != null ->
                WordSearchRules.line(dragStart!!, dragEnd!!, puzzle.size)
            tapStart != null -> listOf(tapStart!!)
            else -> emptyList()
        }
    }

    Box(
        modifier = modifier.aspectRatio(1f)
            // The full corner cells are square like a printed puzzle book.
            // Rounded corners hide parts of corner letters/selection traces.
            .background(palette.background)
            .drawBehind {
                // Same full-board coordinates as selection geometry, but
                // zoomed large boards only issue draw calls near the screen.
                val cell = size.width / puzzle.size
                val rowCell = size.height / puzzle.size
                val thinRule = 0.6.dp.toPx()
                val rowsToDraw = renderWindow?.rows ?: (0 until puzzle.size)
                val colsToDraw = renderWindow?.columns ?: (0 until puzzle.size)
                val top = rowsToDraw.first * rowCell
                val bottom = (rowsToDraw.last + 1) * rowCell
                val left = colsToDraw.first * cell
                val right = (colsToDraw.last + 1) * cell
                for (index in (colsToDraw.first + 1)..colsToDraw.last) {
                    val x = index * cell
                    drawLine(palette.lines, Offset(x, top),
                        Offset(x, bottom), thinRule)
                }
                for (index in (rowsToDraw.first + 1)..rowsToDraw.last) {
                    val y = index * rowCell
                    drawLine(palette.lines, Offset(left, y),
                        Offset(right, y), thinRule)
                }
                fun center(p: GridPoint) = Offset((p.col + 0.5f) * cell, (p.row + 0.5f) * cell)
                hintPaths.forEach { path ->
                    if (path.size > 1 &&
                        (renderWindow == null || renderWindow.mayIntersect(path))) drawLine(
                        ActiveMarker.copy(alpha = 0.22f),
                        center(path.first()), center(path.last()),
                        strokeWidth = cell * 0.38f,
                        cap = StrokeCap.Round
                    )
                }
                confirmed.forEach { (path, isPartner) ->
                    if (path.size > 1 &&
                        (renderWindow == null || renderWindow.mayIntersect(path))) drawLine(
                        (if (isPartner) PartnerMarker else FoundMarker).copy(alpha = 0.58f),
                        center(path.first()), center(path.last()),
                        strokeWidth = cell * 0.54f,
                        cap = StrokeCap.Round
                    )
                }
                when {
                    active.size > 1 &&
                        (renderWindow == null || renderWindow.mayIntersect(active)) -> drawLine(
                        ActiveMarker.copy(alpha = 0.66f),
                        center(active.first()), center(active.last()),
                        strokeWidth = cell * 0.58f,
                        cap = StrokeCap.Round
                    )
                    active.size == 1 &&
                        (renderWindow == null || renderWindow.mayIntersect(active)) -> drawCircle(
                        ActiveMarker.copy(alpha = 0.52f),
                        radius = cell * 0.30f,
                        center = center(active.first())
                    )
                }
                // Type-specific real ink bounds determine optical centering.
                // No independent fontScale: all content is enlarged by the
                // whole-board graphics transform above.
                glyphPainter.drawLetters(this, puzzle.rows, palette.letters, renderWindow)
            }
            .border(2.dp, palette.frame)
            .onSizeChanged { sizePx = it }
            .testTag("word-grid-touch-area")
            // One meaningful accessibility target rather than 400 small
            // cells. The row/column picker supports non-drag input.
            .semantics {
                contentDescription = "Word-search grid, ${puzzle.size} by ${puzzle.size}. " +
                    "Drag or tap letters, or choose precise selection."
                onClick(label = "Select a word by row and column") {
                    onPreciseRequest()
                    true
                }
            }
            .pointerInput(puzzle.id) {
                awaitEachGesture {
                    // Capture the actual DOWN cell, not the coordinate after
                    // Android's drag touch-slop. The latter can be a full
                    // letter away on dense 15x15/20x20 boards.
                    val down = awaitFirstDown(requireUnconsumed = false)
                    // Claim the initial press before a scrollable parent takes it.
                    down.consume()
                    val firstCell = point(down.position)
                    var latestCell = firstCell
                    var cancelled = firstCell == null
                    var sawSecondFinger = false
                    if (firstCell != null) {
                        dragStart = firstCell
                        dragEnd = firstCell
                    }
                    var active = true
                    while (active) {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.firstOrNull { it.id == down.id }
                        if (event.changes.count { it.pressed } >= 2 ||
                            event.changes.any { it.id != down.id && it.pressed } ||
                            latestMultiTouch) {
                            cancelled = true
                            sawSecondFinger = true
                        }
                        if (pointer == null) {
                            active = false
                        } else {
                            if (!cancelled && firstCell != null) {
                                // The UP event can carry the exact final cell;
                                // processing moves only often stopped one
                                // letter short on physical drags.
                                if (pointer.pressed) pointer.consume()
                                val cellWidth = sizePx.width.toFloat() / puzzle.size
                                if (cellWidth > 0f) {
                                    val cellHeight = sizePx.height.toFloat() / puzzle.size
                                    val dx = (pointer.position.x -
                                        (firstCell.col + 0.5f) * cellWidth) / cellWidth
                                    val dy = (pointer.position.y -
                                        (firstCell.row + 0.5f) * cellHeight) / cellHeight
                                    // Reevaluate against the complete displacement, not
                                    // a prematurely locked 0.55-cell direction. Otherwise
                                    // small initial finger wobble can force a long
                                    // horizontal word into an invalid 1-cell diagonal.
                                    val snap = WordDragSnap.update(firstCell, dx, dy,
                                        puzzle.size)
                                    if (snap.end != latestCell) {
                                        latestCell = snap.end
                                        dragEnd = snap.end
                                        latestOnStep(snap.length)
                                    }
                                }
                            }
                            if (!pointer.pressed) active = false
                        }
                    }
                    if (!cancelled && !latestMultiTouch && firstCell != null) {
                        if (latestCell != null && latestCell != firstCell) {
                            tapStart = null
                            latestOnSelect(firstCell, latestCell)
                        } else if (!sawSecondFinger) {
                            selectCell(firstCell)
                        }
                    }
                    dragStart = null
                    dragEnd = null
                }
            }
    ) {
        // Letter ink and grid lines are already drawn on the parent Canvas.
        // No 400 empty child boxes / TalkBack focus stops are required.
        // Physical pointer drag and two-tap selection still work unchanged.
    }
}
