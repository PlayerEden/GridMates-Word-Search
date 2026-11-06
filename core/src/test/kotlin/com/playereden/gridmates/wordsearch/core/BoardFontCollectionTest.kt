package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BoardFontCollectionTest {
    @Test fun defaultIsAlwaysAvailableAndUnknownFontsNeverEquip() {
        val p = SoloProfile(SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 777L
        ))
        assertEquals(setOf(BoardFontCollection.DEFAULT_ID), BoardFontCollection.unlocked(p))
        assertFalse(BoardFontCollection.allowed(p, "plex"))
        assertEquals("atkinson", BoardFontCollection.effective(p, "ghost"))
        assertEquals("atkinson", BoardFontCollection.effective(p, "plex"))
        assertEquals("atkinson", BoardFontCollection.effective(p, null))
        assertEquals(4, BoardFontCollection.progress(p, null).size)
        assertEquals(1, BoardFontCollection.progress(p, null).count { it.unlocked })
    }

    @Test fun recordedAchievementsGrantFreeFontsRetroactivelyWithoutCurrency() {
        val game = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 1144L
        )
        val base = SoloProfile(game)
        val one = base.copy(results = listOf(
            SoloResult(game.puzzleId, game.theme, game.difficulty,
                wordCount = 16, elapsedSeconds = 80, hintSteps = 0, completedAtMs = 1000L,
                boardSize = game.boardSize, challenge = game.challenge)
        ))
        assertTrue(BoardFontCollection.allowed(one, "plex"))
        assertFalse(BoardFontCollection.allowed(one, "dmmono"))
        assertEquals("plex", BoardFontCollection.effective(one, "plex"))
        assertEquals(2, BoardFontCollection.unlocked(one).size)

        val d = DailyChallenge.start("2026-11-06")
        val dailyCompleted = one.copy(dailyEntries = listOf(
            d.copy(completedAtMs = 2000L)
        ))
        assertTrue(BoardFontCollection.allowed(dailyCompleted, "patrick"))
        assertEquals(3, BoardFontCollection.unlocked(dailyCompleted).size)
    }

    @Test fun fontEquipDoesNotTouchPuzzleIdentityOrStats() {
        val game = SoloProgressRules.freshV2(
            WordTheme.SPACE, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.EXPERT, 12345L)
        val profile = SoloProfile(game)
        val originalId = profile.active.puzzleId
        BoardFontCollection.progress(profile, "plex")
        assertEquals(originalId, profile.active.puzzleId)
        assertEquals(0, profile.completions)
        assertTrue(profile.results.isEmpty())
    }
}
