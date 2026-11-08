package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test
import ws09fc.lab.V4EngineLab

/**
 * Experimental 40/50 generator appears ONLY in core/src/test/.
 * It must not be included in core main, Android app, Solo dispatch or backup.
 */
class V4ResearchCoreCompatibilityTest {
    private fun fixture(): Map<String, List<String>> {
        val stream = requireNotNull(javaClass.getResourceAsStream("/ws09fc_v4_test_only_candidates.txt")) {
            "Missing frozen test-only candidate fixture"
        }
        return stream.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() && !it.startsWith("#") }
                .map { raw ->
                    val parts = raw.split('|', limit = 2)
                    require(parts.size == 2)
                    parts[0] to parts[1].split(' ').filter { it.isNotBlank() }
                }.toMap()
        }
    }

    private fun toCore(theme: WordTheme, challenge: PuzzleChallenge, seed: Long,
                       raw: V4EngineLab.Puzzle): WordSearchPuzzle =
        WordSearchPuzzle(
            id = raw.id,
            seed = seed,
            contentVersion = 4,
            theme = theme,
            difficulty = challenge.legacyDifficulty,
            rows = raw.rows,
            targets = raw.targets.map { t ->
                WordTarget(t.word, t.word, t.cells.map { GridPoint(it.row, it.col) })
            }
        )

    @Test fun experimentalBoardDataFitsTheRealWordSearchRulesWithoutChangingTheApp() {
        val packs = fixture()
        assertEquals(setOf("WOODLAND", "SPACE", "GARDEN"), packs.keys)
        var boards = 0
        var pathChecks = 0
        for (theme in WordTheme.entries) for (side in listOf(40, 50))
            for (mode in V4EngineLab.Mode.entries) for (seed in listOf(11L, 42L, 2026L)) {
                val challenge = PuzzleChallenge.valueOf(mode.name)
                val raw = V4EngineLab.generate(theme.name, side, mode, seed,
                    packs.getValue(theme.name))
                V4EngineLab.verify(raw, mode)
                val board = toCore(theme, challenge, seed, raw)
                assertEquals(4, board.contentVersion)
                assertEquals(side, board.size)
                assertEquals(if (side == 40) 70 else 105, board.targets.size)
                assertEquals(challenge.legacyDifficulty, board.difficulty)
                assertTrue(board.id.startsWith("wordsearch:v4:LAB:en:"))
                var found = emptySet<String>()
                board.targets.forEach { target ->
                    assertEquals(target.word, target.positions.mapNotNull(board::letter).joinToString(""))
                    assertEquals(target.positions, WordSearchRules.line(
                        target.positions.first(), target.positions.last(), side))
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                        board, found, target.positions.first(), target.positions.last()).status)
                    found = found + target.id
                    assertEquals(ClaimStatus.ALREADY_FOUND, WordSearchRules.claim(
                        board, found, target.positions.last(), target.positions.first()).status)
                    pathChecks++
                }
                assertTrue(WordSearchRules.complete(board, found))
                assertFalse(WordSearchRules.complete(board, found - board.targets.first().id))
                assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(
                    board, found, GridPoint(-1, 0), GridPoint(0, 0)).status)
                boards++
            }
        assertEquals(54, boards)
        assertEquals(4725, pathChecks) // 3 themes × 3 modes × 3 seeds × (70 + 105)
    }

    @Test fun noExperimentalSizesOrIdsLeakIntoExistingGamePresets() {
        assertEquals(listOf(8, 10, 12, 12, 15, 20, 25, 30),
            PuzzleBoardSize.entries.map { it.cells })
        assertEquals(listOf(12, 15, 20, 25, 30),
            PuzzleBoardSize.soloPresets.map { it.cells })
        assertEquals(listOf(25, 30),
            PuzzleBoardSize.largePresets.map { it.cells })
        assertEquals(5, PuzzleBoardSize.BOOK_BIG.ordinal)
        assertEquals(6, PuzzleBoardSize.BOOK_GRAND.ordinal)
        assertEquals(7, PuzzleBoardSize.BOOK_GIANT.ordinal)
        assertFalse(PuzzleBoardSize.entries.any { it.cells >= 40 })
        assertEquals(3, WordSearchGeneratorV3.CONTENT_VERSION)
        assertEquals(1, WordSearchGenerator.CONTENT_VERSION)
        assertEquals(2, WordSearchGeneratorV2.CONTENT_VERSION)
    }

    @Test fun invalidResearchInputFailsClosed() {
        assertTrue(runCatching {
            V4EngineLab.generate("SPACE", 75, V4EngineLab.Mode.RELAXED, 1L,
                fixture().getValue("SPACE"))
        }.isFailure)
        assertTrue(runCatching {
            V4EngineLab.validatePool(listOf("RIVER", "RIVERBANK"), 2)
        }.isFailure)
    }
}
