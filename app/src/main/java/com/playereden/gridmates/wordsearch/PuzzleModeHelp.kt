package com.playereden.gridmates.wordsearch

/** Small player-facing instructions; these are not tutorial or gameplay rules. */
internal object PuzzleModeHelp {
    fun opening(boardSize: Int): String =
        if (boardSize >= 15)
            "Large board: tap Move to explore, then Find to select words."
        else "Drag a word, or tap its first and last letters."

    fun selected(moving: Boolean): String =
        if (moving)
            "Move: drag to pan or pinch to zoom. Tap Find to select words."
        else
            "Find: drag a word, or tap its first and last letters."
}
