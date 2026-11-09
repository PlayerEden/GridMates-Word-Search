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
 * WS-12I / WS-14B2 — presentation only: locally bundled, verified illustrations.
 *
 * Draws small, pre-rasterized local PNGs from debug assets, never WebViews in
 * scrolling book lists. Prefer imported WS-14 originals when present;
 * missing WS-14 art falls back to the existing WS-12 source-backed QA studies. All six symbols have explicit stable paths. The whole
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

internal fun bookArtPath(
    theme: WordTheme, kind: BookArtKind, fieldNotes: Boolean = false
): String? {
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    val dir = if (fieldNotes) "ws12/raster_field_notes/" else "ws12/raster/"
    return dir + kind.directory + "/" + slug + ".png"
}

/**
 * WS-14B: exact paths accepted only from the approved-direction art importer.
 * Never fetch remote art at runtime, never replace any of the 12 portraits.
 */
internal fun ws14QaCoverPath(theme: WordTheme, kind: BookArtKind): String? {
    if (kind != BookArtKind.COVER) return null
    val themeSlug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws14/books/$themeSlug.png"
}

/** The A/B switch always keeps Study A genuine, even after importing WS-14. */
internal fun preferredWs14QaCoverPath(
    theme: WordTheme, kind: BookArtKind, fieldNotes: Boolean
): String? = if (fieldNotes) ws14QaCoverPath(theme, kind) else null

@Composable
internal fun BookArt(
    theme: WordTheme,
    kind: BookArtKind,
    modifier: Modifier = Modifier,
    fieldNotes: Boolean = false
) {
    val context = LocalContext.current
    val existingStudyPath = bookArtPath(theme, kind, fieldNotes) ?: return
    val preferredNewPath = preferredWs14QaCoverPath(theme, kind, fieldNotes)
    val bitmap = remember(context, existingStudyPath, preferredNewPath) {
        listOfNotNull(preferredNewPath, existingStudyPath)
            .firstNotNullOfOrNull { path ->
                runCatching {
                    context.assets.open(path).use { BitmapFactory.decodeStream(it) }
                }.getOrNull()
            }
    } ?: return

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null, // decorative; accessible status is native text
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}
