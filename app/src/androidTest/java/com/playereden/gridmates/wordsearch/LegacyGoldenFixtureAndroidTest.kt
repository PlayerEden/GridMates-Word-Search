package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Reads the *same checked-in JSON bytes* installed into a real old v0.8.6
 * APK in scripts/ws09a2_partial_upgrade_emulator.sh.
 */
class LegacyGoldenFixtureAndroidTest {
    private val target get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun fixedOldPartialSoloDailyAndVersusFixtureLoadsWithoutTruncation() {
        val filename = "ws09a2_golden_" + System.nanoTime() + ".json"
        val secondary = filename + "_export"
        try {
            val fixture = InstrumentationRegistry.getInstrumentation().context.assets
                .open("ws09a2_legacy_partial_v086.json").bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            val store = SoloStore(target, filename)
            val original = store.profile
            val preview = store.previewBackup(fixture)
            // A preview should never alter the installed puzzle or records.
            assertEquals(original, store.profile)
            assertEquals("wordsearch:v2:en:WOODLAND:BOOK_CLASSIC:CLASSIC:42",
                preview.active.puzzleId)
            assertEquals("RACCOON", SoloProgressRules.puzzle(preview.active).targets[0].id)
            assertEquals("SAPLING", SoloProgressRules.puzzle(preview.active).targets[1].id)
            assertEquals(setOf("RACCOON"), preview.active.foundIds)
            assertEquals(mapOf("SAPLING" to 2), preview.active.hints)
            assertEquals(734L, preview.active.elapsedSeconds)
            assertEquals("WS09A2 Progress Tester", preview.identity.displayName)
            assertEquals(1, preview.raceResults.size)
            assertEquals(1, preview.duelResults.size)

            val daily = preview.dailyEntries.associateBy { it.date }
            assertEquals(setOf("2026-11-05", "2026-11-06"), daily.keys)
            val day1 = requireNotNull(daily["2026-11-05"])
            val day2 = requireNotNull(daily["2026-11-06"])
            assertFalse(day1.completed)
            assertFalse(day2.completed)
            assertEquals("PEONY", SoloProgressRules.puzzle(day1.run).targets[0].id)
            assertEquals("BLOOM", SoloProgressRules.puzzle(day1.run).targets[1].id)
            assertEquals(setOf("PEONY"), day1.run.foundIds)
            assertEquals(mapOf("BLOOM" to 1), day1.run.hints)
            assertEquals(83L, day1.run.elapsedSeconds)
            assertEquals(2984779710590232787L, day1.run.seed)
            assertEquals("BADGER", SoloProgressRules.puzzle(day2.run).targets[0].id)
            assertEquals("MOUSE", SoloProgressRules.puzzle(day2.run).targets[1].id)
            assertEquals(setOf("BADGER"), day2.run.foundIds)
            assertEquals(mapOf("MOUSE" to 2), day2.run.hints)
            assertEquals(117L, day2.run.elapsedSeconds)
            assertEquals(4153857886328470466L, day2.run.seed)

            assertTrue(store.restoreBackup(preview))
            val reopened = SoloStore(target, filename)
            assertEquals(preview, reopened.profile)
            val exported = reopened.exportBackup()
            val restored = SoloStore(target, secondary)
            val parsed = restored.previewBackup(exported)
            assertEquals(preview, parsed)
            assertTrue(restored.restoreBackup(parsed))
            assertEquals(preview, SoloStore(target, secondary).profile)
        } finally {
            listOf(filename, secondary).forEach { name ->
                listOf("", ".bak", ".new").forEach { suffix ->
                    File(target.filesDir, name + suffix).delete()
                }
            }
        }
    }
}
