package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class WordSearchGeneratorV3Test {
    @Test fun v3IsIsolatedFromOldGenerators() {
        assertEquals(3, WordSearchGeneratorV3.CONTENT_VERSION)
        assertEquals(listOf(12,15,20), PuzzleBoardSize.bookPresets.map { it.cells })
        assertEquals(listOf(25,30), PuzzleBoardSize.largePresets.map { it.cells })
        assertEquals(listOf(32,44), PuzzleBoardSize.largePresets.map { it.targets })
        assertEquals(5, PuzzleBoardSize.BOOK_BIG.ordinal)
        WordTheme.entries.forEach { assertTrue(WordLibraryV3.count(it) >= 65) }
        val old = WordSearchGeneratorV2.generate(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC, 42L)
        assertEquals("75f96dab9a80714d393abc071a125c2c7477f4961f4d8ada2dbb8a18eb97f42c",
            digest(old))
        assertTrue(runCatching { WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_GIANT,
            PuzzleChallenge.CLASSIC, 42L) }.isFailure)
    }

    @Test fun allThemesChallengesNewSizesAndSeedsRemainPlayableAndDeterministic() {
        for (theme in WordTheme.entries)
            for (board in PuzzleBoardSize.largePresets)
                for (challenge in PuzzleChallenge.entries)
                    for (seed in listOf(1L,42L,76124L)) {
                        val start = System.nanoTime()
                        val run = SoloProgressRules.freshV3(theme,board,challenge,seed)
                        val puzzle = SoloProgressRules.puzzle(run)
                        val elapsedMs = (System.nanoTime()-start)/1_000_000
                        assertTrue("Generation exceeded 5s for $theme/$board/$challenge/$seed: ${elapsedMs}ms",elapsedMs < 5000)
                        assertEquals(board.cells,puzzle.size)
                        assertEquals(board.targets,puzzle.targets.size)
                        assertEquals(3,puzzle.contentVersion)
                        assertEquals(puzzle,WordSearchGeneratorV3.generate(theme,board,challenge,seed))
                        assertTrue(puzzle.rows.all { it.length==board.cells && it.all { c-> c in 'A'..'Z' } })
                        assertEquals(board.targets,puzzle.targets.map { it.id }.toSet().size)
                        val found=mutableSetOf<String>()
                        puzzle.targets.forEach { target ->
                            assertTrue(target.word.length in 5..16)
                            assertEquals(target.word,target.positions.mapNotNull(puzzle::letter).joinToString(""))
                            assertEquals(target.word.length,target.positions.size)
                            assertTrue(target.positions.all { it.row in 0 until board.cells && it.col in 0 until board.cells })
                            assertEquals(ClaimStatus.FOUND,WordSearchRules.claim(puzzle,found,
                                target.positions.first(),target.positions.last()).status)
                            found += target.id
                        }
                        assertTrue(WordSearchRules.complete(puzzle,found))
                    }
    }

    @Test fun newRunsRestoreExactV3SeedFoundWordsAndHints() {
        val run=SoloProgressRules.freshForBoard(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.EXPERT,20261108L)
        assertEquals("wordsearch:v3:en:GARDEN:BOOK_GIANT:EXPERT:20261108",run.puzzleId)
        val puzzle=SoloProgressRules.puzzle(run)
        assertEquals(44,puzzle.targets.size)
        assertEquals(8,WordHintPolicy.allowance(run))
        val first=puzzle.targets.first()
        val found=SoloProgressRules.find(run,first.positions.first(),first.positions.last()).first
        val hinted=SoloProgressRules.hint(found).first
        assertEquals(setOf(first.id),hinted.foundIds)
        assertTrue(hinted.hints.isNotEmpty())
        assertEquals(hinted,SoloProgressRules.validate(hinted))
        assertEquals(puzzle,SoloProgressRules.puzzle(hinted))
    }

    @Test fun captureThreeNewV3ReferenceBoardsBeforePinning() {
        // Temporary acquisition; replace with immutable literals before acceptance.
        val examples=listOf(
            WordSearchGeneratorV3.generate(WordTheme.WOODLAND,PuzzleBoardSize.BOOK_GRAND,PuzzleChallenge.RELAXED,42L),
            WordSearchGeneratorV3.generate(WordTheme.SPACE,PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.CLASSIC,20261108L),
            WordSearchGeneratorV3.generate(WordTheme.GARDEN,PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.EXPERT,76124L))
        examples.forEach { p ->
            println("WS09E_CAPTURE|${p.id}|${digest(p)}|${p.targets.take(2).joinToString(","){it.id}}")
        }
    }

    private fun digest(p: WordSearchPuzzle):String {
        val canonical=buildString {
            append(p.id).append('\n');append(p.contentVersion).append('\n')
            p.rows.forEach { append(it).append('\n') }
            append("--targets--\n")
            p.targets.forEach { t ->
                append(t.id).append('|').append(t.word).append('|')
                append(t.positions.joinToString(";"){"${it.row},${it.col}"})
                append('\n')
            }
        }
        return MessageDigest.getInstance("SHA-256")
            .digest(canonical.toByteArray(Charsets.UTF_8))
            .joinToString(""){"%02x".format(it.toInt() and 255)}
    }
}
