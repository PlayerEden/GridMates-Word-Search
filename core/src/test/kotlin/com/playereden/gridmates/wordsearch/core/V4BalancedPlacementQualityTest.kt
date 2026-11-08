package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

/** An independent research gate for word spread, directions, and seed variation. */
class V4BalancedPlacementQualityTest {
    private fun tiers(): Map<Pair<String, String>, List<String>> =
        requireNotNull(javaClass.getResourceAsStream("/ws09f_e_v4_provisional_tiers.txt"))
            .bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith("#") }
                    .associate { line ->
                        val parts = line.split('|', limit=3)
                        require(parts.size == 3)
                        (parts[0] to parts[1]) to parts[2].split(' ').filter { it.isNotBlank() }
                    }
            }

    @Test fun deliberatelyBunchedPuzzleCannotPassSpreadAndDirectionGate() {
        val path = (0 until 6).map { GridPoint(0, it) }
        val puzzle = WordSearchPuzzle(
            id = "wordsearch:v4:BALANCED-PREVIEW:synthetic:bad-layout",
            seed = 0L,
            contentVersion = 4,
            theme = WordTheme.GARDEN,
            difficulty = WordDifficulty.EASY,
            rows = List(40) { "A".repeat(40) },
            targets = (0 until 70).map { index ->
                WordTarget("BAD_$index", "FLOWER", path)
            }
        )
        val report = V4PuzzleQuality.measure(puzzle)
        val issues = V4PuzzleQuality.issues(report, PuzzleChallenge.RELAXED)
        assertEquals(1, report.regionsWithTargetCells)
        assertEquals(1, report.centerTilesOccupied)
        assertTrue("Clustering must be rejected", "missing_10x10_region" in issues)
        assertTrue("Direction monoculture must be rejected", "insufficient_directions" in issues)
        assertTrue("Quadrant imbalance must be rejected", "unbalanced_quadrants" in issues)
    }

    @Test fun everyBalancedPreviewUsesTheWholeBoardAndVariedAllowedDirections() {
        val data = tiers()
        var checked = 0
        var claimed = 0
        for (theme in WordTheme.entries) for (size in listOf(40, 50))
            for (challenge in PuzzleChallenge.entries) {
                val familiar = data.getValue(theme.name to "TRIAL_RELAXED")
                val advanced = data.getValue(theme.name to "TRIAL_ADVANCED")
                var previous: WordSearchPuzzle? = null
                for (seed in 0L..2L) {
                    val selected = V4PreviewTierSelection.select(
                        theme, size, challenge, seed, familiar, advanced)
                    val result = V4BalancedPlacement.generate(
                        theme, size, challenge, seed, selected.words)
                    val puzzle = result.puzzle
                    val report = V4PuzzleQuality.measure(puzzle)
                    assertEquals("Quality report must represent actual hidden paths",
                        result.quality, report)
                    assertTrue("Layout failed: " + V4PuzzleQuality.issues(report, challenge),
                        V4PuzzleQuality.issues(report, challenge).isEmpty())
                    assertEquals(if (size == 40) 16 else 25, report.regionsWithTargetCells)
                    assertEquals(16, report.centerTilesOccupied)
                    assertEquals(0, report.directionCounts.values.count { it <= 0 })
                    assertEquals(if (size == 40) 70 else 105, puzzle.targets.size)
                    assertTrue(puzzle.id.startsWith("wordsearch:v4:BALANCED-PREVIEW:"))
                    assertTrue(result.attempts in 1..24)
                    val prior = previous
                    if (prior != null) {
                        assertNotEquals("New seed must change full grid", prior.rows, puzzle.rows)
                        val (sharedWords, identicalPaths) =
                            V4PuzzleQuality.compare(prior, puzzle)
                        assertTrue(sharedWords > 0) // Vocabulary overlap is unavoidable.
                        assertTrue("Shared words should not just repeat old positions",
                            identicalPaths <= 2)
                    }
                    var found = emptySet<String>()
                    for (target in puzzle.targets) {
                        assertEquals(1, V4PreviewPlacement.occurrences(
                            puzzle.rows, target.word))
                        assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                            puzzle, found, target.positions.first(),
                            target.positions.last()).status)
                        found += target.id
                        claimed++
                    }
                    assertTrue(WordSearchRules.complete(puzzle, found))
                    if (seed == 0L) assertEquals("Deterministic replay required",
                        result, V4BalancedPlacement.generate(
                            theme, size, challenge, seed, selected.words))
                    previous = puzzle
                    checked++
                }
            }
        assertEquals(54, checked)
        assertEquals(4725, claimed)
    }

    @Test fun previewEngineRejectsInvalidBoardSizesAndPalindromeWords() {
        val data = tiers()
        val familiar = data.getValue("SPACE" to "TRIAL_RELAXED")
        val advanced = data.getValue("SPACE" to "TRIAL_ADVANCED")
        val words = V4PreviewTierSelection.select(
            WordTheme.SPACE, 40, PuzzleChallenge.RELAXED, 11L,
            familiar, advanced).words
        assertTrue(runCatching { V4BalancedPlacement.generate(
            WordTheme.SPACE, 75, PuzzleChallenge.RELAXED, 11L, words)
        }.isFailure)
        assertTrue(runCatching { V4BalancedPlacement.generate(
            WordTheme.SPACE, 40, PuzzleChallenge.RELAXED, 11L,
            words.dropLast(1) + "RADAR")
        }.isFailure)
        assertEquals(listOf(8, 10, 12, 12, 15, 20, 25, 30),
            PuzzleBoardSize.entries.map { it.cells })
        assertTrue(PuzzleBoardSize.soloPresets.none { it.cells >= 40 })
        assertEquals(3, WordSearchGeneratorV3.CONTENT_VERSION)
    }
}
