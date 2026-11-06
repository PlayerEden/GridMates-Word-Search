package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Android runtime QA: feedback settings survive a completely new store instance. */
class FeedbackPreferencePersistenceTest {
    @Test fun optionalSoundAndHapticsPersistSeparatelyFromPuzzleBackup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04d_b4_feedback_" + System.nanoTime() + ".json"
        try {
            val first = SoloStore(context, name)
            assertTrue(first.hapticsEnabled)
            assertFalse(first.soundEnabled)
            first.setHapticsEnabled(false)
            first.setSoundEnabled(true)
            val second = SoloStore(context, name)
            assertFalse(second.hapticsEnabled)
            assertTrue(second.soundEnabled)
            // Preferences aren't puzzle claims, so schema and seed stay intact.
            assertEquals(first.profile.active.puzzleId, second.profile.active.puzzleId)
        } finally {
            context.getSharedPreferences(name + "_feedback", 0).edit().clear().commit()
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
