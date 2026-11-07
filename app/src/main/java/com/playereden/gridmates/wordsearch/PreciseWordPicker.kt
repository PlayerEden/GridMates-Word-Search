package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordSearchRules

/**
 * Optional non-dragging, non-pinching input path for large or dense grids.
 * Presents only two bounded row/column controls instead of requiring a
 * screen-reader user to tab through 400 tiny letter cells.
 *
 * This never awards or guesses a word: the selected endpoints go through
 * the SAME claim callback as touch dragging and two-tap selection.
 */
@Composable
internal fun PreciseWordPicker(
    puzzle: WordSearchPuzzle,
    onSelect: (GridPoint, GridPoint) -> Unit,
    onDismiss: () -> Unit
) {
    var row by remember(puzzle.id) { mutableIntStateOf(0) }
    var col by remember(puzzle.id) { mutableIntStateOf(0) }
    var start by remember(puzzle.id) { mutableStateOf<GridPoint?>(null) }
    val current = GridPoint(row, col)
    val path = start?.let { WordSearchRules.line(it, current, puzzle.size) }
    val straightLine = start != null && path != null &&
        path.size >= 2 && path.last() == current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select letters precisely") },
        text = {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (start == null)
                        "Choose the first letter by row and column. No dragging needed."
                    else "Now choose the last letter. Use a straight horizontal, vertical, or diagonal line.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
                if (start != null) {
                    Text(
                        "Start · row ${start!!.row + 1}, column ${start!!.col + 1} " +
                            "(${puzzle.rows[start!!.row][start!!.col]})",
                        color = Forest, fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "Selected letter: ${puzzle.rows[row][col]} · row ${row + 1}, column ${col + 1}",
                    color = Forest, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                PrecisionNumberControl(
                    name = "Row", current = row + 1, maximum = puzzle.size,
                    onPrevious = { if (row > 0) row-- },
                    onNext = { if (row < puzzle.size - 1) row++ }
                )
                PrecisionNumberControl(
                    name = "Column", current = col + 1, maximum = puzzle.size,
                    onPrevious = { if (col > 0) col-- },
                    onNext = { if (col < puzzle.size - 1) col++ }
                )
                // Spoken row letters help users locate a real word rather
                // than merely entering blind numerical coordinates.
                Text(
                    "Row ${row + 1} reads: " + puzzle.rows[row].map { it.toString() }.joinToString(" "),
                    color = Ink, style = MaterialTheme.typography.bodySmall
                )
                if (start != null && !straightLine) {
                    Text(
                        if (start == current)
                            "Move to the word's last letter."
                        else "Move the endpoint onto a straight line from the start.",
                        color = Muted, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (start == null) {
                        start = current
                    } else if (straightLine) {
                        onSelect(start!!, current)
                        onDismiss()
                    }
                },
                enabled = start == null || straightLine
            ) {
                Text(if (start == null) "Set start" else "Mark word")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (start == null) onDismiss() else start = null
            }) {
                Text(if (start == null) "Close" else "Change start")
            }
        }
    )
}

@Composable
private fun PrecisionNumberControl(
    name: String,
    current: Int,
    maximum: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onPrevious,
            enabled = current > 1,
            modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Previous ${name.lowercase()}" }
        ) { Text("−", style = MaterialTheme.typography.titleLarge) }
        Text(
            "$name $current of $maximum",
            modifier = Modifier.weight(1f),
            color = Forest, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
            onClick = onNext,
            enabled = current < maximum,
            modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Next ${name.lowercase()}" }
        ) { Text("+", style = MaterialTheme.typography.titleLarge) }
    }
}
