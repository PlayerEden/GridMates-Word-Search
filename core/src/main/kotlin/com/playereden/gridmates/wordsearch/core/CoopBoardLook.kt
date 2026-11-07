package com.playereden.gridmates.wordsearch.core

/**
 * Word Search-specific, FREE preview board appearances. Priority is a
 * technical display choice only, NOT rarity, paid entitlement or an unlock.
 * This first catalog intentionally does not import Sudoku cosmetic IDs.
 */
enum class CoopBoardLook(val id: String, val label: String, val displayPriority: Int) {
    CLASSIC("classic", "Classic Paper", 0),
    LINEN("linen", "Linen Sketch", 1),
    BLUEPRINT("blueprint", "Blueprint Study", 2);

    companion object {
        fun safe(id: String?): CoopBoardLook =
            entries.firstOrNull { it.id == id } ?: CLASSIC

        /** Host and guest independently calculate exactly the same choice. */
        fun resolve(host: String?, guest: String?, guestPresent: Boolean): CoopBoardLook {
            val hostLook = safe(host)
            if (!guestPresent) return hostLook
            val guestLook = safe(guest)
            return if (guestLook.displayPriority > hostLook.displayPriority)
                guestLook else hostLook
        }
    }
}
