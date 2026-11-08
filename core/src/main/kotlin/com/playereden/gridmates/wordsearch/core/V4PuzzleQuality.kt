package com.playereden.gridmates.wordsearch.core

/**
 * PREVIEW-ONLY layout measurements. Deliberately independent of the V4 placement
 * RNG/seed so it cannot silently re-write historical preview grid goldens.
 *
 * Measures ACTUAL target paths, never filler letters or fake "occupied" regions.
 * Numbers are QA evidence, NOT proof of subjective fun or final release gates.
 */
internal object V4PuzzleQuality {
    data class Report(
        val targetCount: Int,
        val targetCentersByQuadrant: List<Int>,
        val centerTilesOccupied: Int,
        val centerTilePeak: Int,
        val regionsWithTargetCells: Int,
        val totalRegions: Int,
        val minDistinctTargetCellsInRegion: Int,
        val maxDistinctTargetCellsInRegion: Int,
        val uniqueTargetCells: Int,
        val directionCounts: Map<String, Int>,
        val lengthBands: List<Int>,
    ) {
        val uniqueDirections: Int get() = directionCounts.size
        val maxDirectionShare: Double get() =
            (directionCounts.values.maxOrNull() ?: 0).toDouble() / targetCount
        val minQuadrantCenters: Int get() = targetCentersByQuadrant.minOrNull() ?: 0
        val maxQuadrantCenters: Int get() = targetCentersByQuadrant.maxOrNull() ?: 0
        val uniqueCellCoverage: Double get() =
            uniqueTargetCells.toDouble() / (if (totalRegions == 16) 1600 else 2500)
    }

    private fun regionOf(coord: Int, size: Int, divisions: Int): Int =
        (coord * divisions / size).coerceIn(0, divisions - 1)

    fun measure(puzzle: WordSearchPuzzle): Report {
        val size = puzzle.size
        require(size == 40 || size == 50) { "Only V4 research grids are measured" }
        val targets = puzzle.targets
        require(targets.size == if (size == 40) 70 else 105)
        val centers = IntArray(16)
        val quadrants = IntArray(4)
        val regionSide = size / 10 // 40->4x4, 50->5x5 (10-cell regions)
        val regionCells = Array(regionSide * regionSide) { mutableSetOf<GridPoint>() }
        val directions = linkedMapOf<String, Int>()
        val lengths = IntArray(3)
        for (target in targets) {
            val cells = target.positions
            require(cells.size == target.word.length && cells.size >= 5)
            val start = cells.first()
            val end = cells.last()
            val dr = (end.row - start.row).compareTo(0)
            val dc = (end.col - start.col).compareTo(0)
            require(dr != 0 || dc != 0)
            val direction = "$dr,$dc"
            directions[direction] = (directions[direction] ?: 0) + 1
            val midRow = (start.row + end.row) / 2
            val midCol = (start.col + end.col) / 2
            require(midRow in 0 until size && midCol in 0 until size)
            centers[regionOf(midRow, size, 4) * 4 + regionOf(midCol, size, 4)]++
            quadrants[regionOf(midRow, size, 2) * 2 + regionOf(midCol, size, 2)]++
            lengths[when {
                target.word.length <= 7 -> 0
                target.word.length <= 10 -> 1
                else -> 2
            }]++
            for (cell in cells) {
                require(cell.row in 0 until size && cell.col in 0 until size)
                val region = (cell.row / 10) * regionSide + cell.col / 10
                regionCells[region] += cell
            }
        }
        require(directions.values.sum() == targets.size)
        return Report(
            targetCount = targets.size,
            targetCentersByQuadrant = quadrants.toList(),
            centerTilesOccupied = centers.count { it > 0 },
            centerTilePeak = centers.max(),
            regionsWithTargetCells = regionCells.count { it.isNotEmpty() },
            totalRegions = regionCells.size,
            minDistinctTargetCellsInRegion = regionCells.minOf { it.size },
            maxDistinctTargetCellsInRegion = regionCells.maxOf { it.size },
            uniqueTargetCells = regionCells.sumOf { it.size },
            directionCounts = directions.toMap(),
            lengthBands = lengths.toList()
        )
    }

    /** Compare two different seeds WITHOUT falsely claiming all words can differ. */
    fun compare(first: WordSearchPuzzle, second: WordSearchPuzzle): Pair<Int, Int> {
        require(first.id != second.id && first.size == second.size)
        val firstWords = first.targets.associateBy { it.word }
        val secondWords = second.targets.associateBy { it.word }
        val shared = firstWords.keys.intersect(secondWords.keys)
        val sameExactPaths = shared.count { word ->
            firstWords.getValue(word).positions == secondWords.getValue(word).positions
        }
        return shared.size to sameExactPaths
    }
}
