package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference

/** On-device UI tests for the no-long-scroll Daily page and no-cost clue review. */
class DailyJournalAndSavedCluesAndroidTest {
    @get:Rule val compose = createComposeRule()

    private fun store(prefix: String): Pair<SoloStore, String> {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = prefix + System.nanoTime() + ".json"
        return SoloStore(ctx, name) to name
    }

    private fun clean(name: String) {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        for (suffix in listOf("", ".bak", ".new")) {
            File(ctx.filesDir, name + suffix).delete()
        }
    }

    @Test fun journalPagesAllSavedDatesWithoutLongScrollingCards() {
        val (store, filename) = store("ws07e_daily_")
        val dates = (1L..8L).map { LocalDate.now().minusDays(it).toString() }
        val opened = AtomicReference<String?>(null)
        try {
            dates.forEach(store::openDaily)
            assertEquals(8, store.profile.dailyEntries.size)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        DailyLandingScreen(store, onPlay = { opened.set(it) }, onBack = {})
                    }
                }
            }
            compose.onNodeWithText("Daily Word Search").assertIsDisplayed()
            compose.onNodeWithText("Play today").assertIsDisplayed()
            compose.onNodeWithText("Daily Journal").assertIsDisplayed()
            compose.onNodeWithText("Page 1 of 3").assertIsDisplayed()
            compose.onNodeWithText(dates.first()).assertExists()
            compose.onNodeWithText(dates.last()).assertDoesNotExist()
            compose.onNodeWithText("Next").performClick()
            compose.onNodeWithText("Page 2 of 3").assertIsDisplayed()
            compose.onNodeWithText(dates[3]).assertExists()
            compose.onNodeWithText("Next").performClick()
            compose.onNodeWithText("Page 3 of 3").assertIsDisplayed()
            compose.onNodeWithText(dates.last()).assertExists()
            // Nothing is deleted/marked complete by opening a page.
            compose.runOnIdle { assertEquals(8, store.profile.dailyEntries.size) }
            compose.onAllNodesWithText("Resume").onFirst().performClick()
            compose.runOnIdle { assertEquals(dates[6], opened.get()) }
        } finally { clean(filename) }
    }

    @Test fun reviewingExhaustedHintsNeverSpendsMoreOrModifiesSave() {
        val (store, filename) = store("ws07e_clues_")
        try {
            store.chooseV2(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC)
            repeat(3) { assertNotNull(store.hint().second) }
            val run = store.profile.active
            val puzzle = SoloProgressRules.puzzle(run)
            assertEquals(0, WordHintPolicy.remaining(run))
            assertEquals(1, savedClues(puzzle, run).size)
            assertEquals(3, savedClues(puzzle, run).single().level)
            val savedHints = run.hints.toMap()
            val savedFound = run.foundIds.toSet()
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        WordSearchScreen(store, onBack = {}, onRecords = {})
                    }
                }
            }
            compose.onNodeWithText("Review clues").assertIsDisplayed().performClick()
            compose.onNodeWithText("Your saved clues").assertIsDisplayed()
            compose.onNodeWithText("This does not spend another hint", substring = true)
                .assertExists()
            compose.onNodeWithText("full path is highlighted", substring = true)
                .assertExists()
            compose.onNodeWithText("Back to puzzle").performClick()
            compose.runOnIdle {
                assertEquals(3, WordHintPolicy.used(store.profile.active))
                assertEquals(run.hints, store.profile.active.hints)
                // Opening review doesn't generate progress or change the backup.
                assertEquals(before, store.exportBackup())
            }
        } finally { clean(filename) }
    }

    @Test fun previouslyBoughtClueCanBeReviewedFromOptionsBeforeBudgetEnds() {
        val (store, filename) = store("ws07e_options_")
        try {
            store.chooseV2(WordTheme.GARDEN,
                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.RELAXED)
            assertNotNull(store.hint().second)
            val used = WordHintPolicy.used(store.profile.active)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        WordSearchScreen(store, onBack = {}, onRecords = {})
                    }
                }
            }
            compose.onNodeWithText("Hint (2)").assertExists()
            compose.onNodeWithText("Options").performClick()
            compose.onNodeWithText("Review saved clues (1)").performClick()
            compose.onNodeWithText("Your saved clues").assertIsDisplayed()
            compose.onNodeWithText("Look near row", substring = true).assertExists()
            compose.onNodeWithText("Back to puzzle").performClick()
            compose.runOnIdle { assertEquals(used, WordHintPolicy.used(store.profile.active)) }
        } finally { clean(filename) }
    }
}
