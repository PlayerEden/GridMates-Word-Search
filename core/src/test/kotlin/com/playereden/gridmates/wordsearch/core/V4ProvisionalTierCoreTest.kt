package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

/**
 * PREVIEW ONLY. Three themes, ten seeds, two sizes, three difficulty tiers.
 * Neither these unreviewed lists nor generated PREVIEW IDs may become saves.
 */
class V4ProvisionalTierCoreTest {
    private fun tiers(): Map<Pair<String, String>, List<String>> =
        requireNotNull(javaClass.getResourceAsStream("/ws09f_e_v4_provisional_tiers.txt"))
            .bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith("#") }.associate { line ->
                    val parts = line.split('|', limit = 3)
                    require(parts.size == 3)
                    (parts[0] to parts[1]) to parts[2].split(' ').filter { it.isNotBlank() }
                }
            }

    private fun words(data: Map<Pair<String, String>, List<String>>,
                      theme: WordTheme, band: String) = data.getValue(theme.name to band)

    @Test fun trialTiersHaveNoHoldsOrOverlappingWordsAndEnoughRelaxedSupply() {
        val data = tiers()
        assertEquals(12, data.size)
        val counts = mapOf(WordTheme.WOODLAND to 126,
            WordTheme.SPACE to 131, WordTheme.GARDEN to 135)
        WordTheme.entries.forEach { theme ->
            val gentle = words(data, theme, "TRIAL_RELAXED")
            val advanced = words(data, theme, "TRIAL_ADVANCED")
            val held = words(data, theme, "EDITOR_HOLD")
            val extra = words(data, theme, "UNREVIEWED_ADDITION")
            assertEquals(counts.getValue(theme), gentle.size)
            assertTrue(gentle.size >= 125)
            assertTrue(held.none { it in gentle || it in advanced })
            assertTrue(extra.all { it in gentle })
            assertEquals(if (theme == WordTheme.SPACE) 10 else 0, extra.size)
            assertTrue(advanced.isNotEmpty())
            V4PreviewPlacement.validatePool(gentle + advanced, 105)
        }
    }

    @Test fun trialRelaxedClassicExpertSupplyRealCorePuzzlesWithStrictQuotas() {
        val data = tiers()
        var generated = 0
        var claims = 0
        val seeds = listOf(11L, 42L, 2026L, 0L, 1L, 7L, 19L, 77L, 144L, 999L)
        val sampledSpaceExtras = mutableSetOf<String>()
        for (theme in WordTheme.entries) {
            val gentle = words(data, theme, "TRIAL_RELAXED")
            val advanced = words(data, theme, "TRIAL_ADVANCED")
            val extra = words(data, theme, "UNREVIEWED_ADDITION")
            for (side in listOf(40, 50)) for (mode in PuzzleChallenge.entries)
                for (seed in seeds) {
                    val selected = V4PreviewTierSelection.select(
                        theme, side, mode, seed, gentle, advanced)
                    val expected = when (mode) {
                        PuzzleChallenge.RELAXED -> 0
                        PuzzleChallenge.CLASSIC -> (if (side == 40) 70 else 105) / 10
                        PuzzleChallenge.EXPERT -> (if (side == 40) 70 else 105) / 4
                    }
                    assertEquals(expected, selected.advancedCount)
                    assertEquals(expected, selected.words.count { it in advanced })
                    assertEquals(if (side == 40) 70 else 105, selected.words.size)
                    val p = V4PreviewTierSelection.previewPuzzle(
                        theme, side, mode, seed, gentle, advanced)
                    assertTrue(p.id.startsWith("wordsearch:v4:PREVIEW:en:"))
                    assertEquals(selected.words.toSet(), p.targets.map { it.word }.toSet())
                    assertEquals(side, p.size)
                    assertEquals(4, p.contentVersion)
                    val again = V4PreviewTierSelection.previewPuzzle(
                        theme, side, mode, seed, gentle, advanced)
                    assertEquals("Seeded preview must replay exactly", p, again)
                    var found = emptySet<String>()
                    for (target in p.targets) {
                        assertEquals(target.word,
                            target.positions.mapNotNull(p::letter).joinToString(""))
                        assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                            p, found, target.positions.first(), target.positions.last()).status)
                        found = found + target.id
                        claims++
                    }
                    assertTrue(WordSearchRules.complete(p, found))
                    if (theme == WordTheme.SPACE && mode == PuzzleChallenge.RELAXED)
                        sampledSpaceExtras.addAll(p.targets.map { it.word }.filter { it in extra })
                    generated++
                }
        }
        assertEquals(180, generated)
        assertEquals(15750, claims)
        assertTrue("Space additions must be exercised in trial previews",
            sampledSpaceExtras.isNotEmpty())
    }

    @Test fun invalidTrialPoolFailsWithoutFallbackToUnreviewedHolds() {
        val d = tiers()
        val common = words(d, WordTheme.GARDEN, "TRIAL_RELAXED")
        val advanced = words(d, WordTheme.GARDEN, "TRIAL_ADVANCED")
        assertTrue(runCatching { V4PreviewTierSelection.select(
            WordTheme.GARDEN, 75, PuzzleChallenge.RELAXED, 1L, common, advanced)
        }.isFailure)
        assertTrue(runCatching { V4PreviewTierSelection.select(
            WordTheme.GARDEN, 50, PuzzleChallenge.RELAXED, 1L,
            common.take(99), advanced)
        }.isFailure)
        assertTrue(runCatching { V4PreviewTierSelection.select(
            WordTheme.GARDEN, 50, PuzzleChallenge.CLASSIC, 1L,
            common + listOf("RIVER", "RIVERBANK"), advanced)
        }.isFailure)
        assertEquals(listOf(8, 10, 12, 12, 15, 20, 25, 30),
            PuzzleBoardSize.entries.map { it.cells })
        assertFalse(PuzzleBoardSize.soloPresets.any { it.cells >= 40 })
    }
}
