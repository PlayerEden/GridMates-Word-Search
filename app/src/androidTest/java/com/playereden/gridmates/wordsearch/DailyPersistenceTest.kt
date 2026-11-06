package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Runtime execution still needs a real device/emulator; CI compiles test source. */
class DailyPersistenceTest {
    @Test fun dailySurvivesRestartAndVersionOneSoloSaveMigration() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04a_migration_" + System.nanoTime() + ".json"
        val file = File(context.filesDir, name)
        try {
            // Exact original WS-03 schema, as exported by its first APK.
            file.writeText("""{"schema":1,"active":{"theme":"WOODLAND","difficulty":"EASY",
              "seed":99,"found":[],"hints":{},"seconds":12},"results":[]}""")
            val store = SoloStore(context, name)
            assertEquals(12L, store.profile.active.elapsedSeconds)
            assertTrue(store.profile.dailyEntries.isEmpty())

            val day1 = "2026-11-05"
            val day2 = "2026-11-06"
            store.openDaily(day1)
            val entry = requireNotNull(store.selectedDaily)
            val p = WordSearchGenerator.generate(
                entry.run.theme, entry.run.difficulty, entry.run.seed)
            val first = p.targets.first()
            assertEquals(ClaimStatus.FOUND,
                store.dailyClaim(first.positions.first(), first.positions.last()).status)
            store.openDaily(day2)
            assertEquals(2, store.profile.dailyEntries.size)
            store.flush()

            val reopened = SoloStore(context, name)
            assertEquals(12L, reopened.profile.active.elapsedSeconds)
            assertEquals(2, reopened.profile.dailyEntries.size)
            reopened.openDaily(day1)
            assertTrue(first.id in requireNotNull(reopened.selectedDaily).run.foundIds)

            val exported = reopened.exportBackup()
            val parsed = reopened.previewBackup(exported)
            assertEquals(2, parsed.dailyEntries.size)
            assertEquals(12L, parsed.active.elapsedSeconds)
            assertTrue(exported.contains("\"schema\":4"))

            // An in-progress past date must never be deleted when today changes.
            reopened.openDaily(day2)
            assertEquals(2, reopened.profile.dailyEntries.size)
        } finally {
            file.delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }

    @Test fun sameDayCompletionCannotProduceTwoDailyCompletions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04a_daily_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            store.openDaily("2026-11-05")
            val entry = requireNotNull(store.selectedDaily)
            val p = WordSearchGenerator.generate(
                entry.run.theme, entry.run.difficulty, entry.run.seed)
            for (target in p.targets) {
                assertEquals(ClaimStatus.FOUND,
                    store.dailyClaim(target.positions.first(), target.positions.last()).status)
            }
            assertEquals(1, store.profile.dailyCompletions)
            assertTrue(requireNotNull(store.selectedDaily).completed)
            assertEquals(ClaimStatus.ALREADY_FOUND,
                store.dailyClaim(p.targets.first().positions.first(),
                    p.targets.first().positions.last()).status)
            assertEquals(1, store.profile.dailyCompletions)

            val reopened = SoloStore(context, name)
            assertEquals(1, reopened.profile.dailyCompletions)
            assertEquals(0, reopened.profile.completions)
            assertEquals(1, reopened.previewBackup(reopened.exportBackup()).dailyCompletions)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
