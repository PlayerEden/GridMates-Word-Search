package com.playereden.gridmates.wordsearch.core

import kotlin.random.Random

/** Version 2 is separate from the frozen English v1 generator and Daily. */
enum class PuzzleBoardSize(val label: String, val cells: Int, val targets: Int) {
    COMPACT("Compact", 8, 6), STANDARD("Standard", 10, 8), LARGE("Large", 12, 10)
}
enum class PuzzleChallenge(val label: String, val description: String) {
    RELAXED("Relaxed", "Forward across and down"),
    CLASSIC("Classic", "Diagonals and some backwards words"),
    EXPERT("Expert", "All eight directions with trickier crossings");
    val legacyDifficulty: WordDifficulty get() = when(this) {
        RELAXED -> WordDifficulty.EASY
        CLASSIC -> WordDifficulty.MEDIUM
        EXPERT -> WordDifficulty.HARD
    }
}
private data class WordEntry(val spelling: String, val familiarity: Int)
private data class DirectionV2(val dr: Int, val dc: Int)
private val relaxed = listOf(DirectionV2(0, 1), DirectionV2(1, 0))
private val classic = listOf(
    DirectionV2(0, 1), DirectionV2(1, 0), DirectionV2(1, 1),
    DirectionV2(1, -1), DirectionV2(0, -1), DirectionV2(-1, 0)
)
private val all = (-1..1).flatMap { y ->
    (-1..1).mapNotNull { x -> if (x == 0 && y == 0) null else DirectionV2(y, x) }
}

/** Hand-edited prototype packs. Familiarity 1..3 is NOT sourced corpus frequency. */
object WordLibraryV2 {
    private fun entries(common: String, familiar: String, unusual: String) = buildList {
        listOf(common, familiar, unusual).forEachIndexed { band, words ->
            words.split(' ').filter { it.isNotBlank() }.forEach {
                add(WordEntry(it, band + 1))
            }
        }
    }.also { pack ->
        require(pack.size >= 28 && pack.map { it.spelling }.distinct().size == pack.size)
        require(pack.all { it.spelling.length in 3..12 &&
            it.spelling.all { c -> c in 'A'..'Z' } })
    }
    private val packs = mapOf(
        WordTheme.WOODLAND to entries(
            "FOX OWL BEAR DEER ROBIN MOUSE RABBIT WOLF LEAF PINE OAK TREE BARK NEST POND RIVER FOREST",
            "BADGER OTTER HARE SQUIRREL FERN MOSS TRAIL ACORN STREAM MEADOW MUSHROOM BEAVER RACCOON HOLLOW",
            "LYNX THICKET CANOPY SPRUCE SAPLING"
        ),
        WordTheme.SPACE to entries(
            "STAR MOON ROCKET PLANET SUN EARTH MARS VENUS JUPITER ALIEN SPACE LAUNCH",
            "COMET ORBIT GALAXY METEOR SATURN SOLAR PLUTO CRATER DUST LUNAR ECLIPSE GRAVITY SHUTTLE MISSION",
            "NEBULA COSMOS ASTEROID NOVA TELESCOPE SATELLITE PHOTON PULSAR QUASAR ASTRONAUT"
        ),
        WordTheme.GARDEN to entries(
            "ROSE MINT SAGE DAISY TULIP LEAF BLOOM SEED ROOT VINE FLOWER SOIL WATER GARDEN WEED HOSE STEM",
            "PEONY PETAL THYME SPRING SPROUT POLLEN NECTAR MULCH SHOVEL HERBS BASIL ORCHID PLANTER BEETLE",
            "COMPOST LAVENDER SUNFLOWER BUTTERFLY SEEDLING"
        )
    )
    fun count(theme: WordTheme): Int = packs.getValue(theme).size

