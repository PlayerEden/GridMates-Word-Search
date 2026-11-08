package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Real navigation: choose BEFORE puzzle creation, even on a narrow phone. */
class SoloPreGameNavigationAndroidTest {
    @get:Rule val rule = createComposeRule()
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun cancelDoesNotReplaceGameAndSelectedOptionsCreateNewSavedSolo() {
        val name = "ws09c_ui_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(ctx, name)
            val original = store.profile.active
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithText("New puzzle").assertIsDisplayed().performClick()
            rule.onNodeWithText("Start puzzle").assertIsDisplayed()
            assertEquals(original, store.profile.active)
            assertTrue(store.profile.pausedSolo.isEmpty())
            rule.onNodeWithText("Cancel").performClick()
            assertEquals(original, store.profile.active)
            rule.onNodeWithText("Continue Solo").assertIsDisplayed()

            rule.onNodeWithText("New puzzle").performClick()
            rule.onNodeWithText("Woodland").performClick()
            rule.onNodeWithText("Space").performClick()
            rule.onNodeWithText("Standard · 15×15 · 16 words").performClick()
            rule.onNodeWithText("Extended · 20×20 · 24 words").performClick()
            rule.onNodeWithText("Classic").performClick()
            rule.onNodeWithText("Expert").performClick()
            assertEquals(original, store.profile.active) // staged, not committed
            rule.onNodeWithText("Start puzzle").performScrollTo().performClick()
            assertEquals(WordTheme.SPACE, store.profile.active.theme)
            assertEquals(PuzzleBoardSize.BOOK_BIG, store.profile.active.boardSize)
            assertEquals(PuzzleChallenge.EXPERT, store.profile.active.challenge)
            assertEquals(listOf(original), store.profile.pausedSolo)
            rule.onNodeWithText("Trace words · pinch to zoom", substring = true)
                .assertExists()
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new"))
                File(ctx.filesDir, name + suffix).delete()
            ctx.getSharedPreferences(name + "_solo_setup", 0).edit().clear().commit()
        }
    }

    @Test fun savedLibraryNewSoloOpensSetupInsteadOfReplacingGameImmediately() {
        val name = "ws09c_library_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(ctx, name)
            val before = store.profile
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithText("My Puzzles").performClick()
            rule.onNodeWithText("New Solo").performClick()
            rule.onNodeWithText("New puzzle").assertIsDisplayed()
            assertEquals(before, store.profile)
            rule.onNodeWithText("Cancel").performClick()
            rule.onNodeWithText("My Puzzles").assertExists()
            assertEquals(before, store.profile)
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new"))
                File(ctx.filesDir, name + suffix).delete()
            ctx.getSharedPreferences(name + "_solo_setup", 0).edit().clear().commit()
        }
    }
}
