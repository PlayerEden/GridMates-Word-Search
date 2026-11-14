package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * WS-14H: a single familiar light-bulb glyph.
 * No emoji/font, new dependency, remote image, or tap overlay.
 * Explicit 48dp hit area stays accessible at large display/text settings.
 */
@Composable
internal fun BookPageHintButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ink = if (enabled) Forest else Muted.copy(alpha = 0.4f)
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(48.dp)
            .border(1.dp, ink, CircleShape)
            .semantics { contentDescription = "Hint" }
            .testTag("ws14h-hint-lightbulb")
    ) {
        Canvas(Modifier.size(24.dp)) {
            val w = size.width
            val h = size.height
            val bulb = Path().apply {
                moveTo(w * 0.36f, h * 0.64f)
                cubicTo(w * 0.34f, h * 0.57f, w * 0.23f, h * 0.51f, w * 0.24f, h * 0.35f)
                cubicTo(w * 0.26f, h * 0.13f, w * 0.74f, h * 0.13f, w * 0.76f, h * 0.35f)
                cubicTo(w * 0.77f, h * 0.51f, w * 0.66f, h * 0.57f, w * 0.64f, h * 0.64f)
            }
            val weight = 1.8.dp.toPx()
            drawPath(bulb, ink, style = Stroke(weight, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawLine(ink, Offset(w * 0.36f, h * 0.69f), Offset(w * 0.64f, h * 0.69f),
                strokeWidth = weight, cap = StrokeCap.Round)
            drawLine(ink, Offset(w * 0.39f, h * 0.79f), Offset(w * 0.61f, h * 0.79f),
                strokeWidth = weight, cap = StrokeCap.Round)
            drawLine(ink, Offset(w * 0.43f, h * 0.87f), Offset(w * 0.57f, h * 0.87f),
                strokeWidth = weight, cap = StrokeCap.Round)
            drawLine(ink, Offset(w * 0.50f, h * 0.02f), Offset(w * 0.50f, h * 0.11f),
                strokeWidth = weight, cap = StrokeCap.Round)
            drawLine(ink, Offset(w * 0.12f, h * 0.32f), Offset(w * 0.20f, h * 0.36f),
                strokeWidth = weight, cap = StrokeCap.Round)
            drawLine(ink, Offset(w * 0.88f, h * 0.32f), Offset(w * 0.80f, h * 0.36f),
                strokeWidth = weight, cap = StrokeCap.Round)
        }
    }
}