    internal fun words(theme: WordTheme, board: PuzzleBoardSize,
                       challenge: PuzzleChallenge, seed: Long, attempt: Int = 0): List<String> {
        val max = (board.cells - 1).coerceAtLeast(4)
        val eligible = packs.getValue(theme).filter { it.spelling.length <= max }
        val pool = if (challenge == PuzzleChallenge.RELAXED) eligible.filter {
            it.familiarity <= 2
        } else eligible
        val rng = Random(seed xor (theme.ordinal.toLong() shl 31)
            xor (board.ordinal.toLong() shl 16) xor (challenge.ordinal + 41).toLong()
            xor (attempt.toLong() * 0x7EE21L))
        val lessCommon = when (challenge) {
            PuzzleChallenge.RELAXED -> emptyList()
            PuzzleChallenge.CLASSIC -> pool.filter { it.familiarity == 3 }.shuffled(rng).take(1)
            PuzzleChallenge.EXPERT -> pool.filter { it.familiarity == 3 }.shuffled(rng).take(2)
        }
        val selected = (lessCommon + pool.shuffled(rng))
            .distinctBy { it.spelling }.take(board.targets)
        require(selected.size == board.targets) { "Insufficient themed words" }
        return selected.map { it.spelling }.sortedByDescending { it.length }
    }
}

object WordSearchGeneratorV2 {
    const val CONTENT_VERSION = 2
    private const val FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"
    fun puzzleId(theme: WordTheme, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge, seed: Long): String =
        "wordsearch:v2:en:${theme.name}:${board.name}:${challenge.name}:$seed"

    fun generate(theme: WordTheme, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge, seed: Long): WordSearchPuzzle {
        val size = board.cells
        // A rare crowded layout must not make an otherwise valid puzzle fail.
        // Each retry explores a different deterministic word subset.
        val rng = Random(seed xor 0x4725AF01L xor ((theme.ordinal + 1L) shl 32)
            xor ((board.ordinal + 1L) shl 21) xor challenge.ordinal.toLong())
        val directions = when(challenge) {
            PuzzleChallenge.RELAXED -> relaxed
            PuzzleChallenge.CLASSIC -> classic
            PuzzleChallenge.EXPERT -> all
        }
        repeat(160) { attempt ->
            val words = WordLibraryV2.words(theme, board, challenge, seed, attempt)
            val grid = Array(size) { CharArray(size) { ' ' } }
            val targets = mutableListOf<WordTarget>()
            var placed = true
            for (word in words) {
                val possibilities = buildList<List<GridPoint>> {
                    for (r in 0 until size) for (c in 0 until size) for (d in directions) {
                        if (r + (word.length - 1) * d.dr !in 0 until size ||
                            c + (word.length - 1) * d.dc !in 0 until size) continue
                        add(List(word.length) { k -> GridPoint(r + k * d.dr, c + k * d.dc) })
                    }
                }.shuffled(rng).filter { path ->
                    path.withIndex().all { (i, p) ->
                        grid[p.row][p.col] == ' ' || grid[p.row][p.col] == word[i]
                    }
                }
                val picked = if (challenge == PuzzleChallenge.EXPERT) {
                    possibilities.firstOrNull { path ->
                        path.any { grid[it.row][it.col] != ' ' }
                    } ?: possibilities.firstOrNull()
                } else possibilities.firstOrNull()
                if (picked == null) { placed = false; break }
                picked.forEachIndexed { i, p -> grid[p.row][p.col] = word[i] }
                targets += WordTarget(word, word, picked)
            }
            if (!placed) return@repeat
            for (r in grid.indices) for (c in grid[r].indices) {
                if (grid[r][c] == ' ') grid[r][c] = FILLER[rng.nextInt(FILLER.length)]
            }
            if (targets.any { countOccurrences(grid, it.word) != 1 }) return@repeat
            return WordSearchPuzzle(
                puzzleId(theme, board, challenge, seed), seed, CONTENT_VERSION,
                theme, challenge.legacyDifficulty,
                grid.map { it.concatToString() }, targets
            )
        }
        throw IllegalStateException("Could not place v2 puzzle")
    }

    private fun countOccurrences(grid: Array<CharArray>, word: String): Int {
        var count = 0
        for (r in grid.indices) for (c in grid[r].indices) for (d in all) {
            if (word.indices.all { i ->
                val y = r + i * d.dr
                val x = c + i * d.dc
                y in grid.indices && x in grid[y].indices && grid[y][x] == word[i]
            }) count++
        }
        return count
    }
}
