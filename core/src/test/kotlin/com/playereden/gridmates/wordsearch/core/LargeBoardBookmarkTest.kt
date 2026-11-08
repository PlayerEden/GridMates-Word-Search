package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class LargeBoardBookmarkTest {
    private val id = "wordsearch:v3:en:WOODLAND:BOOK_GIANT:CLASSIC:42"

    @Test fun storesNormalizedCenterIndependentOfPhonePixelSize() {
        val oldSide = 320f
        val original = LargeBoardViewport().jumpToRegion(2, 1, 30, oldSide)
            .panned(-16f, 19f, oldSide)
        val bookmark = LargeBoardBookmark.capture(id, original, oldSide)
        val restored = assertNotNullReturn(bookmark.restoreFor(id, 472f))
        assertEquals(original.zoom, restored.zoom, 0.0001f)
        val originalView = original.overview(oldSide)
        val restoredView = restored.overview(472f)
        assertEquals(originalView.left, restoredView.left, 0.0001f)
        assertEquals(originalView.top, restoredView.top, 0.0001f)
        assertEquals(originalView.right, restoredView.right, 0.0001f)
        assertEquals(originalView.bottom, restoredView.bottom, 0.0001f)
    }

    @Test fun fitIsAlwaysFullBoardAndNoPan() {
        val bookmark = LargeBoardBookmark.capture(
            id, LargeBoardViewport(), 320f
        )
        assertEquals(0.5f, bookmark.centerX, 0f)
        assertEquals(0.5f, bookmark.centerY, 0f)
        assertEquals(LargeBoardViewport(), bookmark.restoreFor(id, 720f))
    }

    @Test fun bothExtremeMapCornersRoundTripWithoutLosingVisibleEdges() {
        for (cells in listOf(25, 30, 40, 50, 75, 100)) {
            val last = LargeBoardViewport.regionCount(cells) - 1
            for (corner in listOf(0 to 0, last to last, last to 0, 0 to last)) {
                val before = LargeBoardViewport().jumpToRegion(
                    corner.first, corner.second, cells, 320f
                )
                val bookmark = LargeBoardBookmark.capture(id, before, 320f)
                val after = assertNotNullReturn(bookmark.restoreFor(id, 490f))
                assertEquals(before.visibleRows(320f, cells), after.visibleRows(490f, cells))
                assertEquals(before.visibleColumns(320f, cells), after.visibleColumns(490f, cells))
            }
        }
    }

    @Test fun stalePuzzleIdentityAndInvalidDimensionsFailClosed() {
        val bookmark = LargeBoardBookmark.capture(
            id, LargeBoardViewport(3f, -400f, -220f), 320f
        )
        assertNull(bookmark.restoreFor(id + "-different-seed", 320f))
        assertNull(bookmark.restoreFor(id, 0f))
        assertNull(bookmark.restoreFor(id, Float.NaN))
        assertNull(bookmark.restoreFor(id, Float.POSITIVE_INFINITY))
    }

    @Test fun malformedOptionalMetadataNeverCreatesABookmark() {
        assertNull(LargeBoardBookmark.fromFieldsOrNull(null, 2f, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(" ", 2f, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull("bad\nID", 2f, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, Float.NaN, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, 13f, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, 0.9f, .5f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, 2f, -0.1f, .5f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, 2f, .5f, 1.1f))
        assertNull(LargeBoardBookmark.fromFieldsOrNull(id, 2f, Float.POSITIVE_INFINITY, .5f))
        assertNotNull(LargeBoardBookmark.fromFieldsOrNull(id, 2f, .2f, .8f))
    }

    private fun <T : Any> assertNotNullReturn(value: T?): T {
        assertNotNull(value)
        return requireNotNull(value)
    }
}
