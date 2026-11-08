package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Version3LargeBoardPersistenceAndroidTest {
    private val ctx get()=InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun giantAndGrandProgressAndBackupsSurviveSessionSwitching() {
        val name="ws09e_v3_"+System.nanoTime()+".json"
        val imported=name+"_copy"
        try {
            val store=SoloStore(ctx,name)
            val original=store.profile.active
            assertTrue(store.chooseSolo(WordTheme.GARDEN,
                PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.EXPERT))
            val giant=SoloProgressRules.puzzle(store.profile.active)
            assertEquals(30,giant.size)
            assertEquals(44,giant.targets.size)
            assertEquals(3,giant.contentVersion)
            val target=giant.targets.first()
            assertEquals(ClaimStatus.FOUND,store.claim(
                target.positions.first(),target.positions.last()).status)
            assertNotNull(store.hint().second)
            val giantPartial=store.profile.active
            assertEquals(setOf(target.id),giantPartial.foundIds)
            assertTrue(giantPartial.hints.isNotEmpty())
            assertTrue(store.chooseSolo(WordTheme.SPACE,
                PuzzleBoardSize.BOOK_GRAND,PuzzleChallenge.RELAXED))
            assertEquals(25,SoloProgressRules.puzzle(store.profile.active).size)
            assertEquals(listOf(giantPartial,original),store.profile.pausedSolo)
            val grandId=store.profile.active.puzzleId
            val reopened=SoloStore(ctx,name)
            assertEquals(grandId,reopened.profile.active.puzzleId)
            assertEquals(giantPartial,reopened.profile.pausedSolo.first())
            assertTrue(reopened.resumeSolo(giantPartial.puzzleId))
            assertEquals(giantPartial,reopened.profile.active)
            assertEquals(grandId,reopened.profile.pausedSolo.first().puzzleId)
            val exported=reopened.exportBackup()
            assertTrue(exported.contains("BOOK_GIANT"))
            assertTrue(exported.contains("BOOK_GRAND"))
            val restored=SoloStore(ctx,imported)
            assertTrue(restored.restoreBackup(restored.previewBackup(exported)))
            assertEquals(reopened.profile,SoloStore(ctx,imported).profile)
        } finally {
            for (file in listOf(name,imported)) {
                listOf("",".bak",".new").forEach { File(ctx.filesDir,file+it).delete() }
                ctx.getSharedPreferences(file+"_solo_setup",0).edit().clear().commit()
            }
        }
    }

    @Test fun oldV2AndDailyStillReconstructUnchangedAfterV3Use() {
        val legacy=SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_CLASSIC,PuzzleChallenge.CLASSIC,42L)
        val oldPuzzle=SoloProgressRules.puzzle(legacy)
        assertEquals("RACCOON",oldPuzzle.targets.first().id)
        assertEquals(2,oldPuzzle.contentVersion)
        assertEquals(1,SoloProgressRules.puzzle(
            DailyChallenge.dailyRun("2026-11-06")).contentVersion)
        val giant=SoloProgressRules.freshV3(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_GIANT,PuzzleChallenge.CLASSIC,42L)
        assertEquals(3,SoloProgressRules.puzzle(giant).contentVersion)
        assertEquals(oldPuzzle,SoloProgressRules.puzzle(legacy))
    }
}
