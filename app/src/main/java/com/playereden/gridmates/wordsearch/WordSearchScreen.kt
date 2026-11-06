package com.playereden.gridmates.wordsearch

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.DragDirection
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordDragSnap
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import com.playereden.gridmates.wordsearch.core.WordTheme

private val FoundMarker = Color(0xFF9FC5AA)
private val ActiveMarker = Color(0xFFE4B957)
private val GridRule = Color(0xFFD8DFD2)

@Composable
internal fun WordSearchScreen(onBack: () -> Unit) {
    var themeName by rememberSaveable { mutableStateOf(WordTheme.WOODLAND.name) }
    var difficultyName by rememberSaveable { mutableStateOf(WordDifficulty.EASY.name) }
    var seed by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val theme = WordTheme.entries.firstOrNull { it.name == themeName } ?: WordTheme.WOODLAND
    val difficulty = WordDifficulty.entries.firstOrNull { it.name == difficultyName } ?: WordDifficulty.EASY
    val puzzle = remember(theme, difficulty, seed) {
        WordSearchGenerator.generate(theme, difficulty, seed)
    }
    var found by rememberSaveable(puzzle.id) { mutableStateOf(listOf<String>()) }
    var status by remember(puzzle.id) {
        mutableStateOf("Drag a word, or tap its first and last letters.")
    }
    var changeOpen by rememberSaveable { mutableStateOf(false) }
    var feelOpen by rememberSaveable { mutableStateOf(false) }
    var newPuzzleOpen by rememberSaveable { mutableStateOf(false) }
    var pendingTheme by rememberSaveable { mutableStateOf(themeName) }
    var pendingDifficulty by rememberSaveable { mutableStateOf(difficultyName) }
    var hapticsEnabled by rememberSaveable { mutableStateOf(true) }
    var soundEnabled by rememberSaveable { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val finished = WordSearchRules.complete(puzzle, found.toSet())

    fun startNew() {
        seed += 1L
        newPuzzleOpen = false
    }

    if (changeOpen) {
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
                    Text("Difficulty", style = MaterialTheme.typography.titleSmall, color = Forest)
                    WordDifficulty.entries.forEach { item ->
                        Row(Modifier.fillMaxWidth().clickable { pendingDifficulty = item.name },
                            verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = pendingDifficulty == item.name,
                                onClick = { pendingDifficulty = item.name })
                            Text(item.label + " · " + item.gridSize + "×" + item.gridSize)
                        }
                    }
                    Text("Changing these starts a fresh puzzle. Unsaved progress will be replaced.",
                        style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    themeName = pendingTheme
                    difficultyName = pendingDifficulty
                    seed += 1L
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
                        Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it })
                    }
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Word-found click")
                        Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                    }
                    Text("These preferences are local to this prototype; permanent settings come later.",
                        style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            },
            confirmButton = { TextButton(onClick = { feelOpen = false }) { Text("Done") } }
        )
    }

    if (newPuzzleOpen) {
        AlertDialog(
            onDismissRequest = { newPuzzleOpen = false },
            title = { Text("Start another puzzle?") },
            text = { Text("Your current word selections are not saved yet.") },
            confirmButton = { TextButton(onClick = { startNew() }) { Text("New puzzle") } },
            dismissButton = { TextButton(onClick = { newPuzzleOpen = false }) { Text("Keep playing") } }
        )
    }

    BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp)) {
        val landscape = maxWidth > maxHeight * 1.25f
        val boardSize = if (landscape) {
            minOf(maxHeight - 118.dp, maxWidth * 0.52f).coerceAtLeast(130.dp)
        } else {
            minOf(maxWidth, (maxHeight - 185.dp) * 0.83f).coerceAtLeast(130.dp)
        }

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) { Text("‹ Home") }
                Text(theme.label + " · " + difficulty.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Forest, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                TextButton(onClick = {
                    pendingTheme = themeName
                    pendingDifficulty = difficultyName
                    changeOpen = true
                }) { Text("Change") }
            }
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(found.size.toString() + " / " + puzzle.targets.size + " words",
                    style = MaterialTheme.typography.titleMedium, color = Forest,
                    fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    TextButton(onClick = { feelOpen = true }) { Text("Feel") }
                    TextButton(onClick = { newPuzzleOpen = true }) { Text("New Puzzle") }
                }
            }

            val select: (GridPoint, GridPoint) -> Unit = { start, end ->
                val result = WordSearchRules.claim(puzzle, found.toSet(), start, end)
                when (result.status) {
                    ClaimStatus.FOUND -> {
                        val target = requireNotNull(result.target)
                        found = found + target.id
                        status = "Found " + target.word + "!"
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
            if (landscape) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        WordGrid(puzzle, found.toSet(), Modifier.size(boardSize), select, onStep)
                        Text(status, color = Muted, style = MaterialTheme.typography.bodySmall,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    WordList(puzzle, found.toSet(), Modifier.weight(1f).fillMaxSize())
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    WordGrid(puzzle, found.toSet(), Modifier.size(boardSize), select, onStep)
                }
                Text(if (finished) "Puzzle Complete! Great work." else status,
                    color = if (finished) Forest else Muted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                WordList(puzzle, found.toSet(), Modifier.weight(1f).fillMaxWidth())
            }

            if (finished) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("All words found!", color = Forest, fontWeight = FontWeight.Bold)
                    Button(onClick = { startNew() }) { Text("Play Another") }
                }
            }
        }
    }
}

@Composable
private fun WordList(puzzle: WordSearchPuzzle, found: Set<String>, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Words to find", style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold, color = Forest)
        puzzle.targets.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                pair.forEach { target ->
                    val solved = target.id in found
                    Box(
                        Modifier.weight(1f).background(
                            if (solved) FoundMarker.copy(alpha = 0.33f) else Paper,
                            RoundedCornerShape(8.dp)
                        ).padding(horizontal = 8.dp, vertical = 9.dp)
                    ) {
                        Text(
                            (if (solved) "✓ " else "○ ") + target.word,
                            color = if (solved) Forest else Ink,
                            fontWeight = if (solved) FontWeight.Normal else FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (solved) TextDecoration.LineThrough else null,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (pair.size == 1) Box(Modifier.weight(1f))
            }
        }
        Text("Progress saving, daily puzzles and multiplayer are coming later.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun WordGrid(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    modifier: Modifier = Modifier,
    onSelect: (GridPoint, GridPoint) -> Unit,
    onStep: (Int) -> Unit
) {
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

    val confirmed = remember(puzzle.id, found) {
        puzzle.targets.filter { it.id in found }.map { it.positions }
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
                val cell = size.width / puzzle.size
                fun center(p: GridPoint) = Offset((p.col + 0.5f) * cell, (p.row + 0.5f) * cell)
                confirmed.forEach { path ->
                    if (path.size > 1) drawLine(
                        FoundMarker.copy(alpha = 0.58f),
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
                Row(Modifier.fillMaxWidth()) {
                    letters.forEachIndexed { col, char ->
                        val p = GridPoint(row, col)
                        Box(
                            Modifier.weight(1f).aspectRatio(1f)
                                .border(0.5.dp, GridRule)
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
                            Text(
                                char.toString(), color = Ink,
                                fontSize = (26f - 0.8f * puzzle.size).sp,
                                fontWeight = FontWeight.Medium, maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
