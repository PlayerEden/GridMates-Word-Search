package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class SoloProgressTest {
    @Test fun validatedProgressRetainsGoodClaimsAndRejectsBogusOnes() {
        val p = WordSearchGenerator.generate(seed = 42L)
        val target = p.targets.first()
        val run = SoloRun(WordTheme.WOODLAND, WordDifficulty.EASY, 42L,
            foundIds = setOf(target.id, "BOGUS"),
            hints = mapOf(target.id to 2, "BAD" to 3, p.targets.last().id to 99),
            elapsedSeconds = -3)
        val clean = SoloProgressRules.validate(run)
        assertEquals(setOf(target.id), clean.foundIds)
        assertEquals(mapOf(target.id to 2), clean.hints)
        assertEquals(0L, clean.elapsedSeconds)
    }

    @Test fun claimOnlyUpdatesOnceAndCompletionRecordIsIdempotent() {
        val run = SoloProgressRules.fresh(seed = 33)
        val p = WordSearchGenerator.generate(seed = 33)
        val t = p.targets.first()
        val (found, claim) = SoloProgressRules.find(run,t.positions.first(),t.positions.last())
        assertEquals(ClaimStatus.FOUND, claim.status)
        val (again, repeated) = SoloProgressRules.find(found,t.positions.first(),t.positions.last())
        assertEquals(ClaimStatus.ALREADY_FOUND, repeated.status)
        assertEquals(found, again)
        val complete = run.copy(foundIds = p.targets.map { it.id }.toSet(), elapsedSeconds = 81)
        val profile = SoloProgressRules.complete(SoloProfile(complete), 1000L)
        assertEquals(1, profile.completions)
        assertEquals(6, profile.totalWords)
        assertEquals(1, SoloProgressRules.complete(profile, 999999L).completions)
        assertEquals(1000L, SoloProgressRules.complete(profile, 999999L).results.first().completedAtMs)
    }

    @Test fun hintLadderNeverCompletesOrAwardsWordsAutomatically() {
        val start = SoloProgressRules.fresh(seed = 31415L)
        var run = start
        var firstId: String? = null
        repeat(3) {
            val (next, target) = SoloProgressRules.hint(run)
            assertNotNull(target)
            if (firstId == null) firstId = target!!.id
            else assertEquals("Hint must improve the same target first", firstId, target!!.id)
            run = next
            assertEquals(it + 1, run.hints[firstId])
            assertTrue(run.foundIds.isEmpty())
        }
        val (next, _) = SoloProgressRules.hint(run)
        assertEquals(4, next.hints.values.sum())
        assertTrue(next.hints.values.all { it in 1..3 })
        assertEquals(0, SoloProgressRules.complete(SoloProfile(next), 100).completions)
    }

    @Test fun incompleteGameCannotRecordFakeResult() {
        val p = SoloProfile(SoloProgressRules.fresh(seed = 4L))
        assertTrue(SoloProgressRules.complete(p, 100).results.isEmpty())
    }
}
