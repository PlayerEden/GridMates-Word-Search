package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * WS-15 Pass 2 source-compiled Android UI regression. Actual execution on
 * phones/emulators is tracked independently from successful compilation.
 */
class Ws15P2ResponsiveMenuAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun crampedLauncherCardRetainsRealActionAndFullCopy() {
        var opens = 0
        rule.setContent {
            GridMatesTheme {
                Box(Modifier.width(320.dp)) {
                    StorybookQuickEntry(
                        eyebrow = "03 · Together",
                        title = "Same Wi-Fi",
                        actionLabel = "Play Together",
                        actionDescription = "Open Nearby co-op",
                        onAction = { opens++ },
                        modeGlyph = Ws15ModeGlyph.TOGETHER
                    )
                }
            }
        }
        rule.onNodeWithText("03 · TOGETHER").assertExists()
        rule.onNodeWithText("Same Wi-Fi").assertExists()
        rule.onNodeWithContentDescription("Open Nearby co-op").performClick()
        rule.runOnIdle { assertEquals(1, opens) }
    }

    @Test fun collectionStatusAndSettingsIconsNeverReplaceTheirLabels() {
        rule.setContent {
            GridMatesTheme {
                Ws15SectionHeading(
                    glyph = Ws15SectionGlyph.AWARD,
                    title = "Achievements",
                    subtitle = "2 of 12 earned"
                )
            }
        }
        rule.onNodeWithText("Achievements").assertExists()
        rule.onNodeWithText("2 of 12 earned").assertExists()
    }
}
