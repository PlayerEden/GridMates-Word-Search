package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.RaceOutcome
import com.playereden.gridmates.wordsearch.core.SoloProfile
import java.text.DateFormat
import java.util.Date

/**
 * Small-screen-first journal. Modes are distinct; each complete, historical
 * list is paged rather than rendering dozens of cards or silently taking the
 * first N records. Pager state is view-only, never part of the saved profile.
 */
@Composable
internal fun SoloRecordsScreen(profile: SoloProfile) {
    var section by rememberSaveable { mutableStateOf("Overview") }
    var soloPage by rememberSaveable { mutableIntStateOf(0) }
    var racePage by rememberSaveable { mutableIntStateOf(0) }
    var duelPage by rememberSaveable { mutableIntStateOf(0) }

    PaperEyebrow("Your puzzle history")
    Text("Your puzzle journal", color = Forest,
        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Private records saved on this device.",
        color = Muted, style = MaterialTheme.typography.bodySmall)

    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Overview", "Solo", "Race", "Duel").forEach { name ->
            FilterChip(selected = section == name, onClick = { section = name },
                label = { Text(name) })
        }
    }

    when (section) {
        "Overview" -> {
            CompactStatsRow(listOf(
                "Solo" to profile.completions.toString(),
                "Daily" to profile.dailyCompletions.toString(),
                "Race" to profile.raceSummary.played.toString()
            ))
            CompactStatsRow(listOf(
                "Duel" to profile.duelSummary.played.toString(),
                "Words" to profile.totalWords.toString(),
                "Assisted" to profile.assistedCompletions.toString()
            ))
            Text("Your results stay separate for each game mode.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            if (profile.results.isEmpty() && profile.raceResults.isEmpty() &&
                profile.duelResults.isEmpty()) {
                Text("Your story starts with a puzzle. Play Solo, Daily, or Nearby!",
                    color = Forest, style = MaterialTheme.typography.bodyMedium)
            }
            Text("Select Solo, Race, or Duel above to browse past matches.",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
        "Solo" -> {
            CompactStatsRow(listOf(
                "Completed" to profile.completions.toString(),
                "Words found" to profile.totalWords.toString(),
                "With hints" to profile.assistedCompletions.toString()
            ))
            Text("Solo results", color = Forest,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            CompactJournalPage(
                count = profile.results.size, requestedPage = soloPage,
                onPageChange = { soloPage = it }, emptyText = "Finish a Solo puzzle to start your journal."
            ) { offset, count ->
                profile.results.drop(offset).take(count).forEach { record ->
                    CompactHistoryEntry(
                        heading = record.theme.label + " · " +
                            (record.boardSize?.cells ?: record.difficulty.gridSize).toString() +
                            "×" + (record.boardSize?.cells ?: record.difficulty.gridSize) +
                            " · " + (record.challenge?.label ?: record.difficulty.label),
                        detail = "${record.wordCount} words · " +
                            "${record.elapsedSeconds / 60L}m ${record.elapsedSeconds % 60L}s · " +
                            if (record.hintSteps == 0) "No hints"
                            else "${record.hintSteps} hint steps",
                        date = record.completedAtMs
                    )
                }
            }
        }
        "Race" -> {
            Text("Shared Board Race", color = Forest,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Co-op isn't counted as a win or loss.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            val s = profile.raceSummary
            CompactStatsRow(listOf(
                "Played" to s.played.toString(), "Wins" to s.wins.toString(),
                "Losses" to s.losses.toString(), "Ties" to s.ties.toString()
            ))
            CompactJournalPage(
                count = profile.raceResults.size, requestedPage = racePage,
                onPageChange = { racePage = it },
                emptyText = "No races yet. Finish a Shared Board Race with a nearby partner."
            ) { offset, count ->
                profile.raceResults.drop(offset).take(count).forEach { race ->
                    CompactHistoryEntry(
                        heading = when (race.outcome) {
                            RaceOutcome.WIN -> "Victory"
                            RaceOutcome.LOSS -> "Defeat"
                            RaceOutcome.TIE -> "Tie"
                        } + " · You ${race.ownWords}, Partner ${race.partnerWords}",
                        detail = race.theme.label + " · ${race.board.cells}×${race.board.cells} · " +
                            race.challenge.label,
                        date = race.completedAtMs
                    )
                }
            }
        }
        else -> {
            Text("Speed Duel", color = Forest,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Private-board matches. Separate from Shared Board Race.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            val s = profile.duelSummary
            CompactStatsRow(listOf(
                "Played" to s.played.toString(), "Wins" to s.wins.toString(),
                "Losses" to s.losses.toString(), "Words" to s.totalWordsFound.toString()
            ))
            CompactJournalPage(
                count = profile.duelResults.size, requestedPage = duelPage,
                onPageChange = { duelPage = it },
                emptyText = "No Speed Duels yet. Play a private-board match nearby."
            ) { offset, count ->
                profile.duelResults.drop(offset).take(count).forEach { duel ->
                    CompactHistoryEntry(
                        heading = (if (duel.won) "Victory" else "Defeat") +
                            " · You ${duel.ownWords}, Partner ${duel.partnerWords}",
                        detail = duel.theme.label + " · ${duel.board.cells}×${duel.board.cells} · " +
                            duel.challenge.label,
                        date = duel.completedAtMs
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStatsRow(items: List<Pair<String, String>>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { (label, value) ->
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(horizontal = 7.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(value, color = Forest, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(label, color = Muted, style = MaterialTheme.typography.labelSmall,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun CompactHistoryEntry(heading: String, detail: String, date: Long) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(heading, color = Forest, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        Text(detail, color = Muted, style = MaterialTheme.typography.bodySmall,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(date)), color = Muted,
            style = MaterialTheme.typography.labelSmall)
    }
    HorizontalDivider(color = androidx.compose.ui.graphics.Color(0xFFDFE0D6))
}

/** Fully traversable history. Four rows per page, not take(15)/take(25). */
@Composable
private fun CompactJournalPage(
    count: Int, requestedPage: Int, onPageChange: (Int) -> Unit,
    emptyText: String,
    content: @Composable (offset: Int, pageSize: Int) -> Unit
) {
    if (count == 0) {
        Text(emptyText, color = Muted, style = MaterialTheme.typography.bodyMedium)
        return
    }
    val pageSize = 4
    val pageCount = (count + pageSize - 1) / pageSize
    val page = requestedPage.coerceIn(0, pageCount - 1)
    Text("$count saved ${if (count == 1) "result" else "results"}",
        color = Muted, style = MaterialTheme.typography.labelSmall)
    content(page * pageSize, pageSize)
    if (pageCount > 1) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { onPageChange(page - 1) },
                enabled = page > 0) { Text("Previous") }
            Text("Page ${page + 1} of $pageCount",
                color = Muted, style = MaterialTheme.typography.labelMedium)
            TextButton(onClick = { onPageChange(page + 1) },
                enabled = page < pageCount - 1) { Text("Next") }
        }
    }
}
