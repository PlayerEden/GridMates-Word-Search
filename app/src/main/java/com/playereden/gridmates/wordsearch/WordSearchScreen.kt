package com.playereden.gridmates.wordsearch

import android.content.Intent
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BoardViewportTransform
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
    onRecords: () -> Unit
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
        mutableStateOf("Drag a word, or tap its first and last letters.")
    }
    var changeOpen by rememberSaveable { mutableStateOf(false) }
    var feelOpen by rememberSaveable { mutableStateOf(false) }
    var newPuzzleOpen by rememberSaveable { mutableStateOf(false) }
    var optionsOpen by rememberSaveable { mutableStateOf(false) }
    var reviewingCompletedBoard by remember(puzzle.id) { mutableStateOf(false) }
    var pendingTheme by rememberSaveable { mutableStateOf(theme.name) }
    var pendingBoard by rememberSaveable {
        mutableStateOf(
            run.boardSize?.takeIf { it in PuzzleBoardSize.bookPresets }?.name
                ?: PuzzleBoardSize.BOOK_CLASSIC.name
        )
    }
    var pendingChallenge by rememberSaveable {
        mutableStateOf(
            (run.challenge ?: when(difficulty) {
                WordDifficulty.EASY -> PuzzleChallenge.RELAXED
                WordDifficulty.MEDIUM -> PuzzleChallenge.CLASSIC
                WordDifficulty.HARD -> PuzzleChallenge.EXPERT
            }).name
        )
    }
    val hapticsEnabled = store.hapticsEnabled
    val soundEnabled = store.soundEnabled
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val hintsRemaining = WordHintPolicy.remaining(run, daily)
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

    fun startNew() {
        store.newPuzzle()
        newPuzzleOpen = false
    }

    if (changeOpen && !daily) {
        AlertDialog(
            onDismissRequest = { changeOpen = false },
            title = { Text("Choose a puzzle") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text("Theme", style = MaterialTheme.typography.titleSmall, color = Forest)
                    WordTheme.entries.forEach { item ->
                        Row(Modifier.fillMaxWidth().clickable { pendingTheme = item.name },
                            verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = pendingTheme == item.name,
                                onClick = { pendingTheme = item.name })
                            Text(item.label)
                        }
                    }
                    Text("Board size", style = MaterialTheme.typography.titleSmall, color = Forest)
                    PuzzleBoardSize.bookPresets.forEach { item ->
                        Row(Modifier.fillMaxWidth().clickable { pendingBoard = item.name },
                            verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = pendingBoard == item.name,
                                onClick = { pendingBoard = item.name })
                            Text("${item.label} · ${item.cells}×${item.cells} · ${item.targets} words")
                        }
                    }
                    Text("Larger boards use zoom and Move mode so letters remain readable.",
                        style = MaterialTheme.typography.bodySmall, color = Muted)
                    Text("Challenge", style = MaterialTheme.typography.titleSmall, color = Forest)
                    PuzzleChallenge.entries.forEach { item ->
                        Row(Modifier.fillMaxWidth().clickable { pendingChallenge = item.name },
                            verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = pendingChallenge == item.name,
                                onClick = { pendingChallenge = item.name })
                            Column {
                                Text(item.label)
                                Text(item.description, color = Muted,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Text("Board size sets the space and number of words. Challenge changes " +
                        "the directions and word mix. Starting replaces your unfinished Solo board.",
                        style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    store.chooseV2(WordTheme.valueOf(pendingTheme),
                        PuzzleBoardSize.valueOf(pendingBoard),
                        PuzzleChallenge.valueOf(pendingChallenge))
                    changeOpen = false
                }) { Text("Start puzzle") }
            },
            dismissButton = { TextButton(onClick = { changeOpen = false }) { Text("Cancel") } }
        )
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

    if (newPuzzleOpen && !daily) {
        AlertDialog(
            onDismissRequest = { newPuzzleOpen = false },
            title = { Text("Start another puzzle?") },
            text = { Text("Starting a new puzzle replaces your unfinished board. Completed results remain in Records.") },
            confirmButton = { TextButton(onClick = { startNew() }) { Text("New puzzle") } },
            dismissButton = { TextButton(onClick = { newPuzzleOpen = false }) { Text("Keep playing") } }
        )
    }

    // All screen sizes share the same two-row HUD. The separate word panel
    // consumes remaining measured space rather than being pushed offscreen.
    BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 2.dp)) {
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
                        if (!daily) {
                            DropdownMenuItem(text = { Text("Customize puzzle") },
                                onClick = {
                                    optionsOpen = false
                                    pendingTheme = theme.name
                                    pendingBoard = run.boardSize?.takeIf {
                                        it in PuzzleBoardSize.bookPresets
                                    }?.name ?: PuzzleBoardSize.BOOK_CLASSIC.name
                                    pendingChallenge = (run.challenge ?: when (difficulty) {
                                        WordDifficulty.EASY -> PuzzleChallenge.RELAXED
                                        WordDifficulty.MEDIUM -> PuzzleChallenge.CLASSIC
                                        WordDifficulty.HARD -> PuzzleChallenge.EXPERT
                                    }).name
                                    changeOpen = true
                                })
                            DropdownMenuItem(text = { Text("New puzzle") },
                                onClick = {
                                    optionsOpen = false
                                    newPuzzleOpen = true
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
                }, enabled = hintsRemaining > 0 && !finished,
                    modifier = Modifier.semantics {
                        contentDescription = "Hint. $hintsRemaining of " +
                            WordHintPolicy.allowance(run, daily) + " left for this puzzle."
                    }
                ) { Text("Hint ($hintsRemaining)") }
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
                    onAnother = if (daily) null else { { startNew() } },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else if (landscape) {
                Row(Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.width(boardViewport),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ZoomableWordGrid(puzzle, found, hintPaths,
                            boardViewport, select, onStep, boardFontId)
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
                        boardViewport, select, onStep, boardFontId)
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
    val context = LocalContext.current
    var shareUnavailable by remember(puzzle.id) { mutableStateOf(false) }
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
            val summary = PuzzleResultShare.text(puzzle, run, completedSeconds, daily)
            shareUnavailable = runCatching {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, summary)
                }
                context.startActivity(Intent.createChooser(send, "Share puzzle results"))
            }.isFailure
        }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Share results")
        }
        if (shareUnavailable) {
            Text("Sharing isn't available on this device.",
                color = Gold, style = MaterialTheme.typography.bodySmall)
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
}

