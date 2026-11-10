package com.playereden.gridmates.wordsearch

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Release stub: debug UI/tracking code never ships in a public build. */
internal object GridMatesDevTools {
    const val available = false
    fun load(context: Context): DevOverlayFlags = DevOverlayFlags()
    fun save(context: Context, flags: DevOverlayFlags) = Unit
    @Composable fun Controls(flags: DevOverlayFlags, onChange: (DevOverlayFlags) -> Unit) = Unit
    @Composable fun Overlay(flags: DevOverlayFlags, home: Boolean, modifier: Modifier = Modifier) = Unit
}
internal fun Modifier.qaTrackBounds(name: String): Modifier = this
internal fun Modifier.qaObserveTouches(): Modifier = this
