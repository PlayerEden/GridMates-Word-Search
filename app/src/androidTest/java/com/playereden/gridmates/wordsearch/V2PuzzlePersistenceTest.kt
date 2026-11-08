package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchGenerator
import com.playereden.gridmates.wordsearch.core.WordDifficulty
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Compiled in CI; running on an Android emulator/device is a separate gate. */
class V2PuzzlePersistenceTest {
    @Test fun v2SetupSurvivesRestartAndBackupWithoutChangingDaily() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "ws04d_v2_" + System.nanoTime() + ".json"
        try {
            val initial = SoloStore(context,fileName)
            initial.chooseV2(WordTheme.SPACE, PuzzleBoardSize.COMPACT, PuzzleChallenge.EXPERT)
            val created = initial.profile.active
            val p = SoloProgressRules.puzzle(created)
            assertEquals(8,p.size)
            initial.claim(p.targets[0].positions.first(),p.targets[0].positions.last())
            initial.openDaily("2026-11-06")
            val dailyId = requireNotNull(initial.selectedDaily).run.puzzleId
            assertTrue(dailyId.startsWith("wordsearch:v1:"))
            val backup = initial.exportBackup()
            assertTrue(backup.contains("\"schema\":5"))
            val loaded = SoloStore(context,fileName)
            assertEquals(created.puzzleId,loaded.profile.active.puzzleId)
            assertEquals(PuzzleBoardSize.COMPACT,loaded.profile.active.boardSize)
            assertEquals(PuzzleChallenge.EXPERT,loaded.profile.active.challenge)
            assertEquals(1,loaded.profile.active.foundIds.size)
            loaded.openDaily("2026-11-06")
            assertEquals(dailyId,requireNotNull(loaded.selectedDaily).run.puzzleId)
            assertEquals(loaded.profile,loaded.previewBackup(backup))
        } finally {
            File(context.filesDir,fileName).delete()
            File(context.filesDir,fileName+".bak").delete()
            File(context.filesDir,fileName+".new").delete()
        }
    }

    @Test fun schemaThreeLegacyV1BoardRemainsByteIdentical() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "ws04d_legacy_" + System.nanoTime() + ".json"
        val file = File(context.filesDir,fileName)
        try {
            val old = WordSearchGenerator.generate(
                WordTheme.WOODLAND, WordDifficulty.EASY, 42L)
            file.writeText("""{"schema":3,"active":{"theme":"WOODLAND",
                "difficulty":"EASY","seed":42,"found":[],"hints":{},"seconds":17},
                "daily":[],"results":[]}""")
            val loaded = SoloStore(context,fileName)
            assertNull(loaded.profile.active.boardSize)
            assertEquals(old, SoloProgressRules.puzzle(loaded.profile.active))
            assertEquals(17L,loaded.profile.active.elapsedSeconds)
            val upgraded = loaded.exportBackup()
            assertTrue(upgraded.contains("\"schema\":5"))
            val reread = SoloStore(context,fileName)
            assertEquals(old,SoloProgressRules.puzzle(reread.profile.active))
        } finally {
            file.delete()
            File(context.filesDir,fileName+".bak").delete()
            File(context.filesDir,fileName+".new").delete()
        }
    }
}
