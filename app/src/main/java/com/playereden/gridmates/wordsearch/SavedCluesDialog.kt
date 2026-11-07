package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloRun
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle

/**
 * Replay only already-paid hints. No purchase, new hint, auto-completion,
 * score change or mutation of Solo/Daily saved progress occurs on review.
 */
internal data class SavedClue(val word: String, val level: Int, val detail: String)

internal fun savedClues(puzzle: WordSearchPuzzle, run: SoloRun): List<SavedClue> =
    puzzle.targets.mapNotNull { target ->
        val level = run.hints[target.id] ?: return@mapNotNull null
        if (level !in 1..3) return@mapNotNull null
        val first = target.positions.first()
        val detail = when (level) {
            1 -> "Look near row ${first.row + 1}."
            2 -> "Starts at row ${first.row + 1}, column ${first.col + 1}."
            else -> "Starts at row ${first.row + 1}, column ${first.col + 1}. " +
                "Its full path is highlighted in gold on the board."
        }
        SavedClue(target.word, level, detail)
    }

@Composable
internal fun SavedCluesDialog(
    puzzle: WordSearchPuzzle,
    run: SoloRun,
    onDismiss: () -> Unit
) {
    val clues = savedClues(puzzle, run)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your saved clues") },
        text = {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Review clues you already used. This does not spend another hint.",
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
                if (clues.isEmpty()) {
                    Text("You have not used any hints in this puzzle.",
                        color = Ink, style = MaterialTheme.typography.bodyMedium)
                } else {
                    clues.forEach { clue ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(clue.word + " · Clue " + clue.level + " of 3",
                                color = Forest,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                            Text(clue.detail, color = Ink,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Back to puzzle") }
        }
    )
}
