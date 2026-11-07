package com.playereden.gridmates.wordsearch

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** PNG, FileProvider and preview safety checks on real Android framework. */
class ResultPostcardAndroidTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun soloAndDailyRenderTrueFourByFivePngWithoutSolutionGrid() {
        val run = SoloProgressRules.freshV2(WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 13891L)
        val puzzle = SoloProgressRules.puzzle(run)
        for (daily in listOf(false, true)) {
            val summary = PuzzleResultShare.text(
                puzzle, run.copy(hints = mapOf("safe" to 2)), 241L, daily)
            val bitmap = ResultPostcard.render(ctx, summary)
            try {
                assertEquals(1080, bitmap.width)
                assertEquals(1350, bitmap.height)
                val uri = ResultPostcard.writeToPrivateCache(ctx, bitmap)
                assertEquals("content", uri.scheme)
                assertEquals(ctx.packageName + ".postcards", uri.authority)
                assertFalse(uri.toString().contains("files/"))
                assertFalse(uri.toString().contains("gridmates_wordsearch_solo_v1"))
                val decoded = ctx.contentResolver.openInputStream(uri)!!.use {
                    BitmapFactory.decodeStream(it)
                }
                assertNotNull(decoded)
                assertEquals(1080, decoded.width)
                assertEquals(1350, decoded.height)
                assertEquals(bitmap.getPixel(900, 100), decoded.getPixel(900, 100))
                decoded.recycle()
            } finally { bitmap.recycle() }
        }
    }

    @Test fun allFinishedNearbyModesAndLongestResultFitPostcard() {
        val cases = listOf(
            "Nearby Co-op · Puzzle complete" to "We found all 12 words together!",
            "Shared Board Race · Match complete" to
                "My friend won! · Me 4 / Friend 8 · 12 shared words",
            "Speed Duel · Match complete" to
                "My friend finished first! · Me 4 / Friend 12 · 12 words per board"
        )
        cases.forEach { (heading, result) ->
            val summary = listOf("GridMates: Word Search", heading,
                "Woodland · 10×10 · Classic", result,
                "Good puzzles. Good company.").joinToString("\n")
            val bitmap = ResultPostcard.render(ctx, summary)
            try {
                assertEquals(1080, bitmap.width)
                assertEquals(1350, bitmap.height)
            } finally { bitmap.recycle() }
        }
    }

    @Test fun intentGrantsReadOnlyUriAndRejectsUnsafeLocations() {
        val summary = listOf(
            "GridMates: Word Search", "Solo Puzzle complete!",
            "Woodland · 10×10 · Classic",
            "12 words found · 1:33", "No hints used",
            "Good puzzles. Good company.").joinToString("\n")
        val bitmap = ResultPostcard.render(ctx, summary)
        val uri = try { ResultPostcard.writeToPrivateCache(ctx, bitmap) }
            finally { bitmap.recycle() }
        val intent = ResultPostcard.imageShareIntent(ctx, uri)
        assertEquals(Intent.ACTION_SEND, intent.action)
        assertEquals("image/png", intent.type)
        assertEquals(uri, intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
        assertNotNull(intent.clipData)
        assertEquals(uri, intent.clipData!!.getItemAt(0).uri)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        assertEquals(0, intent.flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        assertTrue(runCatching {
            ResultPostcard.imageShareIntent(ctx, Uri.parse("file:///sdcard/unsafe.png"))
        }.isFailure)
        assertTrue(runCatching {
            ResultPostcard.imageShareIntent(ctx, Uri.parse("content://other.app/private"))
        }.isFailure)
    }

    @Test fun malformedOrUnverifiedTextCannotProduceCard() {
        val examples = listOf(
            "GridMates: Word Search\nVictory",
            "GridMates: Word Search\nFriendly victory!\nAnything here",
            "GridMates: Word Search\nSpeed Duel · Match complete\nGARDEN\n" +
                "I won! · Me 12\nUnverified tagline",
            "Another App\nSolo Puzzle complete!\nWoodland · 10×10\n" +
                "12 words found\nGood puzzles. Good company."
        )
        examples.forEach { value ->
            assertTrue(runCatching { ResultPostcard.render(ctx, value) }.isFailure)
        }
    }
    @Test fun sharingPrunesOnlyExpiredPostcardsAndNeverOtherAppFiles() {
        val folder = File(ctx.cacheDir, "result-postcards")
        assertTrue(folder.isDirectory || folder.mkdirs())
        val expired = File(folder, "gridmates-result-ws06h-expired.png")
        val recent = File(folder, "gridmates-result-ws06h-recent.png")
        val unrelated = File(folder, "ws06h-unrelated.dat")
        expired.writeBytes(byteArrayOf(1, 2, 3))
        recent.writeBytes(byteArrayOf(4, 5, 6))
        unrelated.writeBytes(byteArrayOf(7, 8, 9))
        assertTrue(expired.setLastModified(
            System.currentTimeMillis() - 9L * 24L * 60L * 60L * 1000L))
        val summary = listOf(
            "GridMates: Word Search", "Solo Puzzle complete!",
            "Woodland · 10×10 · Classic",
            "12 words found · 1:33", "No hints used",
            "Good puzzles. Good company.").joinToString("\\n")
        try {
            val bitmap = ResultPostcard.render(ctx, summary)
            try { ResultPostcard.writeToPrivateCache(ctx, bitmap) }
            finally { bitmap.recycle() }
            assertFalse("Expired postcard should be removed", expired.exists())
            assertTrue("A recently shared postcard may still be in use", recent.exists())
            assertTrue("Unrelated local cache file must be untouched", unrelated.exists())
        } finally {
            expired.delete()
            recent.delete()
            unrelated.delete()
        }
    }

}
