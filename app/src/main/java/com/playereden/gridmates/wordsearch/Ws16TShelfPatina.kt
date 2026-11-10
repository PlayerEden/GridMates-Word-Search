package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS-16T — subtle irregular transparent pigment and wood-grain overlay.
 * This complements the separately sourced WS16G image pieces without
 * flattening or re-stretching the original gold medallion/wood supports.
 *
 * No outboard undershelf shadows, new bitmap files, touch surfaces,
 * random/flickering positions or book/progress dependency. A richer source-art paintover can replace
 * the existing shelves after the user sees a physical-device comparison.
 */
@Composable
internal fun Ws16TShelfPatina(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize().testTag("ws16t-shelf-patina")) {
        val u = 1.dp.toPx()
        val w = size.width
        if (w <= 0f || size.height < 30*u) return@Canvas

        // The pictured shelf's left/right edges catch less illumination.
        drawRect(
            brush = Brush.horizontalGradient(listOf(
                Color(0x3B472614), Color.Transparent,
                Color.Transparent, Color(0x41432815)
            )),
            topLeft = Offset(0f, 9*u),
            size = Size(w, 25*u)
        )

        // A warm body glaze varies from side to side rather than a flat,
        // synthetic strip across the entire width.
        drawRect(
            brush = Brush.horizontalGradient(listOf(
                Color(0x10EBC07E), Color(0x04EBC07E),
                Color(0x1BB67740), Color(0x08F7D497),
                Color(0x19B37743)
            )),
            topLeft = Offset(16*u, 13*u),
            size = Size((w - 32*u).coerceAtLeast(1f), 17*u)
        )

        clipRect(left = 25*u, top = 15*u,
            right = (w - 25*u).coerceAtLeast(25*u), bottom = 31*u) {
            for (i in 0 until 33) {
                // Deterministic hand-worked bends. Short offset lengths
                // avoid rhythmic parallel stripes and obvious tiling.
                val seed = (i * 37 + 11)
                val startX = 25*u + ((seed * 31) % 331) / 331f *
                    (w - 50*u).coerceAtLeast(1f)
                val length = (8 + seed % 19) * u
                val y = (17.3f + (seed % 11) * .88f) * u
                val p = Path().apply {
                    moveTo(startX, y)
                    cubicTo(startX + length*.24f, y - .75f*u,
                        startX + length*.57f, y + 1.04f*u,
                        startX + length, y + .15f*u)
                }
                drawPath(p,
                    if (i%3 == 0) Color(0x3D59311D)
                    else Color(0x42ECC17A),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = (.40f + (i%3)*.22f)*u, cap = StrokeCap.Round))
            }
        }

        // Hand-burnished dimples and contact wear, concentrated beneath
        // where the central illustrated book actually rests.
        for (i in 0 until 12) {
            val x = w*(.24f + ((i*43)%97)/97f*.52f)
            val y = (6.3f + (i%3)*1.15f)*u
            drawOval(Color(0x246B3B1F), Offset(x,y),
                Size((5+i%4)*u, (.8f+i%2*.3f)*u))
        }

        // WS-16V: no rectangular shadow outside the painted apron.
        // The independently illustrated shelf already has a shaded undercut.
        // Previous 13dp gradient looked like an artificial gray stripe on
        // real Android screenshots. Pigment/woodgrain stays inside the wood.
    }
}
