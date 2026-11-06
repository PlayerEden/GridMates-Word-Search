package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.GameMode

private enum class Page(val label: String) {
    PLAY("Play"), RECORDS("Records"), COLLECTION("Collection"),
    SETTINGS("Settings"), HELP("Help"), PUZZLE("Puzzle")
}

@Composable
private fun GridMatesNavGlyph(page: Page, selected: Boolean) {
    val ink = if (selected) Forest else Muted
    Canvas(Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()
        when (page) {
            Page.PLAY -> {
                val roof = Path().apply {
                    moveTo(w * 0.12f, h * 0.46f)
                    lineTo(w * 0.50f, h * 0.14f)
                    lineTo(w * 0.88f, h * 0.46f)
                }
                drawPath(roof, ink, style = Stroke(width = stroke,
                    cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawRect(ink, topLeft = Offset(w * 0.24f, h * 0.46f),
                    size = Size(w * 0.52f, h * 0.40f),
                    style = Stroke(width = stroke))
            }
            Page.RECORDS -> {
                for (i in 0..2) {
                    val barHeight = h * (0.25f + i * 0.20f)
                    drawRoundRect(ink,
                        topLeft = Offset(w * (0.13f + i * 0.27f), h * 0.89f - barHeight),
                        size = Size(w * 0.16f, barHeight),
                        cornerRadius = CornerRadius(stroke * 0.5f))
                }
            }
            Page.COLLECTION -> {
                for (row in 0..1) for (col in 0..1) {
                    drawRoundRect(ink,
                        topLeft = Offset(w * (0.16f + col * 0.39f),
                            h * (0.16f + row * 0.39f)),
                        size = Size(w * 0.28f, h * 0.28f),
                        cornerRadius = CornerRadius(stroke),
                        style = Stroke(width = stroke))
                }
            }
            Page.SETTINGS -> {
                drawCircle(ink, radius = w * 0.29f, style = Stroke(width = stroke))
                drawCircle(ink, radius = w * 0.11f)
                for (i in 0..7) {
                    val angle = i * Math.PI / 4.0
                    val dx = kotlin.math.cos(angle).toFloat()
                    val dy = kotlin.math.sin(angle).toFloat()
                    drawLine(ink,
                        Offset(w * 0.5f + dx * w * 0.33f, h * 0.5f + dy * h * 0.33f),
                        Offset(w * 0.5f + dx * w * 0.44f, h * 0.5f + dy * h * 0.44f),
                        strokeWidth = stroke, cap = StrokeCap.Round)
                }
            }
            else -> Unit
        }
    }
}

@Composable
internal fun GridMatesShell(soloStore: SoloStore) {
    var pageName by rememberSaveable { mutableStateOf(Page.PLAY.name) }
    val page = Page.entries.firstOrNull { it.name == pageName } ?: Page.PLAY

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            if (page != Page.PUZZLE) NavigationBar(containerColor = Paper) {
                Page.entries.filter { it != Page.HELP && it != Page.PUZZLE }.forEach { item ->
                    NavigationBarItem(
                        modifier = Modifier.semantics {
                            contentDescription = "Open " + item.label
                        },
                        selected = item == page,
                        onClick = { pageName = item.name },
                        icon = { GridMatesNavGlyph(item, item == page) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        if (page == Page.PUZZLE) {
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                WordSearchScreen(soloStore,
                    onBack = { pageName = Page.PLAY.name },
                    onRecords = { pageName = Page.RECORDS.name })
            }
        } else Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (page) {
                Page.PLAY -> HomeScreen(soloStore,
                    onHelp = { pageName = Page.HELP.name },
                    onSolo = { pageName = Page.PUZZLE.name })
                Page.PUZZLE -> Unit
                Page.RECORDS -> SoloRecordsScreen(soloStore.profile)
                Page.COLLECTION -> PlaceholderScreen(
                    "Collection", "Our original Woodland portraits and other GridMates styles will be added from the verified Sudoku art collection."
                )
                Page.SETTINGS -> PlaceholderScreen(
                    "Settings", "Touch feedback, readable letter sizes, color and font preferences are planned. No unsaved controls are shown as working settings."
                )
                Page.HELP -> HelpScreen(onBack = { pageName = Page.PLAY.name })
            }
        }
    }
}

@Composable
private fun Heading(eyebrow: String, heading: String) {
    Text(
        eyebrow.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = Gold
    )
    Text(
        heading,
        style = MaterialTheme.typography.headlineLarge,
        color = Forest,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun HomeScreen(store: SoloStore, onHelp: () -> Unit, onSolo: () -> Unit) {
    Heading("GridMates", "Word Search")
    store.warning?.let { warning ->
        Card(Modifier.fillMaxWidth()) {
            Text(warning, Modifier.padding(12.dp), color = Gold)
        }
    }
    Text(
        "Good puzzles. Good company.",
        color = Muted,
        style = MaterialTheme.typography.bodyLarge
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Your next puzzle", style = MaterialTheme.typography.titleMedium)
            Text(
                store.profile.active.theme.label + " · " + store.profile.active.difficulty.label +
                    " · " + store.profile.active.foundIds.size + " words found" +
                    " · " + store.profile.completions + " completed",
                color = Muted
            )
            Button(onClick = onSolo) { Text("Try Solo Word Search") }
            Text("Progress saves automatically on this device.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onHelp) { Text("How to Play") }
        }
    }
    Text("Ways to play", style = MaterialTheme.typography.titleMedium)
    val modes = listOf(
        GameMode.SOLO to "Solve a themed puzzle at your own pace",
        GameMode.DAILY to "A fresh offline challenge every day",
        GameMode.COOP to "Find words together on two nearby phones",
        GameMode.SHARED_BOARD_RACE to "Race to claim words first",
        GameMode.SPEED_DUEL to "Race on matching private boards"
    )
    modes.forEach { (mode, detail) ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    when (mode) {
                        GameMode.SOLO -> "Solo"
                        GameMode.DAILY -> "Daily"
                        GameMode.COOP -> "Co-op"
                        GameMode.SHARED_BOARD_RACE -> "Shared Board Race"
                        GameMode.SPEED_DUEL -> "Speed Duel"
                    },
                    fontWeight = FontWeight.SemiBold
                )
                Text(detail, color = Muted)
                Text(if (mode == GameMode.SOLO) "Try Solo above" else "Planned",
                    style = MaterialTheme.typography.labelSmall, color = Gold)
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, details: String) {
    Heading("GridMates: Word Search", title)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(details, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text(
                "This section is a navigation preview, not yet a playable or saved feature.",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
    }
}

@Composable
private fun HelpScreen(onBack: () -> Unit) {
    Heading("Getting started", "How to Play")
    Text(
        "In classic Word Search, trace a straight line across the letters to find each listed word. " +
            "Words can run across, down, or diagonally, sometimes backward. " +
            "Found words are crossed off and highlighted.",
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        "When nearby multiplayer arrives, one phone will host and another will join over the same Wi-Fi.",
        color = Muted
    )
    Button(onClick = onBack) {
        Text("Back to Play")
    }
}
