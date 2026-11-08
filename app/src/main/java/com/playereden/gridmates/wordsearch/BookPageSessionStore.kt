package com.playereden.gridmates.wordsearch

import android.content.Context
import android.util.AtomicFile
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookPageSession
import com.playereden.gridmates.wordsearch.core.BookPageSessionCodec
import com.playereden.gridmates.wordsearch.core.BookPageSessionJournal
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.GridPoint
import com.playereden.gridmates.wordsearch.core.WordClaim
import com.playereden.gridmates.wordsearch.core.WordTarget
import java.io.File
import java.io.FileOutputStream

/**
 * WS-11D2A: standalone local in-progress word-search book page file.
 *
 * Always save a FOUND word before returning success to the UI. When a page is
 * fully solved, preserve that finished session on disk until the separate
 * committed BookLibraryStore successfully records the result. This makes
 * crashes between the two files recoverable and keeps SoloStore untouched.
 *
 * No instance is created from MainActivity until real approved book pages
 * and an accessible book-player screen have been published.
 */
internal class BookPageSessionStore(
    context: Context,
    private val catalog: BookCatalog,
    saveName: String = "gridmates_wordsearch_book_sessions_v1.dat"
) {
    private val atomic: AtomicFile
    private var current = BookPageSessionJournal()

    val journal: BookPageSessionJournal get() = current
    var blocked: Boolean = false
        private set
    var warning: String? = null
        private set

    init {
        require(Regex("[a-z0-9_-]{3,80}\\.dat").matches(saveName)) {
            "Session journal filename must be safe and local"
        }
        require(saveName != "gridmates_wordsearch_book_progress_v1.dat") {
            "Session file cannot overwrite the committed book progress"
        }
        atomic = AtomicFile(File(context.filesDir, saveName))
        val base = atomic.baseFile
        if (base.exists() || File(base.path + ".bak").exists()) {
            try {
                current = BookPageSessionCodec.decode(
                    catalog, atomic.openRead().use { it.readBytes() }
                )
            } catch (_: Exception) {
                blocked = true
                warning = "Saved book pages could not be read. Their original file is preserved; " +
                    "this game will not replace it with blank progress."
            }
        }
    }

    fun open(bookId: String, pageId: String): Boolean = try {
        if (blocked) false else commit(BookPageSessionRules.open(
            catalog, current, bookId, pageId
        ))
    } catch (_: IllegalArgumentException) { false }

    fun session(bookId: String, pageId: String): BookPageSession? =
        BookPageSessionRules.session(current, bookId, pageId)

    fun claim(
        bookId: String, pageId: String, start: GridPoint, end: GridPoint
    ): WordClaim {
        if (blocked) return WordClaim(ClaimStatus.INVALID)
        return try {
            val (updated, claim) = BookPageSessionRules.find(
                catalog, current, bookId, pageId, start, end
            )
            if (claim.status != ClaimStatus.FOUND) claim
            else if (commit(updated)) claim else WordClaim(ClaimStatus.INVALID)
        } catch (_: IllegalArgumentException) { WordClaim(ClaimStatus.INVALID) }
    }

    fun hint(bookId: String, pageId: String): WordTarget? {
        if (blocked) return null
        return try {
            val (updated, target) = BookPageSessionRules.hint(
                catalog, current, bookId, pageId
            )
            if (target != null && updated != current && commit(updated)) target else null
        } catch (_: IllegalArgumentException) { null }
    }

    fun tick(bookId: String, pageId: String, seconds: Long = 1L): Boolean {
        if (blocked) return false
        return try {
            val updated = BookPageSessionRules.tick(
                catalog, current, bookId, pageId, seconds
            )
            commit(updated)
        } catch (_: IllegalArgumentException) { false }
    }

    /**
     * Two-phase recoverable completion:
     * (1) solved session was already persisted when final word was FOUND;
     * (2) record the completed page durably in GMWB;
     * (3) only then remove the GMWS sidecar session.
     * If step 3 fails, retry safely: the completed page award is idempotent.
     */
    fun finalizePage(
        progressStore: BookLibraryStore, bookId: String, pageId: String
    ): Boolean {
        if (blocked || progressStore.blocked) return false
        return try {
            val entry = BookPageSessionRules.session(current, bookId, pageId)
                ?: return false
            if (!BookPageSessionRules.complete(catalog, entry)) return false
            val board = BookPageSessionRules.puzzle(catalog, bookId, pageId)
            if (!progressStore.recordCompletion(
                bookId, pageId, board, entry.foundIds
            )) return false
            val remaining = BookPageSessionRules.clearCommitted(
                catalog, current, progressStore.progress, bookId, pageId
            )
            commit(remaining)
        } catch (_: IllegalArgumentException) { false }
    }

    /** Finish a page left solved on disk if the app stopped before award. */
    fun recoverSolvedPages(progressStore: BookLibraryStore): Boolean {
        if (blocked || progressStore.blocked) return false
        for (entry in current.sessions.toList()) {
            if (BookPageSessionRules.complete(catalog, entry) &&
                !finalizePage(progressStore, entry.bookId, entry.pageId)
            ) return false
        }
        return true
    }

    private fun commit(next: BookPageSessionJournal): Boolean {
        if (blocked) return false
        if (next == current) return true
        val bytes = try {
            BookPageSessionCodec.encode(catalog, next)
        } catch (_: IllegalArgumentException) { return false }
        var stream: FileOutputStream? = null
        return try {
            stream = atomic.startWrite()
            stream.write(bytes)
            atomic.finishWrite(stream)
            stream = null
            current = next
            warning = null
            true
        } catch (_: Exception) {
            stream?.let {
                try { atomic.failWrite(it) } catch (_: Exception) { /* keep earlier data */ }
            }
            warning = "Book page could not be saved. Earlier progress was preserved."
            false
        }
    }
}
