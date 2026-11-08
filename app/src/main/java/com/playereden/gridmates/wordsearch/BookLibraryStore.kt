package com.playereden.gridmates.wordsearch

import android.content.Context
import android.util.AtomicFile
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressCodec
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import java.io.File

/**
 * Dedicated local book-journal persistence boundary; separate from SoloStore
 * and all schema-1..5 saves. NOT activated until approved books are packaged.
 *
 * AtomicFile protects from interrupted writes. Broken/unknown catalog data is
 * preserved intact with writes blocked; we never replace it with an empty book
 * journal. All imports and commits are validated BEFORE filesystem mutation.
 */
internal class BookLibraryStore(
    context: Context,
    private val catalog: BookCatalog,
    saveName: String = "gridmates_wordsearch_book_progress_v1.dat"
) {
    private val atomic: AtomicFile
    private var current: BookLibraryProgress = BookLibraryProgress()
    var warning: String? = null
        private set
    var blocked: Boolean = false
        private set

    val progress: BookLibraryProgress get() = current

    init {
        require(Regex("[a-z0-9_-]{3,80}\\.dat").matches(saveName)) {
            "Book save filename must be local and explicit"
        }
        atomic = AtomicFile(File(context.filesDir, saveName))
        val primary = atomic.baseFile
        val backup = File(primary.path + ".bak")
        if (primary.exists() || backup.exists()) {
            try {
                val bytes = atomic.openRead().use { it.readBytes() }
                current = BookProgressCodec.decode(catalog, bytes)
            } catch (_: Exception) {
                // No reset or write: a newer catalog may be temporarily absent,
                // or the file may be corrupt and need explicit recovery.
                blocked = true
                warning = "Your saved puzzle-book progress couldn't be read. " +
                    "The original file is preserved; nothing will overwrite it."
            }
        }
    }

    fun bookmark(bookId: String, pageId: String): Boolean {
        if (blocked) return false
        return try {
            commit(BookProgressRules.bookmark(catalog, current, bookId, pageId))
        } catch (_: IllegalArgumentException) { false }
    }

    fun recordCompletion(
        bookId: String, pageId: String,
        puzzle: WordSearchPuzzle, foundIds: Set<String>
    ): Boolean {
        if (blocked) return false
        return try {
            commit(BookProgressRules.recordVerifiedCompletion(
                catalog, current, bookId, pageId, puzzle, foundIds
            ))
        } catch (_: IllegalArgumentException) { false }
    }

    /** Future manual backup button can export this exact self-validating blob. */
    fun exportForManualBackup(): ByteArray? =
        if (blocked) null else BookProgressCodec.encode(catalog, current)

    /** Caller must obtain explicit user confirmation before restoring a file. */
    fun importManualBackup(bytes: ByteArray): Boolean {
        if (blocked) return false
        return try {
            val restored = BookProgressCodec.decode(catalog, bytes)
            commit(restored)
        } catch (_: IllegalArgumentException) { false }
    }

    private fun commit(next: BookLibraryProgress): Boolean {
        if (blocked) return false
        val content = try {
            BookProgressCodec.encode(catalog, next)
        } catch (_: IllegalArgumentException) { return false }
        var stream: java.io.FileOutputStream? = null
        return try {
            stream = atomic.startWrite()
            stream.write(content)
            atomic.finishWrite(stream)
            stream = null
            current = next
            warning = null
            true
        } catch (_: Exception) {
            stream?.let {
                try { atomic.failWrite(it) } catch (_: Exception) { /* preserve original */ }
            }
            warning = "Couldn't save puzzle-book progress. Previous progress was kept."
            false
        }
    }
}
