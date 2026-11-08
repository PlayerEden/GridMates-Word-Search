package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

/** Review-pending candidates are loaded from core TEST resources, never the app. */
class V4PreviewPlacementCoreTest {
    private fun fixture(): Map<String, List<String>> =
        requireNotNull(javaClass.getResourceAsStream("/ws09fc_v4_test_only_candidates.txt"))
            .bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith('#') }
                    .associate { line ->
                        val parts = line.split('|', limit = 2)
                        require(parts.size == 2)
                        parts[0] to parts[1].split(' ').filter { it.isNotEmpty() }
                    }
            }

    private fun gridFingerprint(rows: List<String>): String =
        MessageDigest.getInstance("SHA-256")
            .digest(rows.joinToString("\n").toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 255) }.take(16)

    // Literal digests frozen independently before promotion into the core.
    private val seed11Goldens = mapOf(
        "GARDEN|40|RELAXED" to "860aa1e4a4746dd4",
        "GARDEN|40|CLASSIC" to "57150c1cf8a7d5ad",
        "GARDEN|40|EXPERT" to "7a5e44bbbdfb12a6",
        "GARDEN|50|RELAXED" to "fdbc1870305df456",
        "GARDEN|50|CLASSIC" to "e00dd78f276ae077",
        "GARDEN|50|EXPERT" to "7787d47528221b69",
        "SPACE|40|RELAXED" to "aea7cfa29d4a871a",
        "SPACE|40|CLASSIC" to "3059e50feb7f20dd",
        "SPACE|40|EXPERT" to "0dfcec549963d92e",
        "SPACE|50|RELAXED" to "98e50184357c1d49",
        "SPACE|50|CLASSIC" to "8679de2b848aeae4",
        "SPACE|50|EXPERT" to "11393082e6ab9087",
        "WOODLAND|40|RELAXED" to "72b4bf61f1dea9db",
        "WOODLAND|40|CLASSIC" to "c66801453c857077",
        "WOODLAND|40|EXPERT" to "e91f5ef432133e52",
        "WOODLAND|50|RELAXED" to "fa8f502556230716",
        "WOODLAND|50|CLASSIC" to "ff0f98bc139b3886",
        "WOODLAND|50|EXPERT" to "6d180338ef349c5f"
    )

    @Test fun allPreviewConfigsMatchLabGoldensAndRealCoreClaimRules() {
        val pools = fixture()
        assertEquals(setOf("WOODLAND", "SPACE", "GARDEN"), pools.keys)
        var checked = 0
        var claims = 0
        val seeds = listOf(11L, 42L, 2026L, 0L, 1L, 7L, 19L, 77L, 144L, 999L)
        for (theme in WordTheme.entries) for (side in listOf(40, 50))
            for (mode in PuzzleChallenge.entries) for (seed in seeds) {
                val pack = pools.getValue(theme.name)
                val p = V4PreviewPlacement.previewPuzzle(theme, side, mode, seed, pack)
                assertTrue(p.id.startsWith("wordsearch:v4:PREVIEW:en:"))
                assertFalse(p.id.contains(":LAB:"))
                assertEquals(4, p.contentVersion)
                assertEquals(side, p.size)
                assertEquals(if (side == 40) 70 else 105, p.targets.size)
                assertEquals(mode.legacyDifficulty, p.difficulty)
                if (seed == 11L) {
                    val key = theme.name + "|" + side + "|" + mode.name
                    assertEquals("Kotlin V4 lab golden changed: " + key,
                        seed11Goldens.getValue(key), gridFingerprint(p.rows))
                }
                assertEquals(p, V4PreviewPlacement.previewPuzzle(theme, side, mode, seed, pack))
                var found = emptySet<String>()
                for (target in p.targets) {
                    assertEquals(target.word,
                        target.positions.mapNotNull(p::letter).joinToString(""))
                    assertEquals(target.positions, WordSearchRules.line(
                        target.positions.first(), target.positions.last(), side))
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                        p, found, target.positions.first(), target.positions.last()).status)
                    found = found + target.id
                    assertEquals(ClaimStatus.ALREADY_FOUND, WordSearchRules.claim(
                        p, found, target.positions.last(), target.positions.first()).status)
                    claims++
                }
                assertTrue(WordSearchRules.complete(p, found))
                assertFalse(WordSearchRules.complete(p, found - p.targets.first().id))
                checked++
            }
        assertEquals(180, checked)
        assertEquals(15750, claims)
    }

    @Test fun poolFingerprintIsPartOfNonShippingPreviewIdentity() {
        val pool = fixture().getValue("SPACE")
        val a = V4PreviewPlacement.previewPuzzle(
            WordTheme.SPACE, 40, PuzzleChallenge.RELAXED, 42L, pool)
        val reversed = V4PreviewPlacement.previewPuzzle(
            WordTheme.SPACE, 40, PuzzleChallenge.RELAXED, 42L, pool.reversed())
        assertNotEquals(a.id, reversed.id)
        assertTrue(a.id.startsWith("wordsearch:v4:PREVIEW:en:SPACE:40x40:RELAXED:42:"))
        assertFalse(a.id.startsWith("wordsearch:v4:en:"))
    }

    @Test fun invalidPreviewContentFailsClosedAndExistingSoloRoutingStaysV3() {
        val pool = fixture().getValue("SPACE")
        assertTrue(runCatching { V4PreviewPlacement.previewPuzzle(
            WordTheme.SPACE, 75, PuzzleChallenge.RELAXED, 42L, pool) }.isFailure)
        assertTrue(runCatching { V4PreviewPlacement.previewPuzzle(
            WordTheme.SPACE, 50, PuzzleChallenge.CLASSIC, 42L,
            listOf("RIVER", "RIVERBANK") + pool) }.isFailure)
        assertEquals(listOf("COMPACT", "STANDARD", "LARGE", "BOOK_SHORT",
            "BOOK_CLASSIC", "BOOK_BIG", "BOOK_GRAND", "BOOK_GIANT"),
            PuzzleBoardSize.entries.map { it.name })
        assertEquals(listOf(25, 30), PuzzleBoardSize.largePresets.map { it.cells })
        assertTrue(PuzzleBoardSize.soloPresets.all { it.cells <= 30 })
        assertEquals(1, WordSearchGenerator.CONTENT_VERSION)
        assertEquals(2, WordSearchGeneratorV2.CONTENT_VERSION)
        assertEquals(3, WordSearchGeneratorV3.CONTENT_VERSION)
        val old = SoloProgressRules.freshV3(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_GIANT,
            PuzzleChallenge.CLASSIC, 42L)
        assertTrue(SoloProgressRules.puzzle(old).id.startsWith("wordsearch:v3:en:"))
    }
}
