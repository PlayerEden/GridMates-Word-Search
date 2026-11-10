package com.playereden.gridmates.wordsearch.core

/**
 * GridMates family typography contract, shared across future puzzle titles.
 *
 * Identity fonts NEVER become achievement/cosmetic unlocks:
 * - Literata: permanent brand/storybook heading font.
 * - Atkinson Hyperlegible: permanent menu/control/reading font.
 *
 * Word Search's historically free board starter font ALSO uses Atkinson:
 * this is an always-available baseline, not an earned or unlockable font.
 * Keeping its persisted ID avoids changing existing players' selections.
 *
 * New player-earned board fonts must have different IDs from both brand
 * fonts. This is a distinction between UI identity and board cosmetics,
 * not an exclusivity claim about OFL fonts.
 */
object GridMatesTypographyRoles {
    const val STORYBOOK = "literata"
    const val INTERFACE = "atkinson"
    const val BOARD_STARTER = "atkinson"

    val brandFontIds = setOf(STORYBOOK, INTERFACE)
}
