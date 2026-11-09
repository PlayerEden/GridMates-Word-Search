package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Compile in CI, execute on device/emulator as separate QA gate.
 *
 * An actual Android BitmapFactory test ensures the book cards won't depend on
 * WebView or raw SVG support and the source graphic paths really are bundled.
 */
class QaBookArtworkIntegrationAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun allSixKindsForEachThemeDecodeLocallyWithoutNetwork() {
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        for (theme in listOf(WordTheme.WOODLAND, WordTheme.SPACE, WordTheme.GARDEN)) {
            for (kind in BookArtKind.entries) {
                val path = requireNotNull(bookArtPath(theme, kind))
                val image = requireNotNull(assets.open(path).use { BitmapFactory.decodeStream(it) }) {
                    "Couldn\'t decode " + path
                }
                assertTrue("Width missing: " + path, image.width >= 35)
                assertTrue("Height missing: " + path, image.height >= 35)
            }
        }
    }

    @Test fun actualBookshelfUsesSourceArtButKeepsNativePlayActions() {
        compose.onNodeWithText("Test Puzzle Books").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Puzzle Books").assertExists()
        compose.onNodeWithText(
            "Prototype artwork · final illustrations can change"
        ).assertExists()
        compose.onNodeWithText("Woodland · TEST BOOK").assertExists()
        compose.onAllNodesWithText("Open book")[0].performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Getting Started · TEST").assertExists()
        compose.onNodeWithText("Start reading").assertExists()
        compose.onNodeWithText("Bookmark page 1").assertExists()
        // Native chapter/page labels and real callback remain; decorative
        // imagery can be replaced later without changing book progress.
    }
}
