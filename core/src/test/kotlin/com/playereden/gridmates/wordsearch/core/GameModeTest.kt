package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameModeTest {
    @Test fun plannedModesHaveDistinctStableIds() {
        assertEquals(5, GameMode.entries.size)
        assertEquals(5, GameMode.entries.map { it.id }.toSet().size)
        assertEquals("shared_board_race", GameMode.SHARED_BOARD_RACE.id)
        assertEquals("speed_duel", GameMode.SPEED_DUEL.id)
    }

    @Test fun onlyNearbyModesRequireAnotherPhone() {
        assertFalse(GameMode.SOLO.multiplayer)
        assertFalse(GameMode.DAILY.multiplayer)
        assertTrue(GameMode.COOP.multiplayer)
        assertTrue(GameMode.SHARED_BOARD_RACE.multiplayer)
        assertTrue(GameMode.SPEED_DUEL.multiplayer)
    }
}
