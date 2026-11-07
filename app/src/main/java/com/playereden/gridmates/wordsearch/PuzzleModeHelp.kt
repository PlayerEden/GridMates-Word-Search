package com.playereden.gridmates.wordsearch

/** Brief touch guidance, no mandatory mode switching or scrolling puzzle. */
internal object PuzzleModeHelp {
    fun opening(boardSize: Int): String =
        if (boardSize >= 15)
            "Trace a word with one finger. Pinch with two fingers to zoom, or tap its first and last letters."
        else "Drag a word, or tap its first and last letters."

    /** Kept as a helper for older guidance tests; no Find/Move toggle exists. */
    fun selected(moving: Boolean): String =
        if (moving) "Two fingers pinch or pan the board. One finger finds words."
        else "One finger traces a word. Two fingers pinch or pan to zoom."
}
