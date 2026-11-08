package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Test actual Android AtomicFile + manual-backup behavior, not only geometry. */
class LargeBoardBookmarkPersistenceAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun giantViewSurvivesTwoPausedGamesReopenAndManualBackup() {
        val name = "ws09f_b2_view_" + System.nanoTime() + ".json"
        val imported = name + "_copy"
        try {
            val store = SoloStore(ctx, name)
            val oldClassic = store.profile.active
            assertFalse(store.saveLargeBoardBookmark(
                LargeBoardBookmark.capture(oldClassic.puzzleId, LargeBoardViewport(), 320f)
            )) // no bookmarks on legacy boards

            assertTrue(store.chooseSolo(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_GIANT, PuzzleChallenge.CLASSIC))
            val giant = SoloProgressRules.puzzle(store.profile.active)
            val target = giant.targets.first()
            assertEquals(ClaimStatus.FOUND, store.claim(
                target.positions.first(), target.positions.last()).status)
            assertNotNull(store.hint().second)
            val id = store.profile.active.puzzleId
            val viewpoint = LargeBoardViewport().jumpToRegion(2, 1, 30, 320f)
            val bookmark = LargeBoardBookmark.capture(id, viewpoint, 320f)
            assertTrue(store.saveLargeBoardBookmark(bookmark))
            assertEquals(bookmark, store.profile.active.viewportBookmark)

            val found = store.profile.active.foundIds
            val hints = store.profile.active.hints
            val seconds = store.profile.active.elapsedSeconds
            assertFalse(store.saveLargeBoardBookmark(
                bookmark.copy(puzzleId = id + "-not-current")
            ))
            assertEquals(bookmark, store.profile.active.viewportBookmark)

            assertTrue(store.chooseSolo(WordTheme.GARDEN,
                PuzzleBoardSize.BOOK_GRAND, PuzzleChallenge.RELAXED))
            assertTrue(store.chooseSolo(WordTheme.SPACE,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC))
            assertEquals(bookmark, store.profile.pausedSolo.firstOrNull {
                it.puzzleId == id
            }?.viewportBookmark)

            val restarted = SoloStore(ctx, name)
            assertTrue(restarted.resumeSolo(id))
            assertEquals(bookmark, restarted.profile.active.viewportBookmark)
            assertEquals(found, restarted.profile.active.foundIds)
            assertEquals(hints, restarted.profile.active.hints)
            assertEquals(seconds, restarted.profile.active.elapsedSeconds)
            val restoredWindow = bookmark.restoreFor(id, 470f)
            assertNotNull(restoredWindow)
            assertEquals(viewpoint.zoom, requireNotNull(restoredWindow).zoom, 0.0001f)

            val backup = restarted.exportBackup()
            assertEquals(5, JSONObject(backup).getInt("schema"))
            assertTrue(JSONObject(backup).getJSONObject("active")
                .has("viewport"))
            val restored = SoloStore(ctx, imported)
            assertTrue(restored.restoreBackup(restored.previewBackup(backup)))
            assertEquals(restarted.profile, SoloStore(ctx, imported).profile)
        } finally {
            cleanup(name)
            cleanup(imported)
        }
    }

    @Test fun oldSchemaFiveOrMalformedViewStillPreservesFoundWordsAndHints() {
        val name = "ws09f_b2_invalid_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            assertTrue(store.chooseSolo(WordTheme.SPACE,
                PuzzleBoardSize.BOOK_GIANT, PuzzleChallenge.CLASSIC))
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            val word = puzzle.targets.first()
            assertEquals(ClaimStatus.FOUND, store.claim(
                word.positions.first(), word.positions.last()).status)
            val expected = store.profile.active
            val original = store.exportBackup()
            val malformed = listOf<Any>(
                "not-an-object",
                JSONObject().put("puzzleId", expected.puzzleId)
                    .put("zoom", "not-a-number").put("centerX", 0.5).put("centerY", 0.5),
                JSONObject().put("puzzleId", expected.puzzleId)
                    .put("zoom", 15).put("centerX", 0.5).put("centerY", 0.5),
                JSONObject().put("puzzleId", "different-puzzle")
                    .put("zoom", 3).put("centerX", 0.5).put("centerY", 0.5),
                JSONObject().put("puzzleId", expected.puzzleId)
                    .put("zoom", 3).put("centerX", -0.5).put("centerY", 0.5)
            )
            for (invalid in malformed) {
                val edited = JSONObject(original)
                edited.getJSONObject("active").put("viewport", invalid)
                val loaded = store.previewBackup(edited.toString())
                assertEquals(expected, loaded.active)
                assertEquals(store.profile.pausedSolo, loaded.pausedSolo)
                assertTrue("metadata cannot corrupt progress",
                    store.restoreBackup(loaded))
                assertNull(store.warning)
            }
            val oldStyle = JSONObject(original)
            oldStyle.getJSONObject("active").remove("viewport")
            assertEquals(expected, store.previewBackup(oldStyle.toString()).active)
        } finally { cleanup(name) }
    }

    private fun cleanup(name: String) {
        for (suffix in listOf("", ".bak", ".new"))
            File(ctx.filesDir, name + suffix).delete()
    }
}
