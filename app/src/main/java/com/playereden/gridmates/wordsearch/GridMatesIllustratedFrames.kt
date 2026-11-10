package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * GM_SHARED V1 illustrated frames, first piloted in Word Search.
 * Decorative source masters: design/visual-system/modular/masters/shared/.
 * Native labels, icons, progress, click/ripple and focus remain independent.
 * Simple quiet app background: all hand-painted character belongs in objects.
 */
internal enum class GridMatesFrameVariant { MEDIUM, FEATURE, PANEL, PRIMARY_PILL }

@Composable
internal fun GridMatesIllustratedFrame(
    modifier: Modifier = Modifier,
    variant: GridMatesFrameVariant = GridMatesFrameVariant.MEDIUM,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(
        if (variant == GridMatesFrameVariant.PRIMARY_PILL) 28.dp else 16.dp
    )
    val inner: @Composable () -> Unit = {
        Box {
            Canvas(Modifier.matchParentSize().clearAndSetSemantics { }) {
                drawGridMatesPaperFrame(variant, enabled, pressed)
            }
            content()
        }
    }
    if (onClick == null) {
        Surface(modifier = modifier, shape = shape,
            color = Color.Transparent, shadowElevation = 1.dp, content = inner)
    } else {
        Surface(onClick = onClick, modifier = modifier,
            enabled = enabled, interactionSource = interactionSource,
            shape = shape, color = Color.Transparent,
            shadowElevation = 1.dp, content = inner)
    }
}

@Composable
internal fun GridMatesIllustratedPill(
    label: String, onClick: (() -> Unit)?,
    modifier: Modifier = Modifier, enabled: Boolean = true
) {
    GridMatesIllustratedFrame(
        modifier = modifier.heightIn(min = 48.dp),
        variant = GridMatesFrameVariant.PRIMARY_PILL,
        onClick = onClick, enabled = enabled
    ) {
        Box(Modifier.padding(horizontal = 19.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center) {
            Text(label, color = Color(0xFFFFF8E8),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold)
        }
    }
}

/** Small native text-only action, no stretched or baked lettering. */
@Composable
internal fun GridMatesIllustratedLink(
    label: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier.heightIn(min = 48.dp)) {
        Text(label, color = Forest,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.SemiBold)
    }
}

