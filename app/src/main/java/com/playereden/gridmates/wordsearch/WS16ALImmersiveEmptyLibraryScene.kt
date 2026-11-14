package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** WS16AL: unwarped carved bands + carefully stretchable plain wood in the two bays. */
private data class ALBand(
    val name: String, val height: Int, val capTop: Int = 0, val capBottom: Int = 0
)
private val alBands = listOf(
    ALBand("crown",246), ALBand("upper_bay",450,200,100),
    ALBand("middle_shelf",128), ALBand("lower_bay",364,210,96),
    ALBand("lower_shelf",112), ALBand("base",236)
)
private data class ALSlices(val top: Bitmap, val middle: Bitmap, val bottom: Bitmap)

@Composable
private fun ALExpandableBay(bitmap: Bitmap?, band: ALBand, height: Float, scale: Float) {
    if (bitmap == null) return
    val slices = remember(bitmap,band) {
        ALSlices(
            Bitmap.createBitmap(bitmap,0,0,bitmap.width,band.capTop),
            Bitmap.createBitmap(bitmap,0,band.capTop,bitmap.width,
                bitmap.height-band.capTop-band.capBottom),
            Bitmap.createBitmap(bitmap,0,bitmap.height-band.capBottom,
                bitmap.width,band.capBottom)
        )
    }
    val capScale = minOf(scale,height/(band.capTop+band.capBottom+1f))
    val a = band.capTop*capScale
    val b = band.capBottom*capScale
    val middle = (height-a-b).coerceAtLeast(0f)
    Column(Modifier.fillMaxWidth().height(height.dp)) {
        Image(slices.top.asImageBitmap(),null,Modifier.fillMaxWidth().height(a.dp),
            contentScale=ContentScale.FillBounds)
        Image(slices.middle.asImageBitmap(),null,Modifier.fillMaxWidth().height(middle.dp),
            contentScale=ContentScale.FillBounds)
        Image(slices.bottom.asImageBitmap(),null,Modifier.fillMaxWidth().height(b.dp),
            contentScale=ContentScale.FillBounds)
    }
}

@Composable
internal fun WS16ALResponsiveBookcaseBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val images = remember(context) {
        alBands.associate { band -> band.name to runCatching {
            context.assets.open("ws16ak/"+band.name+".png").use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull() }
    }
    BoxWithConstraints(modifier.testTag("ws16al-responsive-painted-bookcase")) {
        val scale = minOf(maxWidth.value/1024f,maxHeight.value/1150f)
        val fixed = 722f*scale
        val flexible = (maxHeight.value-fixed).coerceAtLeast(0f)
        val upper = flexible*(450f/814f)
        Column(Modifier.fillMaxSize()) {
            alBands.forEach { band ->
                val h = when(band.name) {
                    "upper_bay" -> upper
                    "lower_bay" -> flexible-upper
                    else -> band.height*scale
                }
                val bitmap = images[band.name]
                if(band.capTop>0) ALExpandableBay(bitmap,band,h,scale)
                else if(bitmap!=null)
                    Image(bitmap.asImageBitmap(),null,
                        Modifier.fillMaxWidth().height(h.dp),
                        contentScale=ContentScale.FillBounds)
            }
        }
    }
}

/**
 * WS16AL empty immersive QA scene. The books remain a separate native overlay
 * in the previous tested library; Show books returns there without save changes.
 */
@Composable
internal fun WS16ALImmersiveEmptyLibraryScene(
    bookCount: Int,
    onBack: () -> Unit,
    onShowBooks: () -> Unit,
    onPreviewOptions: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var qaMenuOpen by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().padding(horizontal=3.dp,vertical=2.dp)
        .testTag("ws16al-immersive-empty-library")) {
        Ws15BackHeader(
            title="Your Library", onBack=onBack, backLabel="Back to Play",
            onPreviewOptions={ qaMenuOpen = true },
            modifier=Modifier.padding(horizontal=6.dp)
        )
        Box(Modifier.fillMaxWidth().weight(1f)
            .padding(horizontal=2.dp).testTag("ws16al-cabinet-stage")) {
            WS16ALResponsiveBookcaseBackground(Modifier.fillMaxSize())

            Box(
                Modifier.align(Alignment.BottomStart)
                    .padding(start=42.dp,bottom=12.dp)
                    .testTag("ws16al-real-book-count")
            ) {
                WS16AMBookCountPlaque(bookCount)
            }
        }
    }
    if (qaMenuOpen) {
        AlertDialog(
            onDismissRequest = { qaMenuOpen = false },
            title = { Text("Library visual test") },
            text = { Text("Inspect the empty woodwork or return to the interactive books.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        qaMenuOpen = false
                        onShowBooks()
                    },
                    modifier = Modifier.testTag("ws16al-show-books")
                ) { Text("Show books") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        qaMenuOpen = false
                        onPreviewOptions?.invoke()
                    }
                ) { Text("More options") }
            }
        )
    }
}