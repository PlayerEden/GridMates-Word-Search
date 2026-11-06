package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BoardFontCollection
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Real Android tests: glyph ink centering, offline assets and free entitlement. */
class BoardFontAndroidTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun everyBundledTypefaceCentersAllCapitalLettersWithSafeMargins() {
        for (id in BoardFontRegistry.resourceById.keys) {
            val renderer = BoardGlyphPainter(BoardFontRegistry.resolve(context, id))
            for (cell in listOf(20f, 32f, 48f, 72f)) {
                for (letter in 'A'..'Z') {
                    val b = renderer.inkBoundsInCell(letter, cell, cell)
                    assertTrue("$id $letter left clipped at cell $cell", b.left >= 0f)
                    assertTrue("$id $letter right clipped at cell $cell", b.right <= cell)
                    assertTrue("$id $letter top clipped at cell $cell", b.top >= 0f)
                    assertTrue("$id $letter bottom clipped at cell $cell", b.bottom <= cell)
                    assertEquals("$id $letter horizontal center", cell / 2f,
                        b.centerX(), 0.02f)
                    assertEquals("$id $letter vertical center", cell / 2f,
                        b.centerY(), 0.02f)
                }
            }
        }
    }

    @Test fun fontFilesAndCompleteOflNoticesArePackagedOffline() {
        for ((id, _) in BoardFontRegistry.resourceById) {
            assertNotNull(BoardFontRegistry.resolve(context, id))
            val notice = context.assets.open("font_licenses/${id}_ofl.txt")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            assertTrue(id, notice.contains("SIL OPEN FONT LICENSE", ignoreCase = true))
            assertTrue(id, notice.contains("Copyright", ignoreCase = true))
        }
    }

    @Test fun lockedFontCannotBeEquippedButEarnedFontPersistsAcrossRestart() {
        val name = "ws04d_b5_earned_fonts_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            assertEquals("atkinson", store.effectiveBoardFontId)
            assertFalse(store.equipBoardFont("plex"))
            assertFalse(store.equipBoardFont("random_unknown_font"))
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            puzzle.targets.forEach {
                store.claim(it.positions.first(), it.positions.last())
            }
            assertEquals(1, store.profile.completions)
            assertTrue(BoardFontCollection.allowed(store.profile, "plex"))
            assertTrue(store.equipBoardFont("plex"))
            assertEquals("plex", store.effectiveBoardFontId)
            store.flush()
            val reopened = SoloStore(context, name)
            assertEquals("plex", reopened.effectiveBoardFontId)
            assertEquals(store.profile.active.puzzleId, reopened.profile.active.puzzleId)
            assertEquals(1, reopened.profile.completions)
            assertFalse(reopened.equipBoardFont("dmmono"))
            assertTrue(reopened.equipBoardFont("atkinson"))
        } finally {
            context.getSharedPreferences(name + "_board_fonts", 0).edit().clear().commit()
            context.getSharedPreferences(name + "_feedback", 0).edit().clear().commit()
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
