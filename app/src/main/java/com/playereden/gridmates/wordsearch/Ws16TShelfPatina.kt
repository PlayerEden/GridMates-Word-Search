package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS-16W: shelf patina exists ONLY on the walnut face, not on its
 * transparent margins. The older horizontal gradient washes painted
 * outside the carved silhouette and formed rectangular gray artifacts.
 *
 * The illustrated shelf components supply real material/shading; these
 * few deterministic fine curves provide variation without synthetic
 * full-width shadows. No new art assets or user input.
 */
@Composable
internal fun Ws16TShelfPatina(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize().testTag("ws16t-shelf-patina")) {
        val u = 1.dp.toPx()
        val w = size.width
        if (w <= 66f * u || size.height < 32f * u) return@Canvas

        // The rail itself is between y=13..30dp and inside fixed brackets.
        // No pigment may escape into the transparent outside margins.
        clipRect(
            left = 28f * u, top = 16f * u,
            right = w - 28f * u, bottom = 29f * u
        ) {
            for (i in 0 until 19) {
                val seed = i * 73 + 19
                val span = (w - 70f * u)
                val startX = 35f * u + ((seed * 13) % 317) / 317f * span
                val length = (6f + (seed % 21)) * u
                val y = (17.4f + (seed % 10) * .98f) * u
                val stroke = Path().apply {
                    moveTo(startX, y)
                    cubicTo(
                        startX + length * .20f, y - .48f * u,
                        startX + length * .73f, y + .66f * u,
                        startX + length, y + .12f * u
                    )
                }
                drawPath(
                    path = stroke,
                    color = if (i % 3 == 0) Color(0x20522D1B)
                        else Color(0x2BE6AE68),
                    style = Stroke(
                        width = (.38f + (i % 3) * .16f) * u,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}
