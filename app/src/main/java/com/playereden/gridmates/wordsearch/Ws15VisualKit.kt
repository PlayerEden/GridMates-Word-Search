package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** WS-15 shared utility icon system. No rasterized labels or tiny tap targets. */
internal enum class Ws15ModeGlyph { SOLO, DAILY, TOGETHER, BOOKS }

@Composable
internal fun Ws15ModeEmblem(kind: Ws15ModeGlyph, modifier: Modifier = Modifier) {
    Box(modifier.size(44.dp)
        .background(StorybookTokens.warmPaper, RoundedCornerShape(12.dp))
        .clearAndSetSemantics { },
        contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(28.dp)) {
            val w = size.width
            val h = size.height
            val stroke = 1.9.dp.toPx()
            fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
                drawLine(Forest, Offset(w*x1,h*y1), Offset(w*x2,h*y2),
                    strokeWidth=stroke, cap=StrokeCap.Round)
            }
            when (kind) {
                Ws15ModeGlyph.SOLO -> {
                    // A tiny grid with a solved diagonal: Word Search identity.
                    drawRoundRect(Forest, topLeft=Offset(w*.09f,h*.1f),
                        size=Size(w*.82f,h*.82f),
                        style=Stroke(stroke))
                    line(.36f,.1f,.36f,.92f)
                    line(.64f,.1f,.64f,.92f)
                    line(.09f,.36f,.91f,.36f)
                    line(.09f,.64f,.91f,.64f)
                    drawLine(Gold,Offset(w*.22f,h*.78f),Offset(w*.78f,h*.22f),
                        strokeWidth=3.2.dp.toPx(),cap=StrokeCap.Round)
                }
                Ws15ModeGlyph.DAILY -> {
                    // A notebook-calendar with one prominent daily mark.
                    drawRoundRect(Forest,topLeft=Offset(w*.12f,h*.20f),
                        size=Size(w*.76f,h*.70f),style=Stroke(stroke))
                    line(.12f,.39f,.88f,.39f)
                    line(.30f,.09f,.30f,.28f)
                    line(.70f,.09f,.70f,.28f)
                    drawCircle(Gold,radius=w*.11f,center=Offset(w*.50f,h*.64f))
                }
                Ws15ModeGlyph.TOGETHER -> {
                    // Two players linked by a small local connection arc.
                    drawCircle(Forest,radius=w*.13f,center=Offset(w*.30f,h*.34f),
                        style=Stroke(stroke))
                    drawCircle(Forest,radius=w*.13f,center=Offset(w*.70f,h*.34f),
                        style=Stroke(stroke))
                    line(.12f,.80f,.12f,.67f)
                    line(.12f,.67f,.48f,.67f)
                    line(.52f,.67f,.88f,.67f)
                    line(.88f,.67f,.88f,.80f)
                    drawCircle(Gold,radius=w*.05f,center=Offset(w*.50f,h*.67f))
                }
                Ws15ModeGlyph.BOOKS -> {
                    // Paired open-book pages without text baked into art.
                    line(.50f,.23f,.50f,.87f)
                    line(.50f,.25f,.32f,.16f)
                    line(.32f,.16f,.12f,.19f)
                    line(.12f,.19f,.12f,.78f)
                    line(.12f,.78f,.34f,.75f)
                    line(.34f,.75f,.50f,.85f)
                    line(.50f,.25f,.68f,.16f)
                    line(.68f,.16f,.88f,.19f)
                    line(.88f,.19f,.88f,.78f)
                    line(.88f,.78f,.66f,.75f)
                    line(.66f,.75f,.50f,.85f)
                    drawLine(Gold,Offset(w*.21f,h*.34f),Offset(w*.36f,h*.34f),
                        strokeWidth=stroke,cap=StrokeCap.Round)
                }
            }
        }
    }
}

