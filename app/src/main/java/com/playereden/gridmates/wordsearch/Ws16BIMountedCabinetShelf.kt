package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS16BI: physically mounted high-res shelf, not the earlier thin ornate bar.
 * Reuses original backboard walnut grain through the deterministic asset
 * pipeline. The top is the book-contact plane (the entire visible raster
 * hangs down from that plane). No lettering, books or UI are painted in.
 *
 * Preserve the 125:586 aspect ratio to fit existing cabinet geometry, but
 * use a 1172x250 source for a cleaner bevel and hardware on phones.
 */
@Composable
internal fun Ws16BIMountedCabinetShelf(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val image = remember(context) {
        runCatching {
            context.assets.open("ws16bi/shelf.png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    }
    if (image == null) {
        Ws16BBMatchedCabinetShelf(modifier)
        return
    }
    BoxWithConstraints(modifier.fillMaxWidth().testTag("ws16bi-mounted-shelf")) {
        val paintedHeight = (maxWidth * (250f / 1172f))
            .coerceIn(42.dp, 72.dp)
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth().height(paintedHeight)
                .offset(y = paintedHeight - 42.dp)
        )
    }
}
