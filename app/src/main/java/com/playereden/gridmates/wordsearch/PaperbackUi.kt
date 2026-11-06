package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val PageRule = Color(0xFFDFE0D6)
private val WarmPaper = Color(0xFFF5F0E3)

/** Reusable, calm paperback panel. No network or profile dependencies. */
@Composable
internal fun PaperPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Paper),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, PageRule),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
internal fun PaperEyebrow(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold, color = Gold)
}

@Composable
internal fun PaperProgress(found: Int, total: Int, modifier: Modifier = Modifier) {
    val fraction = if (total <= 0) 0f
        else (found.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { fraction },
        modifier = modifier.fillMaxWidth(),
        color = Forest,
        trackColor = PageRule
    )
}

@Composable
internal fun PaperbackCover(player: String) {
    PaperPanel {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                PaperEyebrow("GridMates · Puzzle Library")
                Text("Word Search", color = Forest,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("Good puzzles. Good company.", color = Muted,
                    style = MaterialTheme.typography.bodyMedium)
                Text("Welcome, " + player.trim().ifBlank { "Player" },
                    color = Ink, style = MaterialTheme.typography.labelLarge,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            // A decorative, non-interactive word-search motif: never implies
            // a board is playable or changes the user's saved progress.
            val letters = listOf("WORDS", "ATREE", "PENCI", "LOCAL", "SHARE")
            Column(
                modifier = Modifier.size(98.dp).clip(RoundedCornerShape(12.dp))
                    .background(WarmPaper)
                    .padding(8.dp)
                    .clearAndSetSemantics { },
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                letters.forEachIndexed { rowIndex, line ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        line.forEach { letter ->
                            Text(letter.toString(), color = if (rowIndex == 0) Forest else Ink,
                                fontWeight = if (rowIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.weight(1f),
                                maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
