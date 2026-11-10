package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * GM_SHARED: a near-flat, screen-filling warm paper surface.
 *
 * Neither artwork nor game text is baked into a fixed-size background image.
 * The stable, very faint paper specks tile mathematically at any phone/tablet
 * size; a radial edge wash has no seams or empty gutters on wide viewports.
 *
 * Design-source counterparts:
 * design/visual-system/modular/masters/shared/gm-parchment-tile-v1.svg
 * design/visual-system/modular/masters/shared/gm-parchment-edge-wash-v1.svg
 *
 * This is a decorative drawing with no input/semantics. Other GridMates apps
 * can reuse it deliberately, without depending on Word Search or its books.
 */
@Composable
internal fun GridMatesParchmentBackground(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()
        .clearAndSetSemantics { }
        .testTag("gm-shared-parchment-background")) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val unit = 1.dp.toPx()

        // Quiet ivory base; tiny tonal changes rather than a painted room.
        drawRect(Color(0xFFF5F1E7))
        drawRect(brush = Brush.verticalGradient(listOf(
            Color(0x13FFF8E9), Color(0x00FFFDF5), Color(0x0AE6CB9C)
        )))
        drawRect(brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x09C59551),
                Color(0x1CCDA66B)),
            center = Offset(w*.51f, h*.47f),
            radius = maxOf(w, h) * .79f
        ))

        // Deliberately restrained, deterministic flecks: no random flicker,
        // no bitmap resampling, and no seams at unusual aspect ratios.
        val cell = 18f*unit
        val cols = ceil(w / cell).toInt().coerceAtMost(180)
        val rows = ceil(h / cell).toInt().coerceAtMost(200)
        for (y in 0..rows) {
            for (x in 0..cols) {
                val seed = (x*73856093) xor (y*19349663) xor 0x13ab9d
                val sx = ((seed ushr 4) and 255) / 255f
                val sy = ((seed ushr 13) and 255) / 255f
                val cx = (x+sx)*cell
                val cy = (y+sy)*cell
                if (cx < w && cy < h) {
                    drawCircle(
                        color = if ((seed and 1) == 0)
                            Color(0x0DA27842) else Color(0x11FFFFFF),
                        radius = (.29f + (seed and 7)*.08f)*unit,
                        center = Offset(cx,cy)
                    )
                }
            }
        }
    }
}
