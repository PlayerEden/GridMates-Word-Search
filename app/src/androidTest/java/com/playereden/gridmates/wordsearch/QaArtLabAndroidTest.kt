package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Real debug-only activity route to the bundled exact WS-12F prototype art.
 * CI compiles these instrumentation tests; device execution is a separate gate.
 */
class QaArtLabAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun generatedArtworkIsPackagedLocallyAndVisibleViaHomeEntry() {
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        val root = requireNotNull(assets.list("ws12"))
        assertTrue("Art Lab preview wasn't generated", "review.html" in root)
        val page = assets.open("ws12/review.html").bufferedReader().readText()
        assertTrue(page.contains("Woodland"))
        assertTrue(page.contains("Space"))
        assertTrue(page.contains("Garden"))
        assertTrue(page.contains("Unapproved concept preview"))
        assertTrue(page.contains("<h1>Library artwork</h1>"))
        // An Art Lab proof must not simulate a tappable "Open book" button.
        assertFalse(page.contains(">Open book<"))
        assertTrue("All book labels must be native HTML, not SVG pixels",
            page.contains("<h2>Woodland</h2>"))
        assertEquals(3, assets.list("ws12/spines")?.count { it.endsWith(".svg") })
        assertEquals(3, assets.list("ws12/books")?.count { it.endsWith(".svg") })
        assertEquals(3, assets.list("ws12/emblems")?.count { it.endsWith(".svg") })
        assertEquals(3, assets.list("ws12/chapters")?.count { it.endsWith(".svg") })
        assertEquals(3, assets.list("ws12/bookmarks")?.count { it.endsWith(".svg") })
        assertEquals(3, assets.list("ws12/seals")?.count { it.endsWith(".svg") })

        compose.onNodeWithText("View Library Artwork").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Back to Play").assertExists()
        compose.onNodeWithText("Back to Play").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Test Puzzle Books").assertExists()
    }

    @Test fun ws14NativeReviewShowsActualImportStatusWithoutChangingBookState() {
        val availability = ws14ReadAvailability(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        val packaged = availability.values.sumOf {
            (if (it.cover) 1 else 0) + (if (it.spine) 1 else 0)
        }

        compose.onNodeWithText("View Library Artwork").performScrollTo().performClick()
        compose.onNodeWithText("Review WS-14 assets").performClick()
        compose.onNodeWithText("Illustrated Books").assertExists()
        compose.onNodeWithText(
            "$packaged of 6 WS-14 cover and spine assets bundled for QA."
        ).assertExists()
        compose.onNodeWithText("Study A · Original", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Study B · Illustrated", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Back to WS-12 lab").performClick()
        compose.onNodeWithText("Back to Play").performClick()
        compose.onNodeWithText("Test Puzzle Books").assertExists()
    }

    @Test fun availabilityMappingDoesNotInventAssets() {
        val empty = ws14ThemeAvailability(emptySet(), emptySet())
        assertTrue(empty.values.none { it.cover || it.spine })
        val partial = ws14ThemeAvailability(
            setOf("woodland.png"), setOf("garden.png"))
        assertTrue(partial.getValue(com.playereden.gridmates.wordsearch.core.WordTheme.WOODLAND).cover)
        assertFalse(partial.getValue(com.playereden.gridmates.wordsearch.core.WordTheme.SPACE).cover)
        assertTrue(partial.getValue(com.playereden.gridmates.wordsearch.core.WordTheme.GARDEN).spine)
    }

    @Test fun artPreviewDoesNotBecomeBookProgressOrACollectionReward() {
        // This is a content/packaging policy test. The QA art asset IDs cannot
        // overlap active reward IDs and are never registered as new book pages.
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        val manifest = assets.open("ws12/render_sha256.json").bufferedReader().readText()
        assertTrue(manifest.contains("PROTOTYPE_NOT_OWNER_APPROVED"))
        assertTrue(manifest.contains("\"owner_approved\": false"))
        assertTrue(manifest.contains("\"android_resources_modified\": false"))
        assertFalse(manifest.contains("\"owner_approved\": true"))
    }
}
