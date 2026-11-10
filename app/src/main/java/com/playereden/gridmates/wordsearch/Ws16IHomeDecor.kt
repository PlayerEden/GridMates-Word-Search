package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateBottomPadding
import androidx.compose.foundation.layout.calculateTopPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp

/**
 * WS-16I: Five original alpha sprites with a matching watercolor/gouache
 * palette. These are decorations BEHIND Home controls, not tap targets.
 *
 * Debug-only bitmaps are built from versioned SVG masters. When assets are
 * absent (release), nothing draws or changes layout except the regular Home.
 */
@Composable
private fun Ws16IDecorSprite(
    name: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val ctx = LocalContext.current
    val bitmap = remember(ctx, name) {
        runCatching {
            ctx.assets.open("ws16i/decor/" + name + ".png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    } ?: return
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
        contentScale = contentScale, modifier = modifier)
}

/** Always below the transparent Scaffold, therefore cannot intercept Home taps. */
@Composable
internal fun Ws16IHomeEdgeDecor(mode: DisplayMode = DisplayMode.STANDARD) {
    val immersive = mode == DisplayMode.FULLSCREEN
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(Modifier.fillMaxSize().clearAndSetSemantics { }
        .testTag("ws16i-home-edge-decor")) {
        // In Standard the leafy parts start near the visible paper edge, not
        // underneath the clock/notification icons. Fullscreen displays fuller
        // corners in the extra screen space without intercepting touch targets.
        Ws16IDecorSprite("top-left",
            Modifier.align(Alignment.TopStart)
                .offset(x = (-62).dp,
                    y = if (immersive) (-24).dp else topInset - 13.dp)
                .width(if (immersive) 152.dp else 126.dp)
                .height(if (immersive) 100.dp else 78.dp)
                .alpha(if (immersive) .82f else .70f))
        Ws16IDecorSprite("top-right",
            Modifier.align(Alignment.TopEnd)
                .offset(x = if (immersive) 60.dp else 63.dp,
                    y = if (immersive) (-23).dp else topInset - 15.dp)
                .width(if (immersive) 152.dp else 126.dp)
                .height(if (immersive) 100.dp else 78.dp)
                .alpha(if (immersive) .82f else .70f))
        // In Standard, flower bases end ABOVE the navigation control area.
        // In fullscreen, they can extend almost to the physical bottom edge.
        Ws16IDecorSprite("bottom-left",
            Modifier.align(Alignment.BottomStart)
                .offset(x = (-6).dp,
                    y = if (immersive) (-2).dp else -(bottomInset + 3.dp))
                .width(if (immersive) 158.dp else 135.dp)
                .height(if (immersive) 98.dp else 76.dp)
                .alpha(.89f))
        Ws16IDecorSprite("bottom-right",
            Modifier.align(Alignment.BottomEnd)
                .offset(x = 3.dp,
                    y = if (immersive) (-2).dp else -(bottomInset + 3.dp))
                .width(if (immersive) 157.dp else 137.dp)
                .height(if (immersive) 98.dp else 76.dp)
                .alpha(.90f))
    }
}

/** One independent ornament: fixed-height native Home header + live type. */
@Composable
internal fun Ws16IHeaderDivider() {
    Ws16IDecorSprite("divider",
        Modifier.fillMaxWidth().height(11.dp).testTag("ws16i-home-divider"),
        contentScale = ContentScale.FillBounds)
}
