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
        val start = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 31415L
        )
        var run = start
        var firstId: String? = null
        repeat(3) {
            // Exercise the existing three-step clue ladder on a modern
            // Standard 15x15 board, which has three charges.
            val (next, target) = SoloProgressRules.hint(run)
            assertNotNull(target)
            if (firstId == null) firstId = target!!.id
            else assertEquals("Hint must improve the same target first", firstId, target!!.id)
            run = next
            assertEquals(it + 1, run.hints[firstId])
            assertTrue(run.foundIds.isEmpty())
        }
        // Legacy 8x8 Solo now has two hint steps per puzzle.
        // A prior saved third-level clue is preserved, never erased, but
        // no additional clues are granted after the new allowance.
        val (next, target) = SoloProgressRules.hint(run)
        assertNull(target)
        assertEquals(run, next)
        assertEquals(3, next.hints.values.sum())
        assertTrue(next.hints.values.all { it in 1..3 })
        assertEquals(0, SoloProgressRules.complete(SoloProfile(next), 100).completions)
    }

    @Test fun incompleteGameCannotRecordFakeResult() {
        val p = SoloProfile(SoloProgressRules.fresh(seed = 4L))
        assertTrue(SoloProgressRules.complete(p, 100).results.isEmpty())
    }
    @Test fun hintBudgetsScaleWithBoardSizeAndDailyIsLimited() {
        val cases = listOf(
            PuzzleBoardSize.COMPACT to 2,
            PuzzleBoardSize.BOOK_SHORT to 2,
            PuzzleBoardSize.BOOK_CLASSIC to 3,
            PuzzleBoardSize.BOOK_BIG to 4
        )
        cases.forEach { (board, expected) ->
            val fresh = SoloProgressRules.freshV2(
                WordTheme.SPACE, board, PuzzleChallenge.RELAXED, 7123L
            )
            assertEquals(expected, WordHintPolicy.allowance(fresh))
            var run = fresh
            repeat(expected) {
                val (next, target) = SoloProgressRules.hint(run)
                assertNotNull(target)
                assertEquals(expected - it - 1, WordHintPolicy.remaining(next))
                assertEquals(it + 1, WordHintPolicy.used(next))
                run = next
            }
            val (after, denied) = SoloProgressRules.hint(run)
            assertEquals(run, after)
            assertNull(denied)
            assertTrue(after.foundIds.isEmpty())
        }
        var daily = DailyChallenge.start("2026-11-06").run
        assertEquals(2, WordHintPolicy.allowance(daily, daily = true))
        repeat(2) { daily = SoloProgressRules.hint(daily, daily = true).first }
        assertEquals(0, WordHintPolicy.remaining(daily, daily = true))
        assertNull(SoloProgressRules.hint(daily, daily = true).second)
    }

    @Test fun existingCluesBeyondNewBudgetArePreservedButCannotGrow() {
        val p = WordSearchGenerator.generate(seed = 941L)
        val run = SoloRun(WordTheme.WOODLAND, WordDifficulty.EASY, 941L,
            hints = mapOf(p.targets.first().id to 3))
        val clean = SoloProgressRules.validate(run)
        assertEquals(3, clean.hints.values.sum())
        assertEquals(0, WordHintPolicy.remaining(clean))
        assertEquals(clean, SoloProgressRules.hint(clean).first)
    }

    @Test fun repeatedFindAndHintReadsReuseSameImmutableSeededGrid() {
        val big = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.CLASSIC, 445571L
        )
        val first = SoloProgressRules.puzzle(big)
        assertEquals(20, first.size)
        repeat(30) {
            assertSame("A find or hint should not regenerate the 20x20 puzzle",
                first, SoloProgressRules.puzzle(big))
        }
        val other = SoloProgressRules.puzzle(big.copy(seed = big.seed + 1))
        assertNotSame(first, other)
        assertNotEquals(first.id, other.id)
        val old = SoloProgressRules.puzzle(
            SoloProgressRules.fresh(WordTheme.WOODLAND, WordDifficulty.EASY, 42L))
        assertEquals(1, old.contentVersion)
    }

}
