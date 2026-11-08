package ws09fc.lab

import java.io.File

/** Uses review-pending candidate fixture; must never be mistaken for a game build. */
fun main(args: Array<String>) {
    val file = args.getOrNull(0) ?: "labs/ws09f_c_kotlin/candidates_test_only.txt"
    val pools = File(file).readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { line ->
            val fields = line.split('|', limit = 2)
            require(fields.size == 2)
            fields[0] to fields[1].split(' ').filter { it.isNotEmpty() }
        }
    require(pools.keys == setOf("WOODLAND", "GARDEN", "SPACE"))
    val seeds = listOf(11L, 42L, 2026L, 0L, 1L, 7L, 19L, 77L, 144L, 999L)
    var checks = 0
    for (theme in pools.keys.sorted()) for (size in listOf(40, 50))
        for (mode in V4EngineLab.Mode.entries) {
            val pool = pools.getValue(theme)
            var worst = 0
            for (seed in seeds) {
                val puzzle = V4EngineLab.generate(theme, size, mode, seed, pool)
                V4EngineLab.verify(puzzle, mode)
                // The same theme/size/challenge/seed MUST reconstruct exactly.
                val replay = V4EngineLab.generate(theme, size, mode, seed, pool)
                check(puzzle.hash() == replay.hash())
                check(puzzle.targets == replay.targets)
                check(puzzle.id == replay.id)
                worst = maxOf(worst, puzzle.attempts)
                checks++
            }
            println("$theme ${size}x$size $mode: 10/10, worst bounded attempts=$worst")
        }
    check(runCatching {
        V4EngineLab.generate("SPACE", 75, V4EngineLab.Mode.RELAXED, 1, pools.getValue("SPACE"))
    }.isFailure)
    check(runCatching {
        V4EngineLab.validatePool(listOf("RIVER", "RIVERBANK"), 2)
    }.isFailure)
    check(V4EngineLab.id("SPACE", 40, V4EngineLab.Mode.CLASSIC, 42L)
        .startsWith("wordsearch:v4:LAB:"))
    println("PASS $checks v4 Kotlin lab grids and deterministic replays; NOT APK-ready")
}
