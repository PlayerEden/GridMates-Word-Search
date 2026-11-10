package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** WS-16M fullscreen-first native wordmarks plus two distinct art emblems.
 * Native "GridMates" and "Word Search" text remains crisp at large fonts;
 * individually versioned SVGs supply identity illustration in QA.
 */
@Composable
internal fun Ws16HomeHeader(
    displayMode: DisplayMode = DisplayMode.FULLSCREEN,
    onProfile: () -> Unit,
    onMenu: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // WS-16J: Reserve a quiet border between title and hanging leaves.
        // On narrow or enlarged-font layouts, reclaim this space for legibility.
        // The old >=340dp threshold ignored common 360dp devices because
        // horizontal page padding leaves a ~324dp content width.
        val roomy = maxWidth >= 310.dp && LocalDensity.current.fontScale < 1.30f
        val veryRoomy = maxWidth >= 370.dp
        val leftInset = when {
            !roomy -> 4.dp
            displayMode == DisplayMode.FULLSCREEN && veryRoomy -> 35.dp
            displayMode == DisplayMode.FULLSCREEN -> 23.dp
            veryRoomy -> 24.dp
            else -> 17.dp
        }
        Row(Modifier.fillMaxWidth().testTag("ws16-home-header"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(Modifier.weight(1f).padding(start = leftInset),
            verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Ws16FamilyMark()
                Text("GridMates", color = Forest,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Ws16WordSearchMark()
                Text("Word Search", color = Forest,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
        if (roomy && veryRoomy) Spacer(Modifier.width(7.dp))
        IconButton(onClick = onProfile,
            modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Open player profile" }
                .testTag("ws16-profile")) {
            Canvas(Modifier.size(24.dp)) {
                drawCircle(Forest, radius = size.width * .14f,
                    center = Offset(size.width * .50f, size.height * .31f),
                    style = Stroke(2.dp.toPx()))
                drawArc(Forest, startAngle = 196f, sweepAngle = 148f,
                    useCenter = false, topLeft = Offset(size.width * .18f, size.height * .42f),
                    size = androidx.compose.ui.geometry.Size(size.width * .64f,
                        size.height * .45f), style = Stroke(2.dp.toPx(),
                        cap = StrokeCap.Round))
            }
        }
        IconButton(onClick = onMenu,
            modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Open menu" }
                .testTag("ws16-menu")) {
            Canvas(Modifier.size(24.dp)) {
                for (fraction in listOf(.25f, .50f, .75f)) {
                    drawLine(Forest,
                        Offset(size.width * .16f, size.height * fraction),
                        Offset(size.width * .84f, size.height * fraction),
                        strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                }
            }
        }
        }
    }
}

/** Two linked paper tiles: the shared identity belongs to all GridMates titles. */
@Composable
private fun Ws16FamilyMark() {
    if (Ws16MBrandEmblem("gridmates", 25.dp, "ws16-family-mark")) return
    Canvas(Modifier.size(25.dp).clearAndSetSemantics { }
        .testTag("ws16-family-mark")) {
        val w = size.width
        val h = size.height
        drawRoundRect(Gold, topLeft = Offset(w*.08f,h*.12f),
            size = androidx.compose.ui.geometry.Size(w*.50f,h*.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
        drawRoundRect(Forest, topLeft = Offset(w*.42f,h*.34f),
            size = androidx.compose.ui.geometry.Size(w*.49f,h*.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            style = Stroke(2.dp.toPx()))
        drawLine(Paper,Offset(w*.20f,h*.43f),Offset(w*.44f,h*.43f),
            strokeWidth = 2.dp.toPx(),cap = StrokeCap.Round)
    }
}

/** Word Search-only identity, separate from the reusable GridMates logo. */
@Composable
private fun Ws16WordSearchMark() {
    if (Ws16MBrandEmblem("wordsearch", 28.dp, "ws16-word-search-mark")) return
    Canvas(Modifier.size(28.dp).clearAndSetSemantics { }
        .testTag("ws16-word-search-mark")) {
        val w = size.width
        val h = size.height
        drawRoundRect(Forest, topLeft = Offset(w*.08f,h*.08f),
            size = androidx.compose.ui.geometry.Size(w*.84f,h*.84f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            style = Stroke(1.5.dp.toPx()))
        for (v in listOf(.36f,.64f)) {
            drawLine(Forest,Offset(w*v,h*.08f),Offset(w*v,h*.92f),
                strokeWidth = 1.dp.toPx())
            drawLine(Forest,Offset(w*.08f,h*v),Offset(w*.92f,h*v),
                strokeWidth = 1.dp.toPx())
        }
        drawLine(Gold,Offset(w*.22f,h*.78f),Offset(w*.78f,h*.22f),
            strokeWidth = 2.9.dp.toPx(),cap = StrokeCap.Round)
    }
}
