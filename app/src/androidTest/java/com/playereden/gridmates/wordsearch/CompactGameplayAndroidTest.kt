package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Exercises real Compose measurement inside a 320dp-wide viewport at 200%
 * font scale. A 320dp simulated viewport is not a physical 320dp phone.
 */
class CompactGameplayAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun atTwoHundredPercentPuzzleControlsRemainReachable() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val save = "ws05i_compact_" + System.nanoTime() + ".json"
        val store = SoloStore(ctx, save)
        try {
            rule.setContent {
                val base = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(base.density, 2f)) {
                    GridMatesTheme {
                        Box(Modifier.width(320.dp).height(640.dp)) {
                            WordSearchScreen(store, onBack = {}, onRecords = {})
                        }
                    }
                }
            }
            rule.onNodeWithText("Home").assertIsDisplayed()
            rule.onNodeWithText("Options").assertIsDisplayed()
            rule.onNodeWithText("Hint (", substring = true).assertIsDisplayed()
            rule.onNodeWithText("Elapsed", substring = true).assertExists()
            rule.onNodeWithText("Words to find", substring = true).assertExists()
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            rule.runOnIdle {
                puzzle.targets.forEach { target ->
                    store.claim(target.positions.first(), target.positions.last())
                }
            }
            rule.onNodeWithText("Puzzle complete!", substring = true).assertExists()
            rule.onNodeWithText("Share results").performScrollTo().assertIsDisplayed()
            rule.onNodeWithText("Review completed board")
                .performScrollTo().assertIsDisplayed()
        } finally {
            File(ctx.filesDir, save).delete()
        }
    }
}
