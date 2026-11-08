package com.playereden.gridmates.wordsearch.core

/**
 * Paperback-friendly navigation for future giant-board word lists.
 *
 * This is purely a view index: target identity, placement order in saved puzzle
 * data, generator results, score, and hint/claim rules remain unchanged.
 * Only screens with >=70 words opt into alphabetical display/navigation.
 */
object PaperbackWordIndex {
    const val MIN_GIANT_TARGETS = 70

    fun isGiant(targetCount: Int): Boolean = targetCount >= MIN_GIANT_TARGETS

    fun visibleWords(
        targets: List<WordTarget>, foundIds: Set<String>, remainingOnly: Boolean
    ): List<WordTarget> {
        val available = if (remainingOnly) targets.filterNot { it.id in foundIds } else targets
        return available.sortedWith(compareBy<WordTarget>({ it.word }, { it.id }))
    }

    fun availableLetters(ordered: List<WordTarget>): List<Char> =
        ordered.mapNotNull { it.word.firstOrNull() }
            .filter { it in 'A'..'Z' }.distinct().sorted()

    /** Null if no word begins with this letter; do not jump to another letter. */
    fun pageForLetter(ordered: List<WordTarget>, letter: Char, perPage: Int): Int? {
        require(perPage > 0) { "Page capacity must be positive" }
        if (letter !in 'A'..'Z') return null
        val index = ordered.indexOfFirst { it.word.startsWith(letter) }
        return if (index < 0) null else index / perPage
    }

    /** One-based inclusive visible word range; empty means no words remain. */
    fun visibleRange(total: Int, page: Int, perPage: Int): IntRange {
        require(total >= 0 && perPage > 0)
        if (total == 0) return IntRange.EMPTY
        val pages = (total - 1) / perPage + 1
        val start = page.coerceIn(0, pages - 1) * perPage
        return (start + 1)..minOf(start + perPage, total)
    }
}
