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
    data class Selection(val words: List<String>, val advancedCount: Int)

    private fun seeded(seed: Long, theme: WordTheme, size: Int,
                       mode: PuzzleChallenge, purpose: String): Random {
        val material = "ws09f-e-tier-preview-v1:${theme.name}:$size:${mode.name}:$seed:$purpose"
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
               tentativeRelaxed: List<String>, tentativeAdvanced: List<String>): Selection {
        require(size == 40 || size == 50) { "Only preview 40/50 boards are supported" }
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
        val common = shuffled(tentativeRelaxed, seeded(seed, theme, size, mode, "relaxed"))
            .take(target - advancedCount)
        val advanced = shuffled(tentativeAdvanced, seeded(seed, theme, size, mode, "advanced"))
            .take(advancedCount)
        val selected = shuffled(common + advanced,
            seeded(seed, theme, size, mode, "final-pool-order"))
        check(selected.size == target && selected.distinct().size == target)
        check(selected.count { it in tentativeAdvanced } == advancedCount)
        return Selection(selected, advancedCount)
    }

    /** Test use ONLY. PREVIEW IDs are neither save-safe nor final production IDs. */
    fun previewPuzzle(theme: WordTheme, size: Int, mode: PuzzleChallenge, seed: Long,
                      tentativeRelaxed: List<String>, tentativeAdvanced: List<String>)
        : WordSearchPuzzle {
        val selected = select(theme, size, mode, seed, tentativeRelaxed, tentativeAdvanced)
        return V4PreviewPlacement.previewPuzzle(theme, size, mode, seed, selected.words)
    }
}
