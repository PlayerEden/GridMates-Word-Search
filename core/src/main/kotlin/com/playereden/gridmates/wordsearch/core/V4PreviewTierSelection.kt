package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import java.util.Random

/**
 * WS-09F-E: PREVIEW-ONLY tier selection, not a player-approved vocabulary.
 *
 * No content ships here. Tests inject the proposed source-controlled tiers.
 * These quotas, selections and IDs are NOT the immutable V4 release spec.
 */
internal object V4PreviewTierSelection {
    // Outer selection retries are bounded separately from the inner 24
    // placement attempts. This is a preview-only quality-research limit.
    const val MAX_POOL_DRAWS = 8
    data class Selection(val words: List<String>, val advancedCount: Int)
    data class PreviewOutcome(
        val puzzle: WordSearchPuzzle, val selection: Selection, val poolDraws: Int
    )

    private fun seeded(seed: Long, theme: WordTheme, size: Int,
                       mode: PuzzleChallenge, purpose: String, drawIndex: Int): Random {
        val material = "ws09f-e-tier-preview-v2:${theme.name}:$size:${mode.name}:$seed:$drawIndex:$purpose"
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(material.toByteArray(Charsets.UTF_8))
        var key = 0L
        for (i in 0 until 8) key = (key shl 8) or (bytes[i].toLong() and 255L)
        return Random(key)
    }

    private fun <T> shuffled(items: List<T>, random: Random): List<T> =
        items.toMutableList().apply {
            for (i in lastIndex downTo 1) {
                val j = random.nextInt(i + 1)
                val tmp = this[i]
                this[i] = this[j]
                this[j] = tmp
            }
        }

    /**
     * Trial quotas, subject to editorial playtesting:
     * Relaxed 0 advanced; Classic approximately 10%; Expert approximately 25%.
     * The entire proposed input must be disjoint and substring-safe.
     */
    fun select(theme: WordTheme, size: Int, mode: PuzzleChallenge, seed: Long,
               tentativeRelaxed: List<String>, tentativeAdvanced: List<String>,
               drawIndex: Int = 0): Selection {
        require(size == 40 || size == 50) { "Only preview 40/50 boards are supported" }
        require(drawIndex in 0 until MAX_POOL_DRAWS) { "Preview pool draw out of bounds" }
        val target = if (size == 40) 70 else 105
        val advancedCount = when (mode) {
            PuzzleChallenge.RELAXED -> 0
            PuzzleChallenge.CLASSIC -> target / 10
            PuzzleChallenge.EXPERT -> target / 4
        }
        require(tentativeRelaxed.size >= target) {
            "Preview Relaxed supply must support an entire $target-word puzzle"
        }
        require(tentativeAdvanced.size >= advancedCount) {
            "Not enough advanced preview entries for this trial"
        }
        V4PreviewPlacement.validatePool(tentativeRelaxed + tentativeAdvanced, target)
        val common = shuffled(tentativeRelaxed, seeded(seed, theme, size, mode, "relaxed", drawIndex))
            .take(target - advancedCount)
        val advanced = shuffled(tentativeAdvanced, seeded(seed, theme, size, mode, "advanced", drawIndex))
            .take(advancedCount)
        val selected = shuffled(common + advanced,
            seeded(seed, theme, size, mode, "final-pool-order", drawIndex))
        check(selected.size == target && selected.distinct().size == target)
        check(selected.count { it in tentativeAdvanced } == advancedCount)
        return Selection(selected, advancedCount)
    }

    /**
     * Entirely bounded selection retry. A draw that can't fit within the
     * placement engine's 24 attempts is recorded as a failed candidate draw.
     * The next draw uses a new deterministic SHA-seeded word set; no random
     * fallback, no smaller board and no hidden substitution for a saved game.
     */
    fun previewOutcome(theme: WordTheme, size: Int, mode: PuzzleChallenge, seed: Long,
                       tentativeRelaxed: List<String>, tentativeAdvanced: List<String>)
        : PreviewOutcome {
        for (draw in 0 until MAX_POOL_DRAWS) {
            val selected = select(theme, size, mode, seed,
                tentativeRelaxed, tentativeAdvanced, draw)
            val result = try {
                V4PreviewPlacement.previewPuzzle(theme, size, mode, seed, selected.words)
            } catch (e: IllegalStateException) {
                if (e.message != "Bounded v4 lab generator exhausted attempts") throw e
                null
            }
            if (result != null) {
                return PreviewOutcome(result, selected, draw + 1)
            }
        }
        error("V4 preview exhausted $MAX_POOL_DRAWS distinct word-list selections" +
            " (each limited to ${V4PreviewPlacement.MAX_ATTEMPTS} placements): " +
            "${theme.name} ${size}x$size ${mode.name} seed=$seed")
    }

    /** Test use ONLY. PREVIEW IDs are neither save-safe nor final production IDs. */
    fun previewPuzzle(theme: WordTheme, size: Int, mode: PuzzleChallenge, seed: Long,
                      tentativeRelaxed: List<String>, tentativeAdvanced: List<String>)
        : WordSearchPuzzle = previewOutcome(
            theme, size, mode, seed, tentativeRelaxed, tentativeAdvanced).puzzle
}
