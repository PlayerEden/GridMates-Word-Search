package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/** Asset-first Home actions, retaining the accessible native button hit boxes. */
@Composable
internal fun Ws16BHomeActions(
    onDaily: () -> Unit,
    onNearby: () -> Unit,
    onCustom: () -> Unit,
    onMyPuzzles: () -> Unit,
    showQuickPlayPreview: Boolean,
    compactHome: Boolean = false
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Keep comfortable icon+title space; stack at high font scales rather
        // than compressing the squirrels and shortening accessible labels.
        val stacked = maxWidth < 305.dp || LocalDensity.current.fontScale >= 1.25f
        if (stacked) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Ws16BDailyButton(onDaily, Modifier.fillMaxWidth())
                Ws16BTogetherButton(onNearby, Modifier.fillMaxWidth(), true)
            }
        } else {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Ws16BDailyButton(onDaily, Modifier.weight(1.38f))
                Ws16BTogetherButton(onNearby, Modifier.weight(1f), false)
            }
        }
    }

    // WS-17 needs its own mixed lexicon, deterministic generator and safe saves.
    // Reserve a real-sized asset slot in QA only, never a fake playable button.
    if (showQuickPlayPreview) {
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth()
                .qaTrackBounds("Quick Play")
                .testTag("ws16b-quick-play-preview")
                .semantics { contentDescription =
                    "Quick Play. Mixed-word mode in development, not playable yet" },
            variant = GridMatesFrameVariant.FEATURE
        ) {
            Row(Modifier.fillMaxWidth()
                .heightIn(min = if (compactHome) 58.dp else 64.dp)
                .padding(horizontal = 12.dp, vertical = if (compactHome) 4.dp else 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Ws16BActionArt(Ws16BArt.QUICK, iconSize = 43.dp)
                // An inset antique-gold keyline gives the painted action its
                // own lane, as in the storybook reference, without raster text.
                Spacer(Modifier.width(1.dp).height(31.dp)
                    .background(Color(0x77B78F47)))
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Quick Play", color = Forest,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Text("Mixed-word boards · coming soon",
                        color = Muted.copy(alpha = .86f),
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        .qaTrackBounds("Footer links"),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        GridMatesIllustratedLink("Custom", onCustom,
            modifier = Modifier.testTag("ws16-new-solo"))
        // The reference's quiet rule visually groups the two footer actions.
        // Remains non-interactive and does not enlarge the Home layout.
        Spacer(Modifier.width(1.dp).height(18.dp)
            .background(Gold.copy(alpha = .67f)))
        GridMatesIllustratedLink("My Puzzles", onMyPuzzles,
            modifier = Modifier.testTag("ws16-my-puzzles"))
    }
}

@Composable
private fun Ws16BDailyButton(onClick: () -> Unit, modifier: Modifier) {
    GridMatesIllustratedFrame(
        modifier = modifier.heightIn(min = 68.dp)
            .qaTrackBounds("Daily Puzzle")
            .testTag("ws16-action-daily")
            .semantics { contentDescription = "Open Daily Puzzle" },
        variant = GridMatesFrameVariant.MEDIUM,
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 68.dp)
            .padding(horizontal = 9.dp, vertical = 5.dp),
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
    GridMatesIllustratedFrame(
        modifier = modifier.heightIn(min = 68.dp)
            .qaTrackBounds("Together")
            .testTag("ws16-action-together")
            .semantics { contentDescription = "Open Together on local Wi-Fi" },
        variant = GridMatesFrameVariant.MEDIUM,
        onClick = onClick
    ) {
        if (stacked) {
            Row(Modifier.fillMaxWidth().heightIn(min = 68.dp)
                .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Ws16BActionArt(Ws16BArt.TOGETHER, iconSize = 42.dp)
                Text("Together", color = Forest,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
            }
        } else {
            // Same art size and title hierarchy as Daily, adjusted for this
            // narrower card. No top-stacked squirrel icon or tiny caption.
            Row(Modifier.fillMaxWidth().heightIn(min = 68.dp)
                .padding(horizontal = 7.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Ws16BActionArt(Ws16BArt.TOGETHER, iconSize = 39.dp)
                Text("Together", modifier = Modifier.weight(1f),
                    color = Forest, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
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
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth().testTag("ws16b-compact-menu"),
            variant = GridMatesFrameVariant.PANEL
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Word Search menu", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    androidx.compose.material3.TextButton(onClick = onDismiss,
                        modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Close")
                    }
                }
                options.forEach { (label, action) ->
                    androidx.compose.material3.TextButton(onClick = { onDismiss(); action() },
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
