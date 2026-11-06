package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextButton
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
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchRules

private enum class Page(val label: String) {
    PLAY("Play"), RECORDS("Records"), COLLECTION("Collection"),
    SETTINGS("Settings"), HELP("Help"), NEARBY("Nearby"), PUZZLE("Puzzle"), DAILY("Daily"), DAILY_PUZZLE("Daily Puzzle")
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
internal fun GridMatesShell(soloStore: SoloStore, nearbyRooms: NearbyRoomViewModel) {
    var pageName by rememberSaveable { mutableStateOf(Page.PLAY.name) }
    val page = Page.entries.firstOrNull { it.name == pageName } ?: Page.PLAY

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            if (page != Page.PUZZLE && page != Page.DAILY_PUZZLE && page != Page.NEARBY) NavigationBar(containerColor = Paper) {
                Page.entries.filter {
                    it == Page.PLAY || it == Page.RECORDS ||
                        it == Page.COLLECTION || it == Page.SETTINGS
                }.forEach { item ->
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
        if (page == Page.NEARBY) {
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                NearbyCoopScreen(soloStore, nearbyRooms, onBack = {
                    nearbyRooms.leaveRoom()
                    pageName = Page.PLAY.name
                })
            }
        } else if (page == Page.PUZZLE || page == Page.DAILY_PUZZLE) {
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                WordSearchScreen(
                    soloStore,
                    daily = page == Page.DAILY_PUZZLE,
                    onBack = {
                        pageName = if (page == Page.DAILY_PUZZLE)
                            Page.DAILY.name else Page.PLAY.name
                    },
                    onRecords = { pageName = Page.RECORDS.name }
                )
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
                    onSolo = { pageName = Page.PUZZLE.name },
                    onDaily = { pageName = Page.DAILY.name },
                    onNearby = { pageName = Page.NEARBY.name })
                Page.PUZZLE, Page.DAILY_PUZZLE, Page.NEARBY -> Unit
                Page.DAILY -> DailyLandingScreen(soloStore,
                    onPlay = { date ->
                        soloStore.openDaily(date)
                        pageName = Page.DAILY_PUZZLE.name
                    },
                    onBack = { pageName = Page.PLAY.name })
                Page.RECORDS -> SoloRecordsScreen(soloStore.profile)
                Page.COLLECTION -> CollectionScreen(soloStore)
                Page.SETTINGS -> SoloSettingsScreen(soloStore)
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
private fun HomeScreen(store: SoloStore, onHelp: () -> Unit, onSolo: () -> Unit,
    onDaily: () -> Unit, onNearby: () -> Unit) {
    val run = store.profile.active
    val puzzle = androidx.compose.runtime.remember(run.puzzleId) {
        SoloProgressRules.puzzle(run)
    }
    val finished = WordSearchRules.complete(puzzle, run.foundIds)
    val challengeLabel = run.challenge?.label ?: run.difficulty.label
    val boardLabel = run.boardSize?.cells ?: run.difficulty.gridSize

    Heading("GridMates", "Word Search")
    Text("Good puzzles. Good company.", color = Muted,
        style = MaterialTheme.typography.bodyLarge)
    store.warning?.let { msg ->
        Card(Modifier.fillMaxWidth()) { Text(msg, Modifier.padding(14.dp), color = Gold) }
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (finished) "Another little adventure?" else "Pick up where you left off",
                color = Forest, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
            Text(run.theme.label + " · " + boardLabel + "×" + boardLabel +
                " · " + challengeLabel, color = Muted)
            Text(run.foundIds.size.toString() + " of " + puzzle.targets.size +
                " words found · Autosaved", color = Muted,
                style = MaterialTheme.typography.bodySmall)
            Button(onClick = onSolo, modifier = Modifier.fillMaxWidth()) {
                Text(if (finished) "Play Solo" else "Continue Solo")
            }
            TextButton(onClick = onHelp) { Text("How to play") }
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Daily puzzle", style = MaterialTheme.typography.titleLarge,
                color = Forest, fontWeight = FontWeight.Bold)
            Text("A fresh word search each day. No streak pressure.",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onDaily, modifier = Modifier.fillMaxWidth()) {
                Text("Open Daily")
            }
        }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Play Together — Nearby Co-op", color = Forest,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Text("Try sharing one word search on two phones over the same Wi-Fi. Versus modes are still planned.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onNearby, modifier = Modifier.fillMaxWidth()) {
                Text("Play Together (QA preview)")
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
        "For nearby co-op, both phones use the same Wi-Fi. One hosts, and the other joins to find words together.",
        color = Muted
    )
    Button(onClick = onBack) {
        Text("Back to Play")
    }
}
