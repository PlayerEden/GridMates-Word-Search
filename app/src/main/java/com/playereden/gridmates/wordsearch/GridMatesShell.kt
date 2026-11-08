package com.playereden.gridmates.wordsearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
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
    PLAY("Play"), BOOKS_QA("Books QA"), RECORDS("Records"), COLLECTION("Collection"),
    SETTINGS("Settings"), HELP("Help"), NEARBY("Nearby"), PUZZLE("Puzzle"), DAILY("Daily"), DAILY_PUZZLE("Daily Puzzle"),
    MY_PUZZLES("My Puzzles"), SOLO_SETUP("New Puzzle")
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
internal fun GridMatesShell(
    soloStore: SoloStore, nearbyRooms: NearbyRoomViewModel,
    qaBookshelf: QaBookshelfRuntime? = null
) {
    val context = LocalContext.current
    val defaultQaRuntime = remember(context) { QaBookshelfRuntime(context) }
    val qa = qaBookshelf ?: defaultQaRuntime
    var pageName by rememberSaveable { mutableStateOf(Page.PLAY.name) }
    var soloSetupReturnPage by rememberSaveable { mutableStateOf(Page.PLAY.name) }
    fun openSoloSetup(origin: Page) {
        if (origin == Page.PUZZLE) soloStore.flush()
        soloSetupReturnPage = origin.name
        pageName = Page.SOLO_SETUP.name
    }
    val page = Page.entries.firstOrNull { it.name == pageName } ?: Page.PLAY
    val compactNavigation = LocalConfiguration.current.screenWidthDp < 360 ||
        LocalDensity.current.fontScale >= 1.5f
    // A new page must start from its top, not inherit the previous tab's
    // scroll offset and appear mysteriously halfway down.
    val pageScroll = remember(page) { ScrollState(0) }

    // Every nested page should honor Android's system Back gesture rather
    // than unexpectedly exiting the application. Nearby owns its own
    // confirmation dialog and remains handled by NearbyCoopScreen.
    BackHandler(enabled = page != Page.PLAY && page != Page.NEARBY) {
        if (page == Page.PUZZLE || page == Page.DAILY_PUZZLE) soloStore.flush()
        pageName = when (page) {
            Page.DAILY_PUZZLE -> Page.DAILY.name
            Page.SOLO_SETUP -> soloSetupReturnPage
            else -> Page.PLAY.name
        }
    }

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            if (page != Page.PUZZLE && page != Page.DAILY_PUZZLE &&
                page != Page.NEARBY && page != Page.SOLO_SETUP &&
                page != Page.BOOKS_QA) NavigationBar(containerColor = Paper) {
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
                        label = {
                            val shortLabel = when (item) {
                                Page.RECORDS -> "Stats"
                                Page.COLLECTION -> "Items"
                                Page.SETTINGS -> "Setup"
                                else -> "Play"
                            }
                            Text(if (compactNavigation) shortLabel else item.label,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
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
        } else if (page == Page.BOOKS_QA && qa.enabled &&
            qa.progressStore != null && qa.sessionStore != null) {
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                BookLibraryFlow(
                    catalog = qa.catalog,
                    progressStore = qa.progressStore,
                    sessionStore = qa.sessionStore,
                    boardFontId = soloStore.effectiveBoardFontId,
                    onExit = { pageName = Page.PLAY.name }
                )
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
                    onRecords = { pageName = Page.RECORDS.name },
                    onNewPuzzle = if (page == Page.DAILY_PUZZLE) null
                        else { { openSoloSetup(Page.PUZZLE) } }
                )
            }
        } else Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(pageScroll)
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (page) {
                Page.PLAY -> HomeScreen(soloStore,
                    onHelp = { pageName = Page.HELP.name },
                    onSolo = { pageName = Page.PUZZLE.name },
                    onMyPuzzles = { pageName = Page.MY_PUZZLES.name },
                    onNewSolo = { openSoloSetup(Page.PLAY) },
                    onDaily = { pageName = Page.DAILY.name },
                    onNearby = { pageName = Page.NEARBY.name },
                    onQaBooks = if (qa.enabled) {
                        { pageName = Page.BOOKS_QA.name }
                    } else null)
                Page.MY_PUZZLES -> SavedSoloScreen(soloStore,
                    onBack = { pageName = Page.PLAY.name },
                    onNewSolo = { openSoloSetup(Page.MY_PUZZLES) },
                    onResume = { id ->
                        if (soloStore.profile.active.puzzleId == id ||
                            soloStore.resumeSolo(id)) pageName = Page.PUZZLE.name
                    })
                Page.SOLO_SETUP -> SoloSetupScreen(soloStore,
                    onBack = { pageName = soloSetupReturnPage },
                    onStarted = { pageName = Page.PUZZLE.name })
                Page.PUZZLE, Page.DAILY_PUZZLE, Page.NEARBY, Page.BOOKS_QA -> Unit
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
    onMyPuzzles: () -> Unit, onNewSolo: () -> Unit,
    onDaily: () -> Unit, onNearby: () -> Unit,
    onQaBooks: (() -> Unit)? = null) {
    val run = store.profile.active
    val puzzle = remember(run.puzzleId) { SoloProgressRules.puzzle(run) }
    val finished = WordSearchRules.complete(puzzle, run.foundIds)
    val challengeLabel = run.challenge?.label ?: run.difficulty.label
    val boardSize = run.boardSize?.cells ?: run.difficulty.gridSize
    val name = store.profile.identity.displayName.trim().ifBlank { "Player" }

    // The Home page should be an immediately usable launcher, not a long
    // stack of oversized explanation cards. Keep the paperback colors and
    // hierarchy while making Solo, Daily, and Nearby visible at first glance.
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        PaperEyebrow("GridMates · Puzzle Library")
        Text("Word Search", color = Forest,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold)
        Text("Good puzzles. Good company.", color = Muted,
            style = MaterialTheme.typography.bodyMedium)
        Text("Welcome, " + name, color = Ink,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    store.warning?.let { message ->
        PaperPanel(Modifier.fillMaxWidth()) {
            Text(message, color = Gold, style = MaterialTheme.typography.bodyMedium)
        }
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            PaperEyebrow("01 · Solo")
            if (!finished) TextButton(onClick = onNewSolo) {
                Text("New puzzle")
            }
        }
        Text(if (finished) "Ready for another?" else "Pick up where you left off",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = Forest, maxLines = 2)
        Text(run.theme.label + " · " + boardSize + "×" + boardSize +
            " · " + challengeLabel +
            (if (finished) " · Complete" else
                " · " + run.foundIds.size + "/" + puzzle.targets.size + " found"),
            color = Muted, style = MaterialTheme.typography.bodySmall,
            maxLines = 2)
        PaperProgress(run.foundIds.size, puzzle.targets.size)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = if (finished) onNewSolo else onSolo,
                modifier = Modifier.weight(1f).height(48.dp)) {
                Text(if (finished) "New Solo" else "Continue Solo", maxLines = 1)
            }
            OutlinedButton(onClick = onMyPuzzles, modifier = Modifier.height(48.dp)) {
                Text("My Puzzles", maxLines = 1)
            }
        }
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                PaperEyebrow("02 · A little every day")
                Text("Daily puzzle", style = MaterialTheme.typography.titleSmall,
                    color = Forest, fontWeight = FontWeight.SemiBold, maxLines = 2)
            }
            Button(onClick = onDaily) { Text("Open Daily", maxLines = 1) }
        }
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                PaperEyebrow("03 · Together is better")
                Text("Same Wi-Fi", style = MaterialTheme.typography.titleSmall,
                    color = Forest, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
            Button(onClick = onNearby, modifier = Modifier.semantics {
                contentDescription = "Open Nearby co-op"
            }) {
                Text("Play Together", maxLines = 2)
            }
        }
    }

    if (onQaBooks != null) {
        PaperPanel(Modifier.fillMaxWidth()) {
            PaperEyebrow("04 · DEVELOPMENT PREVIEW · NOT FINAL CONTENT")
            Text("Puzzle Books — TEST ONLY", color = Forest,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text("Try Woodland, Space and Garden test books. " +
                "Your ordinary Solo and Daily games are separate.",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onQaBooks,
                modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Test Puzzle Books")
            }
        }
    }

    TextButton(onClick = onHelp, modifier = Modifier.fillMaxWidth()) {
        Text("How to play")
    }
}

