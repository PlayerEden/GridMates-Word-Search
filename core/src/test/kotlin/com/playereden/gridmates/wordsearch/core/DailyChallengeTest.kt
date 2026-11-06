package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DailyChallengeTest {
    @Test fun canonicalDateIsStableAcrossReopenAndNewDaysChangeSeed() {
        val first = DailyChallenge.dailyRun("2026-11-05")
        assertEquals(first, DailyChallenge.dailyRun("2026-11-05"))
        assertEquals(WordDifficulty.EASY, first.difficulty)
        assertEquals(WordDifficulty.EASY, DailyChallenge.dailyRun("2026-11-06").difficulty)
        assertNotEquals(first.puzzleId, DailyChallenge.dailyRun("2026-11-06").puzzleId)
        assertEquals(first, DailyChallenge.start("2026-11-05").run)
    }

    @Test fun invalidAndAmbiguousDatesAreRejected() {
        for (invalid in listOf("2026-1-5", "2026-13-01", "2026-02-30",
                "today", "2026-11-05T00:00:00", "2026/11/05")) {
            var rejected = false
            try { DailyChallenge.day(invalid) }
            catch (ex: IllegalArgumentException) { rejected = true }
            assertTrue("Expected rejected $invalid", rejected)
        }
        assertEquals(LocalDate.of(2026, 11, 5), DailyChallenge.day("2026-11-05"))
    }

    @Test fun completionNeverAwardsTwiceAndIncompleteIsNotCompleted() {
        val date = "2026-11-05"
        val active = DailyChallenge.start(date)
        assertFalse(DailyChallenge.complete(active, 10L).completed)
        val puzzle = WordSearchGenerator.generate(active.run.theme,
            active.run.difficulty, active.run.seed)
        val all = active.copy(run = active.run.copy(
            foundIds = puzzle.targets.map { it.id }.toSet()
        ))
        val done = DailyChallenge.complete(all, 1000L)
        assertTrue(done.completed)
        assertEquals(1000L, done.completedAtMs)
        assertEquals(1000L, DailyChallenge.complete(done, 8000L).completedAtMs)
        assertEquals(done, DailyChallenge.validate(done))
    }

    @Test fun mismatchedDateCannotClaimAnotherDailyBoard() {
        val november = DailyChallenge.start("2026-11-05")
        val swapped = november.copy(date = "2026-11-06")
        var rejected = false
        try { DailyChallenge.validate(swapped) }
        catch (ex: IllegalArgumentException) { rejected = true }
        assertTrue(rejected)
    }

    @Test fun unfinishedPriorDateRemainsIndependentlyReconstructible() {
        val yesterday = DailyChallenge.start("2026-11-04")
        val today = DailyChallenge.start("2026-11-05")
        val board = WordSearchGenerator.generate(yesterday.run.theme,
            yesterday.run.difficulty, yesterday.run.seed)
        val first = board.targets.first()
        val progressed = yesterday.copy(run = yesterday.run.copy(foundIds = setOf(first.id)))
        assertEquals(progressed, DailyChallenge.validate(progressed))
        assertTrue(today.run.puzzleId != progressed.run.puzzleId)
    }

    @Test fun aWeekOfDatesGenerateAndValidateValidBoards() {
        for (offset in 0..6) {
            val date = LocalDate.of(2026, 11, 5).plusDays(offset.toLong()).toString()
            val e = DailyChallenge.start(date)
            val p = WordSearchGenerator.generate(e.run.theme, e.run.difficulty, e.run.seed)
            assertEquals(6, p.targets.size)
            assertEquals(8, p.size)
            assertTrue(p.targets.all { it.positions.size == it.word.length })
        }
    }
}
