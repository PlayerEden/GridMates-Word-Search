package com.playereden.gridmates.wordsearch

/** Small player-facing instructions; these are not tutorial or gameplay rules. */
internal object PuzzleModeHelp {
    fun opening(boardSize: Int): String =
        if (boardSize >= 15)
            "Large board: choose Move to explore, then Find to select words."
        else "Drag a word, or tap its first and last letters."

    fun selected(moving: Boolean): String =
        if (moving)
            "Move mode: drag the board to explore or pinch to zoom. Select Find to trace words."
        else
            "Find mode: drag across a word, or tap its first and last letters."
}
