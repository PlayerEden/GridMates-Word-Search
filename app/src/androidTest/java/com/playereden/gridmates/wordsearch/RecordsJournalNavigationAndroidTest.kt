package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

/** Records remain readable and reachable without one endless history wall. */
class RecordsJournalNavigationAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun pageMathNeverDropsTheTailOfAnyHistory() {
        assertEquals(1, journalPageCount(0, 4))
        assertEquals(1, journalPageCount(4, 4))
        assertEquals(2, journalPageCount(5, 4))
        assertEquals(7, journalPageCount(25, 4))
        assertEquals(13, journalPageCount(50, 4))
    }

    @Test fun soloRaceAndDuelAreSeparateDiscoverableTabs() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws07f_tabs_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            compose.setContent {
                GridMatesTheme {
                    Column(Modifier.width(360.dp).height(720.dp)
                        .verticalScroll(rememberScrollState())) {
                        SoloRecordsScreen(store.profile)
                    }
                }
            }
            compose.onNodeWithText("Browse your results").assertExists()
            compose.onNodeWithText("Solo").assertExists()
            compose.onNodeWithText("Race").assertIsDisplayed()
            compose.onNodeWithText("Duel").assertIsDisplayed()
            compose.onNodeWithText("Recent puzzles").assertExists()
            compose.onNodeWithText("Shared Board Race").assertDoesNotExist()
            compose.onNodeWithText("Race").performClick()
            compose.onNodeWithText("Shared Board Race").assertIsDisplayed()
            compose.onNodeWithText("Recent puzzles").assertDoesNotExist()
            compose.onNodeWithText("Duel").performClick()
            compose.onNodeWithText("Speed Duel").assertIsDisplayed()
            compose.onNodeWithText("Shared Board Race").assertDoesNotExist()
            compose.onNodeWithText("Solo").performClick()
            compose.onNodeWithText("Recent puzzles").assertExists()
        } finally {
            listOf("", ".bak", ".new").forEach {
                File(context.filesDir, name + it).delete()
            }
        }
    }
}
