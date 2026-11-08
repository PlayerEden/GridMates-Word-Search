package ws09fc.lab

import java.io.File
import java.util.Locale
import kotlin.math.max

/** Separate JVM stress lab; never shipped or exposed by Android Solo. */
fun main(args: Array<String>) {
    val path = args.getOrNull(0) ?: "labs/ws09f_c_kotlin/candidates_test_only.txt"
    val pools = File(path).readLines().filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { line ->
            val parts = line.split("|", limit = 2)
            require(parts.size == 2)
            parts[0] to parts[1].split(" ").filter { it.isNotBlank() }
        }
    require(pools.keys == setOf("WOODLAND", "SPACE", "GARDEN"))
    val seeds = (0L..94L).toList() + listOf(-1L, -2026L, Long.MIN_VALUE, Long.MAX_VALUE,
        0x1A2B3C4D5E6F70L)
    require(seeds.distinct().size == 100)
    var passed = 0
    var failed = 0
    var worst = 0
    var longestNs = 0L
    val errors = ArrayList<String>()
    val startNs = System.nanoTime()
    val runtime = Runtime.getRuntime()
    for (theme in pools.keys.sorted()) for (size in listOf(40, 50))
        for (mode in V4EngineLab.Mode.entries) {
            val times = ArrayList<Long>(100)
            var failedHere = 0
            var attemptsHere = 0L
            var worstHere = 0
            for (seed in seeds) {
                val tick = System.nanoTime()
                try {
                    val p = V4EngineLab.generate(theme, size, mode, seed, pools.getValue(theme))
                    V4EngineLab.verify(p, mode)
                    val ns = System.nanoTime() - tick
                    times += ns
                    longestNs = max(longestNs, ns)
                    worstHere = max(worstHere, p.attempts)
                    worst = max(worst, p.attempts)
                    attemptsHere += p.attempts
                    passed++
                    if (seed in setOf(0L, 42L, -1L, Long.MIN_VALUE)) {
                        val again = V4EngineLab.generate(theme, size, mode, seed, pools.getValue(theme))
                        check(p.id == again.id && p.rows == again.rows && p.targets == again.targets)
                    }
                } catch (e: Exception) {
                    failed++
                    failedHere++
                    errors += theme + " " + size + "x" + size + " " + mode + " seed=" + seed +
                        ": " + e.javaClass.simpleName + " " + e.message
                }
            }
            times.sort()
            val p50 = times.getOrNull((times.size * .50).toInt())?.div(1000000.0) ?: -1.0
            val p95 = times.getOrNull((times.size * .95).toInt().coerceAtMost(times.lastIndex))
                ?.div(1000000.0) ?: -1.0
            println(String.format(Locale.US,
                "%s %dx%d %s: passed=%d/100 worst_attempts=%d mean_attempts=%.2f runner_p50_ms=%.2f runner_p95_ms=%.2f",
                theme, size, size, mode, 100 - failedHere, worstHere,
                attemptsHere.toDouble() / max(1, times.size), p50, p95))
        }
    val elapsed = (System.nanoTime() - startNs) / 1000000.0
    val heap = (runtime.totalMemory() - runtime.freeMemory()) / 1048576.0
    println(String.format(Locale.US,
        "RESULT: passed=%d/1800 failed=%d worst_attempts=%d longest_single_ms=%.2f total_runner_ms=%.1f heap_at_end_MiB=%.1f",
        passed, failed, worst, longestNs / 1000000.0, elapsed, heap))
    if (errors.isNotEmpty()) {
        println("FAILED SEEDS (first 50 of " + failed + "):")
        errors.take(50).forEach(::println)
    }
    println("JVM timing is not Android frame-time, FPS, memory peak or low-end performance.")
    println("Candidate vocabulary is NOT editorially approved; board sizes are NOT enabled.")
    check(failed == 0) { "Stop: " + failed + "/1800 deterministic failures before integration." }
    println("PASS 1800/1800 bounded Kotlin v4 candidate grids, exact paths and 8-way uniqueness.")
}
