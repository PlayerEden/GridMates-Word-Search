package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS-09C setup is a distinct route, not an in-game dialog. Nothing changes in
 * SoloStore until Start puzzle succeeds; cancel/back is always non-destructive.
 * Live 12–30 cell presets only. The giant 100×100 viewport is future work.
 */
@Composable
internal fun SoloSetupScreen(
    store: SoloStore,
    onBack: () -> Unit,
    onStarted: () -> Unit
) {
    val preset = remember(store) { store.soloSetupPreset() }
    var themeName by rememberSaveable { mutableStateOf(preset.theme.name) }
    var boardName by rememberSaveable { mutableStateOf(preset.board.name) }
    var challengeName by rememberSaveable { mutableStateOf(preset.challenge.name) }
    val theme = WordTheme.valueOf(themeName)
    val board = PuzzleBoardSize.valueOf(boardName)
    val challenge = PuzzleChallenge.valueOf(challengeName)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        PaperEyebrow("CUSTOM · BEFORE YOU PLAY")
        TextButton(onClick = onBack) { Text("Cancel") }
    }
    Text("New puzzle", style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold, color = Forest)
    Text("Pick your puzzle, then start. You can come back to unfinished games " +
        "from My Puzzles.", color = Muted, style = MaterialTheme.typography.bodyMedium)

    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("Your puzzle")
        SetupChoice(
            title = "Theme",
            selection = theme.label,
            options = WordTheme.entries.map { it.name to it.label },
            onSelect = { themeName = it }
        )
        SetupChoice(
            title = "Board size",
            selection = "${board.label} · ${board.cells}×${board.cells} · ${board.targets} words",
            options = PuzzleBoardSize.soloPresets.map {
                it.name to "${it.label} · ${it.cells}×${it.cells} · ${it.targets} words"
            },
            onSelect = { boardName = it }
        )
        SetupChoice(
            title = "Challenge",
            selection = challenge.label,
            options = PuzzleChallenge.entries.map { it.name to it.label },
            onSelect = { challengeName = it }
        )
        Text(challenge.description, style = MaterialTheme.typography.bodySmall,
            color = Muted)
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        Text("Ready to begin?", fontWeight = FontWeight.SemiBold, color = Forest)
        Text("${theme.label} · ${board.cells}×${board.cells} · " +
            "${board.targets} words · ${challenge.label}",
            color = Ink, style = MaterialTheme.typography.bodyMedium)
        Text("Your current unfinished puzzle will be saved automatically. " +
            "The new one will start with a fresh board.",
            style = MaterialTheme.typography.bodySmall, color = Muted)
        store.warning?.let { Text(it, color = Gold,
            style = MaterialTheme.typography.bodySmall) }
        Button(onClick = {
            if (store.chooseSolo(theme, board, challenge)) onStarted()
        }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text("Start puzzle", maxLines = 1)
        }
    }
}

/** Dropdowns keep setup short even on narrow devices and larger font scales. */
@Composable
private fun SetupChoice(
    title: String,
    selection: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge,
            color = Forest, fontWeight = FontWeight.SemiBold)
        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(selection, modifier = Modifier.weight(1f),
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(" ▾")
            }
            DropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false }) {
                options.forEach { (id, label) ->
                    DropdownMenuItem(text = { Text(label, maxLines = 2) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        })
                }
            }
        }
    }
}
