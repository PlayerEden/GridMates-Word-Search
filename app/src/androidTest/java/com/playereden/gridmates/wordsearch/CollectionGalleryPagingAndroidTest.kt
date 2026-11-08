package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/** A paged gallery must expose later rewards without losing stored unlocks. */
class CollectionGalleryPagingAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun portraitAndAchievementNextPagesStayReachableAtPhoneSize() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws07g_gallery_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, filename)
            val oldPortraits = WoodlandCollection.progress(store.profile)
            val oldAchievements = WordSearchAchievements.progress(store.profile)
            compose.setContent {
                GridMatesTheme {
                    Column(Modifier.width(360.dp).height(720.dp)
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CollectionScreen(store)
                    }
                }
            }
            compose.onNodeWithText("Your Collection").assertIsDisplayed()
            compose.onNodeWithText("Edit display name").assertExists()
            compose.onNodeWithTag("portraits-next").performScrollTo()
                .assertIsEnabled().performClick()
            compose.onNodeWithText("Page 2 of", substring = true)
                .performScrollTo().assertIsDisplayed()

            compose.onNodeWithText("Achievements").performScrollTo().performClick()
            compose.onNodeWithTag("achievements-next").performScrollTo()
                .assertIsEnabled().performClick()
            compose.onNodeWithText("Page 2 of", substring = true)
                .performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Fonts").performScrollTo().performClick()
            compose.onNodeWithText("board fonts unlocked", substring = true).assertExists()

            compose.runOnIdle {
                assertEquals(12, WoodlandCollection.progress(store.profile).size)
                assertEquals(8, WordSearchAchievements.progress(store.profile).size)
                assertEquals(oldPortraits, WoodlandCollection.progress(store.profile))
                assertEquals(oldAchievements, WordSearchAchievements.progress(store.profile))
            }
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