/**
 * Space-efficient real action for small launcher cards. Icon-only visual,
 * but a full 48dp tap target with a descriptive accessible destination.
 */
@Composable
internal fun Ws15ForwardActionButton(
    onClick: () -> Unit,
    actionDescription: String,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
            .semantics { contentDescription = actionDescription }
            .testTag("storybook-quick-action")
    ) {
        Canvas(Modifier.size(24.dp)) {
            drawLine(Forest, Offset(size.width*.22f,size.height*.5f),
                Offset(size.width*.79f,size.height*.5f),
                strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
            drawLine(Forest, Offset(size.width*.56f,size.height*.24f),
                Offset(size.width*.79f,size.height*.5f),
                strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
            drawLine(Forest, Offset(size.width*.79f,size.height*.5f),
                Offset(size.width*.56f,size.height*.76f),
                strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
        }
    }
}

/** Shared compact book/menu header. Screen-reader labels preserve destination. */
@Composable
internal fun Ws15BackHeader(
    title: String,
    onBack: () -> Unit,
    backLabel: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onPreviewOptions: (() -> Unit)? = null
) {
    Row(modifier.fillMaxWidth().testTag("ws15-screen-header"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)
            .semantics { contentDescription = backLabel }
            .testTag("ws15-back")) {
            Canvas(Modifier.size(24.dp)) {
                val w=size.width
                val h=size.height
                drawLine(Forest,Offset(w*.18f,h*.50f),Offset(w*.83f,h*.50f),
                    strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
                drawLine(Forest,Offset(w*.18f,h*.50f),Offset(w*.46f,h*.22f),
                    strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
                drawLine(Forest,Offset(w*.18f,h*.50f),Offset(w*.46f,h*.78f),
                    strokeWidth=2.3.dp.toPx(),cap=StrokeCap.Round)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(1.dp)) {
            Text(title, color=Forest, style=MaterialTheme.typography.titleMedium,
                fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle,color=Muted,style=MaterialTheme.typography.labelMedium,
                    maxLines=1,overflow=TextOverflow.Ellipsis)
            }
        }
        if (onPreviewOptions != null) {
            IconButton(onClick=onPreviewOptions,modifier=Modifier.size(48.dp)
                .semantics { contentDescription = "Preview options" }
                .testTag("ws15-preview-tools")) {
                Canvas(Modifier.size(24.dp)) {
                    for (x in listOf(.25f,.50f,.75f)) {
                        drawCircle(Forest,radius=1.85.dp.toPx(),
                            center=Offset(size.width*x,size.height*.5f))
                    }
                }
            }
        }
    }
}

/** Hidden QA comparison controls remain reachable without occupying the library cover area. */
@Composable
internal fun Ws15LibraryPreviewDialog(
    fieldNotesArt: Boolean,
    onToggleArt: (() -> Unit)?,
    onToggleLayout: (() -> Unit)?,
    onDismiss: () -> Unit,
    layoutActionLabel: String = "Classic shelves"
) {
    AlertDialog(
        onDismissRequest=onDismiss,
        title={ Text("Preview options",color=Forest) },
        text={
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Compare book layouts and artwork in this QA build. These choices do not change saved puzzle progress.",
                    color=Muted,style=MaterialTheme.typography.bodyMedium)
                if (onToggleLayout != null) {
                    TextButton(onClick={onToggleLayout();onDismiss()},
                        modifier=Modifier.fillMaxWidth().testTag("ws15-classic-layout")) {
                        Text(layoutActionLabel)
                    }
                }
                if (onToggleArt != null) {
                    TextButton(onClick={onToggleArt();onDismiss()},
                        modifier=Modifier.fillMaxWidth().testTag("ws15-art-toggle")) {
                        Text(if (fieldNotesArt) "Original art (A)" else "Illustrated art (B)")
                    }
                }
            }
        },
        confirmButton={ TextButton(onClick=onDismiss) { Text("Close") } }
    )
}
