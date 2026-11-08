package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** WS-09C: setting up a puzzle must not mutate active/paused games prematurely. */
class SoloSetupPreferenceAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun lastSuccessfulSoloConfigurationPersistsAcrossRestartAndCancelDoesNotSave() {
        val name = "ws09c_preset_" + System.nanoTime() + ".json"
        try {
            val first = SoloStore(ctx, name)
            val original = first.profile.active
            val before = first.exportBackup()
            val initial = first.soloSetupPreset()
            assertEquals(WordTheme.WOODLAND, initial.theme)
            assertEquals(PuzzleBoardSize.BOOK_CLASSIC, initial.board)
            assertEquals(PuzzleChallenge.CLASSIC, initial.challenge)
            // Opening the setup route calls only the read operation.
            assertEquals(before, first.exportBackup())
            assertEquals(original, first.profile.active)

            assertTrue(first.chooseV2(WordTheme.SPACE,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT))
            val generated = first.profile.active
            assertEquals(1, first.profile.pausedSolo.size)
            assertEquals(original, first.profile.pausedSolo.single())
            val next = SoloStore(ctx, name)
            assertEquals(generated, next.profile.active)
            assertEquals(original, next.profile.pausedSolo.single())
            assertEquals(SoloSetupPreset(WordTheme.SPACE, PuzzleBoardSize.BOOK_BIG,
                PuzzleChallenge.EXPERT), next.soloSetupPreset())
            val backup = next.exportBackup()
            assertTrue(backup.contains("\"pausedSolo\""))
        } finally { cleanup(name) }
    }

    @Test fun malformedLastChoicePreferenceFallsBackToValidBookOptions() {
        val name = "ws09c_legacy_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            val before = store.profile
            ctx.getSharedPreferences(name + "_solo_setup", 0).edit()
                .putString("theme", "INVALID")
                .putString("board", "FUTURE_100X100")
                .putString("challenge", "INVALID")
                .commit()
            val reopened = SoloStore(ctx, name)
            assertEquals(before, reopened.profile)
            val preset = reopened.soloSetupPreset()
            assertEquals(WordTheme.WOODLAND, preset.theme)
            assertEquals(PuzzleBoardSize.BOOK_CLASSIC, preset.board)
            assertEquals(PuzzleChallenge.CLASSIC, preset.challenge)
            assertFalse(preset.board !in PuzzleBoardSize.bookPresets)
        } finally { cleanup(name) }
    }

    private fun cleanup(name: String) {
        for (suffix in listOf("", ".bak", ".new"))
            File(ctx.filesDir, name + suffix).delete()
        ctx.getSharedPreferences(name + "_solo_setup", 0).edit().clear().commit()
    }
}
