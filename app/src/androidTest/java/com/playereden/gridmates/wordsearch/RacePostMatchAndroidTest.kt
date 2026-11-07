package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Rule
import org.junit.Test

/** Inspect actual player-facing result and comparison, no second phone needed. */
class RacePostMatchAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun finishedRaceShowsPersonalResultAndWordByWordComparison() {
        val room = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 2011L),
            "ws06b-ui-race", NearbyMatch.SHARED_BOARD_RACE)
        room.admitOrRejoinGuest()
        room.puzzle.targets.forEachIndexed { i, word ->
            room.submit(if (i < 3) CoopSeat.GUEST else CoopSeat.HOST,
                room.roomId, "find-$i", word.positions.first(), word.positions.last())
        }
        compose.setContent {
            GridMatesTheme {
                RacePostMatchCard(room.snapshot(), CoopSeat.HOST, room.puzzle, true)
            }
        }
        compose.onNodeWithText("You won!").assertExists()
        compose.onNodeWithText("9 / 12").assertExists()
        compose.onNodeWithText("3 / 12").assertExists()
        compose.onNodeWithText("Saved to your local Versus journal.").assertExists()
        compose.onNodeWithText("Compare the words you each found").performClick()
        compose.onNodeWithText("You found · 9").assertExists()
        compose.onNodeWithText("Partner found · 3").assertExists()
    }
}
