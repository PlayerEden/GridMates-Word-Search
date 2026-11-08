package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import kotlin.math.max

/**
 * Isolated JVM tier-preview stress. Proposed words are read from a core TEST
 * fixture. This is not an Android APK or a publicly saved V4 puzzle.
 */
fun main(args: Array<String>) {
    val path = args.getOrNull(0) ?: "core/src/test/resources/ws09f_e_v4_provisional_tiers.txt"
    val tiers = File(path).readLines().filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { row ->
            val parts = row.split('|', limit = 3)
            require(parts.size == 3)
            (parts[0] to parts[1]) to parts[2].split(' ').filter { it.isNotBlank() }
        }
    val names = setOf("WOODLAND", "SPACE", "GARDEN")
    val bands = setOf("TRIAL_RELAXED", "TRIAL_ADVANCED", "EDITOR_HOLD",
        "UNREVIEWED_ADDITION")
    require(tiers.keys == names.flatMap { t -> bands.map { t to it } }.toSet())
    val seeds = (0L..94L).toList() + listOf(
        -1L, -2026L, Long.MIN_VALUE, Long.MAX_VALUE, 0x1A2B3C4D5E6F70L
    )
    require(seeds.size == 100 && seeds.distinct().size == 100)
    val failures = ArrayList<String>()
    var checked = 0
    var paths = 0
    var alternate = 0
    var worstDraw = 0
    var longestNs = 0L
    val begin = System.nanoTime()
    for (theme in WordTheme.entries) for (side in listOf(40, 50))
        for (mode in PuzzleChallenge.entries) {
            val gentle = tiers.getValue(theme.name to "TRIAL_RELAXED")
            val advanced = tiers.getValue(theme.name to "TRIAL_ADVANCED")
            val holds = tiers.getValue(theme.name to "EDITOR_HOLD")
            val durations = ArrayList<Long>(100)
            var hereFailed = 0
            var hereAlternate = 0
            var hereWorstDraw = 0
            for (seed in seeds) {
                val start = System.nanoTime()
                try {
                    val outcome = V4PreviewTierSelection.previewOutcome(
                        theme, side, mode, seed, gentle, advanced)
                    val p = outcome.puzzle
                    val elapsed = System.nanoTime() - start
                    durations += elapsed
                    longestNs = max(longestNs, elapsed)
                    check(outcome.poolDraws in 1..V4PreviewTierSelection.MAX_POOL_DRAWS)
                    worstDraw = max(worstDraw, outcome.poolDraws)
                    hereWorstDraw = max(hereWorstDraw, outcome.poolDraws)
                    if (outcome.poolDraws > 1) { alternate++; hereAlternate++ }
                    check(p.contentVersion == 4)
                    check(p.size == side)
                    check(p.targets.size == if (side == 40) 70 else 105)
                    check(p.id.startsWith("wordsearch:v4:PREVIEW:en:"))
                    check(p.targets.all { it.id !in holds })
                    val expectedAdvanced = outcome.selection.advancedCount
                    check(p.targets.count { it.word in advanced } == expectedAdvanced)
                    check(p.targets.map { it.word }.toSet() == outcome.selection.words.toSet())
                    var found: Set<String> = emptySet()
                    for (target in p.targets) {
                        check(target.positions.mapNotNull(p::letter).joinToString("") == target.word)
                        check(WordSearchRules.claim(p, found, target.positions.first(),
                            target.positions.last()).status == ClaimStatus.FOUND)
                        found = found + target.id
                        paths++
                    }
                    check(WordSearchRules.complete(p, found))
                    if (seed in setOf(0L, 42L, -1L, Long.MIN_VALUE)) {
                        check(outcome == V4PreviewTierSelection.previewOutcome(
                            theme, side, mode, seed, gentle, advanced))
                    }
                    checked++
                } catch (e: Exception) {
                    hereFailed++
                    failures += "${theme.name} ${side}x$side ${mode.name} seed=$seed: " +
                        "${e.javaClass.simpleName}: ${e.message}"
                }
            }
            val t = durations.sorted()
            val p50 = t.getOrNull((t.size * .50).toInt())?.div(1_000_000.0) ?: -1.0
            val p95 = t.getOrNull((t.size * .95).toInt().coerceAtMost(t.lastIndex))
                ?.div(1_000_000.0) ?: -1.0
            println(String.format(Locale.US,
                "%s %dx%d %s: passed=%d/100 failed=%d alternate_draws=%d max_draws=%d runner_p50_ms=%.2f runner_p95_ms=%.2f",
                theme, side, side, mode, 100-hereFailed, hereFailed, hereAlternate,
                hereWorstDraw, p50, p95))
        }
    val elapsed = (System.nanoTime() - begin) / 1_000_000.0
    println(String.format(Locale.US,
        "RESULT: passed=%d/1800 failed=%d claimed_paths=%d alternate_pool_cases=%d max_pool_draws=%d max_single_ms=%.2f runner_total_ms=%.1f",
        checked, failures.size, paths, alternate, worstDraw, longestNs/1_000_000.0, elapsed))
    failures.take(50).forEach { println("FAILED SEED: $it") }
    println("NOTE: JVM runner only. This does NOT validate phone FPS, battery, reading comfort or content approval.")
    check(failures.isEmpty()) { "${failures.size} / 1800 candidate V4 tiered boards failed. Do not integrate." }
    println("PASS 1800/1800 tiered candidate previews, original core claim paths, 8-way occurrence checks.")
}
