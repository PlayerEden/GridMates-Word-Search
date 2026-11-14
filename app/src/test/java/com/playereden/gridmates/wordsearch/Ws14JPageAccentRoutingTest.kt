package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** WS-14J: artwork must never silently appear outside the QA page-header slot. */
class Ws14JPageAccentRoutingTest {
    @Test fun woodlandHeaderUsesTheStandaloneCandidate() {
        assertEquals("ws14j/page-accents/woodland.png",
            ws14jQaPageAccentPath(WordTheme.WOODLAND, BookArtKind.EMBLEM, true))
    }

    @Test fun otherThemesAndArtKindsKeepExistingFallbacks() {
        assertNull(ws14jQaPageAccentPath(WordTheme.SPACE, BookArtKind.EMBLEM, true))
        assertNull(ws14jQaPageAccentPath(WordTheme.GARDEN, BookArtKind.EMBLEM, true))
        assertNull(ws14jQaPageAccentPath(WordTheme.WOODLAND, BookArtKind.COVER, true))
        assertNull(ws14jQaPageAccentPath(WordTheme.WOODLAND, BookArtKind.EMBLEM, false))
    }
}
