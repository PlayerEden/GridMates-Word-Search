package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertNotEquals
import org.junit.Test

/** Only the surrounding paper responds to book theme; board style stays separate. */
class Ws14HThemePaperTest {
    @Test fun threeBooksHaveDistinctAndStablePaperTints() {
        val woodland = bookPageThemePaperTint(WordTheme.WOODLAND)
        val space = bookPageThemePaperTint(WordTheme.SPACE)
        val garden = bookPageThemePaperTint(WordTheme.GARDEN)
        assertNotEquals(woodland, space)
        assertNotEquals(space, garden)
        assertNotEquals(garden, woodland)
    }
}
