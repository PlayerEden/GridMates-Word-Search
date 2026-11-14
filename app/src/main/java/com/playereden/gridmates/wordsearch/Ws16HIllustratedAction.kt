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
import androidx.compose.ui.unit.Dp

/**
 * WS-16H2: Six original illustrated actions, generated from independent SVGs.
 * The debug source set packages them under assets/ws16h/actions. The release
 * APK never has these unreviewed sprites, so consumers keep the prior native
 * vector/Canvas art without changing any action's accessibility or click.
 */
@Composable
internal fun Ws16HIllustratedAction(
    name: String,
    iconSize: Dp,
    modifier: Modifier = Modifier
): Boolean {
    val context = LocalContext.current
    val bitmap = remember(context, name) {
        runCatching {
            context.assets.open("ws16h/actions/" + name + ".png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    }
    if (bitmap == null) return false
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(iconSize))
    return true
}
