package com.playereden.gridmates.wordsearch.core

/**
 * Durable, versionable Solo state. The generator version is captured in the puzzle ID.
 * Invalid IDs are discarded on load rather than awarded as words.
 */
data class SoloRun(
    val theme: WordTheme,
    val difficulty: WordDifficulty,
    val seed: Long,
    val foundIds: Set<String> = emptySet(),
    val hints: Map<String, Int> = emptyMap(),
    val elapsedSeconds: Long = 0L,
    val boardSize: PuzzleBoardSize? = null,
    val challenge: PuzzleChallenge? = null
) {
    val puzzleId: String get() =
        if (boardSize != null && challenge != null)
            WordSearchGeneratorV2.puzzleId(theme, boardSize, challenge, seed)
        else "wordsearch:v1:" + theme.name + ":" + difficulty.name + ":" + seed
}

data class SoloResult(
    val puzzleId: String,
    val theme: WordTheme,
    val difficulty: WordDifficulty,
    val wordCount: Int,
    val elapsedSeconds: Long,
    val hintSteps: Int,
    val completedAtMs: Long,
    val boardSize: PuzzleBoardSize? = null,
    val challenge: PuzzleChallenge? = null
) {
    val assisted: Boolean get() = hintSteps > 0
}

data class SoloProfile(
    val active: SoloRun,
    val results: List<SoloResult> = emptyList(),
    val dailyEntries: List<DailyProgress> = emptyList(),
    val identity: PlayerIdentity = PlayerIdentity()
) {
    val completions: Int get() = results.size
    val assistedCompletions: Int get() = results.count { it.assisted }
    val totalWords: Int get() = results.sumOf { it.wordCount }
    val dailyCompletions: Int get() = dailyEntries.count { it.completed }
}

/**
 * A small, non-monetized allowance per puzzle. Existing saved hint levels
 * remain intact; upgrading never erases a prior clue or completion.
 * Unlike Sudoku's generous hints, Word Search rewards independent searching.
 */
object WordHintPolicy {
    fun allowance(run: SoloRun, daily: Boolean = false): Int {
        if (daily) return 2
        val cells = run.boardSize?.cells ?: run.difficulty.gridSize
        return when {
            cells >= 20 -> 4
            cells >= 15 -> 3
            else -> 2
        }
    }

    fun used(run: SoloRun): Int = run.hints.values.sum()

    fun remaining(run: SoloRun, daily: Boolean = false): Int =
        (allowance(run, daily) - used(run)).coerceAtLeast(0)
}

object SoloProgressRules {
    // Rebuilding 20x20 layouts for every find/hint creates avoidable work.
    // The immutable puzzle ID includes content version, theme, board, challenge
    // and seed. Cache is small and independent of saved profiles or Android.
    private val puzzleCache = object : LinkedHashMap<String, WordSearchPuzzle>(8, 0.75f, true) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<String, WordSearchPuzzle>
        ): Boolean = size > 6
    }

    fun fresh(
        theme: WordTheme = WordTheme.WOODLAND,
        difficulty: WordDifficulty = WordDifficulty.EASY,
        seed: Long
    ): SoloRun = SoloRun(theme, difficulty, seed)

    fun freshV2(
        theme: WordTheme, board: PuzzleBoardSize,
        challenge: PuzzleChallenge, seed: Long
    ): SoloRun = SoloRun(theme, challenge.legacyDifficulty, seed,
        boardSize = board, challenge = challenge)

    /** Version dispatch is the only place that reconstructs a saved puzzle. */
    @Synchronized
    fun puzzle(run: SoloRun): WordSearchPuzzle {
        require((run.boardSize == null) == (run.challenge == null)) {
            "Partially specified v2 puzzle; preserve original save"
        }
        val id = run.puzzleId
        puzzleCache[id]?.let { return it }
        val generated = if (run.boardSize == null) {
            WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
        } else {
            WordSearchGeneratorV2.generate(
                run.theme, run.boardSize, requireNotNull(run.challenge), run.seed
            )
        }
        check(generated.id == id) { "Puzzle ID mismatch for cached seed" }
        puzzleCache[id] = generated
        return generated
    }

    fun validate(run: SoloRun): SoloRun {
        val puzzle = puzzle(run)
        val allowed = puzzle.targets.map { it.id }.toSet()
        return run.copy(
            foundIds = run.foundIds.intersect(allowed),
            hints = run.hints.filter { (id, level) -> id in allowed && level in 1..3 },
            elapsedSeconds = run.elapsedSeconds.coerceIn(0L, 365L * 24 * 3600)
        )
    }

    fun find(run: SoloRun, start: GridPoint, end: GridPoint): Pair<SoloRun, WordClaim> {
        val puzzle = puzzle(run)
        val claim = WordSearchRules.claim(puzzle, run.foundIds, start, end)
        return if (claim.status == ClaimStatus.FOUND) {
            run.copy(foundIds = run.foundIds + requireNotNull(claim.target).id) to claim
        } else run to claim
    }

    /**
     * Three-step help ladder, always selecting an unfinished word:
     * zone -> starting cell -> full path preview. No auto-completion.
     */
    fun hint(run: SoloRun, daily: Boolean = false): Pair<SoloRun, WordTarget?> {
        // A caller cannot bypass the allowance by requesting more levels.
        // Old saves with more used hints remain readable but receive no extras.
        if (WordHintPolicy.remaining(run, daily) == 0) return run to null
        val puzzle = puzzle(run)
        // Finish the three increasingly helpful clues for the selected word
        // before opening a different target. Already fully revealed words are
        // skipped until all other unfinished targets receive their clues.
        val next = puzzle.targets.firstOrNull {
            it.id !in run.foundIds && (run.hints[it.id] ?: 0) in 1..2
        } ?: puzzle.targets.firstOrNull {
            it.id !in run.foundIds && (run.hints[it.id] ?: 0) == 0
        } ?: puzzle.targets.firstOrNull { it.id !in run.foundIds }
        if (next == null) return run to null
        val current = run.hints[next.id] ?: 0
        return if (current >= 3) run to next else {
            run.copy(hints = run.hints + (next.id to (current + 1))) to next
        }
    }

    fun complete(profile: SoloProfile, nowMs: Long): SoloProfile {
        val run = profile.active
        val puzzle = puzzle(run)
        if (!WordSearchRules.complete(puzzle, run.foundIds)) return profile
        if (profile.results.any { it.puzzleId == run.puzzleId }) return profile
        val record = SoloResult(
            puzzleId = run.puzzleId,
            theme = run.theme,
            difficulty = run.difficulty,
            wordCount = puzzle.targets.size,
            elapsedSeconds = run.elapsedSeconds,
            hintSteps = run.hints.values.sum(),
            completedAtMs = nowMs,
            boardSize = run.boardSize,
            challenge = run.challenge
        )
        return profile.copy(results = listOf(record) + profile.results)
    }
}
