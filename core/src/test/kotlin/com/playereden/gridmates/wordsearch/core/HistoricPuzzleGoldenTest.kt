package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS-09A2 historical golden manifest.
 *
 * Initial acquisition captures digests from the frozen v0.8.6 v1/v2 engine.
 * Once confirmed, literal digests below must replace dynamic expectations.
 * Future engine/content changes must NEVER silently re-generate old saves.
 */
class HistoricPuzzleGoldenTest {
    private data class Fixture(val name: String, val puzzle: WordSearchPuzzle)

    private fun fixtures(): List<Fixture> = listOf(
        Fixture("v1-woodland-easy-42",
            WordSearchGenerator.generate(WordTheme.WOODLAND, WordDifficulty.EASY, 42L)),
        Fixture("v1-garden-hard-6001",
            WordSearchGenerator.generate(WordTheme.GARDEN, WordDifficulty.HARD, 6001L)),
        Fixture("v2-woodland-compact-471",
            WordSearchGeneratorV2.generate(WordTheme.WOODLAND, PuzzleBoardSize.COMPACT,
                PuzzleChallenge.CLASSIC, 471L)),
        Fixture("v2-garden-standard-20261106",
            WordSearchGeneratorV2.generate(WordTheme.GARDEN, PuzzleBoardSize.STANDARD,
                PuzzleChallenge.RELAXED, 20261106L)),
        Fixture("v2-space-bookshort-20261106",
            WordSearchGeneratorV2.generate(WordTheme.SPACE, PuzzleBoardSize.BOOK_SHORT,
                PuzzleChallenge.CLASSIC, 20261106L)),
        Fixture("v2-woodland-bookclassic-42",
            WordSearchGeneratorV2.generate(WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
                PuzzleChallenge.CLASSIC, 42L)),
        Fixture("v2-garden-bookbig-76124",
            WordSearchGeneratorV2.generate(WordTheme.GARDEN, PuzzleBoardSize.BOOK_BIG,
                PuzzleChallenge.CLASSIC, 76124L)),
        Fixture("daily-2026-11-05",
            SoloProgressRules.puzzle(DailyChallenge.dailyRun("2026-11-05"))),
        Fixture("daily-2026-11-06",
            SoloProgressRules.puzzle(DailyChallenge.dailyRun("2026-11-06")))
    )

    private fun sha256(p: WordSearchPuzzle): String {
        val canonical = buildString {
            append(p.id).append('\n')
            append(p.contentVersion).append('\n')
            p.rows.forEach { append(it).append('\n') }
            append("--targets--\n")
            p.targets.forEach { target ->
                append(target.id).append('|').append(target.word).append('|')
                append(target.positions.joinToString(";") { "${it.row},${it.col}" })
                append('\n')
            }
        }
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(canonical.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it.toInt() and 255) }
    }

    @Test fun acquireHistoricalGoldenManifestFromUnmodifiedV1AndV2() {
        val items = fixtures()
        assertEquals(9, items.size)
        items.forEach { item ->
            assertTrue(item.puzzle.rows.isNotEmpty())
            assertTrue(item.puzzle.targets.isNotEmpty())
            // Baseline capture from the unchanged WS-09A1 v0.8.6 engine.
            // Replace output with immutable literal expectations next commit.
            println("WS09A2_CAPTURE|${item.name}|${sha256(item.puzzle)}|" +
                item.puzzle.targets.take(2).joinToString(",") { it.id } +
                "|${item.puzzle.id}")
        }
    }
}
