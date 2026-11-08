package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloSessionLibrary
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class MultiSessionPersistenceAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun schemaFourPartiallySolvedPuzzleMovesIntoNewLibraryWithoutLosingOtherJournals() {
        val name = "ws09b_schema5_" + System.nanoTime() + ".json"
        val imported = name + "_copy"
        try {
            val legacy = InstrumentationRegistry.getInstrumentation().context.assets
                .open("ws09a2_legacy_partial_v086.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val store = SoloStore(ctx, name)
            val preview = store.previewBackup(legacy)
            assertTrue(store.restoreBackup(preview))
            val original = store.profile.active
            assertEquals(setOf("RACCOON"), original.foundIds)
            assertEquals(mapOf("SAPLING" to 2), original.hints)
            assertTrue(store.profile.pausedSolo.isEmpty())
            val daily = store.profile.dailyEntries
            val identity = store.profile.identity
            val races = store.profile.raceResults
            val duels = store.profile.duelResults

            assertTrue(store.chooseV2(WordTheme.SPACE,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC))
            assertEquals(listOf(original), store.profile.pausedSolo)
            val second = store.profile.active
            val target = SoloProgressRules.puzzle(second).targets.first()
            assertEquals(ClaimStatus.FOUND,
                store.claim(target.positions.first(), target.positions.last()).status)
            val secondPartial = store.profile.active
            assertTrue(store.chooseV2(WordTheme.GARDEN,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC))
            assertEquals(listOf(secondPartial, original), store.profile.pausedSolo)
            val third = store.profile.active
            assertTrue(store.resumeSolo(original.puzzleId))
            assertEquals(original, store.profile.active)
            assertEquals(listOf(third, secondPartial), store.profile.pausedSolo)
            assertEquals(daily, store.profile.dailyEntries)
            assertEquals(identity, store.profile.identity)
            assertEquals(races, store.profile.raceResults)
            assertEquals(duels, store.profile.duelResults)

            val exported = store.exportBackup()
            val document = JSONObject(exported)
            assertEquals(5, document.getInt("schema"))
            assertEquals(2, document.getJSONArray("pausedSolo").length())
            assertEquals(store.profile, SoloStore(ctx, name).profile)
            val clean = SoloStore(ctx, imported)
            assertTrue(clean.restoreBackup(clean.previewBackup(exported)))
            assertEquals(store.profile, SoloStore(ctx, imported).profile)
            assertTrue(clean.resumeSolo(secondPartial.puzzleId))
            assertEquals(secondPartial, clean.profile.active)
            assertEquals(2, clean.profile.pausedSolo.size)
            assertTrue(clean.discardPausedSolo(third.puzzleId))
            assertEquals(1, clean.profile.pausedSolo.size)
            assertEquals(secondPartial, clean.profile.active)
        } finally {
            cleanup(name)
            cleanup(imported)
        }
    }

    @Test fun corruptedOrDuplicatedPausedGamesFailClosedAndPreserveInstalledBytes() {
        val name = "ws09b_corrupt_" + System.nanoTime() + ".json"
        val file = File(ctx.filesDir, name)
        try {
            val store = SoloStore(ctx, name)
            assertTrue(store.newPuzzle())
            val safe = JSONObject(store.exportBackup())
            assertEquals(5, safe.getInt("schema"))
            assertEquals(1, safe.getJSONArray("pausedSolo").length())
            val cases = listOf(
                JSONObject(safe.toString()).put("pausedSolo", "not-an-array"),
                JSONObject(safe.toString()).remove("pausedSolo").let { it },
                JSONObject(safe.toString()).put("pausedSolo",
                    JSONArray().put(safe.getJSONObject("active"))),
                JSONObject(safe.toString()).put("pausedSolo",
                    JSONArray().put(JSONObject(safe.getJSONArray("pausedSolo")
                        .getJSONObject(0).toString()).put("found", JSONArray().put("BOGUS")))),
                JSONObject(safe.toString()).put("pausedSolo",
                    JSONArray().put(JSONObject(safe.getJSONArray("pausedSolo")
                        .getJSONObject(0).toString()).put("seconds", -10)))
            )
            for ((index, item) in cases.withIndex()) {
                val bad = item.toString()
                assertTrue("malformed #$index should fail preview",
                    runCatching { store.previewBackup(bad) }.isFailure)
                file.writeText(bad, Charsets.UTF_8)
                val blocked = SoloStore(ctx, name)
                assertNotNull("unreadable save must warn", blocked.warning)
                assertTrue(runCatching { blocked.exportBackup() }.isFailure)
                blocked.flush()
                assertEquals("original bytes preserved #$index", bad,
                    file.readText(Charsets.UTF_8))
                file.writeText(safe.toString(), Charsets.UTF_8)
            }
        } finally { cleanup(name) }
    }

    private fun cleanup(name: String) {
        for (suffix in listOf("", ".bak", ".new")) {
            File(ctx.filesDir, name + suffix).delete()
        }
    }
}
