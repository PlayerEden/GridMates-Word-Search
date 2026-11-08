package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import java.util.Random

/**
 * PREVIEW-ONLY version-4 placement engine. The algorithm is integrated in core,
 * but NO reviewed V4 dictionary ships and neither Solo nor any app UI calls it.
 * The required candidate word list is injected by core TESTS only.
 *
 * Do not persist PREVIEW IDs: the separate immutable production corpus/version
 * and SoloRun reconstruction are intentionally unimplemented until editorial QA.
 */
internal object V4PreviewPlacement {
    const val CONTENT_VERSION = 4
    const val MAX_ATTEMPTS = 24
    const val SAMPLES = 160
    private const val FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"
    data class Cell(val row: Int, val col: Int)
    data class Dir(val dr: Int, val dc: Int)
    enum class Mode { RELAXED, CLASSIC, EXPERT }
    data class Target(val word: String, val cells: List<Cell>)
    data class Puzzle(val id: String, val rows: List<String>, val targets: List<Target>, val attempts: Int) {
        val size get() = rows.size
        fun hash(): String = sha(rows.joinToString("\n")).take(16)
    }
    private val all = (-1..1).flatMap { r -> (-1..1).mapNotNull { c ->
        if (r == 0 && c == 0) null else Dir(r, c)
    } }
    private val relaxed = listOf(Dir(0, 1), Dir(1, 0))
    private val classic = listOf(Dir(0, 1), Dir(1, 0), Dir(1, 1),
        Dir(1, -1), Dir(0, -1), Dir(-1, 0))
    private fun directions(mode: Mode) = when(mode) {
        Mode.RELAXED -> relaxed
        Mode.CLASSIC -> classic
        Mode.EXPERT -> all
    }
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun random(theme: String, size: Int, mode: Mode, seed: Long, attempt: Int): Random {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("ws09fc-kotlin-lab-v1:$theme:$size:${mode.name}:$seed:$attempt".toByteArray())
        var key = 0L
        for (i in 0 until 8) key = (key shl 8) or (bytes[i].toLong() and 255L)
        return Random(key)
    }
    private fun <T> shuffle(list: List<T>, rng: Random) = list.toMutableList().apply {
        for (i in lastIndex downTo 1) {
            val j = rng.nextInt(i + 1)
            val t = this[i]; this[i] = this[j]; this[j] = t
        }
    }
    fun occurrences(rows: List<String>, word: String): Int {
        val size = rows.size
        var matches = 0
        for (r in 0 until size) for (c in 0 until size) {
            if (rows[r][c] != word[0]) continue
            for ((dr, dc) in all) {
                val er = r + (word.length - 1) * dr
                val ec = c + (word.length - 1) * dc
                if (er !in 0 until size || ec !in 0 until size) continue
                if (word.indices.all { rows[r + it * dr][c + it * dc] == word[it] } &&
                    ++matches > 1) return matches
            }
        }
        return matches
    }
    fun validatePool(words: List<String>, count: Int) {
        require(words.size >= count && words.distinct().size == words.size)
        require(words.all { it.length in 5..16 && it.all { ch -> ch in 'A'..'Z' } })
        require(words.indices.all { i -> words.indices.none { j ->
            i != j && (words[i] in words[j] || words[j] in words[i])
        } })
    }
    // PREVIEW identity depends on every pool word and its order; changing an
    // unapproved test fixture must never alias a different puzzle for the seed.
    fun id(theme: String, size: Int, mode: Mode, seed: Long, pool: List<String>) =
        "wordsearch:v4:PREVIEW:en:$theme:${size}x$size:${mode.name}:$seed:" +
            sha("candidate-pool-v1:" + pool.joinToString("|")).take(16)

