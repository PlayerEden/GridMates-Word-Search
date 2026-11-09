package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Original, resolution-independent action illustrations; native text remains separate.
 * Editable SVG art masters live in design/visual-system/masters/ws16b-*.
 */
internal enum class Ws16BArt { DAILY, TOGETHER, QUICK, CONTINUE }

@Composable
internal fun Ws16BActionArt(kind: Ws16BArt,
                            modifier: Modifier = Modifier, iconSize: Dp = 44.dp) {
    Box(modifier.size(iconSize)
        .background(StorybookTokens.warmPaper, RoundedCornerShape(11.dp))
        .clearAndSetSemantics { },
        contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(iconSize * 0.76f)) {
            val w = size.width
            val h = size.height
            val s = 1.75.dp.toPx()
            fun line(x: Float, y: Float, x2: Float, y2: Float, accent: Boolean = false) {
                drawLine(if (accent) Gold else Forest,
                    Offset(w*x,h*y),Offset(w*x2,h*y2),strokeWidth=s,cap=StrokeCap.Round)
            }
            when(kind) {
                Ws16BArt.DAILY -> {
                    drawRoundRect(Forest, topLeft=Offset(w*.11f,h*.16f),
                        size=Size(w*.78f,h*.72f),cornerRadius=CornerRadius(s),
                        style=Stroke(s))
                    line(.11f,.40f,.89f,.40f)
                    line(.30f,.09f,.30f,.25f)
                    line(.70f,.09f,.70f,.25f)
                    for (x in listOf(.30f,.48f,.66f))
                        drawCircle(Muted, radius=w*.035f, center=Offset(w*x,h*.55f))
                    line(.39f,.74f,.49f,.81f,true)
                    line(.49f,.81f,.72f,.58f,true)
                }
                Ws16BArt.TOGETHER -> {
                    drawCircle(Forest,radius=w*.13f,center=Offset(w*.31f,h*.32f))
                    drawCircle(Forest,radius=w*.13f,center=Offset(w*.69f,h*.32f))
                    drawRoundRect(Forest,topLeft=Offset(w*.10f,h*.53f),
                        size=Size(w*.37f,h*.26f),cornerRadius=CornerRadius(w*.14f))
                    drawRoundRect(Forest,topLeft=Offset(w*.53f,h*.53f),
                        size=Size(w*.37f,h*.26f),cornerRadius=CornerRadius(w*.14f))
                    drawCircle(Gold,radius=w*.075f,center=Offset(w*.50f,h*.72f))
                }
                Ws16BArt.QUICK -> {
                    drawRoundRect(Forest,topLeft=Offset(w*.07f,h*.18f),
                        size=Size(w*.33f,h*.44f),cornerRadius=CornerRadius(s),style=Stroke(s))
                    drawRoundRect(Forest,topLeft=Offset(w*.58f,h*.18f),
                        size=Size(w*.33f,h*.44f),cornerRadius=CornerRadius(s),style=Stroke(s))
                    line(.18f,.47f,.24f,.31f)
                    line(.24f,.31f,.30f,.47f)
                    line(.19f,.41f,.29f,.41f)
                    line(.67f,.30f,.67f,.50f)
                    line(.67f,.30f,.80f,.34f)
                    line(.80f,.34f,.67f,.41f)
                    line(.67f,.41f,.80f,.46f)
                    line(.80f,.46f,.67f,.50f)
                    line(.33f,.79f,.67f,.79f,true)
                    line(.58f,.70f,.67f,.79f,true)
                    line(.67f,.79f,.58f,.88f,true)
                }
                Ws16BArt.CONTINUE -> {
                    drawRoundRect(Forest,topLeft=Offset(w*.26f,h*.10f),
                        size=Size(w*.48f,h*.75f),cornerRadius=CornerRadius(s),
                        style=Stroke(s))
                    line(.27f,.68f,.50f,.54f)
                    line(.50f,.54f,.73f,.68f)
                    line(.40f,.32f,.60f,.32f,true)
                }
            }
        }
    }
}
