package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Android tests are currently compiled in CI; device execution is a separate QA gate. */
class IdentityPersistenceTest {
    @Test fun schemaTwoUpgradesWithoutChangingSoloData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04b_legacy_" + System.nanoTime() + ".json"
        val file = File(context.filesDir, name)
        try {
            file.writeText("""{"schema":2,"active":{"theme":"WOODLAND",
               "difficulty":"EASY","seed":42,"found":[],"hints":{},"seconds":18},
               "results":[],"daily":[]}""")
            val store = SoloStore(context, name)
            assertEquals(18L, store.profile.active.elapsedSeconds)
            assertEquals("Player", store.profile.identity.displayName)
            assertEquals("woodland_owl", store.profile.identity.portraitId)
            store.updateDisplayName("A Puzzle Friend")
            assertTrue(store.equipPortrait("woodland_fox"))
            val revived = SoloStore(context, name)
            assertEquals(18L, revived.profile.active.elapsedSeconds)
            assertEquals("A Puzzle Friend", revived.profile.identity.displayName)
            assertEquals("woodland_fox", revived.profile.identity.portraitId)
            assertTrue(revived.exportBackup().contains("\"schema\":3"))
        } finally {
            file.delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }

    @Test fun realDailyCompletionUnlocksRabbitAndBackupRestoresChoice() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04b_portrait_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            assertFalse(store.equipPortrait("woodland_rabbit"))
            store.openDaily("2026-11-05")
            val entry = requireNotNull(store.selectedDaily)
            val puzzle = WordSearchGenerator.generate(
                entry.run.theme, entry.run.difficulty, entry.run.seed
            )
            puzzle.targets.forEach {
                store.dailyClaim(it.positions.first(), it.positions.last())
            }
            assertEquals(1, store.profile.dailyCompletions)
            assertTrue(store.equipPortrait("woodland_rabbit"))
            store.updateDisplayName("  Meadow  ")
            val imported = store.previewBackup(store.exportBackup())
            assertEquals("Meadow", imported.identity.displayName)
            assertEquals("woodland_rabbit", imported.identity.portraitId)
            val reloaded = SoloStore(context, name)
            assertEquals(imported, reloaded.profile)
            assertEquals(1, reloaded.profile.dailyCompletions)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }

    @Test fun unsupportedFutureSchemaIsNotSilentlyOverwritten() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04b_future_" + System.nanoTime() + ".json"
        val file = File(context.filesDir, name)
        val original = """{"schema":999,"futureState":{"doNotErase":true}}"""
        try {
            file.writeText(original)
            val store = SoloStore(context, name)
            assertNotNull(store.warning)
            store.updateDisplayName("Should Not Write")
            assertEquals(original, file.readText())
        } finally {
            file.delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
