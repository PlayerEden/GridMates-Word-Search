package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.playereden.gridmates.wordsearch.core.BoardFontCollection

/** Every font referenced here is packaged from an exact, byte-pinned OFL source. */
internal object BoardFontRegistry {
    val resourceById = mapOf(
        "atkinson" to R.font.gm_atkinson,
        "plex" to R.font.gm_plex_mono,
        "dmmono" to R.font.gm_dm_mono,
        "patrick" to R.font.gm_patrick_hand
    )

    fun resolve(context: Context, id: String): Typeface {
        val resource = resourceById[id] ?: R.font.gm_atkinson
        return ResourcesCompat.getFont(context, resource) ?: Typeface.DEFAULT
    }
}

@Composable
internal fun rememberBoardGlyphPainter(id: String): BoardGlyphPainter {
    val ctx = LocalContext.current
    val supported = id.takeIf { BoardFontRegistry.resourceById.containsKey(it) }
        ?: BoardFontCollection.DEFAULT_ID
    return remember(ctx, supported) { BoardGlyphPainter(BoardFontRegistry.resolve(ctx, supported)) }
}

/**
 * Font-neutral optical centering: measure every actual glyph's ink rectangle,
 * then center it in its own equal-size grid cell. The same code draws real
 * puzzles AND Collection previews. Typeface, cell bounds, borders, markers
 * and touch coordinates never depend on Android's device font settings.
 *
 * The chosen text size is constrained by the WIDEST and TALLEST A-Z sample
 * for this actual typeface; no glyph can intrude into a neighbor cell.
 */
internal class BoardGlyphPainter(private val typeface: Typeface) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        this.typeface = this@BoardGlyphPainter.typeface
        style = Paint.Style.FILL
        isDither = true
    }
    private val bounds = Rect()
    private var cachedCellWidth = -1f
    private var cachedCellHeight = -1f
    private var cachedFontSize = 0f

    private fun fitFontSize(cellWidth: Float, cellHeight: Float): Float {
        if (cellWidth == cachedCellWidth && cellHeight == cachedCellHeight)
            return cachedFontSize
        paint.textSize = 100f
        var maxInkW = 1f
        var maxInkH = 1f
        for (char in "ABCDEFGHIJKLMNOPQRSTUVWXYZ") {
            val t = char.toString()
            paint.getTextBounds(t, 0, 1, bounds)
            maxInkW = maxOf(maxInkW, bounds.width().toFloat())
            maxInkH = maxOf(maxInkH, bounds.height().toFloat())
        }
        val px = minOf(
            100f * (cellWidth * 0.69f) / maxInkW,
            100f * (cellHeight * 0.68f) / maxInkH
        ).coerceAtLeast(1f)
        cachedCellWidth = cellWidth
        cachedCellHeight = cellHeight
        cachedFontSize = px
        return px
    }

    /**
     * Exact tight-ink bounds inside one cell, useful for real Android tests.
     * Unlike advances/line box bounds, this measures the actual letter shape.
     */
    fun inkBoundsInCell(letter: Char, cellWidth: Float, cellHeight: Float): RectF {
        require(cellWidth > 0f && cellHeight > 0f)
        paint.textSize = fitFontSize(cellWidth, cellHeight)
        val glyph = letter.toString()
        paint.getTextBounds(glyph, 0, glyph.length, bounds)
        val x = cellWidth / 2f - (bounds.left + bounds.right) / 2f
        val y = cellHeight / 2f - (bounds.top + bounds.bottom) / 2f
        return RectF(bounds.left + x, bounds.top + y,
            bounds.right + x, bounds.bottom + y)
    }

    fun drawLetters(scope: DrawScope, rows: List<String>, ink: Color) {
        if (rows.isEmpty()) return
        val width = scope.size.width / rows[0].length
        val height = scope.size.height / rows.size
        if (width <= 0f || height <= 0f) return
        paint.color = android.graphics.Color.argb(
            (ink.alpha * 255f).toInt(),
            (ink.red * 255f).toInt(),
            (ink.green * 255f).toInt(),
            (ink.blue * 255f).toInt()
        )
        paint.textSize = fitFontSize(width, height)
        scope.drawIntoCanvas { bridge ->
            val canvas = bridge.nativeCanvas
            rows.forEachIndexed { row, letters ->
                letters.forEachIndexed { col, letter ->
                    val glyph = letter.toString()
                    paint.getTextBounds(glyph, 0, glyph.length, bounds)
                    val x = (col + 0.5f) * width -
                        (bounds.left + bounds.right) / 2f
                    val baseline = (row + 0.5f) * height -
                        (bounds.top + bounds.bottom) / 2f
                    canvas.drawText(glyph, x, baseline, paint)
                }
            }
        }
    }
}

@Composable
internal fun BoardFontPreview(fontId: String, modifier: Modifier = Modifier) {
    val painter = rememberBoardGlyphPainter(fontId)
    Canvas(modifier.fillMaxWidth().height(55.dp)
        .background(Paper, RoundedCornerShape(8.dp))) {
        val cellW = size.width / 5
        val cellH = size.height
        for (i in 1 until 5) {
            drawLine(Color(0xFFD8DFD2),
                Offset(i * cellW, 0f), Offset(i * cellW, size.height),
                strokeWidth = 1.dp.toPx())
        }
        painter.drawLetters(this, listOf("MWIOQ"), Ink)
    }
}
