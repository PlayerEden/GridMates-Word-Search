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
import com.playereden.gridmates.wordsearch.core.LargeBoardBookmark
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.RaceResult
import com.playereden.gridmates.wordsearch.core.RaceJournalRules
import com.playereden.gridmates.wordsearch.core.DuelResult
import com.playereden.gridmates.wordsearch.core.DuelJournalRules
import com.playereden.gridmates.wordsearch.core.NearbyMatch
import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import com.playereden.gridmates.wordsearch.core.CoopSeat
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.SoloResult
import com.playereden.gridmates.wordsearch.core.SoloSessionLibrary
import com.playereden.gridmates.wordsearch.core.SoloRun
import com.playereden.gridmates.wordsearch.core.LargeBoardBookmark
import com.playereden.gridmates.wordsearch.core.LargeBoardViewport
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
/** Persistent choice defaults only: progress is still stored in the atomic profile. */
internal data class SoloSetupPreset(
    val theme: WordTheme,
    val board: PuzzleBoardSize,
    val challenge: PuzzleChallenge
)

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

    // Remember the last successful New Solo configuration across app launches
    // and compatible APK updates. Preferences are nonessential: full puzzle
    // state and progress remain in the portable schema-5 JSON backup.
    private val soloSetupPrefs = context.getSharedPreferences(
        saveName + "_solo_setup", Context.MODE_PRIVATE
    )

    /** Invalid or legacy values fall back to known, supported book presets. */
    fun soloSetupPreset(): SoloSetupPreset {
        val active = profile.active
        val theme = soloSetupPrefs.getString("theme", null)
            ?.let { saved -> WordTheme.entries.firstOrNull { it.name == saved } }
            ?: active.theme
        val board = soloSetupPrefs.getString("board", null)
            ?.let { saved -> PuzzleBoardSize.soloPresets.firstOrNull {
                it.name == saved
            } }
            ?: active.boardSize?.takeIf { it in PuzzleBoardSize.soloPresets }
            ?: PuzzleBoardSize.BOOK_CLASSIC
        val challenge = soloSetupPrefs.getString("challenge", null)
            ?.let { saved -> PuzzleChallenge.entries.firstOrNull {
                it.name == saved
            } }
            ?: active.challenge
            ?: when (active.difficulty) {
                WordDifficulty.EASY -> PuzzleChallenge.RELAXED
                WordDifficulty.MEDIUM -> PuzzleChallenge.CLASSIC
                WordDifficulty.HARD -> PuzzleChallenge.EXPERT
            }
        return SoloSetupPreset(theme, board, challenge)
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

    // Optional view-only preferences. Schema-5 progress and portable JSON backup
    // remain byte-format compatible; an absent/corrupt bookmark simply starts Fit.
    // A later opt-in backup migration can include viewing positions separately.
    private val largeBoardViewPrefs = context.getSharedPreferences(
        saveName + "_large_board_views", Context.MODE_PRIVATE
    )

    /** Only the currently active 25/30-cell Solo game can restore a view. */
    fun largeBoardBookmark(puzzleId: String): LargeBoardBookmark? {
        if (blocked || profile.active.puzzleId != puzzleId ||
            (profile.active.boardSize?.cells ?: 0) < 25) return null
        val raw = runCatching {
            largeBoardViewPrefs.getString(puzzleId, null)
        }.getOrNull() ?: return null
        val fields = raw.split(',')
        if (fields.size != 3) return null
        return LargeBoardBookmark.fromFieldsOrNull(
            puzzleId,
            fields[0].toFloatOrNull(),
            fields[1].toFloatOrNull(),
            fields[2].toFloatOrNull()
        )
    }

    /**
     * Persist a settled zoom/map/pan position, never a gameplay claim.
     * This intentionally does NOT flush or mutate the schema-5 SoloProfile.
     */
    fun saveLargeBoardBookmark(bookmark: LargeBoardBookmark) {
        if (blocked || profile.active.puzzleId != bookmark.puzzleId ||
            (profile.active.boardSize?.cells ?: 0) < 25) return
        val encoded = "${bookmark.zoom},${bookmark.centerX},${bookmark.centerY}"
        if (largeBoardViewPrefs.getString(bookmark.puzzleId, null) != encoded) {
            largeBoardViewPrefs.edit().putString(bookmark.puzzleId, encoded).apply()
        }
    }

    /** Keep at most the active and paused game's views after a safe session commit. */
    private fun pruneLargeBoardBookmarks() {
        if (blocked) return
        val retained = (listOf(profile.active) + profile.pausedSolo)
            .filter { (it.boardSize?.cells ?: 0) >= 25 }
            .map { it.puzzleId }.toSet()
        val stale = largeBoardViewPrefs.all.keys.filter { it !in retained }
        if (stale.isNotEmpty()) {
            largeBoardViewPrefs.edit().apply {
                stale.forEach(::remove)
            }.apply()
        }
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
        val schema = root.getInt("schema")
        require(schema in 1..5) { "Unsupported save schema" }
        val active = root.getJSONObject("active")
        val run = readRun(active)
        val clean = SoloProgressRules.validate(run)
        val results = root.optJSONArray("results")
        require(!root.has("results") || results != null) { "Malformed Solo completion records" }
        val loaded = mutableListOf<SoloResult>()
        if (results != null) for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            loaded += SoloResult(
                puzzleId = item.getString("id"),
                theme = WordTheme.valueOf(item.getString("theme")),
                difficulty = WordDifficulty.valueOf(item.getString("difficulty")),
                wordCount = item.getInt("words"),
                elapsedSeconds = item.getLong("seconds"),
                hintSteps = item.getInt("hints"),
                completedAtMs = item.getLong("completedAt"),
                boardSize = item.optString("board", "").takeIf { it.isNotBlank() }
                    ?.let { PuzzleBoardSize.valueOf(it) },
                challenge = item.optString("challenge", "").takeIf { it.isNotBlank() }
                    ?.let { PuzzleChallenge.valueOf(it) }
            )
        }
        // Fail closed rather than silently shrink a player's completion history
        // when old or imported JSON includes corrupt records. The original file
        // remains intact, and import previews never mutate the installed profile.
        require(loaded.all {
            it.puzzleId.isNotBlank() && it.wordCount in 1..44 &&
                it.elapsedSeconds >= 0L && it.hintSteps >= 0 &&
                it.completedAtMs > 0L
        }) { "Malformed Solo completion record" }
        require(loaded.map { it.puzzleId }.distinct().size == loaded.size) {
            "Duplicate Solo completion record"
        }
        val daily = mutableListOf<DailyProgress>()
        val dailyRaw = root.optJSONArray("daily")
        require(!root.has("daily") || dailyRaw != null) { "Malformed Daily journal" }
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
        require(!root.has("identity") || identityData != null) { "Malformed player identity" }
        val rawIdentity = PlayerIdentity(
            displayName = identityData?.optString("name", "Player") ?: "Player",
            portraitId = identityData?.optString("portraitId", "woodland_owl") ?: "woodland_owl"
        )
        val races = mutableListOf<RaceResult>()
        val raceRaw = root.optJSONArray("races")
        // Missing means a legitimate pre-race backup; present-but-not-array
        // must fail CLOSED rather than silently erase saved Versus results.
        require(!root.has("races") || raceRaw != null) { "Malformed Race journal" }
        if (raceRaw != null) {
            require(raceRaw.length() <= 1_000) { "Too many race records" }
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
        // Optional additive schema-4 journal. Older backups and on-device
        // profiles do not contain this key; they safely start at zero Duels.
        val duelRaw = root.optJSONArray("duels")
        require(!root.has("duels") || duelRaw != null) { "Malformed Speed Duel journal" }
        val duels = mutableListOf<DuelResult>()
        if (duelRaw != null) {
            require(duelRaw.length() <= 1_000) { "Too many Speed Duel records" }
            for (i in 0 until duelRaw.length()) {
                val item = duelRaw.getJSONObject(i)
                duels += DuelResult(
                    roomId = item.getString("room"),
                    puzzleId = item.getString("puzzle"),
                    boardFingerprint = item.getString("digest"),
                    theme = WordTheme.valueOf(item.getString("theme")),
                    board = PuzzleBoardSize.valueOf(item.getString("board")),
                    challenge = PuzzleChallenge.valueOf(item.getString("challenge")),
                    localSeat = CoopSeat.valueOf(item.getString("seat")),
                    winner = CoopSeat.valueOf(item.getString("winner")),
                    ownWords = item.getInt("own"),
                    partnerWords = item.getInt("partner"),
                    completedAtMs = item.getLong("completedAt")
                ).validate()
            }
        }
        // Schema 1–4 had exactly one active Solo puzzle. Starting with schema 5,
        // suspended sessions are part of the SAME atomic save and JSON backup.
        // Missing or wrong-type schema-5 data must fail closed, not erase games.
        val pausedRaw = root.optJSONArray("pausedSolo")
        if (schema == 5) require(pausedRaw != null) { "Missing paused Solo journal" }
        else require(!root.has("pausedSolo")) { "Unexpected paused Solo journal" }
        val paused = mutableListOf<SoloRun>()
        if (pausedRaw != null) {
            require(pausedRaw.length() <= SoloSessionLibrary.MAX_PAUSED) {
                "Too many paused Solo puzzles"
            }
            for (i in 0 until pausedRaw.length()) {
                val saved = readRun(pausedRaw.getJSONObject(i))
                // Reject and protect a corrupt file rather than silently
                // stripping any found word, hint or elapsed time.
                require(SoloProgressRules.validate(saved) == saved) {
                    "Invalid saved Solo progress"
                }
                paused += saved
            }
        }
        val loadedProfile = SoloProfile(clean, loaded, daily, rawIdentity,
            RaceJournalRules.validateImported(races),
            DuelJournalRules.validateImported(duels), paused)
        SoloSessionLibrary.verify(loadedProfile)
        return loadedProfile.copy(identity = WoodlandCollection.sanitize(rawIdentity, loadedProfile))
    }

    private fun readRun(source: JSONObject): SoloRun {
        val found = source.optJSONArray("found")
        require(!source.has("found") || found != null) { "Malformed found-word progress" }
        val hints = source.optJSONObject("hints")
        require(!source.has("hints") || hints != null) { "Malformed hint progress" }
        val run = SoloRun(
            theme = WordTheme.valueOf(source.getString("theme")),
            difficulty = WordDifficulty.valueOf(source.getString("difficulty")),
            seed = source.getLong("seed"),
            foundIds = found.strings().toSet(),
            hints = hints.toIntMap(),
            elapsedSeconds = source.optLong("seconds", 0L),
            boardSize = source.optString("board", "").takeIf { it.isNotBlank() }
                ?.let { PuzzleBoardSize.valueOf(it) },
            challenge = source.optString("challenge", "").takeIf { it.isNotBlank() }
                ?.let { PuzzleChallenge.valueOf(it) }
        )
        // "viewport" is purely optional presentation state, including in old
        // schema-5 saves and manual backups. Never reject or overwrite a valid
        // found-word profile because zoom metadata is missing or malformed.
        val view = source.optJSONObject("viewport")
        val bookmark = view?.let {
            LargeBoardBookmark.fromFieldsOrNull(
                it.optString("puzzleId", ""),
                it.optDouble("zoom", Double.NaN).toFloat(),
                it.optDouble("centerX", Double.NaN).toFloat(),
                it.optDouble("centerY", Double.NaN).toFloat()
            )
        }?.takeIf { it.puzzleId == run.puzzleId &&
            run.boardSize in PuzzleBoardSize.largePresets }
        return run.copy(viewportBookmark = bookmark)
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

    private fun writeRun(run: SoloRun): JSONObject {
        val data = JSONObject().put("theme", run.theme.name)
            .put("difficulty", run.difficulty.name).put("seed", run.seed)
            .put("seconds", run.elapsedSeconds)
            .put("found", JSONArray(run.foundIds.sorted()))
            .put("hints", JSONObject(run.hints))
        run.boardSize?.let { data.put("board", it.name) }
        run.challenge?.let { data.put("challenge", it.name) }
        run.viewportBookmark?.takeIf {
            run.boardSize in PuzzleBoardSize.largePresets &&
                it.puzzleId == run.puzzleId
        }?.let { bookmark ->
            data.put("viewport", JSONObject()
                .put("puzzleId", bookmark.puzzleId)
                .put("zoom", bookmark.zoom.toDouble())
                .put("centerX", bookmark.centerX.toDouble())
                .put("centerY", bookmark.centerY.toDouble()))
        }
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
        val duels = JSONArray()
        for (d in value.duelResults) {
            duels.put(JSONObject()
                .put("room", d.roomId).put("puzzle", d.puzzleId)
                .put("digest", d.boardFingerprint).put("theme", d.theme.name)
                .put("board", d.board.name).put("challenge", d.challenge.name)
                .put("seat", d.localSeat.name).put("winner", d.winner.name)
                .put("own", d.ownWords).put("partner", d.partnerWords)
                .put("completedAt", d.completedAtMs))
        }
        val paused = JSONArray()
        for (run in value.pausedSolo) paused.put(writeRun(run))
        return JSONObject().put("schema", 5).put("active", active)
            .put("results", results).put("daily", daily).put("identity", identity)
            .put("races", races).put("duels", duels).put("pausedSolo", paused)
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
        val checked = runCatching {
            restored.copy(active = SoloProgressRules.validate(restored.active))
                .also(SoloSessionLibrary::verify)
        }.getOrElse {
            warning = "Invalid Solo backup. Your existing progress was not replaced."
            return false
        }
        val previous = profile
        profile = checked.copy(identity = WoodlandCollection.sanitize(checked.identity, checked))
        flush()
        if (warning != null) {
            // Never present an uncommitted import as the user's saved data.
            profile = previous
            return false
        }
        pruneLargeBoardBookmarks()
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
        val result = runCatching {
            RaceResult.fromVerifiedSnapshot(snapshot, localSeat, nowMs)
        }.getOrElse { return false } // never reward an unverified snapshot
        if (profile.raceResults.any { it.roomId == result.roomId }) return true
        val previous = profile
        val updated = runCatching {
            RaceJournalRules.addOnce(previous.raceResults, result)
        }.getOrElse {
            warning = "Versus journal is full. Back up your records before playing more."
            return false
        }
        profile = previous.copy(raceResults = updated)
        flush()
        if (warning != null) {
            // AtomicFile failWrite retains the prior on-disk profile.
            profile = previous
            return false
        }
        return true
    }

    /**
     * Save only a completed recipient-scoped host-confirmed Speed Duel state.
     * Neither a guest's submitted bare score nor an unfinished room can
     * become a permanent record. Rejoins and rerenders cannot award twice.
     */
    fun recordDuel(snapshot: CoopSnapshot, localSeat: CoopSeat,
                   nowMs: Long = System.currentTimeMillis()): Boolean {
        if (blocked) return false
        val result = runCatching {
            DuelResult.fromVerifiedSnapshot(snapshot, localSeat, nowMs)
        }.getOrElse {
            warning = "Could not verify this Speed Duel result."
            return false
        }
        if (profile.duelResults.any { it.roomId == result.roomId }) return true
        val previous = profile
        val updated = runCatching {
            DuelJournalRules.addOnce(previous.duelResults, result)
        }.getOrElse {
            warning = "Speed Duel journal full. Export a backup before playing more."
            return false
        }
        profile = previous.copy(duelResults = updated)
        flush()
        if (warning != null) {
            profile = previous // AtomicFile protects previous on-disk save.
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

    /** Commit a whole session switch or import; roll back in-memory state on disk failure. */
    private fun commitSession(next: SoloProfile): Boolean {
        if (blocked) return false
        val previous = profile
        profile = next
        flush()
        if (warning != null) {
            profile = previous
            return false
        }
        pruneLargeBoardBookmarks()
        return true
    }

    /** Historical v1 choice remains supported; it now preserves unfinished Solo. */
    fun choose(theme: WordTheme, difficulty: WordDifficulty): Boolean {
        if (blocked) return false
        val candidate = SoloProgressRules.fresh(theme, difficulty, nextSeed())
        SoloProgressRules.puzzle(candidate)
        val updated = SoloSessionLibrary.start(profile, candidate)
        if (updated == null) {
            warning = "My Puzzles is full. Remove a paused puzzle to start another."
            return false
        }
        return commitSession(updated)
    }

    /** All new Solo sizes. V3 boards never pass through frozen V2 generation. */
    fun chooseSolo(theme: WordTheme, board: PuzzleBoardSize,
                   challenge: PuzzleChallenge): Boolean {
        if (blocked) return false
        val candidate = SoloProgressRules.freshForBoard(theme, board, challenge, nextSeed())
        SoloProgressRules.puzzle(candidate) // verify BEFORE changing saved session
        val updated = SoloSessionLibrary.start(profile, candidate)
        if (updated == null) {
            warning = "My Puzzles is full. Remove a paused puzzle to start another."
            return false
        }
        if (!commitSession(updated)) return false
        soloSetupPrefs.edit()
            .putString("theme", theme.name)
            .putString("board", board.name)
            .putString("challenge", challenge.name)
            .apply()
        return true
    }

    /** Existing callers of the V2 API remain supported for legacy book sizes. */
    fun chooseV2(theme: WordTheme, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge): Boolean {
        require(board in PuzzleBoardSize.v2Presets)
        return chooseSolo(theme, board, challenge)
    }

    fun newPuzzle(): Boolean {
        if (blocked) return false
        val current = profile.active
        val board = current.boardSize?.takeIf { it in PuzzleBoardSize.soloPresets }
            ?: PuzzleBoardSize.BOOK_CLASSIC
        val challenge = current.challenge ?: when (current.difficulty) {
            WordDifficulty.EASY -> PuzzleChallenge.RELAXED
            WordDifficulty.MEDIUM -> PuzzleChallenge.CLASSIC
            WordDifficulty.HARD -> PuzzleChallenge.EXPERT
        }
        return chooseSolo(current.theme, board, challenge)
    }

    /**
     * Persist only the optional view location for the CURRENT large Solo game.
     * The AtomicFile profile and manual backup own the same metadata, so
     * switching among 24 paused games does not swap or discard another view.
     * Never allow a stale gesture or a Nearby/Daily view to mutate Solo data.
     */
    fun saveLargeBoardBookmark(bookmark: LargeBoardBookmark): Boolean {
        if (blocked) return false
        val old = profile
        val run = old.active
        if (run.boardSize !in PuzzleBoardSize.largePresets ||
            run.puzzleId != bookmark.puzzleId) return false
        if (run.viewportBookmark == bookmark) return true
        profile = old.copy(active = run.copy(viewportBookmark = bookmark))
        flush()
        if (warning != null) {
            profile = old
            return false
        }
        return true
    }

    /** Selected archived entry becomes active; previous unfinished active is retained. */
    fun resumeSolo(puzzleId: String): Boolean {
        if (blocked) return false
        val updated = SoloSessionLibrary.resume(profile, puzzleId) ?: return false
        return commitSession(updated)
    }

    /** A paused puzzle is only removed after a separate confirmation in the UI. */
    fun discardPausedSolo(puzzleId: String): Boolean {
        if (blocked) return false
        val updated = SoloSessionLibrary.discardPaused(profile, puzzleId) ?: return false
        return commitSession(updated)
    }

    private fun nextSeed(): Long {
        val seeds = (listOf(profile.active) + profile.pausedSolo)
            .map { it.seed }.filter { it >= 0 && it < Long.MAX_VALUE }
        val maxPrior = seeds.maxOrNull() ?: 0L
        return maxOf(System.currentTimeMillis(), maxPrior + 1)
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
