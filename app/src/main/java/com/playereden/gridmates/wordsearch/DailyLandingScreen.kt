package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.DailyChallenge

/**
 * Today is the primary action. Historic entries are paged in a small journal,
 * not rendered as an ever-growing wall of cards on the main navigation page.
 * Every saved date stays reachable; this never deletes or rewrites progress.
 */
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
    val fontLarge = LocalDensity.current.fontScale >= 1.5f
    val pageSize = if (fontLarge) 2 else 3
    val totalPages = ((past.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    var page by rememberSaveable { mutableIntStateOf(0) }
    val visiblePage = page.coerceIn(0, totalPages - 1)

    PaperEyebrow("Your daily puzzle")
    Text("Daily Word Search", color = Forest,
        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("A fresh offline puzzle each day. No deadline or sign-in.",
        color = Muted, style = MaterialTheme.typography.bodySmall)

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Today · $today", color = Gold,
                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(run.theme.label + " · " + run.difficulty.targetCount + " words",
                        color = Forest, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold, maxLines = 2)
                    Text(when {
                        existing?.completed == true -> "Completed today"
                        (existing?.run?.foundIds?.size ?: 0) > 0 ->
                            existing!!.run.foundIds.size.toString() +
                                " of " + run.difficulty.targetCount + " found"
                        else -> "Ready to play"
                    }, color = Muted, style = MaterialTheme.typography.bodySmall,
                        maxLines = 2)
                }
                Button(onClick = { onPlay(today) }) {
                    Text(when {
                        existing?.completed == true -> "Review"
                        (existing?.run?.foundIds?.size ?: 0) > 0 -> "Continue"
                        else -> "Play today"
                    }, maxLines = 1)
                }
            }
            PaperProgress(existing?.run?.foundIds?.size ?: 0, run.difficulty.targetCount)
        }
    }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Daily Journal", color = Forest,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold)
        Text("${store.profile.dailyCompletions} completed",
            color = Muted, style = MaterialTheme.typography.labelMedium)
    }

    if (past.isEmpty()) {
        Text("Earlier puzzles will appear here as you play.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    } else {
        val visible = past.drop(visiblePage * pageSize).take(pageSize)
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            visible.forEach { entry ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(entry.date, color = Forest,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text((if (entry.completed) "Completed" else "Unfinished") +
                            " · " + entry.run.foundIds.size + "/" +
                            entry.run.difficulty.targetCount + " words" +
                            (if (entry.run.hints.isNotEmpty()) " · Hints used" else ""),
                            color = Muted, style = MaterialTheme.typography.bodySmall,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(onClick = { onPlay(entry.date) }) {
                        Text(if (entry.completed) "Review" else "Resume")
                    }
                }
                HorizontalDivider(color = androidx.compose.ui.graphics.Color(0xFFDFE0D6))
            }
        }
        if (totalPages > 1) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { page = visiblePage - 1 },
                    enabled = visiblePage > 0) { Text("Previous") }
                Text("Page ${visiblePage + 1} of $totalPages",
                    color = Muted, style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = { page = visiblePage + 1 },
                    enabled = visiblePage < totalPages - 1) { Text("Next") }
            }
        }
    }
    TextButton(onClick = onBack) { Text("Back to Play") }
}
