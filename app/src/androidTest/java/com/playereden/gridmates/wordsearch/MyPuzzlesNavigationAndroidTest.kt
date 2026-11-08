package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

class MyPuzzlesNavigationAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun playerCanOpenPausedLibraryAndResumeWithoutDiscardingCurrentGame() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws09b_library_ui_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(context, name)
            val pausedId = store.profile.active.puzzleId
            check(store.newPuzzle())
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithText("My Puzzles").assertIsDisplayed().performClick()
            rule.onNodeWithText("PAUSED · 1 / 24").assertExists()
            rule.onNodeWithText("Resume").performScrollTo().performClick()
            rule.onNodeWithText("Trace words · pinch to zoom", substring = true)
                .assertExists()
            assert(store.profile.active.puzzleId == pausedId)
            assert(store.profile.pausedSolo.size == 1)
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, name + suffix).delete()
            }
        }
    }
}
