package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NearbyPresenceShareAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun finishedResultPreviewCanBeCancelledWithoutSharing() {
        var dismissals = 0
        compose.setContent {
            GridMatesTheme {
                ShareResultPreview("GridMates: Word Search\nFriendly victory!\nNo secrets") {
                    dismissals++
                }
            }
        }
        compose.onNodeWithText("Preview result to share").assertExists()
        compose.onNodeWithText("GridMates: Word Search", substring = true).assertExists()
        compose.onNodeWithText("Only this text is offered", substring = true).assertExists()
        compose.onNodeWithText("Share now").assertExists()
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle { assertEquals(1, dismissals) }
    }

    @Test fun realConnectedFriendAndSharedContributionsAreEasyToSee() {
        val room = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 5023L), "ws06e-ui-coop")
        room.admitOrRejoinGuest()
        val t = room.puzzle.targets[0]
        room.submit(CoopSeat.GUEST, room.roomId, "guest-word",
            t.positions.first(), t.positions.last())
        val state = NearbyUiState(role = NearbyRole.HOST, hostName = "Host",
            guestName = "Ava", snapshot = room.snapshot())
        compose.setContent {
            GridMatesTheme {
                NearbyPlayerPresenceCard(state, room.snapshot(), CoopSeat.HOST,
                    room.puzzle.targets.size, recentWord = t.word)
            }
        }
        compose.onNodeWithText("Connected · Ava is here").assertExists()
        compose.onNodeWithText("FRIEND · Ava").assertExists()
        compose.onNodeWithText("1 / 12").assertExists()
        compose.onNodeWithText("Friend's latest shared find:", substring = true).assertExists()
        compose.onNodeWithText("1 of 12 shared words found").assertExists()
    }

    @Test fun privateDuelOnlyRendersOpponentCountAndNeverAWord() {
        val room = NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 5024L), "ws06e-ui-duel", NearbyMatch.SPEED_DUEL)
        room.admitOrRejoinGuest()
        val duel = SpeedDuelSession(room.puzzle)
        duel.ready(CoopSeat.HOST, true)
        duel.ready(CoopSeat.GUEST, true)
        val t = room.puzzle.targets[0]
        duel.submit(CoopSeat.GUEST, "partner-discovery",
            t.positions.first(), t.positions.last(), true)
        val snapshot = room.snapshot().copy(duel = duel.view(CoopSeat.HOST))
        val state = NearbyUiState(role = NearbyRole.HOST,
            guestName = "Noah", snapshot = snapshot)
        compose.setContent {
            GridMatesTheme {
                NearbyPlayerPresenceCard(state, snapshot, CoopSeat.HOST, 12)
            }
        }
        compose.onNodeWithText("Connected · Noah is here").assertExists()
        compose.onNodeWithText("Your friend cannot see which specific words", substring = true)
            .assertExists()
        compose.onNodeWithText("Friend's latest shared find:", substring = true)
            .assertDoesNotExist()
    }
}
