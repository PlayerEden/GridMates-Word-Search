package com.playereden.gridmates.wordsearch.core

/**
 * WS-09F-B2: resolution-independent viewport bookmark for one saved puzzle.
 *
 * This is an opt-in, pure data contract ONLY. Neither the schema-5 save file
 * nor any active UI currently writes bookmarks. Preserve old puzzle IDs,
 * all existing found words and hints. Future persistence must validate a
 * bookmark independently and never discard a SoloRun for bad view metadata.
 */
data class LargeBoardBookmark(
    val puzzleId: String,
    val zoom: Float,
    val centerX: Float,
    val centerY: Float
) {
    init {
        require(valid(puzzleId, zoom, centerX, centerY)) {
            "Invalid large-board bookmark"
        }
    }

    /**
     * Caller supplies the CURRENT puzzle ID. Bookmarks from another run
     * must never affect this board, even if dimensions or theme match.
     */
    fun restoreFor(expectedPuzzleId: String, viewportSidePx: Float): LargeBoardViewport? {
        if (puzzleId != expectedPuzzleId ||
            !viewportSidePx.isFinite() || viewportSidePx <= 0f) return null

        // The original transform stores *screen pixels*. Rebuild those from
        // a normalized center at the new window size (including rotation).
        val side = viewportSidePx
        return LargeBoardViewport(
            zoom = zoom,
            panX = side * (0.5f - centerX * zoom),
            panY = side * (0.5f - centerY * zoom)
        ).clamped(side)
    }

    companion object {
        private const val MAX_ID_LENGTH = 256

        private fun valid(id: String?, zoom: Float?, x: Float?, y: Float?): Boolean =
            !id.isNullOrBlank() && id.length <= MAX_ID_LENGTH &&
                id.none { it.code < 32 || it.code == 127 } &&
                zoom != null && zoom.isFinite() && zoom in 1f..LargeBoardViewport.MAX_ZOOM &&
                x != null && x.isFinite() && x in 0f..1f &&
                y != null && y.isFinite() && y in 0f..1f

        /**
         * Future loader: malformed optional view data should be ignored and
         * the player's puzzle restored at Fit, not treated as corrupt progress.
         */
        fun fromFieldsOrNull(
            puzzleId: String?, zoom: Float?, centerX: Float?, centerY: Float?
        ): LargeBoardBookmark? {
            if (!valid(puzzleId, zoom, centerX, centerY)) return null
            return LargeBoardBookmark(
                requireNotNull(puzzleId), requireNotNull(zoom),
                requireNotNull(centerX), requireNotNull(centerY)
            )
        }

        /** Capture a clamped full-board center, not dp/pixels or a region ID. */
        fun capture(
            puzzleId: String, viewport: LargeBoardViewport, viewportSidePx: Float
        ): LargeBoardBookmark {
            require(viewportSidePx.isFinite() && viewportSidePx > 0f)
            val side = viewportSidePx
            val clamped = viewport.clamped(side)
            return LargeBoardBookmark(
                puzzleId = puzzleId,
                zoom = clamped.zoom,
                centerX = ((0.5f - clamped.panX / side) / clamped.zoom).coerceIn(0f, 1f),
                centerY = ((0.5f - clamped.panY / side) / clamped.zoom).coerceIn(0f, 1f)
            )
        }
    }
}
