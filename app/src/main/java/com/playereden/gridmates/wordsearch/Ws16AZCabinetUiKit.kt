package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Reusable wood-and-gold UI, independent of the five-piece cabinet artwork.
 * All labels remain editable native text; the glyph plates are full 48dp buttons. */
internal enum class Ws16AZCabinetGlyph { BACK, PREVIOUS, NEXT, ART_OPTIONS }

@Composable
internal fun Ws16AZCabinetControl(
    glyph: Ws16AZCabinetGlyph,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier.size(48.dp)
            .semantics { contentDescription = description }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val u = 1.dp.toPx()
            val w = size.width
            val h = size.height
            val fade = if (enabled) 1f else .42f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xE04B2914), Color(0xF42A180D))),
                size = Size(w, h), cornerRadius = CornerRadius(12f*u),
                alpha = fade
            )
            drawRoundRect(
                color = Color(0xFFE3B862),
                topLeft = Offset(1.3f*u,1.3f*u),
                size = Size(w-2.6f*u,h-2.6f*u),
                cornerRadius = CornerRadius(11f*u),
                style = Stroke(1.25f*u), alpha = fade
            )
            drawRoundRect(
                color = Color(0x806F4B29),
                topLeft = Offset(4.4f*u,4.4f*u),
                size = Size(w-8.8f*u,h-8.8f*u),
                cornerRadius = CornerRadius(8f*u),
                style = Stroke(.7f*u), alpha = fade
            )
            val ink = Color(0xFFFFE5A5).copy(alpha = fade)
            val line = 2.4f*u
            if (glyph == Ws16AZCabinetGlyph.ART_OPTIONS) {
                for (x in listOf(.30f,.50f,.70f)) {
                    drawCircle(ink, radius=2.15f*u, center=Offset(w*x,h*.5f))
                }
            } else {
                val left = glyph != Ws16AZCabinetGlyph.NEXT
                val head = if (left) .31f else .69f
                val tail = if (left) .69f else .31f
                val joint = if (left) .49f else .51f
                drawLine(ink, Offset(w*tail,h*.5f), Offset(w*head,h*.5f),
                    strokeWidth=line,cap=StrokeCap.Round)
                drawLine(ink, Offset(w*joint,h*.32f), Offset(w*head,h*.5f),
                    strokeWidth=line,cap=StrokeCap.Round)
                drawLine(ink, Offset(w*head,h*.5f), Offset(w*joint,h*.68f),
                    strokeWidth=line,cap=StrokeCap.Round)
            }
        }
    }
}

/** Resizable native-text placard: border/wood texture are never text images. */
@Composable
internal fun Ws16AZCabinetPlaque(
    label: String,
    title: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val u = 1.dp.toPx()
            val w = size.width
            val h = size.height
            if (w <= 4f*u || h <= 4f*u) return@Canvas
            val r = if (title) 13f*u else 10f*u
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(
                    Color(0xD342240F),Color(0xEA27160C))),
                size=Size(w,h),cornerRadius=CornerRadius(r)
            )
            drawRoundRect(Color(0xD3D9AB59),topLeft=Offset(.9f*u,.9f*u),
                size=Size(w-1.8f*u,h-1.8f*u),cornerRadius=CornerRadius(r),
                style=Stroke(1f*u))
            if (w>55f*u) {
                drawLine(Color(0x90F5D28C),Offset(16f*u,4f*u),
                    Offset(w-16f*u,4f*u),strokeWidth=.9f*u,cap=StrokeCap.Round)
                drawLine(Color(0x774A260F),Offset(15f*u,h-4f*u),
                    Offset(w-15f*u,h-4f*u),strokeWidth=.9f*u)
            }
        }
        Text(label,modifier=Modifier.padding(
                horizontal=if (title) 21.dp else 14.dp,
                vertical=if (title) 8.dp else 7.dp),
            color=Color(0xFFFFE7BA),
            style=if (title) MaterialTheme.typography.titleLarge
                  else MaterialTheme.typography.labelMedium,
            fontWeight=FontWeight.Bold,
            maxLines=1,overflow=TextOverflow.Ellipsis)
    }
}
