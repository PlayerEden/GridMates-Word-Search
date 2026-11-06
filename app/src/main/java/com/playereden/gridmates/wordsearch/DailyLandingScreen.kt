package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.DailyChallenge

@Composable
internal fun DailyLandingScreen(
    store: SoloStore,
    onPlay: (String) -> Unit,
    onBack: () -> Unit
) {
    val today = DailyChallenge.today()
    val run = DailyChallenge.dailyRun(today)
    val existing = store.profile.dailyEntries.firstOrNull { it.date == today }
    val past = store.profile.dailyEntries.filter { it.date != today }
        .sortedByDescending { it.date }

    Text("The Daily Puzzle", color = Forest,
        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("One offline puzzle for each local calendar date. Same day, same board. " +
        "No account or time deadline.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("Today · " + today, color = Gold,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(run.theme.label + " · Easy · 6 words",
                color = Forest, style = MaterialTheme.typography.titleLarge)
            Text(when {
                existing?.completed == true -> "Completed for today"
                (existing?.run?.foundIds?.size ?: 0) > 0 ->
                    existing!!.run.foundIds.size.toString() +
                        " of 6 words found — ready to resume"
                else -> "A fresh little puzzle is ready."
            }, color = Muted)
            Button(onClick = { onPlay(today) }) {
                Text(when {
                    existing?.completed == true -> "Review today's puzzle"
                    (existing?.run?.foundIds?.size ?: 0) > 0 -> "Continue Daily"
                    else -> "Play Today's Puzzle"
                })
            }
        }
    }
    Text("Your Daily Journal", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text(store.profile.dailyCompletions.toString() + " dated puzzles completed",
        style = MaterialTheme.typography.bodyMedium, color = Muted)
    if (past.isEmpty()) {
        Text("Play today, and earlier puzzles will appear here without disappearing.",
            color = Muted, style = MaterialTheme.typography.bodyMedium)
    } else {
        past.forEach { entry ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(entry.date, color = Forest, fontWeight = FontWeight.SemiBold)
                        Text((if (entry.completed) "Completed" else "Unfinished") +
                            " · " + entry.run.foundIds.size + " / 6 words" +
                            (if (entry.run.hints.isNotEmpty()) " · Hints used" else ""),
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { onPlay(entry.date) }) {
                        Text(if (entry.completed) "Review" else "Resume")
                    }
                }
            }
        }
    }
    TextButton(onClick = onBack) { Text("Back to Play") }
}
