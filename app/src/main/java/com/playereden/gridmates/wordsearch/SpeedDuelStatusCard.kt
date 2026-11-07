package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.DuelView

/** Both sides get the same board, but ONLY their own marked words are known.
 * The opponent's progress is a count; no personal IDs or paths are displayed.
 */
@Composable
internal fun SpeedDuelStatusCard(view: DuelView, total: Int, connected: Boolean,
                                 onReady: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            PaperEyebrow("Speed Duel · Private boards")
            if (view.finished) {
                Text(if (view.winner == view.recipient) "You finished first!"
                     else "Your partner finished first",
                    color = Forest, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text("Final result confirmed by the host. " +
                    "The first completed board accepted by the host wins.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            } else if (!view.started) {
                Text("Ready, then GO!", color = Forest,
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Both players must tap Ready. The puzzle stays hidden until GO.",
                    color = Muted, style = MaterialTheme.typography.bodyMedium)
                Text("You: " + (if (view.ownReady) "Ready ✓" else "Not ready") +
                    "  ·  Partner: " + (if (view.opponentReady) "Ready ✓" else "Not ready"),
                    color = Forest, style = MaterialTheme.typography.bodyMedium)
                if (!view.ownReady) {
                    Button(onClick = onReady, enabled = connected,
                        modifier = Modifier.fillMaxWidth()) {
                        Text("I'm ready")
                    }
                } else {
                    Text("Waiting for your partner to get ready…",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Text(if (connected) "GO! Find all the words first"
                     else "Connection paused · reconnect to continue",
                    color = Forest, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text("You each solve the identical puzzle separately. " +
                    "Your partner sees your progress count, not which words you found.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            val available = total.coerceAtLeast(1)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("YOU", color = Forest, style = MaterialTheme.typography.labelMedium)
                    Text("${view.ownCount} / $total", color = Forest,
                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { view.ownCount.toFloat() / available },
                        modifier = Modifier.fillMaxWidth())
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("PARTNER", color = Muted, style = MaterialTheme.typography.labelMedium)
                    Text("${view.opponentCount} / $total", color = Forest,
                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { view.opponentCount.toFloat() / available },
                        modifier = Modifier.fillMaxWidth())
                }
            }
            if (view.finished) {
                Text("Duel outcomes aren't added to Shared Board Race records. " +
                    "A separate Duel journal is planned.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
