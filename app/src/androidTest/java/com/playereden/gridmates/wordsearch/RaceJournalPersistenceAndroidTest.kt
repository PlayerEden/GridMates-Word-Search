package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Actual Android atomic-file saves, reloads, JSON backup and import safety. */
class RaceJournalPersistenceAndroidTest {
    private fun finish(seed: Long = 81244L): CoopSnapshot {
        val room = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, seed), "ws06b-test-$seed",
            NearbyMatch.SHARED_BOARD_RACE)
        room.admitOrRejoinGuest()
        room.puzzle.targets.forEachIndexed { idx, target ->
            room.submit(if (idx < 4) CoopSeat.GUEST else CoopSeat.HOST,
                room.roomId, "test-$idx",
                target.positions.first(), target.positions.last())
        }
        return room.snapshot()
    }

    @Test fun finishedRaceIsPersistedExactlyOnceAcrossReloadAndBackup() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws06b_race_" + System.nanoTime() + ".json"
        try {
            val saved = SoloStore(ctx, name)
            val originalSolo = saved.profile.active
            val complete = finish()
            assertTrue(saved.recordRace(complete, CoopSeat.HOST, 1_700_000_000_000L))
            assertTrue(saved.recordRace(complete, CoopSeat.HOST, 1_700_000_001_000L))
            assertEquals(1, saved.profile.raceResults.size)
            assertEquals(originalSolo, saved.profile.active)
            val reopened = SoloStore(ctx, name)
            assertEquals(1, reopened.profile.raceSummary.played)
            assertEquals(1, reopened.profile.raceSummary.wins)
            assertEquals(8, reopened.profile.raceResults.single().ownWords)
            assertTrue(reopened.recordRace(complete, CoopSeat.HOST, 1_700_000_002_000L))
            assertEquals(1, reopened.profile.raceResults.size)

            val exported = reopened.exportBackup()
            assertEquals(1, JSONObject(exported).getJSONArray("races").length())
            val preview = reopened.previewBackup(exported)
            assertEquals(reopened.profile, preview)
            // A new installation can import the same JSON backup.
            val restoreName = "ws06b_restored_" + System.nanoTime() + ".json"
            try {
                val receiver = SoloStore(ctx, restoreName)
                assertTrue(receiver.restoreBackup(receiver.previewBackup(exported)))
                assertEquals(reopened.profile.raceResults, receiver.profile.raceResults)
                assertEquals(reopened.profile.active, receiver.profile.active)
            } finally { clean(ctx.filesDir, restoreName) }
        } finally { clean(ctx.filesDir, name) }
    }

    @Test fun legacyBackupsHaveNoRacesAndForgedResultsCannotOverwriteSave() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws06b_legacy_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            assertTrue(store.recordRace(finish(42), CoopSeat.HOST))
            val before = store.profile
            val legacy = JSONObject(store.exportBackup()).apply { remove("races") }
            assertTrue(store.previewBackup(legacy.toString()).raceResults.isEmpty())
            val tampered = JSONObject(store.exportBackup())
            tampered.getJSONArray("races").getJSONObject(0).put("own", 999)
            assertTrue(runCatching { store.previewBackup(tampered.toString()) }.isFailure)
            assertEquals(before, store.profile)
            val duplicate = JSONObject(store.exportBackup())
            duplicate.getJSONArray("races").put(
                JSONObject(duplicate.getJSONArray("races").getJSONObject(0).toString()))
            assertTrue(runCatching { store.previewBackup(duplicate.toString()) }.isFailure)
            assertEquals(before, store.profile)
        } finally { clean(ctx.filesDir, name) }
    }

    @Test fun malformedPresentRaceArrayCannotEraseOldHistoryOrOverwriteDisk() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws06h_race_safety_" + System.nanoTime() + ".json"
        try {
            val saved = SoloStore(ctx, name)
            assertTrue(saved.recordRace(finish(777L), CoopSeat.HOST))
            val initial = saved.exportBackup()
            val before = saved.profile
            val corrupt = JSONObject(initial).put("races", "not-an-array").toString()

            // The import preview must reject even though "races" is optional
            // in legitimate old backups. All existing in-memory data stays.
            assertTrue(runCatching { saved.previewBackup(corrupt) }.isFailure)
            assertEquals(before, saved.profile)
            assertEquals(1, saved.profile.raceResults.size)

            // Likewise, an unreadable on-device save is protected from
            // being replaced with an empty "new user" profile.
            File(ctx.filesDir, name).writeText(corrupt, Charsets.UTF_8)
            val reopened = SoloStore(ctx, name)
            assertTrue(reopened.warning?.contains("couldn't be read") == true)
            assertTrue(runCatching { reopened.exportBackup() }.isFailure)
            reopened.flush()
            assertEquals(corrupt, File(ctx.filesDir, name).readText(Charsets.UTF_8))
        } finally { clean(ctx.filesDir, name) }
    }

    private fun clean(directory: File, name: String) {
        for (suffix in listOf("", ".bak", ".new")) File(directory, name + suffix).delete()
    }
}
