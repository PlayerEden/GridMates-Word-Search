package com.playereden.gridmates.wordsearch

import android.content.Context
import android.util.AtomicFile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.playereden.gridmates.wordsearch.core.BoardFontCollection
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.DailyProgress
import com.playereden.gridmates.wordsearch.core.PlayerIdentity
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.RaceResult
import com.playereden.gridmates.wordsearch.core.RaceJournalRules
import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import com.playereden.gridmates.wordsearch.core.CoopSeat
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
internal class SoloStore(
    context: Context,
    saveName: String = "gridmates_wordsearch_solo_v1.json"
) {
    private val atomic = AtomicFile(File(context.filesDir, saveName))
    // Gameplay feedback is a tiny local preference, not puzzle/save data.
    // A profile import or new game must not unexpectedly reset user settings.
    private val feedbackPrefs = context.getSharedPreferences(
        saveName + "_feedback", Context.MODE_PRIVATE
    )
    var hapticsEnabled by mutableStateOf(feedbackPrefs.getBoolean("haptics", true))
        private set
    var soundEnabled by mutableStateOf(feedbackPrefs.getBoolean("sound", false))
        private set

    fun updateHapticsPreference(enabled: Boolean) {
        hapticsEnabled = enabled
        feedbackPrefs.edit().putBoolean("haptics", enabled).apply()
    }

    fun updateSoundPreference(enabled: Boolean) {
        soundEnabled = enabled
        feedbackPrefs.edit().putBoolean("sound", enabled).apply()
    }

    private val fontPrefs = context.getSharedPreferences(
        saveName + "_board_fonts", Context.MODE_PRIVATE
    )
    var selectedBoardFontId by mutableStateOf(
        fontPrefs.getString("selected_id", BoardFontCollection.DEFAULT_ID)
            ?: BoardFontCollection.DEFAULT_ID
    )
        private set

    /** Unknown/locked IDs fall back safely without corrupting a saved puzzle. */
    val effectiveBoardFontId: String
        get() = BoardFontCollection.effective(profile, selectedBoardFontId)

    fun equipBoardFont(id: String): Boolean {
        if (blocked || !BoardFontCollection.allowed(profile, id)) return false
        selectedBoardFontId = id
        fontPrefs.edit().putString("selected_id", id).apply()
        return true
    }

    private var blocked = false
    private var inForeground = false
    fun setForeground(value: Boolean) {
        inForeground = value
        if (!value) flush()
    }

    var selectedDailyDate by mutableStateOf(DailyChallenge.today())
        private set

    val selectedDaily: DailyProgress?
        get() = profile.dailyEntries.firstOrNull { it.date == selectedDailyDate }

    var warning by mutableStateOf<String?>(null)
        private set

    var profile by mutableStateOf(readInitial())
        private set

    private fun initial(): SoloProfile =
        SoloProfile(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, System.currentTimeMillis()
        ))

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
        require(root.getInt("schema") in 1..4) { "Unsupported save schema" }
        val active = root.getJSONObject("active")
        val run = readRun(active)
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
                completedAtMs = item.getLong("completedAt"),
                boardSize = item.optString("board", "").takeIf { it.isNotBlank() }
                    ?.let { PuzzleBoardSize.valueOf(it) },
                challenge = item.optString("challenge", "").takeIf { it.isNotBlank() }
                    ?.let { PuzzleChallenge.valueOf(it) }
            )
        }
        val valid = loaded.filter {
            it.puzzleId.isNotBlank() && it.wordCount in 1..30 &&
                it.completedAtMs > 0 && it.hintSteps >= 0
        }.distinctBy { it.puzzleId }
        val daily = mutableListOf<DailyProgress>()
        val dailyRaw = root.optJSONArray("daily")
        if (dailyRaw != null) for (i in 0 until dailyRaw.length()) {
            val item = dailyRaw.getJSONObject(i)
            val entry = DailyProgress(
                date = item.getString("date"),
                run = readRun(item.getJSONObject("run")),
                completedAtMs = item.optLong("completedAt", 0L).takeIf { it > 0L }
            )
            daily += DailyChallenge.validate(entry)
        }
        require(daily.map { it.date }.distinct().size == daily.size) {
            "Duplicate daily date in backup"
        }
        val identityData = root.optJSONObject("identity")
        val rawIdentity = PlayerIdentity(
            displayName = identityData?.optString("name", "Player") ?: "Player",
            portraitId = identityData?.optString("portraitId", "woodland_owl") ?: "woodland_owl"
        )
        val races = mutableListOf<RaceResult>()
        val raceRaw = root.optJSONArray("races")
        if (raceRaw != null) {
            require(raceRaw.length() <= 10_000) { "Too many race records" }
            for (i in 0 until raceRaw.length()) {
                val item = raceRaw.getJSONObject(i)
                races += RaceResult(
                    roomId = item.getString("room"),
                    puzzleId = item.getString("puzzle"),
                    boardFingerprint = item.getString("digest"),
                    theme = WordTheme.valueOf(item.getString("theme")),
                    board = PuzzleBoardSize.valueOf(item.getString("board")),
                    challenge = PuzzleChallenge.valueOf(item.getString("challenge")),
                    localSeat = CoopSeat.valueOf(item.getString("seat")),
                    ownWords = item.getInt("own"),
                    partnerWords = item.getInt("partner"),
                    completedAtMs = item.getLong("completedAt")
                ).validate()
            }
        }
        val loadedProfile = SoloProfile(clean, valid, daily, rawIdentity,
            RaceJournalRules.validateImported(races))
        return loadedProfile.copy(identity = WoodlandCollection.sanitize(rawIdentity, loadedProfile))
    }

    private fun readRun(source: JSONObject): SoloRun = SoloRun(
        theme = WordTheme.valueOf(source.getString("theme")),
        difficulty = WordDifficulty.valueOf(source.getString("difficulty")),
        seed = source.getLong("seed"),
        foundIds = source.optJSONArray("found").strings().toSet(),
        hints = source.optJSONObject("hints").toIntMap(),
        elapsedSeconds = source.optLong("seconds", 0L),
        boardSize = source.optString("board", "").takeIf { it.isNotBlank() }
            ?.let { PuzzleBoardSize.valueOf(it) },
        challenge = source.optString("challenge", "").takeIf { it.isNotBlank() }
            ?.let { PuzzleChallenge.valueOf(it) }
    )

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

    private fun writeRun(run: SoloRun): JSONObject {
        val data = JSONObject().put("theme", run.theme.name)
            .put("difficulty", run.difficulty.name).put("seed", run.seed)
            .put("seconds", run.elapsedSeconds)
            .put("found", JSONArray(run.foundIds.sorted()))
            .put("hints", JSONObject(run.hints))
        run.boardSize?.let { data.put("board", it.name) }
        run.challenge?.let { data.put("challenge", it.name) }
        return data
    }

    private fun encode(value: SoloProfile): ByteArray {
        val active = writeRun(value.active)
        val results = JSONArray()
        for (r in value.results) results.put(JSONObject().put("id", r.puzzleId)
            .put("theme", r.theme.name).put("difficulty", r.difficulty.name)
            .put("words", r.wordCount).put("seconds", r.elapsedSeconds)
            .put("hints", r.hintSteps).put("completedAt", r.completedAtMs)
            .apply {
                r.boardSize?.let { put("board", it.name) }
                r.challenge?.let { put("challenge", it.name) }
            })
        val daily = JSONArray()
        for (entry in value.dailyEntries) {
            val item = JSONObject().put("date", entry.date)
                .put("run", writeRun(entry.run))
            entry.completedAtMs?.let { item.put("completedAt", it) }
            daily.put(item)
        }
        val identity = JSONObject()
            .put("name", value.identity.displayName)
            .put("portraitId", value.identity.portraitId)
        val races = JSONArray()
        for (r in value.raceResults) {
            races.put(JSONObject()
                .put("room", r.roomId).put("puzzle", r.puzzleId)
                .put("digest", r.boardFingerprint).put("theme", r.theme.name)
                .put("board", r.board.name).put("challenge", r.challenge.name)
                .put("seat", r.localSeat.name).put("own", r.ownWords)
                .put("partner", r.partnerWords).put("completedAt", r.completedAtMs))
        }
        return JSONObject().put("schema", 4).put("active", active)
            .put("results", results).put("daily", daily).put("identity", identity)
            .put("races", races)
            .toString().toByteArray(StandardCharsets.UTF_8)
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
        // If the existing on-device save is unreadable, 'profile' is only a
        // temporary in-memory fallback. Never export it as if it were a valid
        // recovered backup; this would mislead a player into losing history.
        check(!blocked) { "Unreadable save is protected; export unavailable" }
        flush()
        check(warning == null) { "Could not safely save current progress before export" }
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
        val checked = restored.copy(active = SoloProgressRules.validate(restored.active))
        val previous = profile
        profile = checked.copy(identity = WoodlandCollection.sanitize(checked.identity, checked))
        flush()
        if (warning != null) {
            // Never present an uncommitted import as the user's saved data.
            profile = previous
            return false
        }
        return true
    }

    /**
     * Called only for a completed host-verified nearby Race snapshot. The same
     * room may be replayed repeatedly after rejoin or recomposition; its result
     * is recorded once and stays in the existing atomic local save + JSON backup.
     */
    fun recordRace(snapshot: CoopSnapshot, localSeat: CoopSeat,
                   nowMs: Long = System.currentTimeMillis()): Boolean {
        if (blocked) return false
        val result = RaceResult.fromVerifiedSnapshot(snapshot, localSeat, nowMs)
        if (profile.raceResults.any { it.roomId == result.roomId }) return true
        val previous = profile
        profile = previous.copy(raceResults =
            RaceJournalRules.addOnce(previous.raceResults, result))
        flush()
        if (warning != null) {
            profile = previous
            return false
        }
        return true
    }

    /** Opens today's puzzle or a previously started date, without touching Solo. */
    fun openDaily(date: String = DailyChallenge.today()) {
        DailyChallenge.day(date)
        if (blocked) return
        selectedDailyDate = date
        if (profile.dailyEntries.none { it.date == date }) {
            profile = profile.copy(
                dailyEntries = profile.dailyEntries + DailyChallenge.start(date)
            )
            flush()
        }
    }

    private fun replaceDaily(entry: DailyProgress) {
        profile = profile.copy(dailyEntries = profile.dailyEntries.map {
            if (it.date == entry.date) entry else it
        })
        flush()
    }

    fun dailyClaim(start: GridPoint, end: GridPoint): WordClaim {
        if (blocked) return WordClaim(ClaimStatus.INVALID)
        val current = selectedDaily ?: return WordClaim(ClaimStatus.INVALID)
        val (updated, claim) = SoloProgressRules.find(current.run, start, end)
        if (claim.status == ClaimStatus.FOUND) {
            replaceDaily(DailyChallenge.complete(
                current.copy(run = updated), System.currentTimeMillis()
            ))
        }
        return claim
    }

    fun dailyHint(): Pair<SoloRun?, com.playereden.gridmates.wordsearch.core.WordTarget?> {
        if (blocked) return selectedDaily?.run to null
        val current = selectedDaily ?: return null to null
        if (current.completed) return current.run to null
        val (updated, target) = SoloProgressRules.hint(current.run, daily = true)
        if (updated != current.run) replaceDaily(current.copy(run = updated))
        return updated to target
    }

    fun tickDaily() {
        if (blocked || !inForeground) return
        val current = selectedDaily ?: return
        if (current.completed || current.run.foundIds.size >=
            current.run.difficulty.targetCount) return
        val updated = current.copy(run = current.run.copy(
            elapsedSeconds = current.run.elapsedSeconds + 1L
        ))
        profile = profile.copy(dailyEntries = profile.dailyEntries.map {
            if (it.date == current.date) updated else it
        })
        if (updated.run.elapsedSeconds % 10L == 0L) flush()
    }

    fun updateDisplayName(input: String) {
        if (blocked) return
        profile = profile.copy(
            identity = profile.identity.copy(displayName = WoodlandCollection.sanitizeName(input))
        )
        flush()
    }

    /** The collection is a catalogue; locked portraits never equip. */
    fun equipPortrait(id: String): Boolean {
        if (blocked) return false
        val updated = WoodlandCollection.equip(profile, id) ?: return false
        profile = updated
        flush()
        return warning == null
    }

    /** Compatibility path used by historical QA fixtures. Never mutates v1 rules. */
    fun choose(theme: WordTheme, difficulty: WordDifficulty) {
        if (blocked) return
        profile = profile.copy(active = SoloProgressRules.fresh(theme, difficulty, nextSeed()))
        flush()
    }

    fun chooseV2(theme: WordTheme, board: PuzzleBoardSize, challenge: PuzzleChallenge) {
        if (blocked) return
        // Build before replacing the previous saved board. Invalid input cannot
        // discard the active session, and no new game is started without action.
        val candidate = SoloProgressRules.freshV2(theme, board, challenge, nextSeed())
        SoloProgressRules.puzzle(candidate)
        profile = profile.copy(active = candidate)
        flush()
    }

    fun newPuzzle() {
        if (blocked) return
        val current = profile.active
        // Fresh Solo plays use book-sized choices. Previously saved 8x8/10x10
        // and original 12x12 v2 boards still load untouched until replaced.
        val board = current.boardSize?.takeIf { it in PuzzleBoardSize.bookPresets }
            ?: PuzzleBoardSize.BOOK_CLASSIC
        val challenge = current.challenge ?: when (current.difficulty) {
            WordDifficulty.EASY -> PuzzleChallenge.RELAXED
            WordDifficulty.MEDIUM -> PuzzleChallenge.CLASSIC
            WordDifficulty.HARD -> PuzzleChallenge.EXPERT
        }
        chooseV2(current.theme, board, challenge)
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
        if (blocked || !inForeground) return
        val p = profile
        if (p.active.foundIds.size >=
            (p.active.boardSize?.targets ?: p.active.difficulty.targetCount)) return
        profile = p.copy(active = p.active.copy(elapsedSeconds = p.active.elapsedSeconds + 1))
        if (profile.active.elapsedSeconds % 10L == 0L) flush()
    }
}
