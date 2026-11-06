package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Compiled in CI; actual Android device/emulator execution is separately required. */
class BookSizePersistenceTest {
    @Test fun newBookDefaultAndExtendedBookSurviveRestartAndBackup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04d_books_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            assertEquals(PuzzleBoardSize.BOOK_CLASSIC, store.profile.active.boardSize)
            store.chooseV2(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.RELAXED)
            val before = store.profile.active
            val puzzle = SoloProgressRules.puzzle(before)
            assertEquals(20, puzzle.size)
            assertEquals(24, puzzle.targets.size)
            val target = puzzle.targets.first()
            store.claim(target.positions.first(), target.positions.last())
            val backup = store.exportBackup()
            assertTrue(backup.contains("\"BOOK_BIG\""))
            val reopened = SoloStore(context, name)
            assertEquals(before.puzzleId, reopened.profile.active.puzzleId)
            assertEquals(setOf(target.id), reopened.profile.active.foundIds)
            assertEquals(backup, reopened.exportBackup())
            reopened.newPuzzle()
            assertEquals(PuzzleBoardSize.BOOK_BIG, reopened.profile.active.boardSize)
            assertEquals(PuzzleChallenge.RELAXED, reopened.profile.active.challenge)
        } finally {
            File(context.filesDir, name).delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }

    @Test fun oldV2EightByEightStillReopensWithOriginalIdentity() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws04d_old_v2_" + System.nanoTime() + ".json"
        val file = File(context.filesDir, name)
        try {
            file.writeText("""{"schema":4,"active":{"theme":"SPACE",
               "difficulty":"HARD","board":"COMPACT","challenge":"EXPERT",
               "seed":471,"found":[],"hints":{},"seconds":36},
               "results":[],"daily":[]}""")
            val store = SoloStore(context, name)
            assertEquals(8, SoloProgressRules.puzzle(store.profile.active).size)
            assertEquals("wordsearch:v2:en:SPACE:COMPACT:EXPERT:471",
                store.profile.active.puzzleId)
            assertEquals(36L, store.profile.active.elapsedSeconds)
            assertNull(store.warning)
        } finally {
            file.delete()
            File(context.filesDir, name + ".bak").delete()
            File(context.filesDir, name + ".new").delete()
        }
    }
}
