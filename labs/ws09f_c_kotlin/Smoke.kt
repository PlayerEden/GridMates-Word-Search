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
    // Frozen LAB goldens for seed 11, captured from real Kotlin/JVM CI run
    // #42471203834 (before these literal constants were added).
    val goldenSeed11 = mapOf(
        "GARDEN|40|RELAXED" to "860aa1e4a4746dd4",
        "GARDEN|40|CLASSIC" to "57150c1cf8a7d5ad",
        "GARDEN|40|EXPERT" to "7a5e44bbbdfb12a6",
        "GARDEN|50|RELAXED" to "fdbc1870305df456",
        "GARDEN|50|CLASSIC" to "e00dd78f276ae077",
        "GARDEN|50|EXPERT" to "7787d47528221b69",
        "SPACE|40|RELAXED" to "aea7cfa29d4a871a",
        "SPACE|40|CLASSIC" to "3059e50feb7f20dd",
        "SPACE|40|EXPERT" to "0dfcec549963d92e",
        "SPACE|50|RELAXED" to "98e50184357c1d49",
        "SPACE|50|CLASSIC" to "8679de2b848aeae4",
        "SPACE|50|EXPERT" to "11393082e6ab9087",
        "WOODLAND|40|RELAXED" to "72b4bf61f1dea9db",
        "WOODLAND|40|CLASSIC" to "c66801453c857077",
        "WOODLAND|40|EXPERT" to "e91f5ef432133e52",
        "WOODLAND|50|RELAXED" to "fa8f502556230716",
        "WOODLAND|50|CLASSIC" to "ff0f98bc139b3886",
        "WOODLAND|50|EXPERT" to "6d180338ef349c5f"
    )
    val seeds = listOf(11L, 42L, 2026L, 0L, 1L, 7L, 19L, 77L, 144L, 999L)
    var checks = 0
    for (theme in pools.keys.sorted()) for (size in listOf(40, 50))
        for (mode in V4EngineLab.Mode.entries) {
            val pool = pools.getValue(theme)
            var worst = 0
            for (seed in seeds) {
                val puzzle = V4EngineLab.generate(theme, size, mode, seed, pool)
                V4EngineLab.verify(puzzle, mode)
                if (seed == 11L) check(puzzle.hash() == goldenSeed11.getValue("$theme|$size|$mode")) { "V4 lab golden changed: $theme $size $mode" }
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
