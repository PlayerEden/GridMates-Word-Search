package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class CoopBoardLookTest {
    private fun session(host: CoopBoardLook = CoopBoardLook.CLASSIC,
                        mode: NearbyMatch = NearbyMatch.COOP) =
        NearbyCoopSession(SoloProgressRules.freshV2(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 98201L),
            "ws06g-shared-room", mode, host)

    @Test fun symmetricDeterministicPreferenceNotCosmeticOwnership() {
        assertEquals(CoopBoardLook.BLUEPRINT, CoopBoardLook.resolve(
            "classic", "blueprint", true))
        assertEquals(CoopBoardLook.BLUEPRINT, CoopBoardLook.resolve(
            "blueprint", "classic", true))
        assertEquals(CoopBoardLook.BLUEPRINT, CoopBoardLook.resolve(
            "linen", "blueprint", true))
        assertEquals(CoopBoardLook.LINEN, CoopBoardLook.resolve(
            "linen", "classic", true))
        assertEquals(CoopBoardLook.LINEN, CoopBoardLook.resolve(
            "linen", "linen", true))
        assertEquals(CoopBoardLook.CLASSIC, CoopBoardLook.resolve(
            "unknown-from-future", null, true))
        assertEquals(CoopBoardLook.LINEN, CoopBoardLook.resolve(
            "linen", "blueprint", false))
    }

    @Test fun guestOfferDoesNotReserveThemeUntilAcceptance() {
        val room = session(CoopBoardLook.LINEN)
        assertEquals("linen", room.snapshot().boardLookId)
        assertEquals(CoopBoardLook.BLUEPRINT, room.offeredBoardLook("blueprint"))
        assertFalse(room.snapshot().guestReserved)
        assertEquals("linen", room.snapshot().boardLookId)
        assertEquals("blueprint", room.freezeGuestBoardLook("blueprint").boardLookId)
        val admitted = room.admitOrRejoinGuest()
        assertEquals("blueprint", admitted.boardLookId)
        assertEquals(0L, admitted.revision)
        assertTrue(runCatching { NearbyCoopSession.validateSnapshot(admitted) }.isSuccess)
    }

    @Test fun frozenAppearanceSurvivesClaimsAndGuestReconnectWithoutFlicker() {
        val room = session(CoopBoardLook.CLASSIC)
        room.freezeGuestBoardLook("blueprint")
        room.admitOrRejoinGuest()
        val t = room.puzzle.targets.first()
        val afterClaim = room.submit(CoopSeat.HOST, room.roomId, "test-claim",
            t.positions.first(), t.positions.last()).snapshot
        assertEquals("blueprint", afterClaim.boardLookId)
        assertEquals(1L, afterClaim.revision)
        room.guestDisconnected()
        assertEquals("blueprint", room.snapshot().boardLookId)
        assertEquals("blueprint", room.offeredBoardLook("classic").id)
        room.freezeGuestBoardLook("classic")
        assertEquals("blueprint", room.admitOrRejoinGuest().boardLookId)
        assertTrue(runCatching {
            NearbyCoopSession.validateSnapshot(room.snapshot())
        }.isSuccess)
    }

    @Test fun malformedOrCompetitiveBorrowedStyleCannotBeAccepted() {
        val room = session()
        val snap = room.snapshot()
        assertTrue(runCatching {
            NearbyCoopSession.validateSnapshot(snap.copy(boardLookId = "../file"))
        }.isFailure)
        assertTrue(runCatching {
            NearbyCoopSession.validateSnapshot(snap.copy(
                match = NearbyMatch.SHARED_BOARD_RACE, boardLookId = "linen"))
        }.isFailure)
        val race = session(CoopBoardLook.BLUEPRINT, NearbyMatch.SHARED_BOARD_RACE)
        assertEquals("classic", race.snapshot().boardLookId)
        assertTrue(runCatching {
            race.freezeGuestBoardLook("blueprint")
        }.isFailure)
        val duel = session(CoopBoardLook.LINEN, NearbyMatch.SPEED_DUEL)
        assertEquals("classic", duel.snapshot().boardLookId)
    }

    @Test fun temporarySharedStyleDoesNotChangeSoloProfileOrFoundWords() {
        val run = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 992L)
        val original = SoloProfile(run)
        val room = NearbyCoopSession(run, "ws06g-independent", NearbyMatch.COOP,
            CoopBoardLook.BLUEPRINT)
        room.admitOrRejoinGuest()
        assertEquals("blueprint", room.snapshot().boardLookId)
        assertEquals(0, original.completions)
        assertTrue(original.active.foundIds.isEmpty())
        assertEquals(run, original.active)
    }
}
