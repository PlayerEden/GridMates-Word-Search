package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
private fun Ws16IDecorSprite(name: String, modifier: Modifier) {
    val ctx = LocalContext.current
    val bitmap = remember(ctx, name) {
        runCatching {
            ctx.assets.open("ws16i/decor/" + name + ".png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    } ?: return
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
        contentScale = ContentScale.Fit, modifier = modifier)
}

/** Always below the transparent Scaffold, therefore cannot intercept Home taps. */
@Composable
internal fun Ws16IHomeEdgeDecor() {
    Box(Modifier.fillMaxSize().clearAndSetSemantics { }
        .testTag("ws16i-home-edge-decor")) {
        Ws16IDecorSprite("top-left",
            Modifier.align(Alignment.TopStart).offset(x = (-5).dp, y = 31.dp)
                .width(111.dp).height(76.dp).alpha(.68f))
        Ws16IDecorSprite("top-right",
            Modifier.align(Alignment.TopEnd).offset(x = 3.dp, y = 28.dp)
                .width(116.dp).height(76.dp).alpha(.67f))
        Ws16IDecorSprite("bottom-left",
            Modifier.align(Alignment.BottomStart).offset(x = (-4).dp, y = (-22).dp)
                .width(132.dp).height(76.dp).alpha(.84f))
        Ws16IDecorSprite("bottom-right",
            Modifier.align(Alignment.BottomEnd).offset(x = 2.dp, y = (-22).dp)
                .width(136.dp).height(74.dp).alpha(.88f))
    }
}

/** One independent ornament: fixed-height native Home header + live type. */
@Composable
internal fun Ws16IHeaderDivider() {
    Ws16IDecorSprite("divider",
        Modifier.fillMaxWidth().height(15.dp).testTag("ws16i-home-divider"))
}
