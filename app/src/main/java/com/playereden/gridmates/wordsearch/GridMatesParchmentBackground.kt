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

        // WS-16L: the soft, irregular warmth of an aged paperback leaf.
        // These washes are deliberately lower contrast than any native text:
        // they give the paper depth, not obvious corners or a painted vignette.
        drawRect(brush = Brush.horizontalGradient(listOf(
            Color(0x13C58E4E), Color(0x02C58E4E),
            Color(0x00C58E4E), Color(0x0DBF8F58)
        )))
        drawCircle(
            brush = Brush.radialGradient(listOf(
                Color(0x13D8B77C), Color(0x00D8B77C)
            ), center = Offset(w*.09f, h*.25f),
                radius = maxOf(w*.56f, 145f*unit)),
            radius = maxOf(w*.56f, 145f*unit),
            center = Offset(w*.09f, h*.25f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(
                Color(0x0FC99A5C), Color(0x00C99A5C)
            ), center = Offset(w*.94f, h*.83f),
                radius = maxOf(w*.60f, 165f*unit)),
            radius = maxOf(w*.60f, 165f*unit),
            center = Offset(w*.94f, h*.83f)
        )

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

        // A sparse second pass of tiny paper fibers. All positions come from
        // integer coordinates: no random changes on recomposition, animation
        // flicker, bitmap tiling seams or new assets in the release package.
        val fiberCell = 61f*unit
        val fiberCols = ceil(w/fiberCell).toInt().coerceAtMost(80)
        val fiberRows = ceil(h/fiberCell).toInt().coerceAtMost(100)
        for (y in 0..fiberRows) {
            for (x in 0..fiberCols) {
                val seed = (x*28411) xor (y*71933) xor 0x5a13
                val fx = (x + ((seed ushr 5) and 127)/128f)*fiberCell
                val fy = (y + ((seed ushr 13) and 127)/128f)*fiberCell
                if (fx < w && fy < h) {
                    val dx = (2.4f + (seed and 3))*unit
                    val dy = ((seed ushr 3) and 3)*.48f*unit
                    drawLine(
                        color = if ((seed and 1) == 0)
                            Color(0x14B08C5A) else Color(0x13FFFFFF),
                        start = Offset(fx,fy),
                        end = Offset((fx+dx).coerceAtMost(w),
                            (fy+dy).coerceAtMost(h)),
                        strokeWidth = .33f*unit
                    )
                }
            }
        }
    }
}
