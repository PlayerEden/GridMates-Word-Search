package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * WS-15 Pass 2 native visual asset family, reused by actual Collection,
 * Records and Settings screens. Decorative symbols never replace live copy.
 */
internal enum class Ws15SectionGlyph {
    PORTRAIT, TYPEFACE, AWARD, JOURNAL, BACKUP, PREFERENCES, ARTWORK
}

@Composable
internal fun Ws15SectionHeading(
    glyph: Ws15SectionGlyph,
    title: String,
    subtitle: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.size(38.dp)
            .background(StorybookTokens.warmPaper, RoundedCornerShape(10.dp))
            .clearAndSetSemantics {},
            contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(24.dp)) {
                val w = size.width
                val h = size.height
                val stroke = 1.9.dp.toPx()
                fun line(ax:Float,ay:Float,bx:Float,by:Float) {
                    drawLine(Forest,Offset(w*ax,h*ay),Offset(w*bx,h*by),
                        strokeWidth=stroke,cap=StrokeCap.Round)
                }
                fun ring(x:Float,y:Float,r:Float) {
                    drawCircle(Forest,radius=w*r,center=Offset(w*x,h*y),
                        style=Stroke(stroke))
                }
                when(glyph) {
                    Ws15SectionGlyph.PORTRAIT -> {
                        ring(.50f,.33f,.18f)
                        line(.18f,.88f,.18f,.74f)
                        line(.18f,.74f,.34f,.62f)
                        line(.34f,.62f,.66f,.62f)
                        line(.66f,.62f,.82f,.74f)
                        line(.82f,.74f,.82f,.88f)
                        line(.18f,.88f,.82f,.88f)
                    }
                    Ws15SectionGlyph.TYPEFACE -> {
                        line(.20f,.85f,.50f,.15f)
                        line(.50f,.15f,.80f,.85f)
                        line(.31f,.60f,.69f,.60f)
                        line(.15f,.87f,.33f,.87f)
                        line(.67f,.87f,.85f,.87f)
                    }
                    Ws15SectionGlyph.AWARD -> {
                        val star = listOf(
                            .50f to .12f,.62f to .40f,.90f to .42f,
                            .69f to .62f,.76f to .90f,.50f to .75f,
                            .24f to .90f,.31f to .62f,.10f to .42f,
                            .38f to .40f,.50f to .12f
                        )
                        star.zipWithNext().forEach { (a,b) ->
                            line(a.first,a.second,b.first,b.second)
                        }
                    }
                    Ws15SectionGlyph.JOURNAL -> {
                        line(.20f,.14f,.80f,.14f)
                        line(.20f,.14f,.20f,.86f)
                        line(.20f,.86f,.80f,.86f)
                        line(.80f,.86f,.80f,.14f)
                        line(.32f,.33f,.68f,.33f)
                        line(.32f,.51f,.68f,.51f)
                        line(.32f,.69f,.58f,.69f)
                    }
                    Ws15SectionGlyph.BACKUP -> {
                        line(.21f,.62f,.21f,.83f)
                        line(.21f,.83f,.79f,.83f)
                        line(.79f,.83f,.79f,.62f)
                        line(.50f,.14f,.50f,.69f)
                        line(.29f,.35f,.50f,.14f)
                        line(.71f,.35f,.50f,.14f)
                    }
                    Ws15SectionGlyph.PREFERENCES -> {
                        ring(.50f,.50f,.20f)
                        listOf(.15f,.50f,.85f).forEach { x ->
                            if(x!=.50f) line(x,.50f,if(x<.5f) .24f else .76f,.50f)
                        }
                        line(.50f,.15f,.50f,.25f)
                        line(.50f,.75f,.50f,.85f)
                        ring(.50f,.50f,.06f)
                    }
                    Ws15SectionGlyph.ARTWORK -> {
                        line(.17f,.83f,.36f,.28f)
                        line(.36f,.28f,.78f,.15f)
                        line(.78f,.15f,.84f,.28f)
                        line(.84f,.28f,.54f,.63f)
                        line(.54f,.63f,.17f,.83f)
                        ring(.32f,.84f,.07f)
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color=Forest,
                style=MaterialTheme.typography.titleMedium,
                fontWeight=FontWeight.SemiBold)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, color=Muted,
                    style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}
