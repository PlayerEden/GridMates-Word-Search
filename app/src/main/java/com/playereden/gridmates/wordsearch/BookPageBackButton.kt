package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Compact native back arrow with a full 48dp touch target and clear TalkBack name. */
@Composable
internal fun BookPageBackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onBack,
        modifier = modifier.size(48.dp)
            .semantics { contentDescription = "Back to book" }
            .testTag("ws14k-back-to-book")
    ) {
        Canvas(Modifier.size(24.dp)) {
            val w = size.width
            val h = size.height
            val line = 2.dp.toPx()
            drawLine(Forest, Offset(w * 0.22f, h * 0.50f),
                Offset(w * 0.80f, h * 0.50f), strokeWidth = line, cap = StrokeCap.Round)
            drawLine(Forest, Offset(w * 0.22f, h * 0.50f),
                Offset(w * 0.46f, h * 0.25f), strokeWidth = line, cap = StrokeCap.Round)
            drawLine(Forest, Offset(w * 0.22f, h * 0.50f),
                Offset(w * 0.46f, h * 0.75f), strokeWidth = line, cap = StrokeCap.Round)
        }
    }
}
