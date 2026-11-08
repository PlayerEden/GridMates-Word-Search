package com.playereden.gridmates.wordsearch.core

/**
 * WS-11B local-first future book progress.
 *
 * Not wired into the existing schema-5 Solo profile or on-device persistence
 * yet. Keeping the book journal independent prevents accidental corruption of
 * older Free Play, Daily, Nearby and multiplayer progress.
 */
data class BookVolumeProgress(
    val bookId: String,
    val completedPageIds: Set<String> = emptySet(),
    val bookmarkedPageId: String? = null
)

data class BookLibraryProgress(
    val volumes: Map<String, BookVolumeProgress> = emptyMap()
)

/** Pure, deterministic operations. Serialization/AtomicFile migration is WS-11C. */
object BookProgressRules {
    fun validate(catalog: BookCatalog, progress: BookLibraryProgress) {
        require(progress.volumes.size <= catalog.books.size) { "Unknown book progress" }
        progress.volumes.forEach { (bookId, saved) ->
            val pages = catalog.pages(bookId)
            require(pages.isNotEmpty() && saved.bookId == bookId) {
                "Unknown/incorrect saved book ID"
            }
            val validPageIds = pages.map { it.id }.toSet()
            require(saved.completedPageIds.all { it in validPageIds }) {
                "Unknown saved chapter page; preserve raw save for recovery"
            }
            require(saved.bookmarkedPageId == null ||
                (saved.bookmarkedPageId in validPageIds &&
                    saved.bookmarkedPageId !in saved.completedPageIds)) {
                "Invalid bookmark or bookmark points at completed page"
            }
        }
    }

    fun completedCount(catalog: BookCatalog, progress: BookLibraryProgress, bookId: String): Int {
        validate(catalog, progress)
        requireNotNull(catalog.book(bookId)) { "Unknown book" }
        return progress.volumes[bookId]?.completedPageIds?.size ?: 0
    }

    fun isComplete(catalog: BookCatalog, progress: BookLibraryProgress, bookId: String): Boolean {
        val total = catalog.pages(bookId).size
        require(total > 0) { "Unknown book" }
        return completedCount(catalog, progress, bookId) == total
    }

    /** Respects a valid manual bookmark, otherwise the first unfinished page. */
    fun continuePage(catalog: BookCatalog, progress: BookLibraryProgress,
                     bookId: String): BookPage? {
        validate(catalog, progress)
        val pages = catalog.pages(bookId)
        require(pages.isNotEmpty()) { "Unknown book" }
        val state = progress.volumes[bookId]
        val completed = state?.completedPageIds.orEmpty()
        val bookmarked = state?.bookmarkedPageId
        return pages.firstOrNull { it.id == bookmarked && it.id !in completed }
            ?: pages.firstOrNull { it.id !in completed }
    }

    /** Player-chosen, unfinished page, with no save/puzzle mutation. */
    fun bookmark(catalog: BookCatalog, progress: BookLibraryProgress,
                 bookId: String, pageId: String): BookLibraryProgress {
        validate(catalog, progress)
        requireNotNull(catalog.page(bookId, pageId)) { "Unknown bookmark page" }
        val old = progress.volumes[bookId] ?: BookVolumeProgress(bookId)
        require(pageId !in old.completedPageIds) { "Already finished page" }
        val updated = progress.copy(
            volumes = progress.volumes + (bookId to old.copy(bookmarkedPageId = pageId))
        )
        validate(catalog, updated)
        return updated
    }

    /**
     * Cannot award completion from a raw UI click: require a full, known board
     * payload with an EXACT registered digest plus all its actual target IDs.
     * Still requires publisher editorial approval before any book is shipped.
     */
    fun recordVerifiedCompletion(
        catalog: BookCatalog,
        progress: BookLibraryProgress,
        bookId: String,
        pageId: String,
        puzzle: WordSearchPuzzle,
        foundIds: Set<String>
    ): BookLibraryProgress {
        validate(catalog, progress)
        require(catalog.matchesPublishedPuzzle(bookId, pageId, puzzle)) {
            "Puzzle was not an exact registered book page"
        }
        require(foundIds.size == puzzle.targets.size &&
            WordSearchRules.complete(puzzle, foundIds)) {
            "Book page is not fully solved"
        }
        val old = progress.volumes[bookId] ?: BookVolumeProgress(bookId)
        if (pageId in old.completedPageIds) return progress
        val completed = old.completedPageIds + pageId
        val pages = catalog.pages(bookId)
        val remainingBookmark = old.bookmarkedPageId?.takeIf { it !in completed }
        val currentPosition = pages.indexOfFirst { it.id == pageId }
        val next = remainingBookmark ?: (
            pages.drop(currentPosition + 1).firstOrNull { it.id !in completed }
                ?: pages.firstOrNull { it.id !in completed }
        )?.id
        val updated = progress.copy(volumes = progress.volumes + (
            bookId to old.copy(completedPageIds = completed, bookmarkedPageId = next)
        ))
        validate(catalog, updated)
        return updated
    }
}
