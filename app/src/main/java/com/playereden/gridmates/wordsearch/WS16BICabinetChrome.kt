package com.playereden.gridmates.wordsearch

import android.animation.ValueAnimator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * WS16BM: GridMates shared parchment/gold illustrated controls inside
 * the carved walnut environment. The painter is identical to the Home
 * feature-card painter and icons use the Home header's dark forest ink.
 * WS16BL 115ms press-depth animation and 54dp hitboxes stay untouched.
 * All names, tap actions and enabled/disabled states remain native.
 */
@Composable
internal fun WS16BICabinetControl(
    glyph: Ws16AZCabinetGlyph,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // WS16BL: move the inset metal plate, never shrink the 54dp touch
    // target. System animation-off respects reduced-motion preference.
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val motionAllowed = ValueAnimator.areAnimatorsEnabled()
    val inset by animateFloatAsState(
        targetValue = if (enabled && pressed) 1f else 0f,
        animationSpec = tween(durationMillis = if (motionAllowed) 115 else 0),
        label = "ws16bl-cabinet-press"
    )
    Box(
        modifier.size(54.dp)
            .semantics { contentDescription = description }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier.size(44.dp)
                .graphicsLayer {
                    scaleX = 1f - .052f * inset
                    scaleY = 1f - .052f * inset
                    translationY = 1.5.dp.toPx() * inset
                }
                .testTag("ws16bl-animated-face")
        ) {
            // WS16BM: draw the EXACT frame used by Home's illustrated cards,
            // not a separate dark-wood miniature button. Shared warm paper,
            // dual antique-gold borders and corner leaf engravings stay in sync
            // whenever GridMatesIllustratedFrame is updated app-wide.
            val unit = 1.dp.toPx()
            drawRoundRect(
                color = Color(0x66140C06),
                topLeft = Offset(2f*unit,3.2f*unit),
                size = Size(size.width-4f*unit,size.height-3.5f*unit),
                cornerRadius = CornerRadius(14f*unit)
            )
            drawGridMatesPaperFrame(
                variant = GridMatesFrameVariant.MEDIUM,
                enabled = enabled,
                pressed = inset > .5f
            )
            // Native dark-forest icons match the actual Home header and
            // Ws15BackHeader icons, with no lettering baked into assets.
            val ink = Forest.copy(alpha = if (enabled) 1f else .43f)
            val w = size.width
            val h = size.height
            val stroke = 2.55f*unit
            if (glyph == Ws16AZCabinetGlyph.ART_OPTIONS) {
                // Same three horizontal lines as the Home menu glyph.
                for (fraction in listOf(.30f,.50f,.70f)) {
                    drawLine(
                        color = ink,
                        start = Offset(w*.25f,h*fraction),
                        end = Offset(w*.75f,h*fraction),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            } else {
                val left = glyph != Ws16AZCabinetGlyph.NEXT
                val tail = if (left) .74f else .26f
                val tip = if (left) .26f else .74f
                val bend = if (left) .49f else .51f
                drawLine(ink, Offset(w*tail,h*.50f),Offset(w*tip,h*.50f),
                    strokeWidth=stroke,cap=StrokeCap.Round)
                drawLine(ink, Offset(w*bend,h*.28f),Offset(w*tip,h*.50f),
                    strokeWidth=stroke,cap=StrokeCap.Round)
                drawLine(ink, Offset(w*tip,h*.50f),Offset(w*bend,h*.72f),
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

/** Keep earlier AZ design as an intact fallback for compact/landscape modes. */
@Composable
internal fun WS16BIAdaptivePlaque(
    label: String,
    title: Boolean,
    styled: Boolean,
    modifier: Modifier = Modifier
) {
    // WS16BJ is intentionally scoped to the feature title. Count labels and
    // page numbers keep their compact v68 fittings, and the older AZ kit is
    // still the fallback for compact/landscape scenes.
    if (styled && title) WS16BJCarvedTitlePlaque(label, modifier)
    else if (styled) WS16BICabinetPlaque(label, title, modifier)
    else Ws16AZCabinetPlaque(label, title, modifier)
}

@Composable
internal fun WS16BIAdaptiveControl(
    glyph: Ws16AZCabinetGlyph,
    description: String,
    onClick: () -> Unit,
    styled: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (styled) WS16BICabinetControl(glyph, description, onClick, modifier, enabled)
    else Ws16AZCabinetControl(glyph, description, onClick, modifier, enabled)
}
