package com.playereden.gridmates.wordsearch.core

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.security.MessageDigest

/**
 * WS-11A/B: fixed, offline *metadata* for future curated puzzle books.
 *
 * There are intentionally NO production book records in this source. Only a
 * fully reviewed, immutable puzzle payload may become a published page.
 * Free Play, Daily, Nearby and all existing Solo save IDs remain separate.
 */
data class BookPage(
    val id: String,
    val puzzleId: String,
    val generatorVersion: Int,
    val lexiconVersion: String,
    val boardSize: PuzzleBoardSize,
    val challenge: PuzzleChallenge,
    val seed: Long,
    /** SHA-256 of BookBoardDigest.v1(puzzle), including letters AND answer paths. */
    val boardSha256: String
)

data class BookChapter(
    val id: String,
    val title: String,
    val pages: List<BookPage>
)

data class PuzzleBook(
    val id: String,
    val title: String,
    val volume: Int,
    val theme: WordTheme,
    val chapters: List<BookChapter>,
    /** Optional visual reference, never proof that art was approved. */
    val coverAssetId: String? = null
)

/**
 * An empty catalog is valid: the app must not invent playable books when the
 * editorial content or final artwork has not been approved.
 */
class BookCatalog(val books: List<PuzzleBook> = emptyList()) {
    private val byId: Map<String, PuzzleBook>

    init {
        require(books.size <= 200) { "Unreasonable book catalog size" }
        val allIds = mutableSetOf<String>()
        val allPuzzleIds = mutableSetOf<String>()
        for (book in books) {
            require(safeId(book.id) && allIds.add(book.id)) { "Invalid/duplicate book ID" }
            require(titleOk(book.title) && book.volume in 1..100) { "Invalid book title/volume" }
            require(book.coverAssetId == null || safeId(book.coverAssetId)) {
                "Invalid cover resource ID"
            }
            require(book.chapters.isNotEmpty() && book.chapters.size <= 80) {
                "Published books must have chapters"
            }
            for (chapter in book.chapters) {
                require(safeId(chapter.id) && allIds.add(chapter.id)) {
                    "Invalid/duplicate chapter ID"
                }
                require(titleOk(chapter.title) && chapter.pages.isNotEmpty() &&
                    chapter.pages.size <= 150) { "Invalid/empty chapter" }
                for (page in chapter.pages) {
                    require(safeId(page.id) && allIds.add(page.id)) {
                        "Invalid/duplicate page ID"
                    }
                    require(page.puzzleId.startsWith("wordsearch:") &&
                        page.puzzleId.length in 15..200 &&
                        !page.puzzleId.contains("PREVIEW", ignoreCase = true) &&
                        allPuzzleIds.add(page.puzzleId)) {
                        "Unapproved, preview or duplicated puzzle ID"
                    }
                    require(page.generatorVersion in 1..100 && safeId(page.lexiconVersion)) {
                        "Unversioned chapter content"
                    }
                    require(page.boardSize in PuzzleBoardSize.soloPresets) {
                        "New books cannot republish legacy board presets"
                    }
                    require(SHA256.matches(page.boardSha256) &&
                        page.boardSha256.any { it != '0' }) {
                        "Missing/noncanonical board content SHA-256"
                    }
                }
            }
        }
        byId = books.associateBy { it.id }
    }

    fun book(bookId: String): PuzzleBook? = byId[bookId]

    fun pages(bookId: String): List<BookPage> =
        book(bookId)?.chapters?.flatMap { it.pages } ?: emptyList()

    fun page(bookId: String, pageId: String): BookPage? =
        pages(bookId).firstOrNull { it.id == pageId }

    /**
     * This checks actual immutable page contents, not just ID/seed. The
     * WordSearchRules.complete check happens separately in BookProgressRules.
     */
    fun matchesPublishedPuzzle(bookId: String, pageId: String, puzzle: WordSearchPuzzle): Boolean {
        val book = book(bookId) ?: return false
        val page = page(bookId, pageId) ?: return false
        if (puzzle.id != page.puzzleId || puzzle.seed != page.seed ||
            puzzle.contentVersion != page.generatorVersion ||
            puzzle.theme != book.theme ||
            puzzle.size != page.boardSize.cells ||
            puzzle.targets.size != page.boardSize.targets ||
            puzzle.rows.any { row -> row.length != puzzle.size ||
                row.any { it !in 'A'..'Z' } } ||
            puzzle.targets.map { it.id }.distinct().size != puzzle.targets.size ||
            puzzle.targets.any { target ->
                target.word.isBlank() || target.positions.size != target.word.length ||
                    target.positions.withIndex().any { (i, point) ->
                        puzzle.letter(point) != target.word[i]
                    }
            }
        ) return false
        return BookBoardDigest.sha256(puzzle) == page.boardSha256
    }

    /**
     * Never silently change the pages or published puzzle recipe behind a saved
     * ID. New book IDs are fine; replacing/removing an old book is not.
     * Display titles/cover resource labels may be updated independently.
     */
    fun requireCompatibleUpgrade(next: BookCatalog) {
        for (oldBook in books) {
            val newer = next.book(oldBook.id)
            require(newer != null && oldBook.theme == newer.theme &&
                oldBook.volume == newer.volume) {
                "Published volume removed or repurposed"
            }
            val oldChapters = oldBook.chapters
            val newChapters = newer.chapters
            require(oldChapters.size == newChapters.size &&
                oldChapters.indices.all { i ->
                    oldChapters[i].id == newChapters[i].id &&
                        oldChapters[i].pages == newChapters[i].pages
                }) { "Existing published chapter/page content changed" }
        }
    }

    companion object {
        private val ID = Regex("[a-z][a-z0-9-]{2,63}")
        private val SHA256 = Regex("[0-9a-f]{64}")

        private fun safeId(id: String) = ID.matches(id)
        private fun titleOk(value: String) =
            value.length in 1..80 && value == value.trim() &&
                value.any { !it.isWhitespace() } && value.none { it.isISOControl() }
    }
}

/** Stable on-device and editorial SHA-256 for exact grid rows and target paths.
 * Versioned explicitly: if serialization ever changes use a NEW digest method,
 * never reinterpret a hash that has already been published.
 */
object BookBoardDigest {
    const val VERSION = 1

    fun sha256(puzzle: WordSearchPuzzle): String {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { stream ->
            fun putString(value: String) {
                val encoded = value.toByteArray(Charsets.UTF_8)
                stream.writeInt(encoded.size)
                stream.write(encoded)
            }
            stream.writeInt(VERSION)
            putString(puzzle.id)
            stream.writeLong(puzzle.seed)
            stream.writeInt(puzzle.contentVersion)
            putString(puzzle.theme.name)
            putString(puzzle.difficulty.name)
            stream.writeInt(puzzle.rows.size)
            puzzle.rows.forEach(::putString)
            stream.writeInt(puzzle.targets.size)
            puzzle.targets.forEach { target ->
                putString(target.id)
                putString(target.word)
                stream.writeInt(target.positions.size)
                target.positions.forEach { point ->
                    stream.writeInt(point.row)
                    stream.writeInt(point.col)
                }
            }
        }
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }
    }
}
