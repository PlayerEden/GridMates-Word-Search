package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS16AI: carved walnut crown, pilasters, recessed planks, base — each a
 * separate, source-derived illustration. No books, titles or hit targets
 * are baked into any wood sprite. Debug-only asset path, runtime safe fallback.
 */
private fun loadBookcaseAsset(context: Context, path: String): Bitmap? =
    runCatching {
        context.assets.open(path).use { BitmapFactory.decodeStream(it) }
    }.getOrNull()

@Composable
private fun BookcasePart(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val image = remember(context, name) {
        loadBookcaseAsset(context, "ws16ai/$name.png")
    }
    if (image != null) {
        Image(image.asImageBitmap(), contentDescription = null,
            contentScale = ContentScale.FillBounds, modifier = modifier)
    }
}

@Composable
internal fun BookcaseWoodParts(modifier: Modifier, fieldNotesArt: Boolean) {
    Box(modifier.testTag("ws16ai-painted-bookcase")) {
        // Release-safe fallback; the actual illustrated backing covers this in QA.
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFF75452B))
        }
        if (fieldNotesArt) {
            BookcasePart("backboard", Modifier.fillMaxSize())
            BookcasePart("crown", Modifier.align(Alignment.TopCenter)
                .fillMaxWidth().height(62.dp))
            BookcasePart("pillar-left", Modifier.align(Alignment.CenterStart)
                .width(31.dp).fillMaxHeight())
            BookcasePart("pillar-right", Modifier.align(Alignment.CenterEnd)
                .width(31.dp).fillMaxHeight())
            BookcasePart("base", Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth().height(38.dp))
        } else {
            Ws16GBookcaseBackboard(Modifier.fillMaxSize())
        }
    }
}

/** Separate spine artwork, not a resized/cropped full cover. */
@Composable
private fun BookcaseSpineImage(name: String, modifier: Modifier, profile: String? = null): Boolean {
    val context = LocalContext.current
    // WS16AO deliberately does not change the previous WS16AI fallback view.
    val bitmap = remember(context, name, profile) {
        val candidate = if (profile == "tall" || profile == "standard") {
            loadBookcaseAsset(context, "ws16ao/spines/$profile/$name.png")
        } else null
        candidate ?: loadBookcaseAsset(context, "ws16ai/spines/$name.png")
    }
    if (bitmap != null) {
        Image(bitmap.asImageBitmap(), contentDescription = null,
            contentScale = if (profile == null) ContentScale.FillBounds else ContentScale.Fit,
            modifier = modifier)
    }
    return bitmap != null
}

@Composable
internal fun BookcaseSpineArt(
    theme: WordTheme, fieldNotesArt: Boolean, modifier: Modifier = Modifier,
    profile: String? = null
) {
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    if (!fieldNotesArt || !BookcaseSpineImage(slug, modifier, profile)) {
        BookArt(theme, BookArtKind.SPINE, modifier, fieldNotes = fieldNotesArt)
    }
}

@Composable
internal fun BookcaseFutureSpineArt(
    style: String, modifier: Modifier = Modifier, profile: String? = null
) {
    // Explicitly decorative: no book ID, progress, click handler or pages.
    if (!BookcaseSpineImage(style, modifier, profile)) {
        BookFactoryBlankSpine(style, modifier)
    }
}
