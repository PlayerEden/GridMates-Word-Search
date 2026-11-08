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

    // Fingerprints acquired from unchanged v0.8.6/code40 source based on
    // the exact verified WS-09A1 executable. Never calculate these expected
    // values from the generator under test.
    private data class Golden(val digest: String, val first: String, val second: String)
    private val expected = mapOf(
        "v1-woodland-easy-42" to Golden(
            "d2a14dac7c4a5172cc82784550a5dae6e02c70828658d299c73e55440cbec8be",
            "SQUIRREL", "BADGER"),
        "v1-garden-hard-6001" to Golden(
            "698d8840af14d0bd0b923d9fd9fddbda67f9e29907f7dfb2d73d5767bdb85ab2",
            "DAISY", "TULIP"),
        "v2-woodland-compact-471" to Golden(
            "a1f1db7f94b01a6cbdd6c3dff88ae98d7b3e128530634c21f8f4aa8d3baeea03",
            "SPRUCE", "HOLLOW"),
        "v2-garden-standard-20261106" to Golden(
            "d6372a8e3fec6079e4fe1ca4923771ea2364152c2cbe3cf99c4145a919fd2248",
            "SHOVEL", "GARDEN"),
        "v2-space-bookshort-20261106" to Golden(
            "3ac878828ac41702e38133a979f3c435f51ff30b499b7fb0443a762763db265a",
            "SHUTTLE", "CRATER"),
        "v2-woodland-bookclassic-42" to Golden(
            "75f96dab9a80714d393abc071a125c2c7477f4961f4d8ada2dbb8a18eb97f42c",
            "RACCOON", "SAPLING"),
        "v2-garden-bookbig-76124" to Golden(
            "b72723ecf722c966073ad084eb0e5d628d110aca9dee176e7f664db96810226d",
            "BUTTERFLY", "LAVENDER"),
        "daily-2026-11-05" to Golden(
            "c21e775648cffd126af91a430ecc9ac9bef0685c04e59f6477039cab821c2cf9",
            "PEONY", "BLOOM"),
        "daily-2026-11-06" to Golden(
            "40ca41a34e75be751b68fb541efe0926a1c2a0d28f3ec06d4c4fe62198b8bf91",
            "BADGER", "MOUSE")
    )

    @Test fun archivedLayoutsAndPlacementsNeverChangeAcrossUpdates() {
        val items = fixtures()
        assertEquals(9, items.size)
        assertEquals(expected.keys, items.map { it.name }.toSet())
        items.forEach { item ->
            val golden = requireNotNull(expected[item.name])
            assertTrue(item.puzzle.rows.isNotEmpty())
            assertTrue(item.puzzle.targets.isNotEmpty())
            assertEquals("${item.name}: exact rows, target IDs, direction and cells changed",
                golden.digest, sha256(item.puzzle))
            assertEquals("${item.name}: target order changed",
                listOf(golden.first, golden.second),
                item.puzzle.targets.take(2).map { it.id })
        }
    }
}
