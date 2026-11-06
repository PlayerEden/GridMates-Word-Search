package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Instrumentation execution on a device/emulator is a separate QA gate. */
class SoloPersistenceTest {
    @Test fun saveResumeCompletionAndExportImportPreserveRecords() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws03_test_" + System.nanoTime() + ".json"
        try {
            val first = SoloStore(context, name)
            first.choose(WordTheme.SPACE, WordDifficulty.MEDIUM)
            val run = first.profile.active
            val puzzle = WordSearchGenerator.generate(run.theme, run.difficulty, run.seed)
            val target = puzzle.targets.first()
            assertEquals(ClaimStatus.FOUND,
                first.claim(target.positions.first(), target.positions.last()).status)
            first.setForeground(true)
            first.tick()
            first.setForeground(false)
            first.flush()

            val resumed = SoloStore(context, name)
            assertEquals(puzzle.id, resumed.profile.active.puzzleId)
            assertTrue(target.id in resumed.profile.active.foundIds)
            assertEquals(1L, resumed.profile.active.elapsedSeconds)
            assertEquals(0, resumed.profile.completions)

            val hints = resumed.hint()
            assertTrue(hints.second != null)
            for (remaining in puzzle.targets.drop(1)) {
                assertEquals(ClaimStatus.FOUND,
                    resumed.claim(remaining.positions.first(), remaining.positions.last()).status)
            }
            assertEquals(1, resumed.profile.completions)
            assertTrue(resumed.profile.results.first().assisted)

            val reread = SoloStore(context, name)
            assertEquals(1, reread.profile.completions)
            val exported = reread.exportBackup()
            val preview = reread.previewBackup(exported)
            assertEquals(reread.profile, preview)
            assertFalse(exported.contains("password"))
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }

    @Test fun invalidBackupCannotReplaceExistingSave() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws03_bad_test_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            store.flush()
            val good = store.profile
            var rejected = false
            try {
                store.previewBackup("{\"schema\":42,\"active\":{}}")
            } catch (e: Exception) { rejected = true }
            assertTrue(rejected)
            assertEquals(good, store.profile)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
