package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * WS16AK: source-cropped illustrated EMPTY cabinet background, QA only.
 * Do not call until six real PNG bands are bundled under debug assets/ws16ak.
 * Individual book sprites, tap slots, headings and counts remain live overlays.
 * The current WS16AI BookcaseWoodParts stays the fallback until art import.
 */
private data class BookcaseBand(val name:String, val masterPx:Float, val fixed:Boolean)

private val ws16akBands = listOf(
    BookcaseBand("crown",246f,true),
    BookcaseBand("upper_bay",450f,false),
    BookcaseBand("middle_shelf",128f,true),
    BookcaseBand("lower_bay",364f,false),
    BookcaseBand("lower_shelf",112f,true),
    BookcaseBand("base",236f,true)
)

@Composable
internal fun WS16AKEmptyBookcaseBackground(modifier:Modifier = Modifier) {
    val context = LocalContext.current
    val images = remember(context) {
        ws16akBands.associate { band ->
            band.name to runCatching {
                context.assets.open("ws16ak/${band.name}.png").use {
                    BitmapFactory.decodeStream(it)
                }
            }.getOrNull()
        }
    }
    BoxWithConstraints(modifier.testTag("ws16ak-empty-bookcase-background")) {
        val scale = maxWidth.value / 1024f
        val fixed = ws16akBands.filter { it.fixed }.sumOf {
            (it.masterPx * scale).toDouble()
        }.toFloat()
        val free = (maxHeight.value - fixed).coerceAtLeast(80f)
        val upper = free * (450f / (450f + 364f))
        Column(Modifier.fillMaxSize()) {
            for (band in ws16akBands) {
                val h = when (band.name) {
                    "upper_bay" -> upper
                    "lower_bay" -> free - upper
                    else -> band.masterPx * scale
                }
                images[band.name]?.let { bitmap ->
                    Image(bitmap.asImageBitmap(), contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(h.dp),
                        contentScale = ContentScale.FillBounds)
                }
            }
        }
    }
}
