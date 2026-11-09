package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Functional UI icons, deliberately simple vectors with real 48dp touch targets. */
@Composable
internal fun BookPageToolsButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)
        .semantics { contentDescription = "Puzzle tools" }.testTag("ws14k2-tools")) {
        Canvas(Modifier.size(24.dp)) {
            drawCircle(Forest, radius = size.width * .26f,
                center = Offset(size.width * .4f, size.height * .4f),
                style = Stroke(1.8.dp.toPx()))
            drawLine(Forest, Offset(size.width * .61f, size.height * .61f),
                Offset(size.width * .86f, size.height * .86f),
                strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
            drawLine(Forest, Offset(size.width * .27f, size.height * .4f),
                Offset(size.width * .53f, size.height * .4f),
                strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
            drawLine(Forest, Offset(size.width * .4f, size.height * .27f),
                Offset(size.width * .4f, size.height * .53f),
                strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

@Composable
internal fun BookPageNoticeHistoryButton(hasNotices: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)
        .semantics { contentDescription = "Notice history" }.testTag("ws14k2-notice-history")) {
        Canvas(Modifier.size(24.dp)) {
            drawRoundRect(Forest, topLeft = Offset(size.width * .22f, size.height * .17f),
                size = Size(size.width * .57f, size.height * .65f),
                cornerRadius = CornerRadius(3.dp.toPx()), style = Stroke(1.8.dp.toPx()))
            listOf(.35f, .49f, .63f).forEach { y ->
                drawLine(Forest, Offset(size.width * .33f, size.height * y),
                    Offset(size.width * .67f, size.height * y), strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round)
            }
            if (hasNotices) drawCircle(Gold, radius = size.width * .10f,
                center = Offset(size.width * .82f, size.height * .17f))
        }
    }
}

@Composable
internal fun BookPageCompactFilterButton(remainingOnly: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)
        .border(1.dp, if (remainingOnly) Forest else Muted, CircleShape)
        .semantics { contentDescription =
            if (remainingOnly) "Show all words" else "Show remaining words" }
        .testTag("ws14k2-word-filter")) {
        Canvas(Modifier.size(24.dp)) {
            val w = size.width
            val h = size.height
            drawLine(Forest, Offset(w*.18f,h*.25f), Offset(w*.82f,h*.25f),
                strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
            drawLine(Forest, Offset(w*.31f,h*.48f), Offset(w*.69f,h*.48f),
                strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
            drawLine(Forest, Offset(w*.43f,h*.71f), Offset(w*.57f,h*.71f),
                strokeWidth=2.dp.toPx(), cap=StrokeCap.Round)
        }
    }
}

/** Transient overlay; consumes no reserved vertical space in the book page. */
@Composable
internal fun BookPageNoticeOverlay(message: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.widthIn(max = 420.dp).padding(horizontal = 6.dp)
        .semantics { liveRegion = LiveRegionMode.Polite }
        .testTag("ws14k2-notice-overlay"),
        color = Paper, shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp)) {
        Text(message, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            color = Forest, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
    }
}

@Composable
internal fun BookPageNoticeHistoryDialog(notices: List<String>, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Game notices", color = Forest) },
        text = {
            Column(Modifier.heightIn(max = 370.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (notices.isEmpty()) {
                    Text("No notices yet. Hints and game messages appear here while this page is open.",
                        color = Muted)
                } else {
                    Text("Recent messages for this open page", color = Muted,
                        style = MaterialTheme.typography.labelMedium)
                    notices.forEach { notice ->
                        Surface(shape = RoundedCornerShape(8.dp), color = Paper) {
                            Text(notice, modifier = Modifier.fillMaxWidth().padding(10.dp),
                                color = Ink, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
