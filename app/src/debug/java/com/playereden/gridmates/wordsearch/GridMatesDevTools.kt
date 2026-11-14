package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** QA ONLY. No overlays, tracking or preferences are included in release source. */
internal object GridMatesDevTools {
    const val available = true
    private const val PREFS = "gm_visual_guides_debug"
    private val bounds = mutableStateMapOf<String, Rect>()
    private var pressed by mutableStateOf<Offset?>(null)

    fun load(context: Context): DevOverlayFlags {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return DevOverlayFlags(
            enabled = p.getBoolean("enabled", false),
            grid = p.getBoolean("grid", true),
            bounds = p.getBoolean("bounds", true),
            safe = p.getBoolean("safe", true),
            touch = p.getBoolean("touch", false)
        )
    }
    fun save(context: Context, flags: DevOverlayFlags) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean("enabled", flags.enabled)
            .putBoolean("grid", flags.grid)
            .putBoolean("bounds", flags.bounds)
            .putBoolean("safe", flags.safe)
            .putBoolean("touch", flags.touch)
            .apply()
        if (!flags.enabled || !flags.touch) pressed = null
    }

    @Composable fun Controls(flags: DevOverlayFlags, onChange: (DevOverlayFlags) -> Unit) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        PaperPanel(Modifier.fillMaxWidth().testTag("gm-dev-tools")) {
            Text("Developer Options · QA only", color = Forest,
                style = MaterialTheme.typography.titleMedium)
            Text("Visual diagnostics only. No effect on puzzles or save files.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("gm-dev-panel-toggle")) {
                Text(if (expanded) "Hide developer guides" else "Show developer guides")
            }
            if (expanded) {
                Toggle("Enable overlay", flags.enabled, "gm-dev-enable") {
                    onChange(flags.copy(enabled = it))
                }
                Toggle("8dp grid", flags.grid, "gm-dev-grid") {
                    onChange(flags.copy(grid = it))
                }
                Toggle("Real element bounds", flags.bounds, "gm-dev-bounds") {
                    onChange(flags.copy(bounds = it))
                }
                Toggle("Safe-area outlines", flags.safe, "gm-dev-safe") {
                    onChange(flags.copy(safe = it))
                }
                Toggle("Touch markers", flags.touch, "gm-dev-touch") {
                    onChange(flags.copy(touch = it))
                }
                Text("Use Back to inspect Home. Teal = grid, orange = real " +
                    "element bounds, red = safe drawing, purple = safe gestures. " +
                    "Guides never intercept taps or swipes.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    @Composable private fun Toggle(
        label: String, checked: Boolean, tag: String, onChange: (Boolean) -> Unit
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(tag),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, Modifier.weight(1f).padding(top = 12.dp),
                color = Forest, style = MaterialTheme.typography.bodyMedium)
            Checkbox(checked = checked, onCheckedChange = onChange)
        }
    }

    @Composable fun Overlay(flags: DevOverlayFlags, home: Boolean, modifier: Modifier = Modifier) {
        if (!flags.enabled) return
        val density = LocalDensity.current
        val direction = LocalLayoutDirection.current
        val drawInsets = WindowInsets.safeDrawing
        val gestureInsets = WindowInsets.safeGestures
        val spaces = with(density) { 8.dp.toPx() }
        val red = floatArrayOf(
            drawInsets.getLeft(density, direction).toFloat(),
            drawInsets.getTop(density).toFloat(),
            drawInsets.getRight(density, direction).toFloat(),
            drawInsets.getBottom(density).toFloat()
        )
        val purple = floatArrayOf(
            gestureInsets.getLeft(density, direction).toFloat(),
            gestureInsets.getTop(density).toFloat(),
            gestureInsets.getRight(density, direction).toFloat(),
            gestureInsets.getBottom(density).toFloat()
        )
        val items = if (home && flags.bounds) bounds.toMap() else emptyMap()
        val touch = if (flags.touch) pressed else null
        Canvas(modifier.fillMaxSize().clearAndSetSemantics {}
            .testTag("gm-dev-overlay")) {
            val w = size.width
            val h = size.height
            if (flags.grid) {
                var x = spaces
                while (x < w) {
                    drawLine(Color(0x55428EA0), Offset(x, 0f), Offset(x, h), 0.75f)
                    x += spaces
                }
                var y = spaces
                while (y < h) {
                    drawLine(Color(0x55428EA0), Offset(0f, y), Offset(w, y), 0.75f)
                    y += spaces
                }
            }
            if (flags.safe) {
                fun frame(insets: FloatArray, color: Color) {
                    val l = insets[0].coerceIn(0f, w)
                    val t = insets[1].coerceIn(0f, h)
                    val r = (w - insets[2]).coerceIn(l, w)
                    val b = (h - insets[3]).coerceIn(t, h)
                    drawRect(color, Offset(l, t), Size(r - l, b - t),
                        style = Stroke(width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))))
                }
                frame(red, Color(0xFFCC4A4A))
                frame(purple, Color(0xFFAA3EB3))
            }
            if (flags.bounds) {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(173, 92, 20)
                    textSize = with(density) { 10.sp.toPx() }
                }
                items.forEach { (name, rect) ->
                    drawRect(Color(0xFFE2A04E), rect.topLeft, rect.size,
                        style = Stroke(width = 1.6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))))
                    if (rect.top > 15f) drawIntoCanvas {
                        it.nativeCanvas.drawText(name, rect.left.coerceAtLeast(2f),
                            rect.top - 2f, paint)
                    }
                }
            }
            touch?.let {
                drawCircle(Color(0x77E6A03D), radius = 19f, center = it)
                drawCircle(Color(0xFFCA751A), radius = 19f, center = it,
                    style = Stroke(width = 2f))
            }
        }
    }

    fun track(name: String, rect: Rect) {
        if (bounds[name] != rect) bounds[name] = rect
    }
    fun touch(position: Offset?) { if (pressed != position) pressed = position }
}
internal fun Modifier.qaTrackBounds(name: String): Modifier =
    onGloballyPositioned { GridMatesDevTools.track(name, it.boundsInRoot()) }

/** Parent Initial-pass observer: never consumes clicks, swipes or Back. */
internal fun Modifier.qaObserveTouches(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            GridMatesDevTools.touch(event.changes.firstOrNull { it.pressed }?.position)
        }
    }
}
