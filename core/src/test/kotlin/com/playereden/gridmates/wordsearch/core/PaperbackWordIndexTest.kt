package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class PaperbackWordIndexTest {
    private fun words(): List<WordTarget> = (0 until 105).map { i ->
        val letter = 'A' + (i / 5)
        val sub = 'A' + (i % 5)
        WordTarget("ID_$i", "$letter${sub}PAPER", emptyList())
    }

    @Test fun giantOnlyGateDoesNotChangeSmallBoardBehavior() {
        assertFalse(PaperbackWordIndex.isGiant(44))
        assertFalse(PaperbackWordIndex.isGiant(69))
        assertTrue(PaperbackWordIndex.isGiant(70))
        assertTrue(PaperbackWordIndex.isGiant(105))
    }

    @Test fun hundredFiveWordsCanJumpByFirstLetterAtVariousPageSizes() {
        val input = words().reversed()
        val ordered = PaperbackWordIndex.visibleWords(input, emptySet(), false)
        assertEquals(105, ordered.size)
        assertEquals((0 until 21).map { 'A' + it },
            PaperbackWordIndex.availableLetters(ordered))
        assertEquals(0, PaperbackWordIndex.pageForLetter(ordered, 'A', 5))
        assertEquals(19, PaperbackWordIndex.pageForLetter(ordered, 'T', 5))
        assertEquals(20, PaperbackWordIndex.pageForLetter(ordered, 'U', 5))
        assertEquals(95, PaperbackWordIndex.pageForLetter(ordered, 'T', 1))
        assertEquals(9, PaperbackWordIndex.pageForLetter(ordered, 'T', 10))
        assertNull(PaperbackWordIndex.pageForLetter(ordered, 'Z', 5))
        assertNull(PaperbackWordIndex.pageForLetter(ordered, '?', 5))
        assertEquals(96..100, PaperbackWordIndex.visibleRange(105, 19, 5))
        assertEquals(101..105, PaperbackWordIndex.visibleRange(105, 20, 5))
        assertEquals(101..105, PaperbackWordIndex.visibleRange(105, 999, 5))
        assertEquals(1..5, PaperbackWordIndex.visibleRange(105, -8, 5))
        assertTrue(PaperbackWordIndex.visibleRange(0, 0, 5).isEmpty())
        assertEquals(input, words().reversed()) // Never mutate source target order.
    }

    @Test fun remainingFilterAndPagingSkipFoundTargetsWithoutChangingIds() {
        val input = words().reversed()
        val found = input.filter { it.word.startsWith('A') || it.word.startsWith('B') }
            .map { it.id }.toSet()
        val visible = PaperbackWordIndex.visibleWords(input, found, true)
        assertEquals(95, visible.size)
        assertEquals('C', visible.first().word.first())
        assertEquals(0, PaperbackWordIndex.pageForLetter(visible, 'C', 4))
        assertNull(PaperbackWordIndex.pageForLetter(visible, 'A', 4))
        assertFalse(PaperbackWordIndex.availableLetters(visible).contains('B'))
        assertTrue(visible.none { it.id in found })
        assertEquals(105, input.size) // No mutation of puzzle or persisted targets.
    }

    @Test fun invalidCapacityFailsBeforeAnyPageCalculation() {
        assertTrue(runCatching { PaperbackWordIndex.pageForLetter(words(), 'A', 0) }.isFailure)
        assertTrue(runCatching { PaperbackWordIndex.visibleRange(105, 0, 0) }.isFailure)
        assertTrue(runCatching { PaperbackWordIndex.visibleRange(-1, 0, 1) }.isFailure)
    }
}
