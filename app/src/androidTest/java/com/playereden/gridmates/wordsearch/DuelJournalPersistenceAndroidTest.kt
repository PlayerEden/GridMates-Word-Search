package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** True AtomicFile / manual JSON backup checks on Android, no physical peer. */
class DuelJournalPersistenceAndroidTest {
    private fun finish(seed: Long = 1197L, winner: CoopSeat = CoopSeat.HOST):
            Pair<CoopSnapshot, CoopSnapshot> {
        val room = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, seed), "ws06d-android-room-" + seed,
            NearbyMatch.SPEED_DUEL)
        val duel = SpeedDuelSession(room.puzzle)
        room.admitOrRejoinGuest()
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        val loser = if (winner == CoopSeat.HOST) CoopSeat.GUEST else CoopSeat.HOST
        room.puzzle.targets.take(5).forEachIndexed { i, t ->
            duel.submit(loser, "loser-" + i,
                t.positions.first(), t.positions.last(), true)
        }
        room.puzzle.targets.forEachIndexed { i, t ->
            duel.submit(winner, "winner-" + i,
                t.positions.first(), t.positions.last(), true)
        }
        val base = room.snapshot()
        return base.copy(duel = duel.view(CoopSeat.HOST), finished = true) to
            base.copy(duel = duel.view(CoopSeat.GUEST), finished = true)
    }

    @Test fun atomicSaveReopenRetryExportAndRestorePreservesDuelSeparateFromRace() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws06d_journal_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            val beforeSolo = store.profile.active
            val (host, _) = finish()
            assertTrue(store.recordDuel(host, CoopSeat.HOST, 1_700_000_000_000L))
            assertTrue(store.recordDuel(host, CoopSeat.HOST, 1_700_000_009_000L))
            assertEquals(1, store.profile.duelSummary.played)
            assertEquals(1, store.profile.duelSummary.wins)
            assertEquals(0, store.profile.raceSummary.played)
            assertEquals(beforeSolo, store.profile.active)
            val reloaded = SoloStore(ctx, name)
            assertTrue(reloaded.recordDuel(host, CoopSeat.HOST, 1_700_001_000_000L))
            assertEquals(1, reloaded.profile.duelResults.size)
            assertEquals(1_700_000_000_000L, reloaded.profile.duelResults.single().completedAtMs)
            val json = reloaded.exportBackup()
            assertEquals(1, JSONObject(json).getJSONArray("duels").length())
            assertEquals(0, JSONObject(json).getJSONArray("races").length())
            assertEquals(reloaded.profile, reloaded.previewBackup(json))
            val importedName = "ws06d_restored_" + System.nanoTime() + ".json"
            try {
                val second = SoloStore(ctx, importedName)
                assertTrue(second.restoreBackup(second.previewBackup(json)))
                assertEquals(reloaded.profile, second.profile)
                assertEquals(1, SoloStore(ctx, importedName).profile.duelSummary.wins)
            } finally { cleanup(ctx.filesDir, importedName) }
        } finally { cleanup(ctx.filesDir, name) }
    }

    @Test fun legacyBackupAndTamperedDuelArraysFailClosedWithoutOverwriting() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws06d_invalid_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            val (host, guest) = finish(221, CoopSeat.GUEST)
            assertFalse(store.recordDuel(guest, CoopSeat.HOST))
            assertTrue(store.recordDuel(host, CoopSeat.HOST))
            val before = store.profile
            val backup = store.exportBackup()
            val legacy = JSONObject(backup).apply { remove("duels") }
            assertTrue(store.previewBackup(legacy.toString()).duelResults.isEmpty())
            assertEquals(before.raceResults, store.previewBackup(legacy.toString()).raceResults)
            val corrupted = JSONObject(backup)
            corrupted.getJSONArray("duels").getJSONObject(0).put("winner", "HOST")
            assertTrue(runCatching { store.previewBackup(corrupted.toString()) }.isFailure)
            val duplicate = JSONObject(backup)
            duplicate.getJSONArray("duels").put(
                JSONObject(duplicate.getJSONArray("duels").getJSONObject(0).toString()))
            assertTrue(runCatching { store.previewBackup(duplicate.toString()) }.isFailure)
            assertTrue(runCatching {
                store.previewBackup(JSONObject(backup).put("duels", "not-an-array").toString())
            }.isFailure)
            assertEquals(before, store.profile)
            assertEquals(before, SoloStore(ctx, name).profile)
        } finally { cleanup(ctx.filesDir, name) }
    }

    private fun cleanup(dir: File, name: String) {
        listOf("", ".bak", ".new").forEach { File(dir, name + it).delete() }
    }
}
