package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Every theme has its own actual transparent image, with no cover substitution. */
class Ws14K2MotifSelectionTest {
    @Test fun eachBookThemeSelectsDistinctGameMotif() {
        assertEquals("ws14k2/motifs/woodland.png",
            ws14k2QaGameMotifPath(WordTheme.WOODLAND, BookArtKind.EMBLEM, true))
        assertEquals("ws14k2/motifs/space.png",
            ws14k2QaGameMotifPath(WordTheme.SPACE, BookArtKind.EMBLEM, true))
        assertEquals("ws14k2/motifs/garden.png",
            ws14k2QaGameMotifPath(WordTheme.GARDEN, BookArtKind.EMBLEM, true))
    }
    @Test fun artIsExplicitlyGatedToPageEmblems() {
        assertNull(ws14k2QaGameMotifPath(WordTheme.WOODLAND, BookArtKind.COVER, true))
        assertNull(ws14k2QaGameMotifPath(WordTheme.SPACE, BookArtKind.EMBLEM, false))
    }
}
