package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.*
import org.junit.Test

class PuzzleResultShareTest {
    @Test fun soloSummaryIsHonestAndDoesNotRevealPuzzleOrIdentity() {
        val run = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 12900L
        )
        val puzzle = SoloProgressRules.puzzle(run)
        val result = PuzzleResultShare.text(puzzle, run, 125L, daily = false)
        assertTrue(result.contains("GridMates: Word Search"))
        assertTrue(result.contains("Solo Puzzle complete!"))
        assertTrue(result.contains("Woodland"))
        assertTrue(result.contains(puzzle.targets.size.toString() + " words found · 2:05"))
        assertTrue(result.contains("No hints used"))
        assertFalse(result.contains(run.seed.toString()))
        assertFalse(result.contains(puzzle.id))
        assertFalse(result.contains(puzzle.targets.first().word))
        assertFalse(result.contains("room"))
        assertFalse(result.contains("Player"))
    }

    @Test fun dailySummaryShowsAssistanceWithoutInventedScoreOrStreak() {
        val run = SoloProgressRules.freshV2(
            WordTheme.SPACE, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 18000L
        ).copy(hints = mapOf("test" to 2))
        val result = PuzzleResultShare.text(
            SoloProgressRules.puzzle(run), run, 67L, daily = true)
        assertTrue(result.contains("Daily Puzzle complete!"))
        assertTrue(result.contains("Outer Space"))
        assertTrue(result.contains("1:07"))
        assertTrue(result.contains("2 hint steps used"))
        assertFalse(result.contains("100 points"))
        assertFalse(result.contains("streak"))
    }

    @Test fun zeroAndSingularHintAreFormattedCorrectly() {
        val run = SoloProgressRules.freshV2(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 28000L
        )
        val p = SoloProgressRules.puzzle(run)
        assertTrue(PuzzleResultShare.text(p, run, -1, false).contains("0:00"))
        assertTrue(PuzzleResultShare.text(p, run.copy(hints = mapOf("test" to 1)),
            60, false).contains("1 hint step used"))
    }
}
