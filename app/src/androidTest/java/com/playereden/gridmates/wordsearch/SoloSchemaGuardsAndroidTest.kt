package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Reject present-but-wrong-type profile fields instead of silently treating
 * them as missing. Missing keys in older backups remain backward compatible.
 */
class SoloSchemaGuardsAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun malformedPresentFieldsCannotEraseProgressOrOverwriteExistingSave() {
        val name = "ws06i_schema_guard_" + System.nanoTime() + ".json"
        val save = File(ctx.filesDir, name)
        try {
            val store = SoloStore(ctx, name)
            val baseline = store.exportBackup()
            val before = store.profile
            val cases = listOf(
                "results" to { root: JSONObject -> root.put("results", "not-an-array") },
                "daily" to { root: JSONObject -> root.put("daily", JSONObject()) },
                "identity" to { root: JSONObject -> root.put("identity", "not-an-object") },
                "active.found" to { root: JSONObject ->
                    root.getJSONObject("active").put("found", "not-an-array")
                },
                "active.hints" to { root: JSONObject ->
                    root.getJSONObject("active").put("hints", org.json.JSONArray().put(1).put(2))
                }
            )
            for ((label, poison) in cases) {
                val invalid = JSONObject(baseline).also { poison(it) }.toString()
                assertTrue("$label import should be rejected",
                    runCatching { store.previewBackup(invalid) }.isFailure)
                assertEquals("$label import must not change in-memory data", before, store.profile)

                save.writeText(invalid, Charsets.UTF_8)
                val reopened = SoloStore(ctx, name)
                assertTrue("$label corrupted local save should warn", reopened.warning != null)
                assertTrue("$label must not allow exporting a fabricated reset",
                    runCatching { reopened.exportBackup() }.isFailure)
                reopened.flush()
                assertEquals("$label original corrupt file is protected",
                    invalid, save.readText(Charsets.UTF_8))
                save.writeText(baseline, Charsets.UTF_8)
            }
        } finally { clean(name) }
    }

    @Test fun missingLegacyOptionalFieldsStillLoadAndValidRoundTripWorks() {
        val name = "ws06i_legacy_fields_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            val valid = store.exportBackup()
            assertEquals(store.profile, store.previewBackup(valid))
            val legacy = JSONObject(valid).apply {
                remove("results")
                remove("daily")
                remove("identity")
                getJSONObject("active").apply {
                    remove("found")
                    remove("hints")
                }
            }
            val restored = store.previewBackup(legacy.toString())
            assertTrue(restored.results.isEmpty())
            assertTrue(restored.dailyEntries.isEmpty())
            assertTrue(restored.active.foundIds.isEmpty())
            assertTrue(restored.active.hints.isEmpty())
            assertEquals(store.profile.active.puzzleId, restored.active.puzzleId)
        } finally { clean(name) }
    }

    @Test fun invalidOrDuplicateSoloResultsFailClosedWithoutErasingLocalHistory() {
        val name = "ws07i_solo_result_guard_" + System.nanoTime() + ".json"
        val save = File(ctx.filesDir, name)
        try {
            val store = SoloStore(ctx, name)
            val baseline = store.exportBackup()
            val before = store.profile
            val entry = JSONObject()
                .put("id", "wordsearch:v2:en:WOODLAND:BOOK_CLASSIC:CLASSIC:123456")
                .put("theme", "WOODLAND")
                .put("difficulty", "MEDIUM")
                .put("words", 12)
                .put("seconds", 90)
                .put("hints", 1)
                .put("completedAt", 1783456789000L)
                .put("board", "BOOK_CLASSIC")
                .put("challenge", "CLASSIC")
            val goodRoot = JSONObject(baseline)
                .put("results", JSONArray().put(entry))
            val imported = store.previewBackup(goodRoot.toString())
            assertEquals(1, imported.results.size)
            assertEquals(entry.getString("id"), imported.results.single().puzzleId)
            // Preview is read-only: even a valid backup isn't installed without confirmation.
            assertEquals(before, store.profile)

            val corruptions = listOf<Pair<String, (JSONObject) -> Unit>>(
                "duplicate id" to { root ->
                    root.getJSONArray("results").put(JSONObject(entry.toString()))
                },
                "blank id" to { root ->
                    root.getJSONArray("results").getJSONObject(0).put("id", "")
                },
                "zero words" to { root ->
                    root.getJSONArray("results").getJSONObject(0).put("words", 0)
                },
                "negative elapsed time" to { root ->
                    root.getJSONArray("results").getJSONObject(0).put("seconds", -1)
                },
                "negative hint count" to { root ->
                    root.getJSONArray("results").getJSONObject(0).put("hints", -1)
                },
                "invalid completion timestamp" to { root ->
                    root.getJSONArray("results").getJSONObject(0).put("completedAt", 0)
                },
                "non-object result" to { root ->
                    root.getJSONArray("results").put("invalid-entry")
                }
            )
            for ((label, poison) in corruptions) {
                val invalid = JSONObject(goodRoot.toString()).also { poison(it) }.toString()
                assertTrue("$label import should be rejected, never silently truncated",
                    runCatching { store.previewBackup(invalid) }.isFailure)
                assertEquals("$label import must not mutate current progress", before, store.profile)

                save.writeText(invalid, Charsets.UTF_8)
                val reopened = SoloStore(ctx, name)
                assertTrue("$label corrupt installed save should warn", reopened.warning != null)
                assertTrue("$label must block export of fabricated fallback",
                    runCatching { reopened.exportBackup() }.isFailure)
                reopened.flush()
                assertEquals("$label original bytes protected", invalid, save.readText(Charsets.UTF_8))
                save.writeText(baseline, Charsets.UTF_8)
            }
        } finally { clean(name) }
    }

    private fun clean(name: String) {
        for (suffix in listOf("", ".bak", ".new")) {
            File(ctx.filesDir, name + suffix).delete()
        }
    }
}
