package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics

/**
 * Environmental furniture only. These are NOT locked books, inventory,
 * unlockables or clicks. The pinned procedural art contains no text/number.
 * Absent QA-only assets, production quietly displays no pretend ownership.
 */
@Composable
internal fun WS16BIUnletteredBlankSpine(
    index: Int,
    modifier: Modifier = Modifier
) {
    // WS16BK: eight distinct ornamental patterns, no letters or pseudo-titles.
    // Older v68 art is a safe fallback on builds lacking new debug assets.
    val styles = listOf(
        "fern", "dusk", "clay", "garnet",
        "ochre", "teal", "violet", "slate"
    )
    val style = styles[index.mod(styles.size)]
    val context = LocalContext.current
    val bitmap = remember(context, style) {
        val fallback = listOf("fern", "dusk", "clay")[index.mod(3)]
        runCatching {
            context.assets.open("ws16bk/blanks/$style.png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull() ?: runCatching {
            context.assets.open("ws16bi/blanks/$fallback.png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    }
    Box(
        modifier.clearAndSetSemantics {}
            .testTag("ws16bi-blank-$index")
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                // Still subdued versus 100%-opacity real books, but less
                // transparent/flat than the v70 handset preview.
                modifier = Modifier.fillMaxSize().alpha(0.75f)
            )
        }
    }
}
