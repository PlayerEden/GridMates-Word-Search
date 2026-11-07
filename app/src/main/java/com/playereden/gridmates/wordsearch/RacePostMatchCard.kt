package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.CoopSeat
import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import com.playereden.gridmates.wordsearch.core.NearbyMatch
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle

/**
 * The board remains available below the summary. This is a local comparison,
 * not an additional authoritative result or claim: every count comes from
 * the exact same host-validated snapshot used to display the shared board.
 */
@Composable
internal fun RacePostMatchCard(snapshot: CoopSnapshot, seat: CoopSeat,
                               puzzle: WordSearchPuzzle, saved: Boolean, saveFailed: Boolean = false) {
    if (snapshot.match != NearbyMatch.SHARED_BOARD_RACE || !snapshot.finished) return
    val standing = requireNotNull(snapshot.raceStanding)
    val own = if (seat == CoopSeat.HOST) standing.hostPoints else standing.guestPoints
    val partner = if (seat == CoopSeat.HOST) standing.guestPoints else standing.hostPoints
    val total = puzzle.targets.size.coerceAtLeast(1)
    val outcome = when {
        standing.tied -> "It's a tie"
        standing.winner == seat -> "You won!"
        else -> "Your partner won"
    }
    var details by rememberSaveable(snapshot.roomId) { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PaperEyebrow("Shared Board Race · Final")
            Text(outcome, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = Forest)
            Text(when {
                standing.tied -> "Both players claimed the same number of words."
                own > partner -> "You found ${own - partner} more words than your partner."
                else -> "Your partner found ${partner - own} more words."
            }, color = Muted, style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("YOU", color = Forest, style = MaterialTheme.typography.labelMedium)
                    Text("$own / $total", color = Forest,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(progress = { own.toFloat() / total },
                        modifier = Modifier.fillMaxWidth())
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("PARTNER", color = Muted, style = MaterialTheme.typography.labelMedium)
                    Text("$partner / $total", color = Forest,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(progress = { partner.toFloat() / total },
                        modifier = Modifier.fillMaxWidth())
                }
            }
            Text("$total shared words · First valid claim scores · 1 point per word",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            Text(when {
                saved -> "Saved to your local Versus journal."
                saveFailed -> "Couldn't save the result; your earlier records are protected."
                else -> "Saving your result to this device…"
            }, color = if (saved) Forest else Gold,
                style = MaterialTheme.typography.bodySmall)
            HorizontalDivider()
            TextButton(onClick = { details = !details }) {
                Text(if (details) "Hide word-by-word comparison"
                    else "Compare the words you each found")
            }
            if (details) {
                val otherSeat = if (seat == CoopSeat.HOST) CoopSeat.GUEST
                    else CoopSeat.HOST
                for ((label, who) in listOf("You found" to seat,
                    "Partner found" to otherSeat)) {
                    val ids = snapshot.claims.filter { it.seat == who }
                        .map { it.wordId }.toSet()
                    Text("$label · ${ids.size}", fontWeight = FontWeight.SemiBold,
                        color = Forest)
                    Text(puzzle.targets.filter { it.id in ids }
                        .joinToString(" · ") { it.word }.ifBlank { "None" },
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text("Your Solo and Daily progress is separate from this match.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
