package com.playereden.gridmates.wordsearch

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Native, reproducible 4:5 result postcard. Draws ONLY the already sanitized
 * result summary presented in the preview. Decorative cells contain no puzzle
 * letters or answers. No network, account, analytics or external renderer.
 */
internal object ResultPostcard {
    const val WIDTH = 1080
    const val HEIGHT = 1350
    private const val MAX_SUMMARY = 700
    private val Paper = Color.rgb(248, 245, 233)
    private val Forest = Color.rgb(35, 70, 56)
    private val Gold = Color.rgb(182, 145, 81)
    private val Cream = Color.rgb(255, 253, 244)
    private val Soft = Color.rgb(228, 233, 217)

    private fun ink(size: Float, color: Int, typeface: Typeface) =
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            this.color = color
            this.textSize = size
            this.typeface = typeface
            isDither = true
        }

    /** Break at actual measured word widths, never silently drop factual lines. */
    private fun wrap(value: String, paint: Paint, width: Float): List<String> {
        val words = value.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val lines = mutableListOf<String>()
        var current = ""
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= width) {
                current = candidate
            } else {
                if (current.isNotEmpty()) lines.add(current)
                if (paint.measureText(word) <= width) {
                    current = word
                } else {
                    // Bounded fallback for an unexpectedly long single word.
                    var remainder = word
                    while (remainder.isNotEmpty()) {
                        val n = paint.breakText(remainder, true, width, null).coerceAtLeast(1)
                        lines.add(remainder.take(n))
                        remainder = remainder.drop(n)
                    }
                    current = ""
                }
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }

    private fun drawBlock(canvas: Canvas, lines: String, x: Float, startY: Float,
                          width: Float, size: Float, color: Int, font: Typeface,
                          spacing: Float): Float {
        val paint = ink(size, color, font)
        var baseline = startY
        for (line in wrap(lines, paint, width)) {
            canvas.drawText(line, x, baseline, paint)
            baseline += spacing
        }
        return baseline
    }

    fun render(context: Context, summary: String): Bitmap {
        require(summary.length in 40..MAX_SUMMARY && !summary.contains('\r'))
        val lines = summary.split('\n')
        require(lines.size in 5..7 && lines[0] == "GridMates: Word Search") {
            "Postcards must originate from a validated local result"
        }
        require(lines.none { it.isBlank() || it.length > 220 })
        require(lines[1] in setOf(
            "Solo Puzzle complete!", "Daily Puzzle complete!",
            "Nearby Co-op · Puzzle complete",
            "Shared Board Race · Match complete",
            "Speed Duel · Match complete"
        ) && lines.last() == "Good puzzles. Good company.")
        val normal = BoardFontRegistry.resolve(context, "atkinson")
        val bold = Typeface.create(normal, Typeface.BOLD)
        val mono = BoardFontRegistry.resolve(context, "plex")
        return Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawColor(Paper)
            val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
            fill.color = Soft
            canvas.drawRoundRect(RectF(52f, 52f, 1028f, 1298f), 34f, 34f, fill)
            fill.color = Cream
            canvas.drawRoundRect(RectF(62f, 62f, 1018f, 1288f), 30f, 30f, fill)
            canvas.drawText("G R I D M A T E S", 112f, 153f,
                ink(47f, Forest, bold))
            canvas.drawText("WORD SEARCH", 109f, 232f,
                ink(80f, Forest, bold))
            fill.color = Gold
            canvas.drawRoundRect(RectF(110f, 256f, 302f, 265f), 4f, 4f, fill)

            // Text inside the dark card is line-wrapped to stay legible at
            // exported image size, independently of the phone's orientation.
            fill.color = Forest
            canvas.drawRoundRect(RectF(90f, 314f, 990f, 879f), 32f, 32f, fill)
            val titleEnd = drawBlock(canvas, lines[1], 145f, 412f,
                782f, 57f, Cream, bold, 70f)
            val factsEnd = drawBlock(canvas, lines[2], 145f,
                titleEnd + 15f, 780f, 42f, Soft, normal, 53f)
            fill.color = Gold
            canvas.drawRoundRect(RectF(145f, factsEnd + 5f, 255f, factsEnd + 11f),
                3f, 3f, fill)
            var y = factsEnd + 58f
            for (item in lines.subList(3, lines.size - 1)) {
                y = drawBlock(canvas, item, 145f, y,
                    780f, 43f, Cream, normal, 55f) + 8f
            }
            require(y <= 878f) { "Result too long for a safe postcard" }

            // A generic letterless paper puzzle motif. This is intentionally
            // NOT the actual solution grid; a Daily card has no spoilers.
            val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(202, 211, 193)
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            val gridLeft = 335f
            val gridTop = 921f
            val cell = 46f
            for (i in 0..8) {
                val at = i * cell
                canvas.drawLine(gridLeft + at, gridTop,
                    gridLeft + at, gridTop + 8 * cell, gridPaint)
                canvas.drawLine(gridLeft, gridTop + at,
                    gridLeft + 8 * cell, gridTop + at, gridPaint)
            }
            // Paint a small signature accent in the otherwise blank grid.
            fill.color = Gold
            for ((r,c) in listOf(1 to 1, 2 to 3, 4 to 4, 5 to 2, 6 to 6)) {
                canvas.drawCircle(gridLeft + c * cell + cell / 2f,
                    gridTop + r * cell + cell / 2f, 9f, fill)
            }
            drawBlock(canvas, lines.last(), 115f, 1256f,
                850f, 45f, Forest, mono, 52f)
        }
    }

    // Old images are temporary sharing artifacts, not permanent game records.
    // Only the app-owned postcard filename pattern in the dedicated cache
    // subtree is eligible; never prune backups or a freshly shared image.
    private const val POSTCARD_CACHE_TTL_MS = 7L * 24L * 60L * 60L * 1000L

    private fun pruneExpiredPostcards(folder: File, nowMs: Long) {
        val olderThan = nowMs - POSTCARD_CACHE_TTL_MS
        folder.listFiles()?.forEach { candidate ->
            if (candidate.isFile && candidate.name.startsWith("gridmates-result-") &&
                candidate.name.endsWith(".png") &&
                candidate.lastModified() > 0L && candidate.lastModified() < olderThan
            ) {
                candidate.delete()
            }
        }
    }

    /** Only the isolated images cache subtree can be exposed through FileProvider. */
    fun writeToPrivateCache(context: Context, bitmap: Bitmap): Uri {
        require(bitmap.width == WIDTH && bitmap.height == HEIGHT)
        val folder = File(context.cacheDir, "result-postcards")
        require(folder.isDirectory || folder.mkdirs()) { "Cannot create postcard cache" }
        pruneExpiredPostcards(folder, System.currentTimeMillis())
        val file = File.createTempFile("gridmates-result-", ".png", folder)
        try {
            FileOutputStream(file).use { output ->
                require(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    "PNG encoder failed"
                }
            }
            require(file.length() > 1_024L)
            return FileProvider.getUriForFile(context,
                context.packageName + ".postcards", file)
        } catch (t: Throwable) {
            file.delete()
            throw t
        }
    }

    fun imageShareIntent(context: Context, uri: Uri): Intent {
        require(uri.scheme == "content")
        require(uri.authority == context.packageName + ".postcards")
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(context.contentResolver,
                "GridMates result postcard", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
