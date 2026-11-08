package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.RaceOutcome
import java.text.DateFormat
import java.util.Date

@Composable
internal fun SoloRecordsScreen(profile: SoloProfile) {
    Text("Your puzzle journal", color = Forest,
        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Your puzzle and match history stays on this device.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    var journalTab by rememberSaveable { mutableStateOf("Solo") }
    var soloPage by rememberSaveable { mutableIntStateOf(0) }
    var racePage by rememberSaveable { mutableIntStateOf(0) }
    var duelPage by rememberSaveable { mutableIntStateOf(0) }

    Text("Browse your results", color = Forest,
        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Solo", "Race", "Duel").forEach { section ->
            FilterChip(selected = journalTab == section,
                onClick = { journalTab = section },
                label = { Text(section) })
        }
    }
    // Only the Solo tab needs Solo and Daily summary cards; putting them
    // above Race/Duel makes the selected match history inaccessible without
    // scrolling past unrelated statistics on compact screens.
    if (journalTab == "Solo") {
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

    }

    // Each category has its own remembered page. No journal is silently
    // truncated at 12/15/25 entries and no stored result is deleted.
    if (journalTab == "Race") {
    Text("Shared Board Race", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Shared Board Race results saved locally. Co-op isn't counted as a win or loss.",
        color = Muted, style = MaterialTheme.typography.bodySmall)
    val racing = profile.raceSummary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Played", racing.played.toString(), Modifier.weight(1f))
            MetricCard("Wins", racing.wins.toString(), Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Losses", racing.losses.toString(), Modifier.weight(1f))
            MetricCard("Ties", racing.ties.toString(), Modifier.weight(1f))
        }
    }
    if (profile.raceResults.isEmpty()) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("No Versus matches recorded yet.",
                    fontWeight = FontWeight.SemiBold, color = Forest)
                Text("Finish a Shared Board Race with a nearby partner to start your journal.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        Text("Your most recent races", color = Forest,
            fontWeight = FontWeight.SemiBold)
        val page = racePage.coerceIn(0, (profile.raceResults.size - 1).coerceAtLeast(0) / 4)
        profile.raceResults.drop(page * 4).take(4).forEach { race ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(when (race.outcome) {
                            RaceOutcome.WIN -> "Victory"
                            RaceOutcome.LOSS -> "Defeat"
                            RaceOutcome.TIE -> "Tie"
                        }, color = Forest, fontWeight = FontWeight.Bold)
                        Text("You ${race.ownWords} · Partner ${race.partnerWords}",
                            color = Forest, fontWeight = FontWeight.SemiBold)
                    }
                    Text(race.theme.label + " · " + race.board.cells +
                        "×" + race.board.cells + " · " + race.challenge.label,
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Text(DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM, DateFormat.SHORT).format(Date(race.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (profile.raceResults.isNotEmpty()) JournalPages(
        count = profile.raceResults.size, page = racePage, pageSize = 4,
        onPage = { racePage = it })
    }
    if (journalTab == "Duel") {
    Text("Speed Duel", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Separate private-board matches. First host-confirmed completion wins. " +
        "These results are not mixed with Shared Board Race.",
        color = Muted, style = MaterialTheme.typography.bodySmall)
    val dueling = profile.duelSummary
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Played", dueling.played.toString(), Modifier.weight(1f))
            MetricCard("Wins", dueling.wins.toString(), Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Losses", dueling.losses.toString(), Modifier.weight(1f))
            MetricCard("Words found", dueling.totalWordsFound.toString(), Modifier.weight(1f))
        }
    }
    if (profile.duelResults.isEmpty()) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("No Speed Duels recorded yet.",
                    fontWeight = FontWeight.SemiBold, color = Forest)
                Text("Finish a private-board Duel with a nearby friend to save your result.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        Text("Recent Speed Duels", color = Forest, fontWeight = FontWeight.SemiBold)
        val page = duelPage.coerceIn(0, (profile.duelResults.size - 1).coerceAtLeast(0) / 4)
        profile.duelResults.drop(page * 4).take(4).forEach { duel ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (duel.won) "Victory" else "Defeat",
                            color = Forest, fontWeight = FontWeight.Bold)
                        Text("You " + duel.ownWords + " · Partner " + duel.partnerWords,
                            color = Forest, fontWeight = FontWeight.SemiBold)
                    }
                    Text(duel.theme.label + " · " + duel.board.cells +
                        "×" + duel.board.cells + " · " + duel.challenge.label,
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Text(DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM, DateFormat.SHORT).format(Date(duel.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (profile.duelResults.isNotEmpty()) JournalPages(
        count = profile.duelResults.size, page = duelPage, pageSize = 4,
        onPage = { duelPage = it })
    }
    if (journalTab == "Solo") {
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
        val page = soloPage.coerceIn(0, (profile.results.size - 1).coerceAtLeast(0) / 4)
        for (record in profile.results.drop(page * 4).take(4)) {
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
    if (profile.results.isNotEmpty()) JournalPages(
        count = profile.results.size, page = soloPage, pageSize = 4,
        onPage = { soloPage = it })
    }
}

/** Pure page math; every saved record remains reachable by the pager. */
internal fun journalPageCount(count: Int, pageSize: Int): Int =
    ((count.coerceAtLeast(0) + pageSize - 1) / pageSize).coerceAtLeast(1)

@Composable
private fun JournalPages(count: Int, page: Int, pageSize: Int, onPage: (Int) -> Unit) {
    val pages = journalPageCount(count, pageSize)
    if (pages <= 1) return
    val current = page.coerceIn(0, pages - 1)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { onPage(current - 1) }, enabled = current > 0) {
            Text("Previous")
        }
        Text("Page ${current + 1} of $pages",
            color = Muted, style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { onPage(current + 1) }, enabled = current < pages - 1) {
            Text("Next")
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
