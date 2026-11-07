package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileInputStream

/**
 * Full-screen actual Android gameplay + results visual references.
 * Files are written to the emulator's Download folder by the instrumentation
 * shell, so uninstalling the target after tests cannot erase them.
 */
class GameplayResultVisualTest {
    @get:Rule val rule = createComposeRule()

    private fun screenshot(file: String) {
        val desc = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("screencap -p /sdcard/Download/" + file)
        try {
            // Wait for the shell command, rather than racing an adb pull.
            FileInputStream(desc.fileDescriptor).use { it.readBytes() }
        } finally { desc.close() }
    }

    @Test fun playingAndCompletedBoardHaveRealAccessibleControls() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws05g_visual_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            rule.setContent {
                GridMatesTheme {
                    WordSearchScreen(store = store, onBack = {}, onRecords = {})
                }
            }
            rule.onNodeWithText("Words to find", substring = true).assertExists()
            rule.onNodeWithText("Hint (", substring = true).assertExists()
            rule.waitForIdle()
            screenshot("ws05g-gameplay.png")

            rule.runOnIdle {
                for (target in puzzle.targets) {
                    store.claim(target.positions.first(), target.positions.last())
                }
            }
            rule.onNodeWithText("Puzzle complete!", substring = true).assertExists()
            rule.onNodeWithText("Share results").assertExists()
            rule.onNodeWithText("Review completed board").assertExists()
            rule.onNodeWithText("Unassisted solve", substring = true).assertExists()
            rule.waitForIdle()
            screenshot("ws05g-results.png")
            assertTrue(store.profile.results.any { it.puzzleId == puzzle.id })
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, name + suffix).delete()
            }
        }
    }
}
