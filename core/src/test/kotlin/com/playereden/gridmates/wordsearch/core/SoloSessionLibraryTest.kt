package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class SoloSessionLibraryTest {
    @Test fun newAndResumeRetainEveryUnfinishedPuzzleExactly() {
        val a = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC, 42L)
        val puzzle = SoloProgressRules.puzzle(a)
        val partial = a.copy(
            foundIds = setOf(puzzle.targets[0].id),
            hints = mapOf(puzzle.targets[1].id to 2),
            elapsedSeconds = 734L)
        val b = SoloProgressRules.freshV2(WordTheme.SPACE,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 20261106L)
        val c = SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC, 76124L)
        val first = requireNotNull(SoloSessionLibrary.start(SoloProfile(partial), b))
        assertEquals(listOf(partial), first.pausedSolo)
        val second = requireNotNull(SoloSessionLibrary.start(first, c))
        assertEquals(listOf(b, partial), second.pausedSolo)
        val resumed = requireNotNull(SoloSessionLibrary.resume(second, partial.puzzleId))
        assertEquals(partial, resumed.active)
        assertEquals(listOf(c, b), resumed.pausedSolo)
        assertEquals(puzzle, SoloProgressRules.puzzle(resumed.active))
        assertNull(SoloSessionLibrary.resume(resumed, "nonexistent"))
        assertNull(SoloSessionLibrary.start(resumed, resumed.active))
    }

    @Test fun finishedPuzzleDoesNotConsumeASaveSlotAndDiscardIsExplicit() {
        val a = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC, 42L)
        val p = SoloProgressRules.puzzle(a)
        val completed = a.copy(foundIds = p.targets.map { it.id }.toSet())
        val b = SoloProgressRules.freshV2(WordTheme.SPACE,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 20261106L)
        val after = requireNotNull(SoloSessionLibrary.start(SoloProfile(completed), b))
        assertTrue(after.pausedSolo.isEmpty())
        assertNull(SoloSessionLibrary.discardPaused(after, "unknown"))
        val c = SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC, 76124L)
        val saved = requireNotNull(SoloSessionLibrary.start(after, c))
        val removed = requireNotNull(SoloSessionLibrary.discardPaused(saved, b.puzzleId))
        assertTrue(removed.pausedSolo.isEmpty())
        assertEquals(c, removed.active)
        assertEquals(listOf(b), saved.pausedSolo) // immutable original
    }

    @Test fun fullLibraryRejectsNewWithoutEvictionAndResumeStillWorks() {
        val active = SoloProgressRules.fresh(WordTheme.WOODLAND,
            WordDifficulty.EASY, 42L)
        val paused = (1..SoloSessionLibrary.MAX_PAUSED).map { n ->
            SoloProgressRules.fresh(WordTheme.GARDEN, WordDifficulty.EASY, 500L + n)
        }
        val profile = SoloProfile(active = active, pausedSolo = paused)
        SoloSessionLibrary.verify(profile)
        val fresh = SoloProgressRules.fresh(WordTheme.SPACE,
            WordDifficulty.EASY, 6000L)
        assertNull(SoloSessionLibrary.start(profile, fresh))
        assertEquals(paused, profile.pausedSolo)
        val selected = requireNotNull(SoloSessionLibrary.resume(profile, paused.last().puzzleId))
        assertEquals(SoloSessionLibrary.MAX_PAUSED, selected.pausedSolo.size)
        assertEquals(paused.last(), selected.active)
        val duplicate = profile.copy(pausedSolo = listOf(active) + paused.drop(1))
        assertTrue(runCatching { SoloSessionLibrary.verify(duplicate) }.isFailure)
    }
}
