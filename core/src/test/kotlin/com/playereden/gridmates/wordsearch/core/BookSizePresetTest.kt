package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BookSizePresetTest {
    @Test fun paperbackPresetsStartAtTwelveAndScaleIndependentlyOfDifficulty() {
        assertEquals(listOf(12, 15, 20), PuzzleBoardSize.bookPresets.map { it.cells })
        assertEquals(listOf(12, 16, 24), PuzzleBoardSize.bookPresets.map { it.targets })
        assertEquals(0, PuzzleBoardSize.COMPACT.ordinal)
        assertEquals(1, PuzzleBoardSize.STANDARD.ordinal)
        assertEquals(2, PuzzleBoardSize.LARGE.ordinal)
        assertEquals(8, PuzzleBoardSize.COMPACT.cells)
        assertEquals(10, PuzzleBoardSize.STANDARD.cells)
        assertEquals(12, PuzzleBoardSize.LARGE.cells)
        assertTrue(PuzzleBoardSize.bookPresets.all { it.cells >= 12 })
    }

    @Test fun bookSizesGenerateAllChallengesAndAreFullySolvable() {
        for (theme in WordTheme.entries) {
            for (board in PuzzleBoardSize.bookPresets) {
                for (challenge in PuzzleChallenge.entries) {
                    val seed = 20261106L + board.cells * 13 + challenge.ordinal
                    val puzzle = WordSearchGeneratorV2.generate(theme, board, challenge, seed)
                    assertEquals(board.cells, puzzle.size)
                    assertEquals(board.targets, puzzle.targets.size)
                    assertTrue(puzzle.targets.all { it.word.length <= board.cells })
                    assertEquals(puzzle, WordSearchGeneratorV2.generate(theme, board, challenge, seed))
                    val found = mutableSetOf<String>()
                    for (target in puzzle.targets) {
                        assertEquals(ClaimStatus.FOUND,
                            WordSearchRules.claim(puzzle, found,
                                target.positions.first(), target.positions.last()).status)
                        found += target.id
                    }
                    assertTrue(WordSearchRules.complete(puzzle, found))
                }
            }
        }
    }

    @Test fun originalV2SavedBoardIdentifiersRemainStable() {
        for (old in listOf(PuzzleBoardSize.COMPACT, PuzzleBoardSize.STANDARD, PuzzleBoardSize.LARGE)) {
            val run = SoloProgressRules.freshV2(
                WordTheme.WOODLAND, old, PuzzleChallenge.CLASSIC, 471L)
            val oldBoard = SoloProgressRules.puzzle(run)
            assertEquals(old.cells, oldBoard.size)
            assertEquals(old.targets, oldBoard.targets.size)
            assertEquals("wordsearch:v2:en:WOODLAND:${old.name}:CLASSIC:471", run.puzzleId)
        }
        assertEquals(8, SoloProgressRules.puzzle(
            SoloProgressRules.fresh(WordTheme.WOODLAND, WordDifficulty.EASY, 42L)
        ).size)
    }
}
