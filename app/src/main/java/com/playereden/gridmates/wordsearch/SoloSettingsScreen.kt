package com.playereden.gridmates.wordsearch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloProfile

/**
 * Small QA-friendly backup screen. Uses Android's document picker rather than
 * broad storage permissions; the player explicitly chooses where data goes.
 */
@Composable
internal fun SoloSettingsScreen(store: SoloStore) {
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf("") }
    var pendingImport by remember { mutableStateOf<SoloProfile?>(null) }

    val export = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                val stream = requireNotNull(context.contentResolver.openOutputStream(uri))
                stream.use { it.write(store.exportBackup().toByteArray(Charsets.UTF_8)) }
                message = "Backup exported to your chosen location."
            } catch (ex: Exception) {
                message = "Could not export backup. Please try another folder."
            }
        }
    }
    val import = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val raw = requireNotNull(context.contentResolver.openInputStream(uri))
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
                require(raw.length <= 2_000_000) { "Backup too large" }
                pendingImport = store.previewBackup(raw)
            } catch (ex: Exception) {
                message = "This is not a supported GridMates: Word Search backup."
            }
        }
    }

    if (pendingImport != null) {
        val preview = requireNotNull(pendingImport)
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text("Restore this backup?") },
            text = {
                Text(
                    "This backup includes the profile '" + preview.identity.displayName + "', " +
                        "portrait " + preview.identity.portraitId + ", " +
                        preview.completions + " completed Solo puzzles, " +
                        preview.dailyCompletions + " completed Daily puzzles, " +
                        preview.dailyEntries.size + " dated entries, and " +
                        preview.active.foundIds.size + " words in the current Solo puzzle. " +
                        "Restoring will replace the Word Search progress currently on this device. " +
                        "Export the current progress first if you want to keep both."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    message = if (store.restoreBackup(preview))
                        "Backup restored. Continue Solo to review your puzzle."
                    else "Could not restore. Your prior data is protected."
                    pendingImport = null
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { pendingImport = null }) { Text("Cancel") }
            }
        )
    }

    Text("Settings & backup", color = Forest, fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineMedium)
    Text("Local, offline puzzle progress. No account required.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Protect your puzzles", color = Forest,
                fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Text("Progress is automatically saved inside this app. Export a backup before " +
                "uninstalling or replacing a test build. Android uninstallation removes " +
                "local app storage.",
                color = Muted)
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    export.launch("GridMates-WordSearch-Backup.json")
                }) { Text("Export") }
                Button(onClick = {
                    import.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
                }) { Text("Import") }
            }
            Text("Completed: " + store.profile.completions + " Solo · " +
                    store.profile.dailyCompletions + " Daily",
                color = Forest, fontWeight = FontWeight.SemiBold)
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Gameplay preferences", color = Forest,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Text("Choose gentle vibrations and word-found sound in a puzzle's " +
                "Options → Feedback settings. Your choices are remembered on this device.",
                color = Muted)
        }
    }
    if (message.isNotBlank()) {
        Text(message, color = Forest, style = MaterialTheme.typography.bodyMedium)
    }
    PaperPanel(Modifier.fillMaxWidth()) {
        Text("About Word Search", color = Forest,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold)
        Text("Good puzzles. Good company.", color = Muted)
        Text("Offline Solo and Daily puzzles, with optional nearby Wi-Fi co-op. " +
            "Your Solo and Daily records stay on this device.",
            style = MaterialTheme.typography.bodySmall, color = Muted)
    }
    store.warning?.let {
        Text(it, color = Gold, style = MaterialTheme.typography.bodyMedium)
    }
}
