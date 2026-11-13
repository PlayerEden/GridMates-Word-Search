package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * WS16BJ: a fitted, hand-carved library nameplate inside the top cabinet arch.
 *
 * Unlike the old pill-like rectangular plaque, this is a walnut cartouche
 * with shaped shoulders, inset aged-brass edging and small mirrored carved
 * leaves. Native serif lettering is preserved for accessibility/localization.
 * Fixed 46dp total height: no extra line height can descend over book spines.
 *
 * This has no touch surface and no baked lettering/assets; it replaces ONLY
 * the portrait Library title. Book count and hardware remain WS16BI.
 */
@Composable
internal fun WS16BJCarvedTitlePlaque(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(46.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val u = 1.dp.toPx()
            val w = size.width
            val h = size.height
            if (w < 110f * u || h < 20f * u) return@Canvas

            fun shape(inset: Float, lower: Float): Path {
                val l = inset
                val r = w - inset
                val yTop = inset + 1.5f * u
                val yBottom = h - lower
                return Path().apply {
                    moveTo(l + 13f * u, yTop + 8f * u)
                    quadraticBezierTo(l + 3f * u, yTop + 8f * u, l + 4f * u, yTop + 17f * u)
                    lineTo(l + 4f * u, yBottom - 10f * u)
                    quadraticBezierTo(l + 4f * u, yBottom - 3f * u, l + 16f * u, yBottom - 2f * u)
                    lineTo(w * .35f, yBottom - 1.5f * u)
                    quadraticBezierTo(w * .5f, yBottom + 2f * u, w * .65f, yBottom - 1.5f * u)
                    lineTo(r - 16f * u, yBottom - 2f * u)
                    quadraticBezierTo(r - 4f * u, yBottom - 3f * u, r - 4f * u, yBottom - 10f * u)
                    lineTo(r - 4f * u, yTop + 17f * u)
                    quadraticBezierTo(r - 3f * u, yTop + 8f * u, r - 13f * u, yTop + 8f * u)
                    lineTo(w * .80f, yTop + 6f * u)
                    quadraticBezierTo(w * .71f, yTop + 1f * u, w * .63f, yTop + 3.5f * u)
                    quadraticBezierTo(w * .5f, yTop - 2f * u, w * .37f, yTop + 3.5f * u)
                    quadraticBezierTo(w * .29f, yTop + 1f * u, w * .20f, yTop + 6f * u)
                    close()
                }
            }

            // Offset shadow stays wholly underneath the physically mounted sign.
            val shadow = shape(1.4f * u, .6f * u)
            drawPath(shadow, Color(0x98100805))
            val face = shape(1.5f * u, 4.7f * u)
            drawPath(
                face,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB06A30),
                        Color(0xFF75401E),
                        Color(0xFF3B2113),
                        Color(0xFF28160E)
                    ),
                    startY = 2f * u,
                    endY = h - 5f * u
                )
            )
            drawPath(
                face,
                color = Color(0xFFE5AD5B),
                style = Stroke(width = 1.35f * u)
            )
            val insetFace = shape(5.2f * u, 7.5f * u)
            drawPath(insetFace, Color(0x73411D0F), style = Stroke(1.1f * u))

            // Inset gold is a small carved accent, not an oversized border.
            drawLine(
                Color(0xA7F1C37A),
                Offset(w * .31f, 7.5f * u),
                Offset(w * .69f, 7.5f * u),
                strokeWidth = .85f * u,
                cap = StrokeCap.Round
            )
            drawLine(
                Color(0x796C371C),
                Offset(w * .31f, h - 10f * u),
                Offset(w * .69f, h - 10f * u),
                strokeWidth = .9f * u,
                cap = StrokeCap.Round
            )

            // Mirrored leaf engraving connects the title visually to the ivy
            // and gold carved foliage in the existing cabinet crown.
            for (right in listOf(false, true)) {
                val x = if (right) w - 18f * u else 18f * u
                val direction = if (right) -1f else 1f
                val gold = Color(0xFFDCAB63)
                val stem = Path().apply {
                    moveTo(x, h * .62f)
                    quadraticBezierTo(
                        x + direction * 5f * u, h * .46f,
                        x + direction * 2f * u, h * .31f
                    )
                }
                drawPath(stem, gold.copy(alpha = .72f), style = Stroke(1.2f * u))
                for (step in 0..1) {
                    val y = h * (.43f + step * .10f)
                    val px = x + direction * (2f + step * 1f) * u
                    val leaf = Path().apply {
                        moveTo(px, y)
                        quadraticBezierTo(px + direction * 7f * u, y - 5f * u,
                            px + direction * 8f * u, y + 1f * u)
                        quadraticBezierTo(px + direction * 5f * u, y + 5f * u,
                            px, y)
                        close()
                    }
                    drawPath(leaf, gold.copy(alpha = .64f))
                }
                drawCircle(
                    Color(0xFF211308),
                    radius = 2.2f * u,
                    center = Offset(x - direction * 2f * u, h * .66f)
                )
                drawCircle(
                    Color(0xFFFFDC91),
                    radius = 1.15f * u,
                    center = Offset(x - direction * 2f * u, h * .66f - .35f * u)
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 29.dp, vertical = 4.dp),
            color = Color(0xFFFFEAD0),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
