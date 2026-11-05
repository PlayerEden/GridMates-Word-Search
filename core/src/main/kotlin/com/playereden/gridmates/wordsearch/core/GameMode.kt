package com.playereden.gridmates.wordsearch.core

/**
 * Stable IDs for the five approved design experiences.
 * This is planning metadata, not a playable mode implementation.
 */
enum class GameMode(val id: String, val multiplayer: Boolean) {
    SOLO("solo", false),
    DAILY("daily", false),
    COOP("coop", true),
    SHARED_BOARD_RACE("shared_board_race", true),
    SPEED_DUEL("speed_duel", true)
}
