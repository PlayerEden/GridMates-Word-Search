package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * WS-14A Android-source QA of our shared native interface grammar.
 * CI compiles this test; device/emulator execution remains a separate gate.
 */
class StorybookVisualKitAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun illustrationReadyModeCardHasNativeClickableTextAndNoFakeArt() {
        var actionCount = 0
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(480.dp)) {
                    StorybookModeCard(
                        eyebrow = "02 · A little every day",
                        title = "Daily Puzzle",
                        summary = "A fresh puzzle every day.",
                        actionLabel = "Open Daily",
                        onAction = { actionCount++ },
                        status = "Ready"
                    )
                }
            }
        }
        compose.onNodeWithTag("storybook-mode-card").assertExists()
        compose.onNodeWithText("Daily Puzzle").assertExists()
        compose.onNodeWithText("Ready").assertExists()
        compose.onNodeWithTag("storybook-mode-art").assertDoesNotExist()
        compose.onNodeWithText("Open Daily").performClick()
        compose.runOnIdle { assertEquals(1, actionCount) }
    }

    @Test fun compactLauncherHasUsableNativeActionAtLargeFontSize() {
        var opened = 0
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(450.dp)) {
                        StorybookQuickEntry(
                            eyebrow = "04 · Puzzle library · QA only",
                            title = "Puzzle Books",
                            actionLabel = "Test Puzzle Books",
                            onAction = { opened++ }
                        )
                    }
                }
            }
        }
        compose.onNodeWithTag("storybook-quick-entry").assertExists()
        compose.onNodeWithTag("storybook-quick-action").assertIsDisplayed()
        compose.onNodeWithTag("storybook-quick-art").assertDoesNotExist()
        compose.onNodeWithText("Test Puzzle Books").performClick()
        compose.runOnIdle { assertEquals(1, opened) }
    }

    @Test fun largeFontsKeepNativeHeaderAndActionRatherThanBakingTextIntoSprites() {
        var opens = 0
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(620.dp)) {
                        StorybookFrame {
                            StorybookHeading(
                                eyebrow = "Your profile & rewards",
                                title = "Your Collection",
                                subtitle = "Collect more portraits and fonts as you finish puzzles."
                            )
                            StorybookModeCard(
                                eyebrow = "01 · Solo",
                                title = "Solo Puzzle",
                                summary = "Pick up where you left off.",
                                actionLabel = "Continue Solo",
                                onAction = { opens++ }
                            )
                        }
                    }
                }
            }
        }
        compose.onNodeWithTag("storybook-heading").assertExists()
        compose.onNodeWithText("Your Collection").assertExists()
        compose.onNodeWithText("Continue Solo").performClick()
        compose.runOnIdle { assertEquals(1, opens) }
    }
}
