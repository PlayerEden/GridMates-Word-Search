package com.playereden.gridmates.wordsearch

import androidx.compose.ui.graphics.Color
import com.playereden.gridmates.wordsearch.core.CoopBoardLook

/**
 * Local baked-in visual palette only; never download style files or colors
 * from another player's phone. The look ID is allowlisted in core.
 */
internal data class BoardLookPalette(
    val background: Color,
    val letters: Color,
    val lines: Color,
    val frame: Color
)

internal object BoardLookPalettes {
    fun forId(id: String): BoardLookPalette = when (CoopBoardLook.safe(id)) {
        CoopBoardLook.CLASSIC -> BoardLookPalette(
            background = Paper, letters = Ink,
            lines = Color(0xFFD8DFD2), frame = Forest)
        CoopBoardLook.LINEN -> BoardLookPalette(
            background = Color(0xFFF5EBD9),
            letters = Color(0xFF3A3529),
            lines = Color(0xFFCEBFA6),
            frame = Color(0xFF796340))
        CoopBoardLook.BLUEPRINT -> BoardLookPalette(
            background = Color(0xFFE9F2F3),
            letters = Color(0xFF163948),
            lines = Color(0xFFB5CBD0),
            frame = Color(0xFF28546A))
    }
}
