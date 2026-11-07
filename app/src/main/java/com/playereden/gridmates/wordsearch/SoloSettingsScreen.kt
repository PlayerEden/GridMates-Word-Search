package com.playereden.gridmates.wordsearch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.WoodlandCollection

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
                val raw = BackupDocumentReader.readUtf8(
                    requireNotNull(context.contentResolver.openInputStream(uri)))
                pendingImport = store.previewBackup(raw)
                message = ""
            } catch (ex: Exception) {
                message = "This is not a supported GridMates: Word Search backup."
            }
        }
    }

    if (pendingImport != null) {
        val preview = requireNotNull(pendingImport)
        val portrait = WoodlandCollection.portraits.firstOrNull {
            it.id == preview.identity.portraitId
        }?.label ?: "Woodland portrait"
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text("Replace saved progress?") },
            text = {
                Column(
                    Modifier.heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text("This backup contains:",
                        fontWeight = FontWeight.SemiBold, color = Forest)
                    Text("Profile: " + preview.identity.displayName + " · " + portrait)
                    Text(preview.completions.toString() + " Solo puzzles · " +
                        preview.dailyCompletions + " Daily puzzles",
                        color = Muted)
                    Text(preview.active.foundIds.size.toString() +
                        " words saved in the current Solo puzzle.",
                        color = Muted)
                    Text(preview.raceResults.size.toString() +
                        " completed Shared Board Race results.",
                        color = Muted)
                    Text(preview.duelResults.size.toString() +
                        " completed Speed Duel results.",
                        color = Muted)
                    Text("Restoring replaces your current Word Search profile, " +
                        "puzzle and records on this phone. Export the current " +
                        "progress first if you want a copy.",
                        color = Gold, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    message = if (store.restoreBackup(preview))
                        "Backup restored. Your profile, puzzle and records were updated."
                    else "Could not restore. Your previous data is protected."
                    pendingImport = null
                }) { Text("Restore backup") }
            },
            dismissButton = {
                TextButton(onClick = { pendingImport = null }) { Text("Keep current") }
            }
        )
    }

    PaperEyebrow("Your app & saved progress")
    Text("Settings & backup", color = Forest, fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineMedium)
    Text("Your Solo, Daily, nearby results and collections are stored on this device.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("Keep your progress")
        Text("Backup & restore", color = Forest,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge)
        Text("Puzzles save automatically. Export a backup before uninstalling, " +
            "clearing app storage, or switching phones. Uninstalling removes " +
            "your locally saved progress.",
            color = Muted, style = MaterialTheme.typography.bodyMedium)
        Text(store.profile.completions.toString() + " Solo · " +
            store.profile.dailyCompletions + " Daily · " +
            store.profile.raceResults.size + " Races · " +
            store.profile.duelResults.size + " Duels",
            color = Forest, style = MaterialTheme.typography.labelLarge)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val stacked = maxWidth < 350.dp ||
                LocalDensity.current.fontScale >= 1.3f
            if (stacked) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        export.launch("GridMates-WordSearch-Backup.json")
                    }, modifier = Modifier.fillMaxWidth()) { Text("Export backup") }
                    OutlinedButton(onClick = {
                        import.launch(arrayOf("application/json", "text/plain",
                            "application/octet-stream"))
                    }, modifier = Modifier.fillMaxWidth()) { Text("Restore backup") }
                }
            } else {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        export.launch("GridMates-WordSearch-Backup.json")
                    }, modifier = Modifier.weight(1f)) { Text("Export backup") }
                    OutlinedButton(onClick = {
                        import.launch(arrayOf("application/json", "text/plain",
                            "application/octet-stream"))
                    }, modifier = Modifier.weight(1f)) { Text("Restore backup") }
                }
            }
        }
        Text("Export includes Solo, Daily, completed Race and Speed Duel results, " +
            "and profile/collections. Restore asks before replacing progress. " +
            "Older backups without a Duel journal restore with zero Duel results.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        Text("Export saves a file to a location you choose. Restore asks before " +
            "replacing your current Word Search progress.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        Text("QA update note: v0.5.8 and earlier used a different debug signer. " +
            "Export a backup first, then uninstall the old QA app, install " +
            "v0.5.9, and Restore backup. From v0.5.9 onward, install newer " +
            "QA APKs over the existing app to keep its data. Do not clear " +
            "storage or uninstall without a verified backup.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("Puzzle feel")
        Text("Gameplay preferences", color = Forest,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold)
        Text("Adjust sound and gentle vibrations while playing: " +
            "open a puzzle, choose Options, then Feedback settings. " +
            "Your preferences remain on this device.",
            color = Muted, style = MaterialTheme.typography.bodyMedium)
    }

    if (message.isNotBlank()) {
        PaperPanel(Modifier.fillMaxWidth().semantics {
            liveRegion = LiveRegionMode.Polite
        }) {
            Text(message, color = Forest, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { message = "" }) { Text("Dismiss") }
        }
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("About")
        Text("About Word Search", color = Forest,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold)
        Text("Good puzzles. Good company.", color = Muted)
        Text("Play Solo or Daily without a connection. Nearby games use the " +
            "same Wi-Fi and share your display name with a partner. " +
            "There is no online account or cloud backup.",
            style = MaterialTheme.typography.bodySmall, color = Muted)
        Text("Version " + runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
                .versionName
        }.getOrNull().orEmpty().ifEmpty { "Unknown" },
            color = Muted, style = MaterialTheme.typography.labelMedium)
    }
    store.warning?.let {
        Text(it, color = Gold, style = MaterialTheme.typography.bodyMedium)
    }
}
