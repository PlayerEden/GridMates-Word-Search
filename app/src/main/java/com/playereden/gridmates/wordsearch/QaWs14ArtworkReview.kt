package com.playereden.gridmates.wordsearch

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.WordTheme

/** WS-14C: review exact local QA source assets, never call any external service. */
internal data class Ws14ArtAvailability(val cover: Boolean, val spine: Boolean)

/** Pure, testable mapping from bundled filenames to the three stable book themes. */
internal fun ws14ThemeAvailability(
    covers: Set<String>,
    spines: Set<String>
): Map<WordTheme, Ws14ArtAvailability> = WordTheme.entries.associateWith { theme ->
    val file = when (theme) {
        WordTheme.WOODLAND -> "woodland.png"
        WordTheme.SPACE -> "space.png"
        WordTheme.GARDEN -> "garden.png"
    }
    Ws14ArtAvailability(file in covers, file in spines)
}

internal fun ws14ReadAvailability(context: Context): Map<WordTheme, Ws14ArtAvailability> {
    fun files(directory: String): Set<String> =
        runCatching { context.assets.list(directory).orEmpty().toSet() }
            .getOrDefault(emptySet())
    return ws14ThemeAvailability(files("ws14/books"), files("ws14/spines"))
}

/**
 * Owner-facing QA artwork sheet, not a production screen or approval control.
 * Study A is always old artwork, Study B uses individually imported WS-14
 * cover/spine assets if present and falls back gracefully to WS-12 art.
 * Every title and art-loaded status is live Android text, not raster text.
 */
@Composable
internal fun QaWs14ArtworkReview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val availability = remember(context) { ws14ReadAvailability(context) }
    val imported = availability.values.sumOf { (if (it.cover) 1 else 0) + (if (it.spine) 1 else 0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "review-intro") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StorybookHeading(
                    eyebrow = "WS-14 · Android visual QA",
                    title = "Illustrated Books",
                    subtitle = "Compare native, locally bundled artwork at phone size."
                )
                Text(
                    "$imported of 6 WS-14 cover and spine assets bundled for QA.",
                    color = Forest, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (imported == 0)
                        "No new original files are installed yet. Both studies will show existing prototype art."
                    else "Only installed original artwork is shown in Study B. Missing assets fall back to older art.",
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Concept approval does not approve publishing rights, exact pixels, or the release APK.",
                    color = Muted, style = MaterialTheme.typography.labelSmall
                )
            }
        }
        items(WordTheme.entries, key = { it.name }) { theme ->
            val state = availability.getValue(theme)
            StorybookFrame(Modifier.fillMaxWidth()) {
                Text(theme.label, color = Forest,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Study A · Original",
                            color = Forest, style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            BookArt(theme, BookArtKind.COVER,
                                Modifier.width(94.dp).height(126.dp),
                                fieldNotes = false)
                            BookArt(theme, BookArtKind.SPINE,
                                Modifier.width(12.dp).height(110.dp),
                                fieldNotes = false)
                        }
                    }
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Study B · Illustrated",
                            color = Forest, style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            BookArt(theme, BookArtKind.COVER,
                                Modifier.width(94.dp).height(126.dp),
                                fieldNotes = true)
                            BookArt(theme, BookArtKind.SPINE,
                                Modifier.width(12.dp).height(110.dp),
                                fieldNotes = true)
                        }
                    }
                }
                Text(
                    "New cover: " + if (state.cover) "Bundled for QA" else "Awaiting original file",
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "New spine: " + if (state.spine) "Bundled for QA" else "Awaiting original file",
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item(key = "review-footer") {
            Text(
                "This review does not change any saved books, rewards, game modes or release art.",
                color = Muted, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
