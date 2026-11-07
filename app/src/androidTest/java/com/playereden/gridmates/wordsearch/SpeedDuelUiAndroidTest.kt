package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Private board ready/go and winner views are testable without Wi-Fi. */
class SpeedDuelUiAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun readyButtonIsGatedByConnectionAndChangesAfterReady() {
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 242L)
        val duel = SpeedDuelSession(SoloProgressRules.puzzle(run))
        var tapped = 0
        compose.setContent {
            GridMatesTheme {
                SpeedDuelStatusCard(duel.view(CoopSeat.HOST), 12, false) { tapped++ }
            }
        }
        compose.onNodeWithText("Ready, then GO!").assertExists()
        compose.onNodeWithText("I'm ready").assertIsNotEnabled()
        compose.onNodeWithText("Both players must tap Ready.", substring = true).assertExists()
        assertEquals(0, tapped)
    }

    @Test fun privateProgressAndFinalWinnerAreVisibleWithoutOpponentWords() {
        val run = SoloProgressRules.freshV2(WordTheme.GARDEN,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 292L)
        val duel = SpeedDuelSession(SoloProgressRules.puzzle(run))
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        duel.puzzle.targets.forEachIndexed { index, target ->
            duel.submit(CoopSeat.GUEST, "find-$index",
                target.positions.first(), target.positions.last(), true)
        }
        compose.setContent {
            GridMatesTheme {
                SpeedDuelStatusCard(duel.view(CoopSeat.HOST), 12, true) {}
            }
        }
        compose.onNodeWithText("Your partner finished first").assertExists()
        compose.onNodeWithText("0 / 12").assertExists()
        compose.onNodeWithText("12 / 12").assertExists()
        compose.onNodeWithText("I'm ready").assertDoesNotExist()
        compose.onNodeWithText("Saving your Duel result to this device…").assertExists()
        compose.onNodeWithText("Shared Board Race records stay separate", substring = true)
            .assertExists()
    }
    @Test fun confirmedWinnerShowsOwnScoreAndSavedJournalStatus() {
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 301L)
        val session = SpeedDuelSession(SoloProgressRules.puzzle(run))
        session.ready(CoopSeat.HOST, true)
        session.ready(CoopSeat.GUEST, true)
        session.puzzle.targets.forEachIndexed { index, target ->
            session.submit(CoopSeat.HOST, "found-" + index,
                target.positions.first(), target.positions.last(), true)
        }
        compose.setContent {
            GridMatesTheme {
                SpeedDuelStatusCard(
                    session.view(CoopSeat.HOST), 12, true, saved = true) {}
            }
        }
        compose.onNodeWithText("You finished first!").assertExists()
        compose.onNodeWithText("Saved to your local Speed Duel journal.").assertExists()
        compose.onNodeWithText("12 / 12").assertExists()
        compose.onNodeWithText("0 / 12").assertExists()
    }

}
