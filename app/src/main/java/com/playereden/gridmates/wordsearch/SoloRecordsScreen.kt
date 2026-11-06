package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloProfile
import java.text.DateFormat
import java.util.Date

@Composable
internal fun SoloRecordsScreen(profile: SoloProfile) {
    Text("Your puzzle journal", color = Forest,
        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Your completed Solo puzzles are saved privately on this device.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard("Completed", profile.completions.toString(), Modifier.weight(1f))
        MetricCard("Words found", profile.totalWords.toString(), Modifier.weight(1f))
        MetricCard("With hints", profile.assistedCompletions.toString(), Modifier.weight(1f))
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Daily Journal", color = Forest, fontWeight = FontWeight.SemiBold)
            Text(profile.dailyCompletions.toString() + " dated challenges completed · " +
                profile.dailyEntries.count { !it.completed } + " unfinished",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Text("Daily progress is saved separately from Solo records.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }

    Text("Recent puzzles", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    if (profile.results.isEmpty()) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Your story starts with a puzzle.", fontWeight = FontWeight.SemiBold)
                Text("Finish a Solo game and you'll see it here.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        for (record in profile.results.take(25)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(record.theme.label + " · " +
                            (record.boardSize?.let { it.cells.toString() + "×" + it.cells } ?: 
                                (record.difficulty.gridSize.toString() + "×" +
                                    record.difficulty.gridSize)) +
                            " · " + (record.challenge?.label ?: record.difficulty.label),
                            color = Forest, fontWeight = FontWeight.SemiBold)
                        Text(if (record.assisted) "Assisted" else "No hints",
                            color = Gold, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(record.wordCount.toString() + " words · " +
                        (record.elapsedSeconds / 60L).toString() + "m " +
                        (record.elapsedSeconds % 60L).toString() + "s · " +
                        record.hintSteps.toString() + " hint steps",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Date(record.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, amount: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(amount, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Forest)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }
}
