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

/** Records and collections: every saved result and unlock stays reachable. */
class CompactRecordsAndCollectionAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun recordsOpenWithOverviewAndAllSoloHistoryPagesRemainAccessible() {
        val records = (0 until 13).map { index ->
            SoloResult(
                puzzleId = "verified-puzzle-$index",
                theme = WordTheme.WOODLAND,
                difficulty = WordDifficulty.EASY,
                wordCount = 6,
                elapsedSeconds = (index + 1).toLong(),
                hintSteps = 0,
                completedAtMs = 1_700_000_000_000L + index * 86_400_000L
            )
        }
        val profile = SoloProfile(SoloProgressRules.fresh(seed = 1001L), results = records)
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(740.dp)) {
                    SoloRecordsScreen(profile)
                }
            }
        }
        compose.onNodeWithText("Your puzzle journal").assertIsDisplayed()
        compose.onNodeWithText("Select Solo, Race, or Duel", substring = true).assertExists()
        compose.onAllNodesWithText("Solo").onFirst().performClick()
        compose.onNodeWithText("13 saved results").assertExists()
        compose.onNodeWithText("Page 1 of 4").assertIsDisplayed()
        // The first page shows only four records, rather than all 13.
        compose.onNodeWithText("1m 0s", substring = true).assertDoesNotExist()
        repeat(3) {
            compose.onNodeWithText("Next").performClick()
        }
        compose.onNodeWithText("Page 4 of 4").assertIsDisplayed()
        compose.onNodeWithText("7s", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Previous").performClick()
        compose.onNodeWithText("Page 3 of 4").assertIsDisplayed()
        compose.onNodeWithText("Race").performClick()
        compose.onNodeWithText("Shared Board Race").assertIsDisplayed()
        compose.onNodeWithText("No races yet", substring = true).assertExists()
        compose.onNodeWithText("Duel").performClick()
        compose.onNodeWithText("Speed Duel").assertIsDisplayed()
        compose.onNodeWithText("No Speed Duels yet", substring = true).assertExists()
        compose.runOnIdle { assertEquals(13, profile.results.size) }
    }

    @Test fun portraitAndAchievementGalleriesPageWithoutLosingUnlocks() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = "ws07f_gallery_" + System.nanoTime() + ".json"
        val store = SoloStore(ctx, file)
        try {
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(740.dp)) {
                        CollectionScreen(store)
                    }
                }
            }
            compose.onNodeWithText("Your Collection").assertIsDisplayed()
            compose.onNodeWithText("Edit display name").assertExists()
            compose.onNodeWithText("Page 1 of 3").assertExists()
            compose.onNodeWithText("Next").performClick()
            compose.onNodeWithText("Page 2 of 3").assertExists()
            compose.onNodeWithText("Badger").assertExists()
            compose.onNodeWithText("Achievements").performClick()
            compose.onNodeWithText("Page 1 of 2").assertExists()
            compose.onNodeWithText("First Page").assertExists()
            compose.onNodeWithText("Next").performClick()
            compose.onNodeWithText("Page 2 of 2").assertExists()
            compose.onNodeWithText("Three Chapters").assertExists()
            compose.onNodeWithText("Fonts").performClick()
            compose.onNodeWithText("Readable Print").assertExists()
            compose.onNodeWithText("Classic Typewriter").assertExists()
            compose.runOnIdle {
                assertEquals(12, WoodlandCollection.progress(store.profile).size)
                assertEquals(8, WordSearchAchievements.progress(store.profile).size)
            }
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(ctx.filesDir, file + suffix).delete()
            }
        }
    }
}