private fun DrawScope.drawGridMatesPaperFrame(
    variant: GridMatesFrameVariant, enabled: Boolean, pressed: Boolean
) {
    val w = size.width
    val h = size.height
    if (w < 2f || h < 2f) return
    val u = 1.dp.toPx()
    val pill = variant == GridMatesFrameVariant.PRIMARY_PILL
    val rad = if (pill) 26f*u else 16f*u
    val opacity = if (enabled) 1f else .57f
    val touch = if (pressed) .92f else 1f
    val gold = Color(0xFFBC944A)
    val darkGold = Color(0xFF8B662B)

    // Frame center can stretch to any width; corners/ornaments are drawn anew.
    drawRoundRect(
        brush = if (pill)
            Brush.verticalGradient(listOf(Color(0xFF416C49),
                Color(0xFF28583F),Color(0xFF173F32)))
        else Brush.verticalGradient(listOf(
            Color(0xFFFFFDF4),Color(0xFFFFF9EC),Color(0xFFF5E8CF))),
        topLeft = Offset.Zero, size = Size(w,h),
        cornerRadius = CornerRadius(rad), alpha = opacity*touch
    )
    if (!pill) {
        // WS-16L: separate ink/aged-paper glazing from the touch surface.
        // Work is bounded by the existing rounded frame and drawn underneath
        // live labels/icons, never as a fixed-size rasterized button.
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(
                Color(0x10B88A43), Color(0x00B88A43),
                Color(0x00B88A43), Color(0x0DB88A43)
            )),
            topLeft = Offset.Zero, size = Size(w,h),
            cornerRadius = CornerRadius(rad), alpha = opacity
        )
        if (w>55*u && h>30*u) {
            // Light catches the inner top and softly darkens the lower lip.
            drawLine(Color(0x7CFFFFFF),
                Offset(19*u,5*u),Offset(w-19*u,5*u),
                strokeWidth=.85f*u,cap=StrokeCap.Round)
            drawLine(Color(0x41AD8144),
                Offset(19*u,h-5*u),Offset(w-19*u,h-5*u),
                strokeWidth=.8f*u,cap=StrokeCap.Round)
        }
    }
    drawRoundRect(
        color = if (pill) Color(0xFFE5C16F) else gold,
        topLeft = Offset(.9f*u,.9f*u),
        size = Size(w-1.8f*u,h-1.8f*u),
        cornerRadius = CornerRadius(rad-1*u),
        style = Stroke(if (variant == GridMatesFrameVariant.FEATURE)
            1.55f*u else 1.25f*u),
        alpha = opacity
    )
    if (w>12*u && h>12*u) drawRoundRect(
        color = if (pill) Color(0xFF99722E) else Color(0xFFEDDEBE),
        topLeft = Offset(3.2f*u,3.2f*u),
        size = Size(w-6.4f*u,h-6.4f*u),
        cornerRadius = CornerRadius((rad-4*u).coerceAtLeast(3*u)),
        style = Stroke(.8f*u), alpha = opacity*.80f
    )

    if (pill) {
        drawLine(Color(0xFFFFD987).copy(alpha=.65f),
            Offset(15*u,3.5f*u), Offset((w-15*u).coerceAtLeast(15*u),3.5f*u),
            strokeWidth=.8f*u,cap=StrokeCap.Round)
        return
    }

    // Antique-gold leaf-like corner engraving, reusable at any card width.
    val scale = minOf(w*.10f,h*.24f,14*u).coerceAtLeast(4*u)
    fun corner(x: Float,y: Float,sx: Float,sy: Float) {
        val curve=Path().apply {
            moveTo(x+sx*2*u,y+sy*(scale*.79f))
            quadraticTo(x+sx*(scale*.11f),y+sy*(scale*.12f),
                x+sx*(scale*.88f),y+sy*2*u)
        }
        drawPath(curve,gold,style=Stroke(.9f*u,cap=StrokeCap.Round),
            alpha=opacity*.70f)
        if(scale>7*u) {
            val leaf=Path().apply {
                moveTo(x+sx*(scale*.53f),y+sy*(scale*.42f))
                quadraticTo(x+sx*(scale*.83f),y+sy*(scale*.33f),
                    x+sx*(scale*.77f),y+sy*(scale*.67f))
                quadraticTo(x+sx*(scale*.54f),y+sy*(scale*.68f),
                    x+sx*(scale*.53f),y+sy*(scale*.42f))
                close()
            }
            drawPath(leaf,darkGold,alpha=opacity*.55f)
        }
    }
    val m=4.7f*u
    corner(m,m,1f,1f)
    corner(w-m,m,-1f,1f)
    corner(m,h-m,1f,-1f)
    corner(w-m,h-m,-1f,-1f)
    if (w>65*u) drawLine(Color.White.copy(alpha=.66f*opacity),
        Offset(20*u,3.8f*u),Offset(w-20*u,3.8f*u),
        strokeWidth=.8f*u,cap=StrokeCap.Round)

    // Almost invisible flecks at the corners give the paper frame a
    // crafted surface rather than a perfectly flat computer gradient.
    // Keep them outside text-heavy central space and deterministic.
    if (w > 115*u && h > 46*u) {
        val edge = Color(0x3DB98C4D)
        for (i in 0 until 6) {
            val shift = (i*7.2f + 22f)*u
            val fromLeft = (i and 1) == 0
            val x = if (fromLeft) shift else w-shift
            val y = if ((i and 2) == 0) 7.5f*u else h-7.5f*u
            drawCircle(edge, radius=(.28f + (i%3)*.12f)*u,
                center=Offset(x,y),alpha=opacity*.37f)
        }
    }
}
