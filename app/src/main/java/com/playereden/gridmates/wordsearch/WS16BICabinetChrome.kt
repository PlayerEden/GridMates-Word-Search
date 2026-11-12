package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * WS16BI cabinet hardware: fitted walnut plates and aged brass fittings.
 * Not generic pill buttons. All words/glyphs are native and readable.
 * The physical hit region is >= 48dp even though the visible fitting is 44dp.
 */
@Composable
internal fun WS16BICabinetControl(
    glyph: Ws16AZCabinetGlyph,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier.size(54.dp)
        .semantics { contentDescription = description }
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(44.dp)) {
            val u = 1.dp.toPx()
            val w = size.width
            val h = size.height
            val opacity = if (enabled) 1f else .38f
            drawRoundRect(Color(0xA30E0805), topLeft = Offset(1.5f*u,3.5f*u),
                size = Size(w-3f*u,h-2f*u),
                cornerRadius = CornerRadius(5*u), alpha=opacity)
            drawRoundRect(
                brush=Brush.verticalGradient(listOf(
                    Color(0xFFA66D32),Color(0xFF4A2513),
                    Color(0xFF28170D))),
                topLeft=Offset(1*u,1*u),
                size=Size(w-2*u,h-4*u),cornerRadius=CornerRadius(4*u),
                alpha=opacity
            )
            drawRoundRect(
                Color(0xFFE9BD6F),topLeft=Offset(2.2f*u,2*u),
                size=Size(w-4.4f*u,h-6*u),cornerRadius=CornerRadius(3*u),
                style=Stroke(1.4f*u),alpha=opacity
            )
            drawRoundRect(
                Color(0x9C180D08),topLeft=Offset(5*u,5*u),
                size=Size(w-10*u,h-12*u),cornerRadius=CornerRadius(2*u),
                style=Stroke(.9f*u),alpha=opacity
            )
            for (cx in listOf(7.5f*u,w-7.5f*u)) {
                for (cy in listOf(7.5f*u,h-9f*u)) {
                    drawCircle(Color(0xFF30180B),radius=2.4f*u,
                        center=Offset(cx,cy),alpha=opacity)
                    drawCircle(Color(0xFFFFD28A),radius=1.25f*u,
                        center=Offset(cx-.3f*u,cy-.45f*u),alpha=opacity)
                }
            }
            val gold=Color(0xFFFFE0A4).copy(alpha=opacity)
            val stroke=2.8f*u
            if (glyph==Ws16AZCabinetGlyph.ART_OPTIONS) {
                for(x in listOf(.33f,.5f,.67f)) {
                    drawCircle(gold,radius=2.4f*u,center=Offset(w*x,h*.47f))
                }
            } else {
                val left = glyph != Ws16AZCabinetGlyph.NEXT
                val start = if(left) .69f else .31f
                val tip = if(left) .30f else .70f
                val mid = if(left) .49f else .51f
                drawLine(gold,Offset(w*start,h*.47f),Offset(w*tip,h*.47f),
                    strokeWidth=stroke,cap=StrokeCap.Round)
                drawLine(gold,Offset(w*mid,h*.28f),Offset(w*tip,h*.47f),
                    strokeWidth=stroke,cap=StrokeCap.Round)
                drawLine(gold,Offset(w*tip,h*.47f),Offset(w*mid,h*.66f),
                    strokeWidth=stroke,cap=StrokeCap.Round)
            }
        }
    }
}

@Composable
internal fun WS16BICabinetPlaque(
    label: String,
    title: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier, contentAlignment=Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val u=1.dp.toPx()
            val w=size.width
            val h=size.height
            if(w<=8*u||h<=8*u)return@Canvas
            val r=if(title) 5f*u else 3f*u
            // A fitted wood-and-brass object, with narrow rectangle corners.
            drawRoundRect(Color(0x98110704),topLeft=Offset(1*u,4*u),
                size=Size(w-2*u,h-3*u),cornerRadius=CornerRadius(r))
            drawRoundRect(
                brush=Brush.verticalGradient(listOf(
                    Color(0xFF75401C),Color(0xFF4A2915),Color(0xFF29190F))),
                topLeft=Offset(0f,0f),size=Size(w,h-4*u),
                cornerRadius=CornerRadius(r)
            )
            drawRoundRect(
                Color(0xFFE4B565),topLeft=Offset(1.5f*u,1.3f*u),
                size=Size(w-3f*u,h-6.3f*u),
                cornerRadius=CornerRadius(r),
                style=Stroke(if(title)1.6f*u else 1.2f*u)
            )
            drawRoundRect(
                Color(0x70512614),topLeft=Offset(5.2f*u,5*u),
                size=Size(w-10.4f*u,h-14*u),
                cornerRadius=CornerRadius(2*u),style=Stroke(.8f*u)
            )
            // Very small brass mounting pins, not huge decorative brackets.
            if (w>55*u) {
                val y=(h-4*u)*.5f
                for(x in listOf(9*u,w-9*u)){
                    drawCircle(Color(0xFF28160A),2.6f*u,Offset(x,y))
                    drawCircle(Color(0xFFECC17A),1.45f*u,
                        Offset(x-.25f*u,y-.35f*u))
                }
            }
            if(title && w>145*u){
                drawLine(Color(0xAAE1AE59),Offset(28*u,5*u),
                    Offset(w-28*u,5*u),strokeWidth=1.2f*u)
            }
        }
        Text(label,
            modifier=Modifier.padding(
                horizontal=if(title) 25.dp else 16.dp,
                vertical=if(title) 11.dp else 5.dp),
            color=Color(0xFFFFE9C6),
            style=if(title) MaterialTheme.typography.titleLarge
                  else MaterialTheme.typography.labelMedium,
            fontWeight=if(title) FontWeight.Bold else FontWeight.Medium,
            maxLines=1,overflow=TextOverflow.Ellipsis
        )
    }
}
