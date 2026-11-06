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
    val elapsedSeconds: Long = 0L
) {
    val puzzleId: String get() = "wordsearch:v1:" + theme.name + ":" + difficulty.name + ":" + seed
}

data class SoloResult(
    val puzzleId: String,
    val theme: WordTheme,
    val difficulty: WordDifficulty,
    val wordCount: Int,
    val elapsedSeconds: Long,
    val hintSteps: Int,
    val completedAtMs: Long
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

object SoloProgressRules {
    fun fresh(
        theme: WordTheme = WordTheme.WOODLAND,
        difficulty: WordDifficulty = WordDifficulty.EASY,
        seed: Long
    ): SoloRun = SoloRun(theme, difficulty, seed)

    fun validate(run: SoloRun): SoloRun {
        val puzzle = WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
        val allowed = puzzle.targets.map { it.id }.toSet()
        return run.copy(
            foundIds = run.foundIds.intersect(allowed),
            hints = run.hints.filter { (id, level) -> id in allowed && level in 1..3 },
            elapsedSeconds = run.elapsedSeconds.coerceIn(0L, 365L * 24 * 3600)
        )
    }

    fun find(run: SoloRun, start: GridPoint, end: GridPoint): Pair<SoloRun, WordClaim> {
        val puzzle = WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
        val claim = WordSearchRules.claim(puzzle, run.foundIds, start, end)
        return if (claim.status == ClaimStatus.FOUND) {
            run.copy(foundIds = run.foundIds + requireNotNull(claim.target).id) to claim
        } else run to claim
    }

    /**
     * Three-step help ladder, always selecting an unfinished word:
     * zone -> starting cell -> full path preview. No auto-completion.
     */
    fun hint(run: SoloRun): Pair<SoloRun, WordTarget?> {
        val puzzle = WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
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
        val puzzle = WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
        if (!WordSearchRules.complete(puzzle, run.foundIds)) return profile
        if (profile.results.any { it.puzzleId == run.puzzleId }) return profile
        val record = SoloResult(
            puzzleId = run.puzzleId,
            theme = run.theme,
            difficulty = run.difficulty,
            wordCount = puzzle.targets.size,
            elapsedSeconds = run.elapsedSeconds,
            hintSteps = run.hints.values.sum(),
            completedAtMs = nowMs
        )
        return profile.copy(results = listOf(record) + profile.results)
    }
}
