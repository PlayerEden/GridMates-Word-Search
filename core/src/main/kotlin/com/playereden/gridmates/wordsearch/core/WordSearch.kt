package com.playereden.gridmates.wordsearch.core

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign
import kotlin.random.Random

/** Small curated English packs. Adding/renaming entries requires versioning saved puzzles. */
enum class WordTheme(val label: String, val words: List<String>) {
    WOODLAND("Woodland", listOf(
        "FOX", "OWL", "BEAR", "DEER", "ROBIN", "MOUSE", "BADGER",
        "OTTER", "RABBIT", "WOLF", "HARE", "LYNX", "SQUIRREL", "FERN"
    )),
    SPACE("Outer Space", listOf(
        "STAR", "MOON", "COMET", "ORBIT", "ROCKET", "GALAXY", "PLANET",
        "METEOR", "SATURN", "NEBULA", "COSMOS", "ASTEROID", "NOVA", "SOLAR"
    )),
    GARDEN("Garden", listOf(
        "ROSE", "MINT", "SAGE", "DAISY", "TULIP", "PEONY", "PETAL",
        "LEAF", "BLOOM", "SEED", "ROOT", "VINE", "FLOWER", "THYME"
    ))
}

enum class WordDifficulty(val label: String, val gridSize: Int, val targetCount: Int) {
    EASY("Easy", 8, 6),
    MEDIUM("Medium", 10, 8),
    HARD("Hard", 12, 10)
}

data class GridPoint(val row: Int, val col: Int)
data class WordTarget(val id: String, val word: String, val positions: List<GridPoint>)

data class WordSearchPuzzle(
    val id: String,
    val seed: Long,
    val contentVersion: Int,
    val theme: WordTheme,
    val difficulty: WordDifficulty,
    val rows: List<String>,
    val targets: List<WordTarget>
) {
    val size: Int get() = rows.size
    fun letter(point: GridPoint): Char? =
        if (point.row in rows.indices && point.col in rows[point.row].indices)
            rows[point.row][point.col] else null
}

enum class ClaimStatus { FOUND, ALREADY_FOUND, INVALID }
data class WordClaim(val status: ClaimStatus, val target: WordTarget? = null)

object WordSearchRules {
    /** A selection must be a single straight horizontal/vertical/diagonal path. */
    fun line(start: GridPoint, end: GridPoint, boardSize: Int): List<GridPoint> {
        if (boardSize < 1 || start.row !in 0 until boardSize ||
            end.row !in 0 until boardSize || start.col !in 0 until boardSize ||
            end.col !in 0 until boardSize
        ) return emptyList()
        val dr = end.row - start.row
        val dc = end.col - start.col
        if (dr != 0 && dc != 0 && abs(dr) != abs(dc)) return emptyList()
        val count = max(abs(dr), abs(dc)) + 1
        return List(count) { i ->
            GridPoint(start.row + i * dr.sign, start.col + i * dc.sign)
        }
    }

    /** Only placed target paths score; random filler words do not. */
    fun claim(
        puzzle: WordSearchPuzzle,
        existing: Set<String>,
        start: GridPoint,
        end: GridPoint
    ): WordClaim {
        val path = line(start, end, puzzle.size)
        if (path.size < 2) return WordClaim(ClaimStatus.INVALID)
        val target = puzzle.targets.firstOrNull { t ->
            t.positions == path || t.positions.asReversed() == path
        } ?: return WordClaim(ClaimStatus.INVALID)
        return if (target.id in existing) WordClaim(ClaimStatus.ALREADY_FOUND, target)
        else WordClaim(ClaimStatus.FOUND, target)
    }

    fun complete(puzzle: WordSearchPuzzle, found: Set<String>): Boolean =
        puzzle.targets.isNotEmpty() && puzzle.targets.all { it.id in found }
}

object WordSearchGenerator {
    const val CONTENT_VERSION = 1
    private val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private data class Direction(val dr: Int, val dc: Int)
    private val allDirections = listOf(
        Direction(-1, -1), Direction(-1, 0), Direction(-1, 1),
        Direction(0, -1), Direction(0, 1),
        Direction(1, -1), Direction(1, 0), Direction(1, 1)
    )
    private val easyDirections = listOf(Direction(0, 1), Direction(1, 0))

    /**
     * Stable for the same seed/content version. Bounded retries avoid an
     * infinite loading screen on a malformed future content pack.
     */
    fun generate(
        theme: WordTheme = WordTheme.WOODLAND,
        difficulty: WordDifficulty = WordDifficulty.EASY,
        seed: Long = 1L
    ): WordSearchPuzzle {
        val size = difficulty.gridSize
        val words = theme.words.shuffled(Random(seed xor 0x5A17B0L))
            .take(difficulty.targetCount)
            .sortedByDescending { it.length }
        require(words.size == difficulty.targetCount && words.distinct().size == words.size)
        require(words.all { w -> w.length in 2..size && w.all { it in 'A'..'Z' } })
        val rng = Random(seed xor ((theme.ordinal + 1L) shl 32)
            xor difficulty.ordinal.toLong() xor CONTENT_VERSION.toLong())
        val directions = if (difficulty == WordDifficulty.EASY) easyDirections else allDirections

        repeat(120) {
            val grid = Array(size) { CharArray(size) { ' ' } }
            val targets = mutableListOf<WordTarget>()
            var placeable = true
            for (word in words) {
                val candidates = buildList {
                    for (row in 0 until size) for (col in 0 until size)
                        for (dir in directions) {
                            val path = List(word.length) { i ->
                                GridPoint(row + i * dir.dr, col + i * dir.dc)
                            }
                            if (path.all { p -> p.row in 0 until size && p.col in 0 until size })
                                add(path)
                        }
                }.shuffled(rng)
                val position = candidates.firstOrNull { path ->
                    path.withIndex().all { (i, p) ->
                        val old = grid[p.row][p.col]
                        old == ' ' || old == word[i]
                    }
                }
                if (position == null) { placeable = false; break }
                position.forEachIndexed { i, p -> grid[p.row][p.col] = word[i] }
                targets.add(WordTarget(word, word, position))
            }
            if (!placeable) return@repeat

            for (row in grid.indices) for (col in grid[row].indices) {
                if (grid[row][col] == ' ') grid[row][col] = letters[rng.nextInt(letters.length)]
            }
            // No accidental duplicate targets, in forward OR reverse reading.
            if (targets.any { t -> occurrences(grid, t.word) != 1 }) return@repeat

            return WordSearchPuzzle(
                id = "wordsearch:v1:" + theme.name + ":" + difficulty.name + ":" + seed,
                seed = seed,
                contentVersion = CONTENT_VERSION,
                theme = theme,
                difficulty = difficulty,
                rows = grid.map { it.concatToString() },
                targets = targets
            )
        }
        throw IllegalStateException("Unable to build unambiguous puzzle")
    }

    private fun occurrences(grid: Array<CharArray>, word: String): Int {
        val size = grid.size
        var count = 0
        for (row in 0 until size) for (col in 0 until size) for (dir in allDirections) {
            if (word.indices.all { index ->
                val y = row + index * dir.dr
                val x = col + index * dir.dc
                y in 0 until size && x in 0 until size && grid[y][x] == word[index]
            }) count++
        }
        return count
    }
}
