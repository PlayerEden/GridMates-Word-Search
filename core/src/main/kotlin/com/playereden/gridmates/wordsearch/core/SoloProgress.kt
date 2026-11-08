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
    val challenge: PuzzleChallenge? = null,
    /** Optional, per-puzzle viewing position. Never contributes to puzzle identity. */
    val viewportBookmark: LargeBoardBookmark? = null
) {
    val puzzleId: String get() =
        if (boardSize != null && challenge != null)
            if (boardSize in PuzzleBoardSize.largePresets)
                WordSearchGeneratorV3.puzzleId(theme, boardSize, challenge, seed)
            else WordSearchGeneratorV2.puzzleId(theme, boardSize, challenge, seed)
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
    val identity: PlayerIdentity = PlayerIdentity(),
    /** Additive schema-4 race journal in the same atomic save and manual backup. */
    val raceResults: List<RaceResult> = emptyList(),
    /** Completely separate Speed Duel journal in the same atomic profile. */
    val duelResults: List<DuelResult> = emptyList(),
    /** Schema-5 paused Solo puzzle library, newest paused first. No Daily games here. */
    val pausedSolo: List<SoloRun> = emptyList()
) {
    val raceSummary: RaceSummary get() = RaceSummary.of(raceResults)
    val duelSummary: DuelSummary get() = DuelSummary.of(duelResults)
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
            cells >= 30 -> 8
            cells >= 25 -> 6
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
    ): SoloRun {
        require(board in PuzzleBoardSize.v2Presets) { "V2 run cannot use a v3 board" }
        return SoloRun(theme, challenge.legacyDifficulty, seed,
            boardSize = board, challenge = challenge)
    }

    fun freshV3(
        theme: WordTheme, board: PuzzleBoardSize,
        challenge: PuzzleChallenge, seed: Long
    ): SoloRun {
        require(board in PuzzleBoardSize.largePresets) { "V3 needs a large board" }
        return SoloRun(theme, challenge.legacyDifficulty, seed,
            boardSize = board, challenge = challenge)
    }

    fun freshForBoard(theme: WordTheme, board: PuzzleBoardSize,
                      challenge: PuzzleChallenge, seed: Long): SoloRun =
        if (board in PuzzleBoardSize.largePresets) freshV3(theme, board, challenge, seed)
        else freshV2(theme, board, challenge, seed)

    /** Version dispatch is the only place that reconstructs a saved puzzle. */
    @Synchronized
    fun puzzle(run: SoloRun): WordSearchPuzzle {
        require((run.boardSize == null) == (run.challenge == null)) {
            "Partially specified v2 puzzle; preserve original save"
        }
        val id = run.puzzleId
        puzzleCache[id]?.let { return it }
        val generated = when (val board = run.boardSize) {
            null -> WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
            in PuzzleBoardSize.largePresets -> WordSearchGeneratorV3.generate(
                run.theme, board, requireNotNull(run.challenge), run.seed
            )
            else -> WordSearchGeneratorV2.generate(
                run.theme, board, requireNotNull(run.challenge), run.seed
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


/**
 * WS-09B: pure library operations. Retain active Solo as the selected session
 * for old screens/records, and archive every unfinished game before switching.
 * Never auto-trim saved games; a full library rejects new games until the
 * player deliberately discards a paused puzzle.
 */
object SoloSessionLibrary {
    const val MAX_PAUSED = 24

    fun finished(run: SoloRun): Boolean =
        WordSearchRules.complete(SoloProgressRules.puzzle(run), run.foundIds)

    fun verify(profile: SoloProfile) {
        require(profile.pausedSolo.size <= MAX_PAUSED) { "Too many paused Solo puzzles" }
        val allIds = listOf(profile.active.puzzleId) +
            profile.pausedSolo.map { it.puzzleId }
        require(allIds.distinct().size == allIds.size) {
            "Duplicated Solo puzzle in active and paused sessions"
        }
        profile.pausedSolo.forEach { run ->
            require(SoloProgressRules.validate(run) == run) {
                "Invalid progress in paused Solo puzzle"
            }
            require(!finished(run)) { "Completed Solo puzzle cannot be paused" }
        }
    }

    /** Returns null without altering anything if the library is full. */
    fun start(profile: SoloProfile, fresh: SoloRun): SoloProfile? {
        if (fresh.puzzleId == profile.active.puzzleId ||
            profile.pausedSolo.any { it.puzzleId == fresh.puzzleId }) return null
        val paused = if (finished(profile.active)) profile.pausedSolo
            else listOf(profile.active) + profile.pausedSolo
        if (paused.size > MAX_PAUSED) return null
        return profile.copy(active = fresh, pausedSolo = paused)
            .also(::verify)
    }

    /** Switching is a swap: the previously active unfinished puzzle is kept. */
    fun resume(profile: SoloProfile, puzzleId: String): SoloProfile? {
        val selected = profile.pausedSolo.firstOrNull { it.puzzleId == puzzleId }
            ?: return null
        val other = profile.pausedSolo.filterNot { it.puzzleId == puzzleId }
        val paused = if (finished(profile.active)) other
            else listOf(profile.active) + other
        return profile.copy(active = selected, pausedSolo = paused).also(::verify)
    }

    /** Destructive operation requires explicit UI confirmation, never implicit eviction. */
    fun discardPaused(profile: SoloProfile, puzzleId: String): SoloProfile? {
        if (profile.pausedSolo.none { it.puzzleId == puzzleId }) return null
        return profile.copy(pausedSolo = profile.pausedSolo.filterNot {
            it.puzzleId == puzzleId
        })
    }
}
