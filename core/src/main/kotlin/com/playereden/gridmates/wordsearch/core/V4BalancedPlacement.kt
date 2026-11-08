package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import java.util.Random

/**
 * Experimental BALANCED placement variant for giant-board research.
 * The older V4PreviewPlacement and its 18 frozen experimental grid goldens
 * MUST NOT be rewritten. No Solo caller, shipping dictionary, APK or save ID.
 */
internal object V4BalancedPlacement {
    private data class Dir(val dr: Int, val dc: Int) {
        fun key() = "$dr,$dc"
    }
    private val easy = listOf(Dir(0, 1), Dir(1, 0))
    private val classic = listOf(
        Dir(0, 1), Dir(1, 0), Dir(1, 1),
        Dir(1, -1), Dir(0, -1), Dir(-1, 0)
    )
    private val expert = (-1..1).flatMap { dr ->
        (-1..1).mapNotNull { dc ->
            if (dr == 0 && dc == 0) null else Dir(dr, dc)
        }
    }
    private const val FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"
    private const val MAX_ATTEMPTS = 24
    private const val SAMPLES = 160
    data class Outcome(
        val puzzle: WordSearchPuzzle,
        val attempts: Int,
        val quality: V4PuzzleQuality.Report
    )

    private fun digest(s: String) = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray(Charsets.UTF_8))

    private fun seeded(theme: WordTheme, size: Int, mode: PuzzleChallenge,
                       seed: Long, attempt: Int): Random {
        val bytes = digest("ws09f-f-balanced-v1:${theme.name}:$size:${mode.name}:$seed:$attempt")
        var key = 0L
        for (i in 0 until 8) key = (key shl 8) or (bytes[i].toLong() and 255L)
        return Random(key)
    }

    private fun identifier(theme: WordTheme, size: Int, mode: PuzzleChallenge,
                           seed: Long, words: List<String>): String {
        val poolHash = digest("ws09f-f-balanced-candidates:" + words.joinToString("|"))
            .take(8).joinToString("") { "%02x".format(it.toInt() and 255) }
        return "wordsearch:v4:BALANCED-PREVIEW:en:${theme.name}:" +
            "${size}x$size:${mode.name}:$seed:$poolHash"
    }

    private fun directions(mode: PuzzleChallenge) = when (mode) {
        PuzzleChallenge.RELAXED -> easy
        PuzzleChallenge.CLASSIC -> classic
        PuzzleChallenge.EXPERT -> expert
    }

    private fun shuffle(items: List<String>, rng: Random): List<String> =
        items.toMutableList().apply {
            for (i in lastIndex downTo 1) {
                val j = rng.nextInt(i + 1)
                val temp = this[i]
                this[i] = this[j]
                this[j] = temp
            }
        }

    fun generate(theme: WordTheme, size: Int, mode: PuzzleChallenge, seed: Long,
                 selectedWords: List<String>): Outcome {
        require(size == 40 || size == 50)
        val n = if (size == 40) 70 else 105
        require(selectedWords.size == n)
        V4PreviewPlacement.validatePool(selectedWords, n)
        require(selectedWords.none { it == it.reversed() }) {
            "Palindrome candidate cannot occur exactly once in eight directions"
        }
        val dirs = directions(mode)
        val side = size / 10
        var lastQualityFailures = "none"
        for (attempt in 0 until MAX_ATTEMPTS) {
            val rng = seeded(theme, size, mode, seed, attempt)
            val words = shuffle(selectedWords, rng).sortedByDescending { it.length }
            val grid = Array(size) { CharArray(size) { ' ' } }
            val anchors = IntArray(16)
            val quadrants = IntArray(4)
            val regionCells = Array(side * side) { mutableSetOf<GridPoint>() }
            val directionCounts = mutableMapOf<String, Int>()
            val targets = ArrayList<WordTarget>(n)
            var failedToFit = false
            for (word in words) {
                var bestPath: List<GridPoint>? = null
                var bestScore = Double.NEGATIVE_INFINITY
                var bestDirection = ""
                for (sample in 0 until SAMPLES) {
                    val dir = dirs[rng.nextInt(dirs.size)]
                    val minR = if (dir.dr < 0) word.length - 1 else 0
                    val maxR = if (dir.dr > 0) size - word.length + 1 else size
                    val minC = if (dir.dc < 0) word.length - 1 else 0
                    val maxC = if (dir.dc > 0) size - word.length + 1 else size
                    if (minR >= maxR || minC >= maxC) continue
                    val startR = minR + rng.nextInt(maxR - minR)
                    val startC = minC + rng.nextInt(maxC - minC)
                    var overlaps = 0
                    var compatible = true
                    for (i in word.indices) {
                        val existing = grid[startR + i * dir.dr][startC + i * dir.dc]
                        if (existing != ' ' && existing != word[i]) {
                            compatible = false
                            break
                        }
                        if (existing == word[i]) overlaps++
                    }
                    if (!compatible) continue
                    val path = List(word.length) { i ->
                        GridPoint(startR + i * dir.dr, startC + i * dir.dc)
                    }
                    val mid = path[word.length / 2]
                    val anchorTile = (mid.row * 4 / size) * 4 + (mid.col * 4 / size)
                    val quadrant = (mid.row * 2 / size) * 2 + (mid.col * 2 / size)
                    val regions = path.map { it.row / 10 * side + it.col / 10 }.distinct()
                    val direction = dir.key()

                    // Empty/underused 10x10 sections outrank overlaps.
                    // Spread word centers across all quadrants and favor a
                    // healthy mix of allowed directions. Seeded jitter gives
                    // tie variety without destroying deterministic replay.
                    var score = 0.0
                    for (tile in regions) {
                        val existing = regionCells[tile].size
                        score += 55.0 / (2.0 + existing)
                    }
                    score += 18.0 / (1.0 + anchors[anchorTile])
                    score += 10.0 / (1.0 + quadrants[quadrant])
                    score += 12.0 / (1.0 + (directionCounts[direction] ?: 0))
                    score += 0.35 * overlaps
                    score += rng.nextDouble() * 0.15
                    if (score > bestScore) {
                        bestScore = score
                        bestPath = path
                        bestDirection = direction
                    }
                }
                val path = bestPath
                if (path == null) { failedToFit = true; break }
                path.forEachIndexed { i, p ->
                    grid[p.row][p.col] = word[i]
                    regionCells[(p.row / 10) * side + p.col / 10] += p
                }
                val mid = path[word.length / 2]
                anchors[(mid.row * 4 / size) * 4 + mid.col * 4 / size]++
                quadrants[(mid.row * 2 / size) * 2 + mid.col * 2 / size]++
                directionCounts[bestDirection] = (directionCounts[bestDirection] ?: 0) + 1
                targets += WordTarget(word, word, path)
            }
            if (failedToFit) {
                lastQualityFailures = "path-placement-exhausted"
                continue
            }
            for (r in 0 until size) for (c in 0 until size) {
                if (grid[r][c] == ' ') grid[r][c] = FILLER[rng.nextInt(FILLER.length)]
            }
            val rows = grid.map { String(it) }
            if (targets.any { V4PreviewPlacement.occurrences(rows, it.word) != 1 }) {
                lastQualityFailures = "accidental-duplicate-target-occurrence"
                continue
            }
            val puzzle = WordSearchPuzzle(
                id = identifier(theme, size, mode, seed, selectedWords),
                seed = seed,
                contentVersion = 4,
                theme = theme,
                difficulty = mode.legacyDifficulty,
                rows = rows,
                targets = targets
            )
            val report = V4PuzzleQuality.measure(puzzle)
            val problems = V4PuzzleQuality.issues(report, mode)
            if (problems.isNotEmpty()) {
                lastQualityFailures = problems.joinToString(",")
                continue
            }
            return Outcome(puzzle, attempt + 1, report)
        }
        error("Bounded quality V4 preview exhausted $MAX_ATTEMPTS attempts: " +
            "${theme.name} ${size}x$size ${mode.name} seed=$seed; " +
            "last_reason=$lastQualityFailures")
    }
}
