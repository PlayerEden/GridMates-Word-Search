package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS16BB: a replaceable independent walnut-and-gold shelf matching the new
 * 1024px cabinet. Its raster has alpha, no baked books or text.
 * The image's TOP is the book-contact plane. Preserve the original 42dp
 * book-contact offset on every screen width; never stretch the cabinet art.
 */
private fun readWs16BbShelf(context: Context): Bitmap? = runCatching {
    context.assets.open("ws16bb/shelf.png").use {
        BitmapFactory.decodeStream(it)
    }
}.getOrNull()

@Composable
internal fun Ws16BBMatchedCabinetShelf(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val image = remember(context) { readWs16BbShelf(context) }
    if (image == null) {
        Ws16EStorybookShelf(modifier)
        return
    }
    BoxWithConstraints(modifier.fillMaxWidth().testTag("ws16bb-matched-cabinet-shelf")) {
        // 586x125 original artwork. Match source aspect; downscale at phone width.
        val paintedHeight = (maxWidth * (125f / 586f)).coerceIn(42.dp, 72.dp)
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth().height(paintedHeight)
                .offset(y = paintedHeight - 42.dp)
        )
    }
}
