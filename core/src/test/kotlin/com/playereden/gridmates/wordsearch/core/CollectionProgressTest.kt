package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class CollectionProgressTest {
    private fun sampleResult(id: String, theme: WordTheme = WordTheme.WOODLAND,
                             difficulty: WordDifficulty = WordDifficulty.EASY,
                             hints: Int = 0): SoloResult =
        SoloResult("fake:v1:$id", theme, difficulty, 6, 35, hints, 1000)

    @Test fun originalWoodlandIdsAreUniqueAndStable() {
        val expected = setOf(
            "woodland_owl","woodland_fox","woodland_rabbit","woodland_bear",
            "woodland_badger","woodland_hedgehog","woodland_robin","woodland_deer",
            "woodland_squirrel","woodland_otter","woodland_tortoise","woodland_cat"
        )
        assertEquals(expected, WoodlandCollection.ids)
        assertEquals(12, WoodlandCollection.portraits.size)
        assertTrue(WoodlandCollection.portraits.all { it.requiredCompletions >= 0 })
    }

    @Test fun starterAvatarsAvailableAndLockedCannotBeEquipped() {
        val initial = SoloProfile(SoloProgressRules.fresh(seed = 8))
        assertEquals(setOf("woodland_owl","woodland_fox"), WoodlandCollection.unlocked(initial))
        assertNull(WoodlandCollection.equip(initial, "woodland_cat"))
        val changed = WoodlandCollection.equip(initial, "woodland_fox")
        assertNotNull(changed)
        assertEquals("woodland_fox", changed!!.identity.portraitId)
        assertEquals(30, WoodlandCollection.remaining(initial,"woodland_cat"))
    }

    @Test fun realCompletedResultsUnlockPortraitsAndDoNotConsumeThem() {
        val original = SoloProfile(SoloProgressRules.fresh(seed = 8))
        val one = original.copy(results = listOf(sampleResult("1")))
        assertTrue("woodland_rabbit" in WoodlandCollection.unlocked(one))
        assertFalse("woodland_bear" in WoodlandCollection.unlocked(one))
        val equip = requireNotNull(WoodlandCollection.equip(one,"woodland_rabbit"))
        assertEquals(one.results, equip.results)
        assertEquals("woodland_rabbit", equip.identity.portraitId)
    }

    @Test fun sanitizePreventsControlCharsAndInvalidOrLockedIds() {
        val initial = SoloProfile(SoloProgressRules.fresh(seed = 8))
        val sanitized = WoodlandCollection.sanitize(
            PlayerIdentity("\n" + "A".repeat(50) + "\u0000", "made_up_portrait"),initial)
        assertEquals(24, sanitized.displayName.length)
        assertEquals("woodland_owl",sanitized.portraitId)
        assertEquals("Player", WoodlandCollection.sanitizeName("   \n\t"))
    }

    @Test fun achievementsAreComputedFromActualResultsNotButtonTaps() {
        val zero = SoloProfile(SoloProgressRules.fresh(seed = 8))
        assertTrue(WordSearchAchievements.progress(zero).none { it.unlocked })
        val one = zero.copy(results = listOf(sampleResult("1")))
        val progress = WordSearchAchievements.progress(one).associateBy { it.id }
        assertTrue(progress.getValue("first_puzzle").unlocked)
        assertTrue(progress.getValue("no_hints").unlocked)
        assertFalse(progress.getValue("five_puzzles").unlocked)
        assertFalse(progress.getValue("all_themes").unlocked)
        assertEquals(1,progress.getValue("all_themes").current)
    }

    @Test fun assistedResultDoesNotUnlockNoHintBadge() {
        val zero = SoloProfile(SoloProgressRules.fresh(seed = 8))
        val assisted = zero.copy(results = listOf(sampleResult("1", hints = 3)))
        assertFalse(WordSearchAchievements.progress(assisted).first {it.id=="no_hints"}.unlocked)
    }

    @Test fun dailyCompletionsCountWithoutRequiringStreak() {
        val initial = SoloProfile(SoloProgressRules.fresh(seed=8))
        val dates = listOf("2026-10-01","2026-10-09","2026-11-05")
        val finished = dates.map {
            val d = DailyChallenge.start(it)
            val p = WordSearchGenerator.generate(d.run.theme,d.run.difficulty,d.run.seed)
            DailyChallenge.complete(d.copy(
                run=d.run.copy(foundIds=p.targets.map{t->t.id}.toSet())),100L)
        }
        val profile = initial.copy(dailyEntries=finished)
        val badges = WordSearchAchievements.progress(profile).associateBy {it.id}
        assertTrue(badges.getValue("one_daily").unlocked)
        assertEquals(3,badges.getValue("seven_dailies").current)
        assertTrue("woodland_badger" in WoodlandCollection.unlocked(profile))
    }
}
