package com.playereden.gridmates.wordsearch

import android.content.Context
import android.util.AtomicFile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.SoloResult
import com.playereden.gridmates.wordsearch.core.SoloRun
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordSearchRules
import com.playereden.gridmates.wordsearch.core.WordTheme
import com.playereden.gridmates.wordsearch.core.WordClaim
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * No account, network, or Android backup. An AtomicFile commits the entire local
 * profile in one operation, retaining its .bak recovery path on failed writes.
 *
 * Malformed existing data is preserved, NEVER silently reset/overwritten.
 */
internal class SoloStore(context: Context) {
    private val atomic = AtomicFile(File(context.filesDir, "gridmates_wordsearch_solo_v1.json"))
    private var blocked = false
    var warning by mutableStateOf<String?>(null)
        private set

    var profile by mutableStateOf(readInitial())
        private set

    private fun initial(): SoloProfile =
        SoloProfile(SoloProgressRules.fresh(seed = System.currentTimeMillis()))

    private fun readInitial(): SoloProfile {
        if (!atomic.baseFile.exists() && !File(atomic.baseFile.path + ".bak").exists())
            return initial()
        return try {
            val source = atomic.openRead().use { it.readBytes().toString(StandardCharsets.UTF_8) }
            decode(source)
        } catch (ex: Exception) {
            blocked = true
            warning = "Your previous Word Search data couldn't be read. Its file is preserved. " +
                "New changes won't overwrite it; export or recovery support is planned."
            initial()
        }
    }

    private fun decode(raw: String): SoloProfile {
        val root = JSONObject(raw)
        require(root.getInt("schema") == 1) { "Unsupported save schema" }
        val active = root.getJSONObject("active")
        val run = SoloRun(
            theme = WordTheme.valueOf(active.getString("theme")),
            difficulty = WordDifficulty.valueOf(active.getString("difficulty")),
            seed = active.getLong("seed"),
            foundIds = active.optJSONArray("found").strings().toSet(),
            hints = active.optJSONObject("hints").toIntMap(),
            elapsedSeconds = active.optLong("seconds", 0L)
        )
        val clean = SoloProgressRules.validate(run)
        val results = root.optJSONArray("results")
        val loaded = mutableListOf<SoloResult>()
        if (results != null) for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            loaded += SoloResult(
                puzzleId = item.getString("id"),
                theme = WordTheme.valueOf(item.getString("theme")),
                difficulty = WordDifficulty.valueOf(item.getString("difficulty")),
                wordCount = item.getInt("words"),
                elapsedSeconds = item.getLong("seconds").coerceAtLeast(0),
                hintSteps = item.getInt("hints").coerceAtLeast(0),
                completedAtMs = item.getLong("completedAt")
            )
        }
        val valid = loaded.filter {
            it.puzzleId.isNotBlank() && it.wordCount in 1..30 &&
                it.completedAtMs > 0 && it.hintSteps >= 0
        }.distinctBy { it.puzzleId }
        return SoloProfile(clean, valid)
    }

    private fun JSONArray?.strings(): List<String> {
        if (this == null) return emptyList()
        return List(length()) { getString(it) }
    }
    private fun JSONObject?.toIntMap(): Map<String, Int> {
        if (this == null) return emptyMap()
        val keys = keys()
        val data = mutableMapOf<String, Int>()
        while (keys.hasNext()) {
            val key = keys.next()
            data[key] = getInt(key)
        }
        return data
    }

    private fun encode(value: SoloProfile): ByteArray {
        val run = value.active
        val active = JSONObject().put("theme", run.theme.name)
            .put("difficulty", run.difficulty.name).put("seed", run.seed)
            .put("seconds", run.elapsedSeconds)
            .put("found", JSONArray(run.foundIds.sorted()))
            .put("hints", JSONObject(run.hints))
        val results = JSONArray()
        for (r in value.results) results.put(JSONObject().put("id", r.puzzleId)
            .put("theme", r.theme.name).put("difficulty", r.difficulty.name)
            .put("words", r.wordCount).put("seconds", r.elapsedSeconds)
            .put("hints", r.hintSteps).put("completedAt", r.completedAtMs))
        return JSONObject().put("schema", 1).put("active", active)
            .put("results", results).toString().toByteArray(StandardCharsets.UTF_8)
    }

    fun flush() {
        if (blocked) return
        var stream: java.io.FileOutputStream? = null
        try {
            stream = atomic.startWrite()
            stream.write(encode(profile))
            atomic.finishWrite(stream)
            warning = null
        } catch (ex: Exception) {
            if (stream != null) atomic.failWrite(stream)
            warning = "Unable to save Word Search progress. Please free device storage."
        }
    }

    /** Manual Android document backup for QA installs without stable signing. */
    fun exportBackup(): String {
        flush()
        return encode(profile).toString(StandardCharsets.UTF_8)
    }

    /** Parse and validate without modifying existing progress. */
    fun previewBackup(raw: String): SoloProfile = decode(raw)

    /**
     * Only invoke following an explicit confirmation screen.
     * A failed write does not intentionally discard the old atomic backup.
     */
    fun restoreBackup(restored: SoloProfile): Boolean {
        if (blocked) {
            warning = "Recovery of an unreadable save must be handled separately. " +
                "The original data is still protected."
            return false
        }
        profile = restored.copy(active = SoloProgressRules.validate(restored.active))
        flush()
        return warning == null
    }

    fun choose(theme: WordTheme, difficulty: WordDifficulty) {
        if (blocked) return
        profile = profile.copy(active = SoloProgressRules.fresh(theme, difficulty, nextSeed()))
        flush()
    }

    fun newPuzzle() {
        if (blocked) return
        val current = profile.active
        profile = profile.copy(active = SoloProgressRules.fresh(current.theme,
            current.difficulty, nextSeed()))
        flush()
    }

    private fun nextSeed(): Long {
        // Avoid two rapid sessions accidentally receiving the same ID.
        return maxOf(System.currentTimeMillis(), profile.active.seed + 1)
    }

    fun claim(start: GridPoint, end: GridPoint): WordClaim {
        if (blocked) return WordClaim(ClaimStatus.INVALID)
        val current = profile.active
        val (next, claim) = SoloProgressRules.find(current, start, end)
        if (claim.status == ClaimStatus.FOUND && !blocked) {
            profile = SoloProgressRules.complete(profile.copy(active = next), System.currentTimeMillis())
            flush() // save-before-confirmation; no loss of a newly discovered word
        }
        return claim
    }

    fun hint(): Pair<SoloRun, com.playereden.gridmates.wordsearch.core.WordTarget?> {
        if (blocked) return profile.active to null
        val (next, target) = SoloProgressRules.hint(profile.active)
        if (next != profile.active) {
            profile = profile.copy(active = next)
            flush()
        }
        return next to target
    }

    fun tick() {
        if (blocked) return
        val p = profile
        if (p.active.foundIds.size >= p.active.difficulty.targetCount) return
        profile = p.copy(active = p.active.copy(elapsedSeconds = p.active.elapsedSeconds + 1))
        if (profile.active.elapsedSeconds % 10L == 0L) flush()
    }
}
