package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp

/**
 * Independent reusable GridMates family emblem and Word Search emblem.
 * Lettering stays live Compose text; it never becomes low-resolution bitmap
 * lettering or loses accessibility semantics. A missing debug art sprite
 * (e.g. release) simply uses the previous native Canvas icon.
 */
@Composable
internal fun Ws16MBrandEmblem(name: String, size: Dp, legacyTag: String): Boolean {
    val context = LocalContext.current
    val bitmap = remember(context, name) {
        runCatching {
            context.assets.open("ws16m/brand/" + name + ".png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    } ?: return false
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(size).clearAndSetSemantics { }.testTag(legacyTag))
    return true
}
