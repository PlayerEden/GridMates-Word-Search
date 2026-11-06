package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class WordSearchGeneratorV2Test {
    @Test fun expandedPacksLeaveOldPuzzlesUntouched() {
        WordTheme.entries.forEach {
            assertTrue(WordLibraryV2.count(it) >= 30)
            assertEquals(14, it.words.size)
        }
        assertEquals(1, WordSearchGenerator.CONTENT_VERSION)
    }

    @Test fun sizeAndChallengeAreIndependentAndGeneratedWordsAreFindable() {
        for (theme in WordTheme.entries)
            for (board in PuzzleBoardSize.entries)
                for (challenge in PuzzleChallenge.entries)
                    for (seed in listOf(1L, 43L, 2002L, 10991L)) {
                        val p = try {
                            WordSearchGeneratorV2.generate(theme, board, challenge, seed)
                        } catch (ex: Exception) {
                            throw IllegalStateException(
                                "V2 generation failed: $theme / $board / $challenge / seed=$seed", ex
                            )
                        }
                        assertEquals(board.cells, p.size)
                        assertEquals(board.targets, p.targets.size)
                        assertEquals(2, p.contentVersion)
                        assertEquals(p, WordSearchGeneratorV2.generate(theme, board, challenge, seed))
                        val found = mutableSetOf<String>()
                        for (target in p.targets) {
                            assertEquals(target.word, target.positions.map { p.letter(it) }.joinToString(""))
                            assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                                p, found, target.positions.first(), target.positions.last()).status)
                            found += target.id
                        }
                        assertTrue(WordSearchRules.complete(p, found))
                    }
    }

    @Test fun oldSeedAndDailyRemainLegacyV1() {
        val old = SoloProgressRules.fresh(WordTheme.WOODLAND, WordDifficulty.EASY, 42)
        assertEquals(WordSearchGenerator.generate(seed=42), SoloProgressRules.puzzle(old))
        assertEquals("wordsearch:v1:WOODLAND:EASY:42", old.puzzleId)
        val daily = DailyChallenge.start("2026-11-06")
        assertNull(daily.run.boardSize)
        assertEquals(1, SoloProgressRules.puzzle(daily.run).contentVersion)
    }

    @Test fun v2CompletionUsesVersionedIdentityAndPersistsItsOptions() {
        val run = SoloProgressRules.freshV2(
            WordTheme.GARDEN,PuzzleBoardSize.COMPACT,PuzzleChallenge.EXPERT,471L)
        val p = SoloProgressRules.puzzle(run)
        val complete = run.copy(foundIds=p.targets.map { it.id }.toSet(),elapsedSeconds=86)
        val profile = SoloProgressRules.complete(SoloProfile(complete),123456L)
        assertEquals(1,profile.completions)
        assertEquals(PuzzleBoardSize.COMPACT,profile.results[0].boardSize)
        assertEquals(PuzzleChallenge.EXPERT,profile.results[0].challenge)
        assertEquals(1,SoloProgressRules.complete(profile,1234567L).completions)
        assertTrue(SoloProgressRules.validate(run.copy(foundIds=setOf("INVALID"))).foundIds.isEmpty())
    }
}
