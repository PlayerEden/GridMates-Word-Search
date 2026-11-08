package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * WS-09A1: recreate real historical v1/v2 partial saves with found word IDs,
 * hints and an active timer, then test the existing schema-4 backup contract.
 *
 * This is deliberately tests-only: old generators, signer and save encoding
 * must not change while baseline compatibility is being established.
 */
class HistoricalPartialProgressAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun legacyAndBookV2PartiallySolvedBoardsRoundTripWithoutChangingWords() {
        data class Case(
            val theme: WordTheme,
            val difficulty: WordDifficulty,
            val board: PuzzleBoardSize?,
            val challenge: PuzzleChallenge?,
            val seed: Long
        )
        val cases = listOf(
            Case(WordTheme.WOODLAND, WordDifficulty.EASY, null, null, 42L),
            Case(WordTheme.GARDEN, WordDifficulty.HARD, PuzzleBoardSize.COMPACT,
                PuzzleChallenge.EXPERT, 471L),
            Case(WordTheme.SPACE, WordDifficulty.MEDIUM, PuzzleBoardSize.BOOK_SHORT,
                PuzzleChallenge.CLASSIC, 20261106L),
            Case(WordTheme.WOODLAND, WordDifficulty.MEDIUM, PuzzleBoardSize.BOOK_CLASSIC,
                PuzzleChallenge.CLASSIC, 31415L),
            Case(WordTheme.GARDEN, WordDifficulty.MEDIUM, PuzzleBoardSize.BOOK_BIG,
                PuzzleChallenge.CLASSIC, 76124L)
        )
        for ((index, fixture) in cases.withIndex()) {
            val filename = "ws09a1_historical_${index}_" + System.nanoTime() + ".json"
            try {
                val store = SoloStore(ctx, filename)
                val sourceRun = if (fixture.board == null)
                    SoloProgressRules.fresh(fixture.theme, fixture.difficulty, fixture.seed)
                else SoloProgressRules.freshV2(
                    fixture.theme, fixture.board, requireNotNull(fixture.challenge), fixture.seed
                )
                val originalPuzzle = SoloProgressRules.puzzle(sourceRun)
                val found = originalPuzzle.targets.first()
                val hinted = originalPuzzle.targets.first { it.id != found.id }
                val root = JSONObject(store.exportBackup())
                root.put("active", JSONObject()
                    .put("theme", sourceRun.theme.name)
                    .put("difficulty", sourceRun.difficulty.name)
                    .put("seed", sourceRun.seed)
                    .put("found", JSONArray().put(found.id))
                    .put("hints", JSONObject().put(hinted.id, 2))
                    .put("seconds", 734L)
                    .apply {
                        sourceRun.boardSize?.let { put("board", it.name) }
                        sourceRun.challenge?.let { put("challenge", it.name) }
                    })
                val prepared = store.previewBackup(root.toString())
                assertEquals(sourceRun.puzzleId, prepared.active.puzzleId)
                assertEquals(setOf(found.id), prepared.active.foundIds)
                assertEquals(mapOf(hinted.id to 2), prepared.active.hints)
                assertEquals(734L, prepared.active.elapsedSeconds)
                assertSamePuzzle(originalPuzzle, SoloProgressRules.puzzle(prepared.active))
                assertTrue("prepared historical save should restore", store.restoreBackup(prepared))

                // Exactly the same file name mimics app relaunch/process death.
                val reopened = SoloStore(ctx, filename)
                assertEquals(setOf(found.id), reopened.profile.active.foundIds)
                assertEquals(2, reopened.profile.active.hints[hinted.id])
                assertEquals(734L, reopened.profile.active.elapsedSeconds)
                assertSamePuzzle(originalPuzzle, SoloProgressRules.puzzle(reopened.profile.active))
                val saved = reopened.exportBackup()
                val imported = SoloStore(ctx, filename + "_new")
                try {
                    val preview = imported.previewBackup(saved)
                    assertTrue("preview should not replace active run before confirmation",
                        imported.profile.active.puzzleId != preview.active.puzzleId)
                    assertTrue(imported.restoreBackup(preview))
                    assertEquals(prepared.active, imported.profile.active)
                    assertSamePuzzle(originalPuzzle, SoloProgressRules.puzzle(imported.profile.active))
                    assertEquals(setOf(found.id), imported.profile.active.foundIds)
                    assertEquals(2, imported.profile.active.hints[hinted.id])
                } finally {
                    cleanup(filename + "_new")
                }
            } finally {
                cleanup(filename)
            }
        }
    }

    @Test fun historicalDailyPartialAndCompletedRecordsSurviveBackupRestore() {
        val filename = "ws09a1_daily_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, filename)
            val older = DailyChallenge.start("2026-11-05")
            val newer = DailyChallenge.start("2026-11-06")
            val oldPuzzle = SoloProgressRules.puzzle(older.run)
            val newPuzzle = SoloProgressRules.puzzle(newer.run)
            val oldWord = oldPuzzle.targets.first().id
            val newWord = newPuzzle.targets.first().id
            val root = JSONObject(store.exportBackup())
            val completeRun = oldPuzzle.targets.map { it.id }.toSet()
            root.put("daily", JSONArray()
                .put(JSONObject()
                    .put("date", older.date)
                    .put("run", runJson(older.run, completeRun, emptyMap(), 99L))
                    .put("completedAt", 1794000000000L))
                .put(JSONObject()
                    .put("date", newer.date)
                    .put("run", runJson(newer.run, setOf(newWord),
                        mapOf(newPuzzle.targets[1].id to 1), 37L))))
            val preview = store.previewBackup(root.toString())
            assertEquals(2, preview.dailyEntries.size)
            assertTrue(preview.dailyEntries[0].completed)
            assertFalse(preview.dailyEntries[1].completed)
            assertTrue(store.restoreBackup(preview))
            val reopened = SoloStore(ctx, filename)
            assertEquals(2, reopened.profile.dailyEntries.size)
            assertTrue(reopened.profile.dailyEntries[0].completed)
            assertEquals(completeRun, reopened.profile.dailyEntries[0].run.foundIds)
            assertEquals(setOf(newWord), reopened.profile.dailyEntries[1].run.foundIds)
            assertEquals(1, reopened.profile.dailyEntries[1].run.hints[newPuzzle.targets[1].id])
            assertEquals(37L, reopened.profile.dailyEntries[1].run.elapsedSeconds)
            assertSamePuzzle(oldPuzzle,
                SoloProgressRules.puzzle(reopened.profile.dailyEntries[0].run))
            assertSamePuzzle(newPuzzle,
                SoloProgressRules.puzzle(reopened.profile.dailyEntries[1].run))
            assertTrue(reopened.exportBackup().contains(oldWord))
        } finally {
            cleanup(filename)
        }
    }

    private fun runJson(run: com.playereden.gridmates.wordsearch.core.SoloRun,
                        found: Set<String>, hints: Map<String, Int>, seconds: Long) =
        JSONObject().put("theme", run.theme.name)
            .put("difficulty", run.difficulty.name)
            .put("seed", run.seed)
            .put("found", JSONArray(found.toList()))
            .put("hints", JSONObject(hints))
            .put("seconds", seconds)

    private fun assertSamePuzzle(expected: WordSearchPuzzle, actual: WordSearchPuzzle) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.rows, actual.rows)
        assertEquals(expected.targets, actual.targets)
    }

    private fun cleanup(name: String) {
        for (suffix in listOf("", ".bak", ".new")) {
            File(ctx.filesDir, name + suffix).delete()
        }
    }
}
