package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class WordSearchTest {
    @Test fun allEightDirectionsWorkAndBentOrOutsidePathsFail() {
        val center = GridPoint(4, 4)
        for (dr in -1..1) for (dc in -1..1) {
            if (dr == 0 && dc == 0) continue
            val end = GridPoint(4 + dr * 3, 4 + dc * 3)
            val path = WordSearchRules.line(center, end, 10)
            assertEquals(4, path.size)
            assertEquals(center, path.first())
            assertEquals(end, path.last())
        }
        assertTrue(WordSearchRules.line(center, GridPoint(6, 7), 10).isEmpty())
        assertTrue(WordSearchRules.line(GridPoint(-1, 2), center, 10).isEmpty())
        assertEquals(listOf(center), WordSearchRules.line(center, center, 10))
    }

    @Test fun seedReproducibilityAndBoardSize() {
        val a = WordSearchGenerator.generate(seed = 29401)
        val b = WordSearchGenerator.generate(seed = 29401)
        val c = WordSearchGenerator.generate(seed = 29402)
        assertEquals(a, b)
        assertNotEquals(a.rows, c.rows)
        assertEquals(6, a.targets.size)
        assertEquals(8, a.rows.size)
        assertTrue(a.rows.all { it.length == a.size })
    }

    @Test fun targetsFindableInBothDirectionsAndOnlyOnce() {
        for (theme in WordTheme.entries) for (difficulty in WordDifficulty.entries)
            for (seed in listOf(1L, 32L, 6001L)) {
                val p = WordSearchGenerator.generate(theme, difficulty, seed)
                assertEquals(difficulty.targetCount, p.targets.size)
                val found = mutableSetOf<String>()
                for (target in p.targets) {
                    assertEquals(target.word, target.positions.map { p.letter(it) }.joinToString(""))
                    val first = target.positions.first()
                    val last = target.positions.last()
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(p, found, last, first).status)
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(p, found, first, last).status)
                    found.add(target.id)
                    assertEquals(ClaimStatus.ALREADY_FOUND, WordSearchRules.claim(p, found, first, last).status)
                }
                assertTrue(WordSearchRules.complete(p, found))
                assertFalse(WordSearchRules.complete(p, found - p.targets.first().id))
            }
    }

    @Test fun invalidPathsNeverAward() {
        val p = WordSearchGenerator.generate(seed = 8L)
        assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(
            p, emptySet(), GridPoint(0, 0), GridPoint(0, 0)).status)
        assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(
            p, emptySet(), GridPoint(0, 0), GridPoint(2, 3)).status)
        assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(
            p, emptySet(), GridPoint(-1, 0), GridPoint(2, 0)).status)
    }

    @Test fun generatorIsBoundedAndUppercaseAcrossSeedsAndPacks() {
        for (i in 0L until 35L) {
            val p = WordSearchGenerator.generate(
                WordTheme.entries[(i % WordTheme.entries.size).toInt()],
                WordDifficulty.entries[(i % WordDifficulty.entries.size).toInt()],
                i)
            assertEquals(p.targets.size, p.targets.map { it.id }.distinct().size)
            assertTrue(p.rows.all { row -> row.all { it in 'A'..'Z' } })
        }
    }
}
