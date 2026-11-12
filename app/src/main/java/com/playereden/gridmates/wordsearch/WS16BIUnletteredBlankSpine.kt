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
    val style = listOf("fern", "dusk", "clay")[index.mod(3)]
    val context = LocalContext.current
    val bitmap = remember(context, style) {
        runCatching {
            context.assets.open("ws16bi/blanks/$style.png").use {
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
                modifier = Modifier.fillMaxSize().alpha(0.62f)
            )
        }
    }
}
