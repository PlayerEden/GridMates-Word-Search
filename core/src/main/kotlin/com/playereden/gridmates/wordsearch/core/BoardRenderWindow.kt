package com.playereden.gridmates.wordsearch.core

/**
 * A draw-only selection of cells on one continuous board. This has no effect
 * on finding words, touch mapping, hint data or persisted puzzle state.
 * One-cell overscan keeps antialiased ink and marked crossing words intact.
 */
data class BoardRenderWindow(
    val rows: IntRange,
    val columns: IntRange,
    val totalCells: Int
) {
    init {
        require(totalCells > 0)
        require(!rows.isEmpty() && !columns.isEmpty())
        require(rows.first >= 0 && rows.last < totalCells)
        require(columns.first >= 0 && columns.last < totalCells)
    }

    val glyphBudget: Int get() = rows.count() * columns.count()

    /** A placed word can span a region boundary: include any crossing stroke. */
    fun mayIntersect(path: List<GridPoint>): Boolean {
        if (path.isEmpty()) return false
        // All valid word paths are straight, but bounding-box rejection
        // remains conservative for any future bent/annotated paths.
        var minRow = Int.MAX_VALUE
        var maxRow = Int.MIN_VALUE
        var minColumn = Int.MAX_VALUE
        var maxColumn = Int.MIN_VALUE
        path.forEach { p ->
            minRow = minOf(minRow, p.row)
            maxRow = maxOf(maxRow, p.row)
            minColumn = minOf(minColumn, p.col)
            maxColumn = maxOf(maxColumn, p.col)
        }
        return minRow <= rows.last && maxRow >= rows.first &&
            minColumn <= columns.last && maxColumn >= columns.first
    }

    companion object {
        fun all(cells: Int): BoardRenderWindow =
            BoardRenderWindow(0 until cells, 0 until cells, cells)

        fun around(
            viewport: LargeBoardViewport,
            viewportSidePx: Float,
            cells: Int,
            overscanCells: Int = 1
        ): BoardRenderWindow {
            require(cells > 0 && overscanCells in 0..4)
            val rows = viewport.visibleRows(viewportSidePx, cells)
            val cols = viewport.visibleColumns(viewportSidePx, cells)
            return BoardRenderWindow(
                (rows.first - overscanCells).coerceAtLeast(0)..
                    (rows.last + overscanCells).coerceAtMost(cells - 1),
                (cols.first - overscanCells).coerceAtLeast(0)..
                    (cols.last + overscanCells).coerceAtMost(cells - 1),
                cells
            )
        }
    }
}