    fun generate(theme: String, size: Int, mode: Mode, seed: Long, pool: List<String>): Puzzle {
        require(theme in setOf("WOODLAND", "SPACE", "GARDEN"))
        require(size == 40 || size == 50)
        val n = if (size == 40) 70 else 105
        validatePool(pool, n)
        val dirs = directions(mode)
        for (attempt in 0 until MAX_ATTEMPTS) {
            val rng = random(theme, size, mode, seed, attempt)
            val words = shuffle(pool, rng).take(n).sortedByDescending { it.length }
            val grid = Array(size) { CharArray(size) { ' ' } }
            val targets = ArrayList<Target>(n)
            var failed = false
            for (word in words) {
                var first: List<Cell>? = null
                var preferred: List<Cell>? = null
                repeat(SAMPLES) {
                    val (dr, dc) = dirs[rng.nextInt(dirs.size)]
                    val minR = if (dr < 0) word.length - 1 else 0
                    val maxR = if (dr > 0) size - word.length + 1 else size
                    val minC = if (dc < 0) word.length - 1 else 0
                    val maxC = if (dc > 0) size - word.length + 1 else size
                    if (minR >= maxR || minC >= maxC) return@repeat
                    val r = rng.nextInt(minR, maxR)
                    val c = rng.nextInt(minC, maxC)
                    var overlaps = 0
                    var fits = true
                    for (i in word.indices) {
                        val old = grid[r + dr * i][c + dc * i]
                        if (old != ' ' && old != word[i]) { fits = false; break }
                        if (old == word[i]) overlaps++
                    }
                    if (fits) {
                        val path = List(word.length) { i -> Cell(r + dr * i, c + dc * i) }
                        if (first == null) first = path
                        if (overlaps > 0 && preferred == null) preferred = path
                    }
                }
                val path = if (mode == Mode.RELAXED) first else preferred ?: first
                if (path == null) { failed = true; break }
                path.forEachIndexed { i, p -> grid[p.row][p.col] = word[i] }
                targets += Target(word, path)
            }
            if (failed) continue
            for (r in 0 until size) for (c in 0 until size)
                if (grid[r][c] == ' ') grid[r][c] = FILLER[rng.nextInt(FILLER.length)]
            val rows = grid.map { String(it) }
            if (targets.any { occurrences(rows, it.word) != 1 }) continue
            return Puzzle(id(theme, size, mode, seed, pool), rows, targets, attempt + 1)
        }
        error("Bounded v4 lab generator exhausted attempts")
    }
    /** Independent route verification, including reverse-direction uniqueness. */
    fun verify(p: Puzzle, mode: Mode) {
        val size = p.size
        require(size == 40 || size == 50)
        require(p.rows.all { it.length == size && it.all { ch -> ch in 'A'..'Z' } })
        require(p.targets.size == (if (size == 40) 70 else 105))
        require(p.targets.map { it.word }.distinct().size == p.targets.size)
        for ((word, path) in p.targets) {
            require(path.size == word.length && path.size >= 2)
            val dir = Dir(path[1].row - path[0].row, path[1].col - path[0].col)
            require(dir in directions(mode))
            require(path.indices.all { i ->
                val (r, c) = path[i]
                r == path[0].row + i * dir.dr && c == path[0].col + i * dir.dc &&
                    r in 0 until size && c in 0 until size && p.rows[r][c] == word[i]
            })
            require(occurrences(p.rows, word) == 1)
        }
    }
    /**
     * Adapter into the ACTUAL core puzzle model, with a conspicuous non-shipping ID.
     * Call only in tests, never from SoloProgressRules or Android UI.
     */
    fun previewPuzzle(
        theme: WordTheme, cells: Int, challenge: PuzzleChallenge,
        seed: Long, reviewPendingWords: List<String>
    ): WordSearchPuzzle {
        val preview = generate(theme.name, cells, Mode.valueOf(challenge.name),
            seed, reviewPendingWords)
        verify(preview, Mode.valueOf(challenge.name))
        return WordSearchPuzzle(
            id = preview.id,
            seed = seed,
            contentVersion = CONTENT_VERSION,
            theme = theme,
            difficulty = challenge.legacyDifficulty,
            rows = preview.rows,
            targets = preview.targets.map { target ->
                WordTarget(target.word, target.word,
                    target.cells.map { GridPoint(it.row, it.col) })
            }
        )
    }

}
