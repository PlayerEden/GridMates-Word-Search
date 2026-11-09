package com.playereden.gridmates.wordsearch

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * WS-14A: a scalable native visual kit for all public-facing game screens.
 *
 * Art is an OPTIONAL independent sprite resource. These components never
 * bake labels, progress, buttons, unlocks or other live UI state into art.
 * They keep working before any generated illustration has been approved.
 *
 * This kit is deliberately free of debug-only art/runtime dependencies.
 */
internal object StorybookTokens {
    val screenGutter: Dp = 18.dp
    val touchTarget: Dp = 48.dp
    val panelRadius: Dp = 20.dp
    val panelGap: Dp = 12.dp
    val illustratedCardArt: Dp = 76.dp
    val panelBorder: Color = Color(0xFFDEDCCF)
    val selectedTrim: Color = Color(0xFFEEDCA9)
    val antiqueGold: Color = Color(0xFFAB8540)
    val warmPaper: Color = Color(0xFFFFF9EE)
    val foliageInk: Color = Color(0xFF214E3C)
    val woodShadow: Color = Color(0xFF715338)
}

/** Native content hierarchy shared by Home, Bookcase and Collection. */
@Composable
internal fun StorybookHeading(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("storybook-heading"),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        PaperEyebrow(eyebrow)
        Text(
            title,
            color = Forest,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                subtitle,
                color = Muted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Reusable paper frame for artwork-backed user-facing elements.
 * Graphics are independent; this frame can grow with localized text.
 */
@Composable
internal fun StorybookFrame(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(StorybookTokens.panelRadius),
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, StorybookTokens.panelBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(StorybookTokens.panelGap),
            content = content
        )
    }
}

/** Individual local sprite; decorative art is never a clickable UI hotspot. */
@Composable
internal fun StorybookIllustration(
    @DrawableRes resourceId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

/**
 * Native, state-driven mode card. A future separately generated illustration
 * can occupy [art] without replacing the real CTA, focus and status text.
 */
@Composable
internal fun StorybookModeCard(
    eyebrow: String,
    title: String,
    summary: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes artResource: Int? = null,
    status: String? = null
) {
    StorybookFrame(modifier.fillMaxWidth().testTag("storybook-mode-card")) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (artResource != null) {
                StorybookIllustration(
                    resourceId = artResource,
                    modifier = Modifier.size(StorybookTokens.illustratedCardArt)
                        .testTag("storybook-mode-art")
                )
            }
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                PaperEyebrow(eyebrow)
                Text(title, color = Forest,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Text(summary, color = Muted,
                    style = MaterialTheme.typography.bodySmall)
                if (status != null) {
                    Text(status, color = Ink,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(
                onClick = onAction,
                modifier = Modifier.heightIn(min = StorybookTokens.touchTarget)
                    .testTag("storybook-mode-action")
            ) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * WS-14C: short, consistently styled launcher for Daily, Nearby and Books.
 *
 * Uses actual native buttons, text and state. Unlike a flattened illustration,
 * this adapts to 320dp phones and 200% font scale without clipped actions.
 * Optional individual artwork is decorative and never replaces the CTA.
 */
@Composable
internal fun StorybookQuickEntry(
    eyebrow: String,
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    actionDescription: String? = null,
    @DrawableRes artResource: Int? = null
) {
    BoxWithConstraints(modifier.fillMaxWidth().testTag("storybook-quick-entry")) {
        val stacked = maxWidth < 325.dp || LocalDensity.current.fontScale >= 1.5f
        val buttonModifier = Modifier.heightIn(min = StorybookTokens.touchTarget)
            .then(if (stacked) Modifier.fillMaxWidth() else Modifier)
            .then(if (actionDescription.isNullOrBlank()) Modifier else
                Modifier.semantics { contentDescription = actionDescription })
        StorybookFrame(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (artResource != null) {
                    StorybookIllustration(
                        resourceId = artResource,
                        modifier = Modifier.size(StorybookTokens.illustratedCardArt)
                            .testTag("storybook-quick-art")
                    )
                }
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    PaperEyebrow(eyebrow)
                    Text(title, style = MaterialTheme.typography.titleSmall,
                        color = Forest, fontWeight = FontWeight.SemiBold)
                }
                if (!stacked) {
                    Button(onClick = onAction, modifier = buttonModifier
                        .testTag("storybook-quick-action")) {
                        Text(actionLabel, maxLines = 2)
                    }
                }
            }
            if (stacked) {
                Button(onClick = onAction, modifier = buttonModifier
                    .testTag("storybook-quick-action")) {
                    Text(actionLabel)
                }
            }
        }
    }
}
