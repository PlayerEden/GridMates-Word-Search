package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/** Asset-first Home actions, retaining the accessible native button hit boxes. */
@Composable
internal fun Ws16BHomeActions(
    onDaily: () -> Unit,
    onNearby: () -> Unit,
    onCustom: () -> Unit,
    onMyPuzzles: () -> Unit,
    showQuickPlayPreview: Boolean
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = maxWidth < 300.dp || LocalDensity.current.fontScale >= 1.35f
        if (stacked) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Ws16BDailyButton(onDaily, Modifier.fillMaxWidth())
                Ws16BTogetherButton(onNearby, Modifier.fillMaxWidth(), true)
            }
        } else {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Ws16BDailyButton(onDaily, Modifier.weight(1.72f))
                Ws16BTogetherButton(onNearby, Modifier.weight(1f), false)
            }
        }
    }

    // WS-17 needs its own mixed lexicon, deterministic generator and safe saves.
    // Reserve a real-sized asset slot in QA only, never a fake playable button.
    if (showQuickPlayPreview) {
        Surface(modifier = Modifier.fillMaxWidth()
                .testTag("ws16b-quick-play-preview")
                .semantics { contentDescription =
                    "Quick Play. Mixed-word mode in development, not playable yet" },
            shape = RoundedCornerShape(16.dp), color = Paper,
            border = BorderStroke(1.dp, StorybookTokens.panelBorder)) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Ws16BActionArt(Ws16BArt.QUICK, iconSize = 46.dp)
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Quick Play", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Mixed-word boards · coming soon", color = Muted,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onCustom, modifier = Modifier.heightIn(min = 48.dp)
            .testTag("ws16-new-solo")) { Text("Custom") }
        TextButton(onClick = onMyPuzzles, modifier = Modifier.heightIn(min = 48.dp)
            .testTag("ws16-my-puzzles")) { Text("My Puzzles") }
    }
}

@Composable
private fun Ws16BDailyButton(onClick: () -> Unit, modifier: Modifier) {
    Surface(onClick = onClick,
        modifier = modifier.heightIn(min = 82.dp)
            .testTag("ws16-action-daily")
            .semantics { contentDescription = "Open Daily Puzzle" },
        shape = RoundedCornerShape(16.dp), color = Paper,
        border = BorderStroke(1.dp, StorybookTokens.panelBorder)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Ws16BActionArt(Ws16BArt.DAILY, iconSize = 42.dp)
            Text("Daily Puzzle", modifier = Modifier.weight(1f),
                color = Forest, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2)
        }
    }
}

@Composable
private fun Ws16BTogetherButton(onClick: () -> Unit, modifier: Modifier,
                                 stacked: Boolean) {
    Surface(onClick = onClick,
        modifier = modifier.heightIn(min = 82.dp)
            .testTag("ws16-action-together")
            .semantics { contentDescription = "Open Together on local Wi-Fi" },
        shape = RoundedCornerShape(16.dp), color = Paper,
        border = BorderStroke(1.dp, StorybookTokens.panelBorder)) {
        if (stacked) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Ws16BActionArt(Ws16BArt.TOGETHER, iconSize = 42.dp)
                Text("Together", color = Forest,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
            }
        } else {
            Column(Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Ws16BActionArt(Ws16BArt.TOGETHER, iconSize = 42.dp)
                Text("Together", color = Forest,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1)
            }
        }
    }
}

/** Centered, compact paperback dialog; rows remain 48dp and retain real routes. */
@Composable
internal fun Ws16BCompactMenu(
    options: List<Pair<String, () -> Unit>>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Paper,
            modifier = Modifier.fillMaxWidth().testTag("ws16b-compact-menu")) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Word Search menu", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    TextButton(onClick = onDismiss,
                        modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Close")
                    }
                }
                options.forEach { (label, action) ->
                    TextButton(onClick = { onDismiss(); action() },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start) {
                            Text(label, color = Forest,
                                style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
