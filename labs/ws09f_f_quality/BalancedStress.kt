package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import kotlin.math.max

/**
 * Independent A/B evidence on a separately versioned, quality-scored V4 lab.
 * Earlier V4 goldens are NOT replaced. No Android app uses these previews.
 */
fun main(args: Array<String>) {
    val fixture = args.getOrNull(0)
        ?: "core/src/test/resources/ws09f_e_v4_provisional_tiers.txt"
    val count = args.getOrNull(1)?.toInt() ?: 10
    require(count in 1..100)
    val tiers = File(fixture).readLines().filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { row ->
            val parts = row.split('|', limit=3)
            require(parts.size==3)
            (parts[0] to parts[1]) to parts[2].split(' ').filter { it.isNotBlank() }
        }
    val failures = ArrayList<String>()
    var success = 0
    var worstAttempts = 0
    var totalClaims = 0
    var sameGrids = 0
    val started = System.nanoTime()
    for (theme in WordTheme.entries) for (size in listOf(40, 50))
        for (mode in PuzzleChallenge.entries) {
            var hereSuccess = 0
            var hereWorst = 0
            var previous: WordSearchPuzzle? = null
            var minimumCells = Int.MAX_VALUE
            var minCenterTiles = 16
            var minQuadrant = Int.MAX_VALUE
            var minDirections = 8
            for (seed in 0 until count) {
                try {
                    val gentle = tiers.getValue(theme.name to "TRIAL_RELAXED")
                    val advanced = tiers.getValue(theme.name to "TRIAL_ADVANCED")
                    val selection = V4PreviewTierSelection.select(
                        theme, size, mode, seed.toLong(), gentle, advanced)
                    val result = V4BalancedPlacement.generate(
                        theme, size, mode, seed.toLong(), selection.words)
                    val puzzle = result.puzzle
                    val report = V4PuzzleQuality.measure(puzzle)
                    check(V4PuzzleQuality.issues(report, mode).isEmpty())
                    check(result.quality == report)
                    check(puzzle.id.startsWith("wordsearch:v4:BALANCED-PREVIEW:"))
                    check(puzzle.id != V4PreviewPlacement.id(
                        theme.name, size, V4PreviewPlacement.Mode.valueOf(mode.name),
                        seed.toLong(), selection.words))
                    check(result.attempts in 1..24)
                    worstAttempts = max(worstAttempts, result.attempts)
                    hereWorst = max(hereWorst, result.attempts)
                    minimumCells = minOf(minimumCells, report.minDistinctTargetCellsInRegion)
                    minCenterTiles = minOf(minCenterTiles, report.centerTilesOccupied)
                    minQuadrant = minOf(minQuadrant, report.minQuadrantCenters)
                    minDirections = minOf(minDirections, report.uniqueDirections)
                    // Verify every word is actually drawable, findable and unique
                    // in all 8 directions, including modes using fewer directions.
                    var found = emptySet<String>()
                    for (target in puzzle.targets) {
                        check(V4PreviewPlacement.occurrences(puzzle.rows, target.word) == 1)
                        check(WordSearchRules.claim(puzzle, found,
                            target.positions.first(), target.positions.last()
                        ).status == ClaimStatus.FOUND)
                        found += target.id
                        totalClaims++
                    }
                    check(WordSearchRules.complete(puzzle, found))
                    val prior = previous
                    if (prior != null && prior.rows == puzzle.rows) sameGrids++
                    previous = puzzle
                    // Full deterministic reconstruction on representative seeds
                    if (seed in setOf(0, 1)) {
                        check(result == V4BalancedPlacement.generate(
                            theme, size, mode, seed.toLong(), selection.words))
                    }
                    hereSuccess++
                    success++
                } catch (e: Exception) {
                    failures += "${theme.name} ${size}x$size ${mode.name} seed=$seed " +
                        "${e.javaClass.simpleName}: ${e.message}"
                }
            }
            println(String.format(Locale.US,
                "%s %dx%d %s: quality_pass=%d/%d worst_attempts=%d min_10x10_target_cells=%d min_center_tiles=%d/16 min_quadrant_centers=%d min_directions=%d",
                theme.name, size, size, mode.name, hereSuccess, count, hereWorst,
                if (minimumCells==Int.MAX_VALUE) -1 else minimumCells,
                minCenterTiles, if(minQuadrant==Int.MAX_VALUE) -1 else minQuadrant,
                minDirections))
        }
    val total = count * 18
    val elapsed = (System.nanoTime()-started)/1000000.0
    println(String.format(Locale.US,
        "RESULT: quality_pass=%d/%d failures=%d valid_claims=%d worst_attempts=%d same_adjacent_grids=%d elapsed_runner_ms=%.1f",
        success, total, failures.size, totalClaims, worstAttempts, sameGrids, elapsed))
    failures.take(40).forEach { println("FAILED QUALITY SEED: $it") }
    check(failures.isEmpty()) { "${failures.size}/$total quality-gated giant layouts failed" }
    check(sameGrids == 0)
    println("PASS: quality-gated balanced V4 experimental grid generation. No release claimed.")
}
