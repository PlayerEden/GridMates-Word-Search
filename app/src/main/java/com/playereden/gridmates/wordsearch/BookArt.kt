package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS-12I — presentation only: the ACTUAL same-source WS-12F illustrations.
 *
 * Draws small, pre-rasterized local PNGs from debug assets, never WebViews in
 * scrolling book lists. All six symbols have explicit stable paths. The whole
 * feature is gated by showPrototypeArt in the book flow; the release package
 * includes no ws12/raster assets and remains native text-only.
 *
 * Decorative only: the book title, page status, chapter, completion and actions
 * are always native Compose text/controls, including when assets are missing.
 */
internal enum class BookArtKind(val directory: String) {
    COVER("books"),
    SPINE("spines"),
    EMBLEM("emblems"),
    CHAPTER("chapters"),
    BOOKMARK("bookmarks"),
    COMPLETION("seals")
}

internal fun bookArtPath(theme: WordTheme, kind: BookArtKind): String? {
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws12/raster/" + kind.directory + "/" + slug + ".png"
}

@Composable
internal fun BookArt(
    theme: WordTheme,
    kind: BookArtKind,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val path = bookArtPath(theme, kind) ?: return
    val bitmap = remember(context, path) {
        runCatching {
            context.assets.open(path).use { BitmapFactory.decodeStream(it) }
        }.getOrNull()
    } ?: return

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null, // decorative; accessible status is native text
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}