@Composable
private fun WordList(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    daily: Boolean,
    modifier: Modifier = Modifier,
    onShowResults: (() -> Unit)? = null
) {
    var remainingOnly by remember(puzzle.id) { mutableStateOf(false) }
    val targets = if (remainingOnly) puzzle.targets.filter { it.id !in found }
        else puzzle.targets

    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
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
            // Wide two-column index for normal text; a legible single column
            // at 320dp / enlarged system fonts rather than cut-off words.
            val columns = if (maxWidth < 350.dp ||
                LocalDensity.current.fontScale >= 1.3f) 1 else 2
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                if (targets.isEmpty()) {
                    Text("All words found!", color = Forest,
                        style = MaterialTheme.typography.bodyMedium)
                }
                targets.chunked(columns).forEach { pair ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        pair.forEach { target ->
                            val solved = target.id in found
                            val shape = RoundedCornerShape(9.dp)
                            Box(Modifier.weight(1f)
                                .background(
                                    if (solved) FoundMarker.copy(alpha = 0.28f) else Paper,
                                    shape)
                                .border(1.dp, if (solved) Color(0xFFC5D9C8) else GridRule, shape)
                                .padding(horizontal = 9.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    (if (solved) "✓ " else "○ ") + target.word,
                                    color = if (solved) Forest else Ink,
                                    fontWeight = if (solved) FontWeight.Normal
                                        else FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = if (solved) TextDecoration.LineThrough else null,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (pair.size < columns) Box(Modifier.weight(1f))
                    }
                }
                Text(if (daily) "Daily progress saved." else "Your puzzle saves automatically.",
                    color = Muted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * A compact, explicit Move/Find board viewer. One-finger word tracing remains
 * untouched in Find mode; Move mode catches drag gestures above the grid.
 * This avoids ambiguity between panning and a word-selection gesture.
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
    partnerFoundIds: Set<String> = emptySet()
) {
    val viewportPx = with(LocalDensity.current) { viewport.toPx() }
    val initialZoom = when {
        puzzle.size >= 20 -> 1.85f
        puzzle.size >= 15 -> 1.45f
        else -> 1f
    }
    var transform by remember(puzzle.id, viewportPx) {
        mutableStateOf(BoardViewportTransform(1f).zoomed(initialZoom, viewportPx))
    }
    var moving by remember(puzzle.id) { mutableStateOf(false) }

    Column(Modifier.width(viewport),
        verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (viewport < 240.dp) {
            // Landscape can leave less than 240dp for the board; don't squeeze
            // five controls across a 106dp-wide column.
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    transform = transform.zoomed(transform.zoom - 0.25f, viewportPx)
                }, enabled = transform.zoom > 1f,
                    modifier = Modifier.width(40.dp)
                        .semantics { contentDescription = "Zoom out" }) {
                    Text("−")
                }
                TextButton(
                onClick = { transform = transform.zoomed(1f, viewportPx) },
                enabled = transform.zoom > 1f,
                modifier = Modifier.semantics {
                    contentDescription = "Fit entire board. Current zoom " +
                        (transform.zoom * 100f).roundToInt() + " percent."
                }
            ) { Text("${(transform.zoom * 100f).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall) }
                TextButton(onClick = {
                    transform = transform.zoomed(transform.zoom + 0.25f, viewportPx)
                }, enabled = transform.zoom < 2.5f,
                    modifier = Modifier.width(40.dp)
                        .semantics { contentDescription = "Zoom in" }) {
                    Text("+")
                }
            }
            TextButton(onClick = { moving = !moving },
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = if (moving)
                        "Mode: Move. Tap to find words."
                    else "Mode: Find. Tap to move the board."
                }) { Text(if (moving) "Mode: Move" else "Mode: Find") }
        } else {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    transform = transform.zoomed(transform.zoom - 0.25f, viewportPx)
                }, enabled = transform.zoom > 1f,
                    modifier = Modifier.width(40.dp)
                        .semantics { contentDescription = "Zoom out" }) { Text("−") }
                TextButton(
                    onClick = { transform = transform.zoomed(1f, viewportPx) },
                    enabled = transform.zoom > 1f,
                    modifier = Modifier.semantics {
                        contentDescription = "Fit entire board. Current zoom " +
                            (transform.zoom * 100f).roundToInt() + " percent."
                    }
                ) { Text("${(transform.zoom * 100f).roundToInt()}%",
                    style = MaterialTheme.typography.labelMedium) }
                TextButton(onClick = {
                    transform = transform.zoomed(transform.zoom + 0.25f, viewportPx)
                }, enabled = transform.zoom < 2.5f,
                    modifier = Modifier.width(40.dp)
                        .semantics { contentDescription = "Zoom in" }) { Text("+") }
                FilterChip(selected = !moving, onClick = { moving = false },
                    label = { Text("Find") })
                FilterChip(selected = moving, onClick = { moving = true },
                    label = { Text("Move") })
            }
        }
        Box(Modifier.size(viewport).clipToBounds()) {
            // Compose renders, clips, and inverse-hit-tests the ENTIRE native grid
            // as a single graphics layer. Never scale glyph font separately:
            // that enlarged text alone and caused the v0.4.4 overlap screenshots.
            WordGrid(
                puzzle, found, hintPaths,
                modifier = Modifier.size(viewport)
                    .graphicsLayer {
                        scaleX = transform.zoom
                        scaleY = transform.zoom
                        transformOrigin = TransformOrigin(0f, 0f)
                        translationX = transform.panX
                        translationY = transform.panY
                    },
                onSelect = onSelect, onStep = onStep,
                boardFontId = boardFontId, partnerFoundIds = partnerFoundIds
            )
            if (moving) {
                Box(Modifier.fillMaxSize()
                    .pointerInput(puzzle.id, viewportPx) {
                        // One finger pans; two fingers can pinch and pan in Move
                        // mode. Find mode keeps word selection completely separate.
                        detectTransformGestures(panZoomLock = true) { center, pan, scale, _ ->
                            transform = transform
                                .zoomedAt(transform.zoom * scale,
                                    viewportPx, center.x, center.y)
                                .panned(pan.x, pan.y, viewportPx)
                        }
                    }
                    .semantics {
                        contentDescription = "Move mode. Drag to pan or pinch to zoom. " +
                            "Select Find to trace words."
                    }
                )
            }
        }
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
    partnerFoundIds: Set<String> = emptySet()
) {
    val glyphPainter = rememberBoardGlyphPainter(boardFontId)
    var sizePx by remember(puzzle.id) { mutableStateOf(IntSize.Zero) }
    var dragStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var dragEnd by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var locked by remember(puzzle.id) { mutableStateOf<DragDirection?>(null) }
    var tapStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    val latestOnSelect by rememberUpdatedState(onSelect)
    val latestOnStep by rememberUpdatedState(onStep)

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
            .background(Paper, RoundedCornerShape(12.dp))
            .drawBehind {
                // Draw the *same exact* row/column boundaries used for
                // actual letter centers and pointer hit mapping.
                val cell = size.width / puzzle.size
                val thinRule = 0.6.dp.toPx()
                for (index in 1 until puzzle.size) {
                    val x = index * cell
                    drawLine(GridRule, Offset(x, 0f), Offset(x, size.height), thinRule)
                    val y = index * (size.height / puzzle.size)
                    drawLine(GridRule, Offset(0f, y), Offset(size.width, y), thinRule)
                }
                fun center(p: GridPoint) = Offset((p.col + 0.5f) * cell, (p.row + 0.5f) * cell)
                hintPaths.forEach { path ->
                    if (path.size > 1) drawLine(
                        ActiveMarker.copy(alpha = 0.22f),
                        center(path.first()), center(path.last()),
                        strokeWidth = cell * 0.38f,
                        cap = StrokeCap.Round
                    )
                }
                confirmed.forEach { (path, isPartner) ->
                    if (path.size > 1) drawLine(
                        (if (isPartner) PartnerMarker else FoundMarker).copy(alpha = 0.58f),
                        center(path.first()), center(path.last()),
                        strokeWidth = cell * 0.54f,
                        cap = StrokeCap.Round
                    )
                }
                when {
                    active.size > 1 -> drawLine(
                        ActiveMarker.copy(alpha = 0.66f),
                        center(active.first()), center(active.last()),
                        strokeWidth = cell * 0.58f,
                        cap = StrokeCap.Round
                    )
                    active.size == 1 -> drawCircle(
                        ActiveMarker.copy(alpha = 0.52f),
                        radius = cell * 0.30f,
                        center = center(active.first())
                    )
                }
                // Type-specific real ink bounds determine optical centering.
                // No independent fontScale: all content is enlarged by the
                // whole-board graphics transform above.
                glyphPainter.drawLetters(this, puzzle.rows, Ink)
            }
            .border(2.dp, Forest, RoundedCornerShape(12.dp))
            .onSizeChanged { sizePx = it }
            .pointerInput(puzzle.id) {
                detectDragGestures(
                    onDragStart = { offset ->
                        tapStart = null
                        dragStart = point(offset)
                        dragEnd = dragStart
                        locked = null
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val start = dragStart
                        if (start != null && sizePx.width > 0) {
                            val cell = sizePx.width.toFloat() / puzzle.size
                            val dx = (change.position.x - (start.col + 0.5f) * cell) / cell
                            val dy = (change.position.y - (start.row + 0.5f) * cell) / cell
                            val snap = WordDragSnap.update(start, dx, dy, puzzle.size, locked)
                            locked = snap.direction
                            if (snap.end != dragEnd) {
                                dragEnd = snap.end
                                latestOnStep(snap.length)
                            }
                        }
                    },
                    onDragEnd = {
                        val start = dragStart
                        val end = dragEnd
                        if (start != null && end != null && start != end) latestOnSelect(start, end)
                        dragStart = null
                        dragEnd = null
                        locked = null
                    },
                    onDragCancel = {
                        dragStart = null
                        dragEnd = null
                        locked = null
                    }
                )
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            puzzle.rows.forEachIndexed { row, letters ->
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    letters.forEachIndexed { col, char ->
                        val p = GridPoint(row, col)
                        Box(
                            // Use the exact height distributed by the outer
                            // Column. 20 rounded square row heights used to
                            // accumulate beyond viewport and crop the last row.
                            Modifier.weight(1f).fillMaxHeight()
                                .clickable {
                                    val first = tapStart
                                    if (first == null) tapStart = p
                                    else {
                                        latestOnSelect(first, p)
                                        tapStart = null
                                    }
                                }
                                .semantics {
                                    contentDescription = "Row " + (row + 1) +
                                        ", column " + (col + 1) + ", letter " + char
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Native Canvas paints the ink centered behind
                            // this transparent semantic/tappable cell.
                        }
                    }
                }
            }
        }
    }
}
