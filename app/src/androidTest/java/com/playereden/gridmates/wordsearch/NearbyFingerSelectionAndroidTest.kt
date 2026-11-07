package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

/**
 * Real Compose input-injection, NOT just a host-authority unit call. Reproduces
 * a single-finger gesture from the center of the actual start and end cells.
 * Physical second-phone testing remains a separate acceptance requirement.
 */
class NearbyFingerSelectionAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun fifteenCellFingerDragClaimsAWordFromActualBoard() {
        strokeMustClaim(PuzzleBoardSize.BOOK_CLASSIC, CoopSeat.HOST)
    }

    @Test fun twentyCellFingerDragClaimsAWordForGuestAuthorityPath() {
        strokeMustClaim(PuzzleBoardSize.BOOK_BIG, CoopSeat.GUEST)
    }

    private fun strokeMustClaim(board: PuzzleBoardSize, seat: CoopSeat) {
        val run = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, board, PuzzleChallenge.CLASSIC, 90128L)
        val session = NearbyCoopSession(run, match = NearbyMatch.COOP)
        session.admitOrRejoinGuest()
        val puzzle = session.puzzle
        val word = puzzle.targets.first()
        val captured = AtomicReference<CoopClaimDecision?>(null)
        val endpoints = AtomicReference<Pair<GridPoint, GridPoint>?>(null)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.size(350.dp)) {
                    ZoomableWordGrid(puzzle, emptySet(), emptyList(), 320.dp,
                        onSelect = { from, to ->
                            endpoints.set(from to to)
                            captured.set(session.submit(seat, session.roomId,
                                UUID.randomUUID().toString(), from, to))
                        },
                        onStep = {},
                        boardFontId = "atkinson")
                }
            }
        }
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val sideX = center.x * 2f
            val sideY = center.y * 2f
            assertTrue("Puzzle grid must render square at its measured touch bounds", kotlin.math.abs(sideX - sideY) < 2f)
            val from = word.positions.first()
            val to = word.positions.last()
            swipe(
                start = Offset((from.col + .5f) * sideX / board.cells,
                    (from.row + .5f) * sideY / board.cells),
                end = Offset((to.col + .5f) * sideX / board.cells,
                    (to.row + .5f) * sideY / board.cells),
                durationMillis = 550
            )
        }
        compose.runOnIdle {
            val decision = captured.get()
            assertTrue("A real finger trace must call host authority", decision != null)
            assertEquals("Expected " + word.positions.first() + " to " +
                word.positions.last() + " but UI delivered " + endpoints.get(),
                CoopClaimStatus.FOUND, decision!!.status)
            assertEquals(word.id, decision.wordId)
            assertEquals(1, decision.snapshot.foundIds.size)
        }
    }
}
