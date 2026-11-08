package com.playereden.gridmates.wordsearch.core

/** Page gutter choices in dp. No Android/Compose dependency. */
object PaperbackPageLayout {
    /**
     * Side margins are genuine space *outside* the playable grid.
     * Avoid crowding narrow Android phones while respecting paperback
     * proportions; don't use inner cell padding that would misalign touch.
     */
    fun sideMarginDp(screenWidthDp: Int): Int = when {
        screenWidthDp < 360 -> 16
        screenWidthDp < 420 -> 18
        screenWidthDp < 700 -> 20
        else -> 24
    }
}
