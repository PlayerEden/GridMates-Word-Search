package com.playereden.gridmates.wordsearch

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Review the exact spoiler-safe text and rendered 4:5 card, then optionally
 * share text or a standalone PNG. Dismissal never changes puzzle or records.
 * No automatic upload, gallery permission, or external web service.
 */
@Composable
internal fun ShareResultPreview(summary: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var unavailable by remember(summary) { mutableStateOf(false) }
    val bitmap = remember(context, summary) {
        runCatching { ResultPostcard.render(context, summary) }.getOrNull()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preview result to share") },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Preview of your GridMates result postcard",
                        modifier = Modifier.fillMaxWidth().height(215.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("Image preview isn't available; you can still share text.",
                        color = Gold, style = MaterialTheme.typography.bodySmall)
                }
                Text(summary, color = Forest, style = MaterialTheme.typography.bodySmall)
                Text("Sharing is optional. The result contains no room code, address, " +
                    "chat, player name, puzzle answers or private word list.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
                if (unavailable) {
                    Text("Sharing isn't available on this device. Your result is unchanged.",
                        color = Gold, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Column {
                TextButton(onClick = {
                    val sent = runCatching {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, summary)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share puzzle result"))
                    }.isSuccess
                    if (sent) onDismiss() else unavailable = true
                }) { Text("Share text") }
                TextButton(onClick = {
                    val sent = runCatching {
                        val card = requireNotNull(bitmap)
                        val uri = ResultPostcard.writeToPrivateCache(context, card)
                        val intent = ResultPostcard.imageShareIntent(context, uri)
                        context.startActivity(Intent.createChooser(intent, "Share result image"))
                    }.isSuccess
                    if (sent) onDismiss() else unavailable = true
                }, enabled = bitmap != null) { Text("Share image") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
