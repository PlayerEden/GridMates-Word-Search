package com.playereden.gridmates.wordsearch.core

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Device-local calendar date policy: yyyy-MM-dd in the user's current timezone
 * when they OPEN the daily challenge. The date stays pinned while they play.
 * No network/account/synchronized clock is required.
 */
data class DailyProgress(
    val date: String,
    val run: SoloRun,
    val completedAtMs: Long? = null
) {
    val completed: Boolean get() = completedAtMs != null
}

object DailyChallenge {
    const val VERSION = 1

    fun today(): String = LocalDate.now().toString()

    fun day(raw: String): LocalDate {
        require(raw.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            "Daily date must use ISO yyyy-MM-dd"
        }
        return try { LocalDate.parse(raw) }
        catch (ex: DateTimeParseException) { throw IllegalArgumentException("Invalid date", ex) }
    }

    /** Same date, puzzle content version and app release => same grid on every device. */
    fun dailyRun(date: String): SoloRun {
        val epochDay = day(date).toEpochDay()
        val themes = listOf(WordTheme.WOODLAND, WordTheme.SPACE, WordTheme.GARDEN)
        val theme = themes[Math.floorMod(epochDay, themes.size.toLong()).toInt()]
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("gridmates-wordsearch-daily:v$VERSION:$date".toByteArray(Charsets.UTF_8))
        val seed = ByteBuffer.wrap(digest.copyOfRange(0, 8)).long
        return SoloProgressRules.fresh(theme, WordDifficulty.EASY, seed)
    }

    fun start(date: String): DailyProgress = DailyProgress(date, dailyRun(date))

    fun validate(entry: DailyProgress): DailyProgress {
        // Published version-1 Daily puzzles remain English and immutable.
        require(entry.run.boardSize == null && entry.run.challenge == null) {
            "A new-generator puzzle cannot overwrite an English v1 Daily"
        }
        val expected = dailyRun(entry.date)
        require(entry.run.theme == expected.theme &&
            entry.run.difficulty == expected.difficulty &&
            entry.run.seed == expected.seed) { "Daily challenge identity mismatch" }
        val safe = SoloProgressRules.validate(entry.run)
        val p = WordSearchGenerator.generate(safe.theme, safe.difficulty, safe.seed)
        val completedAt = entry.completedAtMs?.takeIf {
            it > 0 && WordSearchRules.complete(p, safe.foundIds)
        }
        return entry.copy(run = safe, completedAtMs = completedAt)
    }

    /**
     * Records completion on the first full solve and never increments it again.
     * This is NOT a paid reward or streak currency.
     */
    fun complete(entry: DailyProgress, timestampMs: Long): DailyProgress {
        if (entry.completed) return entry
        val puzzle = WordSearchGenerator.generate(
            entry.run.theme, entry.run.difficulty, entry.run.seed
        )
        return if (WordSearchRules.complete(puzzle, entry.run.foundIds)) {
            entry.copy(completedAtMs = timestampMs)
        } else entry
    }
}
