package com.playereden.gridmates.wordsearch

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * The share text is visible before any external app is opened.
 * Only explicit Share now launches Android's system chooser. Cancel never
 * creates a new puzzle, changes results, or grants network permissions.
 */
@Composable
internal fun ShareResultPreview(summary: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var unavailable by remember(summary) { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preview result to share") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 380.dp)
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(summary, color = Forest, style = MaterialTheme.typography.bodyMedium)
                Text("Only this text is offered to the app you choose. " +
                    "No room code, address, chat, player name, puzzle answers or private word list.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
                if (unavailable) {
                    Text("Sharing isn't available on this device. You can keep playing.",
                        color = Gold, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val sent = runCatching {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, summary)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share puzzle result"))
                }.isSuccess
                if (sent) onDismiss() else unavailable = true
            }) { Text("Share now") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
