package com.playereden.gridmates.wordsearch.core

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * WS-09F-A: geometry for ONE continuous large word-search board.
 *
 * Unlike the frozen BoardViewportTransform (max 2.5x), this model allows a
 * readable window on dense future grids. Pan is always in screen pixels.
 * It does not alter puzzle IDs, word paths, saves, or rendering content.
 */
data class LargeBoardViewport(
    val zoom: Float = 1f,
    val panX: Float = 0f,
    val panY: Float = 0f
) {
    init { require(zoom.isFinite() && zoom in 1f..MAX_ZOOM) }

    companion object {
        const val MAX_ZOOM = 12f
        const val REGION_CELLS = 10
        const val TARGET_VISIBLE_CELLS = 10f

        fun regionCount(cells: Int): Int {
            require(cells > 0)
            return (cells + REGION_CELLS - 1) / REGION_CELLS
        }

        fun regionCenter(cells: Int, rowRegion: Int, colRegion: Int): GridPoint {
            val count = regionCount(cells)
            require(rowRegion in 0 until count && colRegion in 0 until count)
            fun mid(index: Int): Int {
                val start = index * REGION_CELLS
                val end = minOf(start + REGION_CELLS, cells)
                return start + (end - start - 1) / 2
            }
            return GridPoint(mid(rowRegion), mid(colRegion))
        }

        /** Tap on an overview square maps to a 10-cell named region. */
        fun tappedRegion(
            x: Float, y: Float, mapSidePx: Float, cells: Int
        ): Pair<Int, Int>? {
            if (!x.isFinite() || !y.isFinite() || !mapSidePx.isFinite() ||
                mapSidePx <= 0f || x < 0f || y < 0f ||
                x >= mapSidePx || y >= mapSidePx) return null
            val count = regionCount(cells)
            return ((y / mapSidePx * count).toInt().coerceIn(0, count - 1)) to
                ((x / mapSidePx * count).toInt().coerceIn(0, count - 1))
        }
    }

    fun clamped(side: Float): LargeBoardViewport {
        require(side.isFinite() && side > 0f)
        if (zoom == 1f) return copy(panX = 0f, panY = 0f)
        val minimum = -side * (zoom - 1f)
        return copy(
            panX = panX.coerceIn(minimum, 0f),
            panY = panY.coerceIn(minimum, 0f)
        )
    }

    fun panned(dx: Float, dy: Float, side: Float): LargeBoardViewport {
        require(dx.isFinite() && dy.isFinite())
        return copy(panX = panX + dx, panY = panY + dy).clamped(side)
    }

    fun zoomedAt(nextZoom: Float, side: Float, fx: Float, fy: Float): LargeBoardViewport {
        require(nextZoom.isFinite() && fx.isFinite() && fy.isFinite())
        val next = nextZoom.coerceIn(1f, MAX_ZOOM)
        val factor = next / zoom
        val focusX = fx.coerceIn(0f, side)
        val focusY = fy.coerceIn(0f, side)
        return LargeBoardViewport(next,
            focusX - (focusX - panX) * factor,
            focusY - (focusY - panY) * factor
        ).clamped(side)
    }

    fun zoomed(nextZoom: Float, side: Float): LargeBoardViewport =
        zoomedAt(nextZoom, side, side / 2f, side / 2f)

    /** Selecting a map region centers it, automatically zooming to ~10 cells. */
    fun jumpTo(point: GridPoint, cells: Int, side: Float): LargeBoardViewport {
        require(cells > 0 && side.isFinite() && side > 0f)
        require(point.row in 0 until cells && point.col in 0 until cells)
        val targetZoom = max(zoom,
            (cells / TARGET_VISIBLE_CELLS).coerceIn(1f, MAX_ZOOM))
        val px = side * (point.col + 0.5f) / cells
        val py = side * (point.row + 0.5f) / cells
        return LargeBoardViewport(targetZoom,
            side / 2f - px * targetZoom,
            side / 2f - py * targetZoom
        ).clamped(side)
    }

    fun jumpToRegion(
        rowRegion: Int, colRegion: Int, cells: Int, side: Float
    ): LargeBoardViewport = jumpTo(
        regionCenter(cells, rowRegion, colRegion), cells, side
    )

    fun cellAt(x: Float, y: Float, side: Float, cells: Int): GridPoint? {
        if (cells <= 0 || !side.isFinite() || side <= 0f ||
            !x.isFinite() || !y.isFinite() ||
            x < 0f || y < 0f || x >= side || y >= side) return null
        val boardX = (x - panX) / (zoom * side)
        val boardY = (y - panY) / (zoom * side)
        if (boardX !in 0f..<1f || boardY !in 0f..<1f) return null
        return GridPoint((boardY * cells).toInt().coerceIn(0, cells - 1),
            (boardX * cells).toInt().coerceIn(0, cells - 1))
    }

    /** Inclusive/exclusive index ranges for future viewport-only glyph drawing. */
    fun visibleRows(side: Float, cells: Int): IntRange =
        visibleRange(panY, side, cells)

    fun visibleColumns(side: Float, cells: Int): IntRange =
        visibleRange(panX, side, cells)

    private fun visibleRange(pan: Float, side: Float, cells: Int): IntRange {
        require(side.isFinite() && side > 0f && cells > 0)
        val start = floor((-pan / (zoom * side) * cells).toDouble())
            .toInt().coerceIn(0, cells - 1)
        val end = ceil(((side - pan) / (zoom * side) * cells).toDouble())
            .toInt().coerceIn(start + 1, cells)
        return start until end
    }

    /** Fractional full-board coordinates used by a miniature region overview. */
    fun overview(side: Float): OverviewRect {
        require(side.isFinite() && side > 0f)
        val extent = side * zoom
        val left = (-panX / extent).coerceIn(0f, 1f)
        val top = (-panY / extent).coerceIn(0f, 1f)
        return OverviewRect(left, top,
            (left + 1f / zoom).coerceAtMost(1f),
            (top + 1f / zoom).coerceAtMost(1f))
    }
}

data class OverviewRect(
    val left: Float, val top: Float, val right: Float, val bottom: Float
)
