package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordHintPolicy
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * This test source is compiled on GitHub Actions; actual Android test
 * execution needs a separately documented emulator/device QA gate.
 */
class HintAllowancePersistenceTest {
    @Test fun hintsAreFiniteAndPersistThroughRestartAndBackup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04d_b3_hints_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            store.chooseV2(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC)
            assertEquals(3, WordHintPolicy.remaining(store.profile.active))
            repeat(3) { index ->
                val (next, target) = store.hint()
                assertNotNull(target)
                assertEquals(2 - index, WordHintPolicy.remaining(next))
            }
            val before = store.profile.active
            assertEquals(3, before.hints.values.sum())
            val exhausted = store.hint()
            assertNull(exhausted.second)
            assertEquals(before, exhausted.first)
            val backup = store.exportBackup()
            val restarted = SoloStore(context, name)
            assertEquals(before, restarted.profile.active)
            assertEquals(0, WordHintPolicy.remaining(restarted.profile.active))
            assertEquals(before, restarted.previewBackup(backup).active)

            restarted.openDaily("2026-11-06")
            assertEquals(2, WordHintPolicy.remaining(
                requireNotNull(restarted.selectedDaily).run, daily = true))
            repeat(2) { assertNotNull(restarted.dailyHint().second) }
            val daily = requireNotNull(restarted.selectedDaily).run
            assertEquals(0, WordHintPolicy.remaining(daily, daily = true))
            assertNull(restarted.dailyHint().second)
            val secondRestart = SoloStore(context, name)
            secondRestart.openDaily("2026-11-06")
            assertEquals(daily, requireNotNull(secondRestart.selectedDaily).run)
            assertEquals(DailyChallenge.dailyRun("2026-11-06").puzzleId,
                requireNotNull(secondRestart.selectedDaily).run.puzzleId)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
