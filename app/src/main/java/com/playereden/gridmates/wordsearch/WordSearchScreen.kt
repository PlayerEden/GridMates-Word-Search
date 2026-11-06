package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import com.playereden.gridmates.wordsearch.core.WordTheme

private val ConfirmedFill = Color(0xFFD5E8DB)
private val ActiveFill = Color(0xFFF5E3AE)
private val SelectedFill = Color(0xFFE4D4A1)
private val GridBorder = Color(0xFFD4DCCF)

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
        mutableStateOf("Drag across a word, or tap its first and last letters.")
    }
    val haptics = LocalHapticFeedback.current
    val finished = WordSearchRules.complete(puzzle, found.toSet())

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(onClick = onBack) { Text("Home") }
        Text("GridMates · Word Search", color = Forest, fontWeight = FontWeight.SemiBold)
    }
    Text("A little word finding", style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold, color = Forest)

    Text("Theme", style = MaterialTheme.typography.labelLarge, color = Muted)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        WordTheme.entries.forEach { item ->
            FilterChip(
                selected = item == theme,
                onClick = { themeName = item.name },
                label = { Text(item.label) }
            )
        }
    }

    Text("Difficulty", style = MaterialTheme.typography.labelLarge, color = Muted)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        WordDifficulty.entries.forEach { item ->
            FilterChip(
                selected = item == difficulty,
                onClick = { difficultyName = item.name },
                label = { Text(item.label) }
            )
        }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            found.size.toString() + " of " + puzzle.targets.size + " words",
            style = MaterialTheme.typography.titleMedium,
            color = Forest
        )
        OutlinedButton(onClick = { seed += 1L }) { Text("New Puzzle") }
    }

    WordGrid(puzzle, found.toSet()) { start, end ->
        val result = WordSearchRules.claim(puzzle, found.toSet(), start, end)
        when (result.status) {
            ClaimStatus.FOUND -> {
                found = found + requireNotNull(result.target).id
                status = "Found " + result.target.word + "!"
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            ClaimStatus.ALREADY_FOUND -> status = "You've already found that word."
            ClaimStatus.INVALID -> status = "Not quite — follow a straight word from the list."
        }
    }

    if (finished) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Puzzle Complete!", style = MaterialTheme.typography.titleLarge,
                    color = Forest, fontWeight = FontWeight.Bold)
                Text("You found all " + puzzle.targets.size + " words. Nicely done!")
                Text("This is a gameplay prototype; records and saving arrive in WS-03.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
                Button(onClick = { seed += 1L }) { Text("Play Another") }
            }
        }
    } else {
        Text(status, color = Muted, style = MaterialTheme.typography.bodyMedium)
    }

    Text("Words to find", style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold, color = Forest)
    puzzle.targets.chunked(2).forEach { pair ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pair.forEach { target ->
                val solved = target.id in found
                Box(Modifier.weight(1f).background(
                    if (solved) ConfirmedFill else Paper,
                    RoundedCornerShape(10.dp)
                ).padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(
                        (if (solved) "✓ " else "○ ") + target.word,
                        color = if (solved) Forest else Ink,
                        fontWeight = if (solved) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (solved) TextDecoration.LineThrough else null
                    )
                }
            }
            if (pair.size == 1) Box(Modifier.weight(1f))
        }
    }

    Text(
        "WS-02 gameplay preview · Progress is kept during rotation but not yet saved across app restarts. " +
            "Daily, profiles and two-phone games are still in development.",
        color = Muted, style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun WordGrid(
    puzzle: WordSearchPuzzle,
    found: Set<String>,
    onSelect: (GridPoint, GridPoint) -> Unit
) {
    var sizePx by remember(puzzle.id) { mutableStateOf(IntSize.Zero) }
    var dragStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var dragEnd by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    var tapStart by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }

    fun position(offset: Offset): GridPoint? {
        if (sizePx.width <= 0 || sizePx.height <= 0) return null
        if (offset.x < 0f || offset.y < 0f ||
            offset.x >= sizePx.width || offset.y >= sizePx.height) return null
        val col = (offset.x * puzzle.size / sizePx.width).toInt().coerceIn(0, puzzle.size - 1)
        val row = (offset.y * puzzle.size / sizePx.height).toInt().coerceIn(0, puzzle.size - 1)
        return GridPoint(row, col)
    }

    val accepted = remember(puzzle.id, found) {
        puzzle.targets.filter { it.id in found }.flatMap { it.positions }.toSet()
    }
    val active = remember(puzzle.id, dragStart, dragEnd, tapStart) {
        when {
            dragStart != null && dragEnd != null ->
                WordSearchRules.line(dragStart!!, dragEnd!!, puzzle.size).toSet()
            tapStart != null -> setOf(tapStart!!)
            else -> emptySet()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            .background(Paper, RoundedCornerShape(12.dp))
            .border(2.dp, Forest, RoundedCornerShape(12.dp))
            .onSizeChanged { sizePx = it }
            .pointerInput(puzzle.id, onSelect) {
                detectDragGestures(
                    onDragStart = { offset ->
                        tapStart = null
                        dragStart = position(offset)
                        dragEnd = dragStart
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        position(change.position)?.let { dragEnd = it }
                    },
                    onDragEnd = {
                        val start = dragStart
                        val end = dragEnd
                        if (start != null && end != null) onSelect(start, end)
                        dragStart = null
                        dragEnd = null
                    },
                    onDragCancel = {
                        dragStart = null
                        dragEnd = null
                    }
                )
            }
    ) {
        Column(Modifier.fillMaxWidth()) {
            puzzle.rows.forEachIndexed { row, letters ->
                Row(Modifier.fillMaxWidth()) {
                    letters.forEachIndexed { col, char ->
                        val point = GridPoint(row, col)
                        val highlight = when {
                            point in active -> ActiveFill
                            point in accepted -> ConfirmedFill
                            else -> Paper
                        }
                        Box(
                            Modifier.weight(1f).aspectRatio(1f)
                                .background(highlight)
                                .border(0.5.dp, GridBorder)
                                .clickable {
                                    val first = tapStart
                                    if (first == null) tapStart = point
                                    else {
                                        onSelect(first, point)
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
                                char.toString(),
                                color = Ink,
                                fontSize = (26f - 0.8f * puzzle.size).sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
