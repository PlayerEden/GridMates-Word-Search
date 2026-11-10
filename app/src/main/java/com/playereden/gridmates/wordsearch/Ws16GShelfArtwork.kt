package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS-16G debug-only layered bitmap shelf from eight independent, versioned SVGs.
 * Release APK never bundles candidate assets: safely falls back to Canvas.
 * Book covers, buttons, IDs, progress and interaction remain independently native.
 */
private fun readShelfPieces(context: Context): Map<String, Bitmap>? = runCatching {
    // Keep the legacy contact-shadow PNG in the QA source registry for
    // provenance, but don't load/draw its wide diffuse halo on Home.
    listOf("top", "front", "apron", "bracket-left", "bracket-right",
        "medallion").associateWith { name ->
        context.assets.open("ws16g/shelf/" + name + ".png").use {
            BitmapFactory.decodeStream(it) ?: error("WS16G bad PNG: " + name)
        }
    }
}.getOrNull()

@Composable
private fun ShelfPart(bitmap: Bitmap, modifier: Modifier) {
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null,
        contentScale = ContentScale.FillBounds, modifier = modifier)
}

@Composable
internal fun Ws16EStorybookShelf(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val art = remember(context) { readShelfPieces(context) }
    if (art == null) {
        androidx.compose.foundation.layout.Box(
            modifier.fillMaxWidth().height(42.dp)) {
            Ws16ECanvasShelfFallback()
            Ws16TShelfPatina(Modifier.align(Alignment.Center))
        }
        return
    }
    Box(modifier.fillMaxWidth().height(42.dp).qaTrackBounds("Wooden shelf").testTag("ws16e-storybook-shelf")) {
        ShelfPart(art.getValue("apron"),
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(24.dp))
        ShelfPart(art.getValue("front"),
            Modifier.align(Alignment.TopCenter).offset(y = 10.dp).fillMaxWidth().height(20.dp))
        ShelfPart(art.getValue("bracket-left"),
            Modifier.align(Alignment.BottomStart).width(26.dp).height(34.dp))
        ShelfPart(art.getValue("bracket-right"),
            Modifier.align(Alignment.BottomEnd).width(26.dp).height(34.dp))
        ShelfPart(art.getValue("top"),
            Modifier.align(Alignment.TopCenter).offset(y = 3.dp).fillMaxWidth().height(10.dp))
        // WS-16W: physically localized book weight, not a full-width
        // gray rectangle below the shelf or a repeated ambient drop shadow.
        ShelfBookContact(
            Modifier.align(Alignment.TopCenter).fillMaxWidth().height(12.dp)
        )
        // Irregular, transparent glaze sits above stretched grain panels,
        // below the crisp independent gold carving.
        Ws16TShelfPatina(Modifier.align(Alignment.Center))
        ShelfPart(art.getValue("medallion"),
            Modifier.align(Alignment.TopCenter).offset(y = 17.dp).width(84.dp).height(18.dp))
    }
}

@Composable
internal fun Ws16GBookcaseBackboard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val backing = remember(context) {
        runCatching {
            context.assets.open("ws16g/shelf/backboard.png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    }
    if (backing != null) {
        Image(bitmap = backing.asImageBitmap(), contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.testTag("ws16g-bookcase-backboard"))
    }
}

/**
 * A short, warm contact shadow confined to the walnut's TOP face.
 * Centered under the hero book and feathered away before reaching the shelf
 * brackets. It never paints anything below the shelf silhouette.
 */
@Composable
private fun ShelfBookContact(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val u = 1.dp.toPx()
        if (w <= 0f) return@Canvas
        val center = Offset(w * .5f, 6f * u)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x2E472514), Color(0x14472514),
                    Color.Transparent
                ),
                center = center,
                radius = w * .30f
            ),
            topLeft = Offset(w * .235f, 3.1f * u),
            size = Size(w * .53f, 5.6f * u)
        )
    }
}
