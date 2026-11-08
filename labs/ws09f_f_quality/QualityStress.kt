package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Diagnoses whether physically correct giant-board puzzles are also varied.
 * Pure JVM test lab, review-pending source fixture, no APK or saved V4 IDs.
 * Prints BOTH the worst outcomes and provisional acceptance rates.
 */
fun main(args: Array<String>) {
    val path = args.getOrNull(0)
        ?: "core/src/test/resources/ws09f_e_v4_provisional_tiers.txt"
    val tiers = File(path).readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { row ->
            val parts = row.split('|', limit = 3)
            require(parts.size == 3)
            (parts[0] to parts[1]) to parts[2].split(' ').filter { it.isNotBlank() }
        }
    val seeds = (0L until 100L).toList()
    var puzzleCount = 0
    var allQualityPass = 0
    var uniqueBoards = 0
    var comparedPairs = 0
    var sameGridPairs = 0
    var sameWordPathPairs = 0
    val blockers = mutableMapOf<String, Int>()
    for (theme in WordTheme.entries) for (side in listOf(40, 50))
        for (challenge in PuzzleChallenge.entries) {
            val gentle = tiers.getValue(theme.name to "TRIAL_RELAXED")
            val advanced = tiers.getValue(theme.name to "TRIAL_ADVANCED")
            val reports = ArrayList<V4PuzzleQuality.Report>(100)
            val wordDifferences = mutableListOf<Int>()
            val sharedPaths = mutableListOf<Int>()
            var previous: WordSearchPuzzle? = null
            val allGrids = mutableSetOf<List<String>>()
            for (seed in seeds) {
                val outcome = V4PreviewTierSelection.previewOutcome(
                    theme, side, challenge, seed, gentle, advanced)
                val puzzle = outcome.puzzle
                val report = V4PuzzleQuality.measure(puzzle)
                reports += report
                puzzleCount++
                if (allGrids.add(puzzle.rows)) uniqueBoards++
                val prior = previous
                if (prior != null) {
                    val (sharedWords, samePaths) = V4PuzzleQuality.compare(prior, puzzle)
                    wordDifferences += (puzzle.targets.size - sharedWords)
                    sharedPaths += samePaths
                    comparedPairs++
                    if (puzzle.rows == prior.rows) sameGridPairs++
                    if (samePaths > 0) sameWordPathPairs++
                }
                previous = puzzle

                // These are exploratory, deliberately explicit numerical criteria,
                // NOT yet endorsed as "fun" without screenshots and user testing.
                val problems = arrayListOf<String>()
                if (report.minQuadrantCenters < puzzle.targets.size / 10)
                    problems += "unbalanced_quadrants"
                if (report.centerTilesOccupied < 12)
                    problems += "sparse_4x4_target_centers"
                if (report.regionsWithTargetCells < report.totalRegions)
                    problems += "missing_10x10_region"
                if (report.minDistinctTargetCellsInRegion < 5)
                    problems += "underused_10x10_region"
                val minimumDirections = when (challenge) {
                    PuzzleChallenge.RELAXED -> 2
                    PuzzleChallenge.CLASSIC -> 4
                    PuzzleChallenge.EXPERT -> 6
                }
                if (report.uniqueDirections < minimumDirections)
                    problems += "insufficient_directions"
                val dominantLimit = if (challenge == PuzzleChallenge.RELAXED) 0.65 else 0.48
                if (report.maxDirectionShare > dominantLimit)
                    problems += "dominant_direction"
                if (report.lengthBands.count { it > 0 } < 2)
                    problems += "one_word_length_band"
                if (problems.isEmpty()) allQualityPass++
                else problems.forEach { blockers[it] = (blockers[it] ?: 0) + 1 }
            }
            fun lo(f: (V4PuzzleQuality.Report) -> Int) = reports.minOf(f)
            fun hi(f: (V4PuzzleQuality.Report) -> Int) = reports.maxOf(f)
            val qualityPass = reports.count { report ->
                report.minQuadrantCenters >= report.targetCount/10 &&
                report.centerTilesOccupied >= 12 &&
                report.regionsWithTargetCells == report.totalRegions &&
                report.minDistinctTargetCellsInRegion >= 5 &&
                report.uniqueDirections >= when (challenge) {
                    PuzzleChallenge.RELAXED -> 2
                    PuzzleChallenge.CLASSIC -> 4
                    PuzzleChallenge.EXPERT -> 6
                } &&
                report.maxDirectionShare <=
                    (if (challenge == PuzzleChallenge.RELAXED) 0.65 else 0.48) &&
                report.lengthBands.count { it > 0 } >= 2
            }
            println(String.format(Locale.US,
                "%s %dx%d %s: proposed_quality=%d/100 min_quadrant=%d min_center_tiles=%d/16 min_10x10_regions=%d/%d min_cells_per_region=%d unique_dirs_range=%d..%d max_direction_share=%.3f adjacent_word_difference=%d..%d adjacent_same_word_paths_max=%d",
                theme.name, side, side, challenge.name, qualityPass,
                lo { it.minQuadrantCenters }, lo { it.centerTilesOccupied },
                lo { it.regionsWithTargetCells }, reports.first().totalRegions,
                lo { it.minDistinctTargetCellsInRegion },
                lo { it.uniqueDirections }, hi { it.uniqueDirections },
                reports.maxOf { it.maxDirectionShare },
                wordDifferences.minOrNull() ?: -1, wordDifferences.maxOrNull() ?: -1,
                sharedPaths.maxOrNull() ?: -1
            ))
        }
    println("RESULT: tested=$puzzleCount/1800 proposed_quality_pass=$allQualityPass " +
        "unique_grids=$uniqueBoards/1800 adjacent_pairs=$comparedPairs " +
        "same_grid_pairs=$sameGridPairs pairs_with_same_shared_word_path=$sameWordPathPairs")
    println("PROPOSED_QUALITY_BLOCKER_COUNTS: " +
        blockers.entries.sortedBy { it.key }.joinToString { it.key+"="+it.value })
    println("NOTE: 105 targets from 126-135 familiar candidates forces at least " +
        "75-84 identical words across ANY two 50x50 boards. " +
        "More editor-approved words are needed for genuinely fresh vocabulary.")
    check(puzzleCount == 1800)
    check(uniqueBoards == 1800 && sameGridPairs == 0)
    println("PASS: 1800 measurable giant-board quality reports and distinct full grid layouts.")
}
