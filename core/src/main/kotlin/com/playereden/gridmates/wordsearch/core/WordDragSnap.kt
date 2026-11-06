package com.playereden.gridmates.wordsearch.core

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** One locked direction per drag; never create crooked paths. */
data class DragDirection(val dr: Int, val dc: Int) {
    init {
        require(dr in -1..1 && dc in -1..1 && (dr != 0 || dc != 0))
    }
}

data class SnappedDrag(val end: GridPoint, val direction: DragDirection?, val length: Int)

/**
 * Pure pixel-independent drag model. dx/dy are expressed in *cell widths*,
 * so a slow thumb and a fast stylus follow the same rules on every screen.
 */
object WordDragSnap {
    private const val LOCK_THRESHOLD = 0.55f
    private const val RELEASE_THRESHOLD = 0.27f
    private const val AXIS_RATIO = 2.41421356f // tan(67.5 degrees)

    fun update(
        start: GridPoint,
        dxCells: Float,
        dyCells: Float,
        boardSize: Int,
        previous: DragDirection? = null
    ): SnappedDrag {
        require(boardSize > 0)
        if (start.row !in 0 until boardSize || start.col !in 0 until boardSize)
            return SnappedDrag(start, null, 1)

        val magnitude = max(abs(dxCells), abs(dyCells))
        if (!magnitude.isFinite() || magnitude < RELEASE_THRESHOLD)
            return SnappedDrag(start, null, 1)

        val direction = previous ?: if (magnitude >= LOCK_THRESHOLD) {
            chooseDirection(dxCells, dyCells)
        } else null
        if (direction == null) return SnappedDrag(start, null, 1)

        val projection = when {
            direction.dr == 0 -> abs(dxCells)
            direction.dc == 0 -> abs(dyCells)
            else -> min(abs(dxCells), abs(dyCells))
        }
        val wanted = projection.roundToInt().coerceAtLeast(1)
        val rowLimit = when (direction.dr) {
            1 -> boardSize - 1 - start.row
            -1 -> start.row
            else -> boardSize
        }
        val colLimit = when (direction.dc) {
            1 -> boardSize - 1 - start.col
            -1 -> start.col
            else -> boardSize
        }
        val steps = min(wanted, min(rowLimit, colLimit)).coerceAtLeast(0)
        return SnappedDrag(
            GridPoint(start.row + direction.dr * steps, start.col + direction.dc * steps),
            direction,
            steps + 1
        )
    }

    private fun chooseDirection(dx: Float, dy: Float): DragDirection {
        val ax = abs(dx)
        val ay = abs(dy)
        return when {
            ax >= ay * AXIS_RATIO -> DragDirection(0, if (dx >= 0) 1 else -1)
            ay >= ax * AXIS_RATIO -> DragDirection(if (dy >= 0) 1 else -1, 0)
            else -> DragDirection(if (dy >= 0) 1 else -1, if (dx >= 0) 1 else -1)
        }
    }
}
