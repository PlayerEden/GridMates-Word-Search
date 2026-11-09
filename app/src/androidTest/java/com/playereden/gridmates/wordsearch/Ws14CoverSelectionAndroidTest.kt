package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** WS-14B2: compile in CI; actual device assets and visuals need physical QA. */
class Ws14CoverSelectionAndroidTest {
    @Test fun themeSpecificNewCoverPathsStayLocalAndStable() {
        assertEquals("ws14/books/woodland.png",
            ws14QaCoverPath(WordTheme.WOODLAND, BookArtKind.COVER))
        assertEquals("ws14/books/space.png",
            ws14QaCoverPath(WordTheme.SPACE, BookArtKind.COVER))
        assertEquals("ws14/books/garden.png",
            ws14QaCoverPath(WordTheme.GARDEN, BookArtKind.COVER))
    }

    @Test fun largeOriginalCoverIsDownsampledBeforeRendering() {
        assertEquals(1, bookArtSampleSize(384, 512))
        assertEquals(4, bookArtSampleSize(1728, 2304))
        assertEquals(8, bookArtSampleSize(4096, 4096))
    }

    @Test fun newSpineArtUsesSeparateBundlePathAndLeavesOtherArtAlone() {
        assertEquals("ws14/spines/woodland.png",
            ws14QaBookArtPath(WordTheme.WOODLAND, BookArtKind.SPINE))
        assertEquals("ws14/spines/space.png",
            ws14QaBookArtPath(WordTheme.SPACE, BookArtKind.SPINE))
        assertEquals("ws14/spines/garden.png",
            ws14QaBookArtPath(WordTheme.GARDEN, BookArtKind.SPINE))
        assertNull(ws14QaBookArtPath(WordTheme.WOODLAND, BookArtKind.EMBLEM))
    }

    @Test fun originalStudyAAlwaysRemainsTrueComparison() {
        for (theme in WordTheme.entries) {
            assertNull(preferredWs14QaCoverPath(theme, BookArtKind.COVER, false))
            assertEquals(ws14QaCoverPath(theme, BookArtKind.COVER),
                preferredWs14QaCoverPath(theme, BookArtKind.COVER, true))
            assertNull(preferredWs14QaCoverPath(theme, BookArtKind.SPINE, false))
            assertEquals(ws14QaBookArtPath(theme, BookArtKind.SPINE),
                preferredWs14QaCoverPath(theme, BookArtKind.SPINE, true))
        }
    }

    @Test fun otherArtworkKindsUseExistingStudyPathUntilIndividuallyApproved() {
        for (theme in WordTheme.entries) {
            for (kind in BookArtKind.entries.filter {
                it != BookArtKind.COVER && it != BookArtKind.SPINE
            }) {
                assertNull(ws14QaCoverPath(theme, kind))
                assertEquals(true, bookArtPath(theme, kind)!!.startsWith("ws12/raster/"))
            }
        }
    }
}
