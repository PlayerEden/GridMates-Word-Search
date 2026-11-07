package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
    // Material3's decorative end dot looks like completed progress at 0%.
    Box(
        modifier.fillMaxWidth().height(7.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(PageRule)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f) }
    ) {
        if (fraction > 0f) {
            Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(Forest))
        }
    }
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
            // Decorative letters must never clip or crowd enlarged text.
            val letters = listOf("WORDS", "ATREE", "PENCI", "LOCAL", "SHARE")
            if (LocalDensity.current.fontScale < 1.3f) Column(
                modifier = Modifier.size(98.dp).clip(RoundedCornerShape(12.dp))
                    .background(WarmPaper)
                    .padding(8.dp)
                    .clearAndSetSemantics { },
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // The standard 16sp label line height overflowed this 98dp
                // square on a real phone. Use a bounded, decorative micro-grid
                // with five explicit rows and no font-family dependencies.
                letters.forEachIndexed { rowIndex, line ->
                    Row(Modifier.fillMaxWidth().height(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        line.forEach { letter ->
                            Text(letter.toString(),
                                color = if (rowIndex == 0) Forest else Ink,
                                fontWeight = if (rowIndex == 0) FontWeight.Bold
                                    else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp, lineHeight = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Clip)
                        }
                    }
                }
            }
        }
    }
}
