package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
internal fun GridMatesShell() {
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
                        icon = { Text(if (item == page) "●" else "○") },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        if (page == Page.PUZZLE) {
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                WordSearchScreen(onBack = { pageName = Page.PLAY.name })
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
                Page.PLAY -> HomeScreen(onHelp = { pageName = Page.HELP.name },
                    onSolo = { pageName = Page.PUZZLE.name })
                Page.PUZZLE -> Unit
                Page.RECORDS -> PlaceholderScreen(
                    "Records", "Your achievements and completed puzzles will appear here after Solo gameplay is built."
                )
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
private fun HomeScreen(onHelp: () -> Unit, onSolo: () -> Unit) {
    Heading("GridMates", "Word Search")
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
            Text("Find your first words", style = MaterialTheme.typography.titleMedium)
            Text(
                "Our first playable letter grid is ready to try. This prototype does not save completed puzzles yet.",
                color = Muted
            )
            Button(onClick = onSolo) { Text("Try Solo Word Search") }
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