/**
 * WS-09B first usable paused-game library. The current session remains in the
 * historic 'active' field; other sessions live in schema-5's pausedSolo list.
 * Nothing is removed except through the explicit confirmation below.
 */
@Composable
private fun SavedSoloScreen(
    store: SoloStore,
    onBack: () -> Unit,
    onNewSolo: () -> Unit,
    onResume: (String) -> Unit
) {
    val active = store.profile.active
    val paused = store.profile.pausedSolo
    var removeId by rememberSaveable { mutableStateOf<String?>(null) }
    val removeRun = paused.firstOrNull { it.puzzleId == removeId }
    if (removeRun != null) {
        AlertDialog(
            onDismissRequest = { removeId = null },
            title = { Text("Remove saved puzzle?") },
            text = { Text("This removes only this unfinished Solo puzzle. " +
                "Other games, Daily puzzles and completed records stay saved. " +
                "This cannot be undone unless you have a backup.") },
            confirmButton = {
                TextButton(onClick = {
                    if (store.discardPausedSolo(removeRun.puzzleId)) removeId = null
                }) { Text("Remove puzzle") }
            },
            dismissButton = {
                TextButton(onClick = { removeId = null }) { Text("Keep puzzle") }
            }
        )
    }
    PaperEyebrow("Solo · Puzzle Library")
    Text("My Puzzles", style = MaterialTheme.typography.headlineSmall,
        color = Forest, fontWeight = FontWeight.Bold)
    Text("Switch games anytime. An unfinished puzzle keeps its found words, " +
        "hints and time when you leave it.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)
    store.warning?.let {
        Text(it, color = Gold, style = MaterialTheme.typography.bodySmall)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) { Text("Back to Play") }
        Spacer(Modifier.weight(1f))
        Button(onClick = onNewSolo) { Text("New Solo") }
    }

    @Composable
    fun PuzzleSummary(run: com.playereden.gridmates.wordsearch.core.SoloRun) {
        val puzzle = remember(run.puzzleId) { SoloProgressRules.puzzle(run) }
        val dimension = run.boardSize?.cells ?: run.difficulty.gridSize
        val challenge = run.challenge?.label ?: run.difficulty.label
        Text("${run.theme.label} · ${dimension}×${dimension} · $challenge",
            color = Forest, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold)
        Text("${run.foundIds.size} of ${puzzle.targets.size} found · ${run.elapsedSeconds / 60}m",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        PaperProgress(run.foundIds.size, puzzle.targets.size)
    }

    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("Currently playing")
        PuzzleSummary(active)
        Button(onClick = { onResume(active.puzzleId) },
            modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text(if (com.playereden.gridmates.wordsearch.core.SoloSessionLibrary.finished(active))
                "Review current puzzle" else "Continue current puzzle")
        }
    }

    PaperEyebrow("Paused · ${paused.size} / ${com.playereden.gridmates.wordsearch.core.SoloSessionLibrary.MAX_PAUSED}")
    if (paused.isEmpty()) {
        Text("No paused games yet. Starting another Solo puzzle saves your current " +
            "unfinished board here.", color = Muted,
            style = MaterialTheme.typography.bodyMedium)
    }
    paused.forEach { run ->
        PaperPanel(Modifier.fillMaxWidth()) {
            PuzzleSummary(run)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onResume(run.puzzleId) },
                    modifier = Modifier.weight(1f).height(48.dp)) {
                    Text("Resume")
                }
                TextButton(onClick = { removeId = run.puzzleId }) {
                    Text("Remove")
                }
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
