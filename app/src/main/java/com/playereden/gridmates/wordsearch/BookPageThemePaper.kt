package com.playereden.gridmates.wordsearch

import androidx.compose.ui.graphics.Color
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS-14H: surrounding page paper only, never a paint or hit-zone override
 * on WordGrid itself. The book determines the tint; no new saved preference.
 * A/B art remains QA gated at the caller.
 */
internal fun bookPageThemePaperTint(theme: WordTheme): Color = when (theme) {
    WordTheme.WOODLAND -> Color(0xFFF1F3EA) // pale sage
    WordTheme.SPACE -> Color(0xFFF0F2F7) // pale blue-gray
    WordTheme.GARDEN -> Color(0xFFF6F2EA) // warm floral cream
    else -> Cream
}
