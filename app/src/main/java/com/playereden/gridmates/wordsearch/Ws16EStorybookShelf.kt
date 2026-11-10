package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS-16E illustrated book shelf. Original editable vector-part masters:
 * design/visual-system/modular/masters/wordsearch/ws16e-shelf-*.svg
 *
 * Drawn in native Canvas so the top, repeatable rail, bracket ends, contact shadow
 * and gold leaf medallion scale independently without horizontally stretching art.
 * No text, gesture handlers, save writes or book-specific content here.
 */
@Composable
internal fun Ws16EStorybookShelf(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(42.dp)
        .testTag("ws16e-storybook-shelf")) {
        val u = 1.dp.toPx()
        val w = size.width
        val cap = (w * .075f).coerceIn(19*u, 29*u)
        val dark = Color(0xFF623A1F)
        val edge = Color(0xFF8A4F27)
        val walnut = Color(0xFFAC6832)
        val honey = Color(0xFFCB8643)
        val gold = Color(0xFFDBAD48)
        val lightGold = Color(0xFFF6D27A)

        // A diffuse contact shadow is behind the wood and the books.
        drawOval(dark.copy(alpha = .14f),
            topLeft = Offset(w*.10f, 0f),
            size = Size(w*.80f, 7*u))
        drawOval(dark.copy(alpha = .11f),
            topLeft = Offset(w*.24f, 1*u),
            size = Size(w*.52f, 4*u))

        // WS-16F: sculpted walnut apron, a separate layer that never holds
        // text or touch input and doesn't stretch its gold ornament.
        val apron = Path().apply {
            moveTo(9*u,23*u)
            lineTo(w-9*u,23*u)
            lineTo(w-13*u,34*u)
            cubicTo(w*.79f,34*u,w*.67f,34*u,w*.59f,38*u)
            cubicTo(w*.54f,42*u,w*.46f,42*u,w*.41f,38*u)
            cubicTo(w*.33f,34*u,w*.21f,34*u,13*u,34*u)
            close()
        }
        drawPath(apron, brush = Brush.verticalGradient(listOf(
            Color(0xFFC58543),Color(0xFF945829),Color(0xFF65371E))))
        drawPath(apron, color = dark.copy(alpha=.72f),
            style = Stroke(.85f*u))
        drawLine(Color(0xFFF0BE73).copy(alpha=.65f),
            Offset(18*u,31*u),Offset(w*.31f,31*u),
            strokeWidth=.9f*u,cap=StrokeCap.Round)
        drawLine(Color(0xFFF0BE73).copy(alpha=.65f),
            Offset(w*.69f,31*u),Offset(w-18*u,31*u),
            strokeWidth=.9f*u,cap=StrokeCap.Round)

        // Small carved supports sit behind the face, not on top of labels.
        fun support(left: Boolean) {
            val x = if (left) 7*u else w-7*u
            val d = if (left) 1f else -1f
            val p = Path().apply {
                moveTo(x, 11*u)
                lineTo(x + d*cap, 11*u)
                lineTo(x + d*cap, 18*u)
                cubicTo(x+d*(cap-1*u), 21*u, x+d*(cap-9*u), 21*u, x+d*(cap-10*u), 29*u)
                cubicTo(x+d*(cap-12*u), 33*u, x+d*(cap-20*u), 34*u, x+d*3*u, 31*u)
                close()
            }
            drawPath(p, dark)
            val inset = Path().apply {
                moveTo(x+d*4*u, 14*u)
                lineTo(x+d*(cap-3*u), 14*u)
                lineTo(x+d*(cap-4*u), 19*u)
                quadraticBezierTo(x+d*(cap-12*u), 24*u, x+d*(cap-12*u), 30*u)
                quadraticBezierTo(x+d*7*u, 31*u, x+d*5*u, 27*u)
                close()
            }
            drawPath(inset,
                brush = Brush.verticalGradient(
                    listOf(honey, walnut, Color(0xFF75431F))))
            drawPath(inset, color = edge, style = Stroke(.7f*u))
            drawLine(Color(0xFFEBA85A), Offset(x+d*5*u,16*u),
                Offset(x+d*(cap-5*u),16*u), strokeWidth=.9f*u)
        }
        support(true)
        support(false)

        // Underside darkens the whole rail, reinforcing the physical shelf edge.
        drawRoundRect(
            color = dark.copy(alpha = .65f),
            topLeft = Offset(1*u, 24*u),
            size = Size(w-2*u, 5*u),
            cornerRadius = CornerRadius(2*u))
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFBD793A), Color(0xFF995726), Color(0xFF774421))),
            topLeft = Offset(2*u, 11*u),
            size = Size(w-4*u, 16*u),
            cornerRadius = CornerRadius(3*u))
        // Horizontal recessed carving. No pixel-stretching of ornamental corners.
        drawLine(Color(0xFF6B381C).copy(alpha=.70f),
            Offset(cap, 15*u), Offset(w-cap, 15*u), strokeWidth=.9f*u)
        drawLine(Color(0xFFE6A45B).copy(alpha=.82f),
            Offset(cap, 17*u), Offset(w-cap, 17*u), strokeWidth=.7f*u)
        drawLine(Color(0xFF542E1D).copy(alpha=.7f),
            Offset(4*u, 25*u), Offset(w-4*u, 25*u), strokeWidth=1.1f*u)

        // Individual grain strokes; the geometry scales with available width.
        for (i in 0..7) {
            val y = (19f + (i%4)*1.3f)*u
            val begin = cap + (w-2*cap)*(i*.10f)
            val end = (begin + (w-2*cap)*.13f).coerceAtMost(w-cap)
            if (end > begin) {
                val grain = Path().apply {
                    moveTo(begin, y)
                    cubicTo(begin+(end-begin)*.32f,y-1.2f*u,
                        begin+(end-begin)*.72f,y+.8f*u,end,y)
                }
                drawPath(grain,
                    color = if (i%2 == 0) Color(0xFF663917).copy(alpha=.27f)
                           else Color(0xFFFFC87E).copy(alpha=.33f),
                    style=Stroke(.55f*u,cap=StrokeCap.Round))
            }
        }

        // Top slab: several safe-to-repeat tonal bands and a crisp upper highlight.
        drawRoundRect(
            color = Color(0xFF63391E),
            topLeft = Offset(0f, 4*u),
            size = Size(w, 10*u),
            cornerRadius = CornerRadius(3*u))
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE4A455), Color(0xFFCE8540),
                    Color(0xFFB47030), Color(0xFF8A5027))),
            topLeft = Offset(1*u, 3*u),
            size = Size(w-2*u, 9*u),
            cornerRadius = CornerRadius(2.8f*u))
        drawLine(Color(0xFFFFD58C).copy(alpha=.95f),
            Offset(6*u, 4*u), Offset(w-6*u,4*u), strokeWidth=.9f*u,
            cap=StrokeCap.Round)
        drawLine(Color(0xFF6E3C1D).copy(alpha=.7f),
            Offset(5*u, 11*u), Offset(w-5*u, 11*u), strokeWidth=1*u)
        for (i in 0..5) {
            val begin = w*(.065f + i*.15f)
            val span = w*(if (i%2 == 0) .08f else .12f)
            val grain = Path().apply {
                moveTo(begin,7*u)
                quadraticBezierTo(begin+span*.55f,5.7f*u,begin+span,7.2f*u)
            }
            drawPath(grain, walnut.copy(alpha=.45f),
                style=Stroke(.65f*u,cap=StrokeCap.Round))
        }

        // Gold leaf ornament floats on the carved face as an independent motif.
        val mx = w*.5f
        val my = 20.5f*u
        val ornamentScale = (w / (360*u)).coerceIn(.76f,1.1f)
        val k = ornamentScale*u
        fun flourish(dir: Float) {
            val scroll = Path().apply {
                moveTo(mx, my)
                cubicTo(mx+dir*15*k,my+7*k,mx+dir*23*k,my-6*k,mx+dir*39*k,my)
                cubicTo(mx+dir*46*k,my+2*k,mx+dir*42*k,my+7*k,mx+dir*36*k,my+4*k)
            }
            drawPath(scroll, dark.copy(alpha=.65f),
                style=Stroke(2.6f*k,cap=StrokeCap.Round))
            drawPath(scroll, lightGold,
                style=Stroke(1.25f*k,cap=StrokeCap.Round))
            val leaf1 = Path().apply {
                moveTo(mx+dir*13*k,my)
                quadraticBezierTo(mx+dir*13*k,my-7*k,mx+dir*22*k,my-6*k)
                quadraticBezierTo(mx+dir*23*k,my-1*k,mx+dir*13*k,my)
                close()
            }
            drawPath(leaf1, gold)
            val leaf2 = Path().apply {
                moveTo(mx+dir*26*k,my+2*k)
                quadraticBezierTo(mx+dir*28*k,my+8*k,mx+dir*37*k,my+7*k)
                quadraticBezierTo(mx+dir*34*k,my+1*k,mx+dir*26*k,my+2*k)
                close()
            }
            drawPath(leaf2, gold)
        }
        flourish(-1f)
        flourish(1f)
        val centerLeaf = Path().apply {
            moveTo(mx,my+6*k)
            cubicTo(mx-9*k,my+2*k,mx-5*k,my-6*k,mx,my-7*k)
            cubicTo(mx+5*k,my-6*k,mx+9*k,my+2*k,mx,my+6*k)
            close()
        }
        drawPath(centerLeaf, dark.copy(alpha=.7f))
        drawPath(centerLeaf, gold, style=Stroke(.85f*k))
        drawLine(lightGold, Offset(mx,my-5*k), Offset(mx,my+5*k),
            strokeWidth=.8f*k,cap=StrokeCap.Round)

        // Bracket-tip glints and a thin bottom highlight establish depth.
        drawLine(Color(0xFFE7AA59).copy(alpha=.65f),
            Offset(7*u, 27*u), Offset(cap*.55f, 27*u), strokeWidth=.8f*u)
        drawLine(Color(0xFFE7AA59).copy(alpha=.65f),
            Offset(w-7*u,27*u), Offset(w-cap*.55f,27*u), strokeWidth=.8f*u)
    }
}
