package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*

/**
 * Full-screen live game, not a vertically scrolling settings page. The board
 * gets a fixed, predictable viewport; players only open lists/chat as needed.
 * An optional bottom sheet can scroll, but never intercepts gameplay gestures.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NearbyActiveMatchScreen(
    store: SoloStore,
    runtime: NearbyCoopRuntime,
    state: NearbyUiState,
    onBack: () -> Unit,
    onShare: (String) -> Unit
) {
    val snapshot = state.snapshot ?: return
    val spec = snapshot.spec
    val puzzle = remember(spec.puzzleId, spec.boardFingerprint) { spec.regenerate() }
    val seat = if (state.role == NearbyRole.HOST) CoopSeat.HOST else CoopSeat.GUEST
    val duel = snapshot.duel
    val ownFound = duel?.ownFoundIds ?: snapshot.claims.filter { it.seat == seat }
        .map { it.wordId }.toSet()
    val partnerFound = if (duel != null) emptySet() else snapshot.claims
        .filter { it.seat != seat }.map { it.wordId }.toSet()
    val allFound = if (duel != null) ownFound else snapshot.foundIds
    val remaining = puzzle.targets.filter { it.id !in allFound }
    val connected = snapshot.guestConnected && snapshot.hostConnected && !state.disconnected
    val started = duel == null || duel.started
    val canPlay = connected && started && !snapshot.finished
    val partner = if (state.role == NearbyRole.HOST)
        state.guestName.ifBlank { "Friend" } else state.hostName.ifBlank { "Friend" }

    var wordSheet by remember(snapshot.roomId) { mutableStateOf(false) }
    var chatSheet by remember(snapshot.roomId) { mutableStateOf(false) }
    var roomSheet by remember(snapshot.roomId) { mutableStateOf(false) }
    var comparisonSheet by remember(snapshot.roomId) { mutableStateOf(false) }
    var classicLocal by remember(snapshot.roomId) { mutableStateOf(false) }
    var chatDraft by rememberSaveable(snapshot.roomId) { mutableStateOf("") }
    var touchFeedback by remember(snapshot.roomId) { mutableStateOf("") }

    LaunchedEffect(allFound.size, snapshot.roomId) {
        if (allFound.isNotEmpty()) {
            touchFeedback = if (snapshot.finished) "Puzzle complete!"
                else "Found word recorded. Keep searching!"
        }
    }

    // Very small fixed header: room metadata, chat and comparisons are sheets.
    Column(Modifier.fillMaxSize().imePadding()
        .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            TextButton(onClick = onBack) { Text("← Back") }
            Column(Modifier.weight(1f)) {
                Text(when (snapshot.match) {
                    NearbyMatch.COOP -> "Play Together"
                    NearbyMatch.SHARED_BOARD_RACE -> "Shared Race"
                    NearbyMatch.SPEED_DUEL -> "Speed Duel"
                }, color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (connected) "● $partner connected"
                    else "○ Waiting for $partner",
                    color = Muted, style = MaterialTheme.typography.labelSmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            TextButton(onClick = { roomSheet = true }) { Text("Info") }
        }
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(if (duel == null)
                    "${allFound.size} / ${puzzle.targets.size} found"
                    else "${ownFound.size} / ${puzzle.targets.size} found",
                    color = Forest, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text(if (snapshot.match == NearbyMatch.COOP)
                    "You ${ownFound.size} · $partner ${partnerFound.size}"
                    else "You ${ownFound.size} · $partner ${duel?.opponentCount ?: partnerFound.size}",
                    color = Muted, style = MaterialTheme.typography.bodySmall,
                    maxLines = 1)
            }
            if (duel != null && !duel.started) {
                Button(onClick = runtime::readySpeedDuel,
                    enabled = connected && !duel.ownReady) {
                    Text(if (duel.ownReady) "Waiting…" else "Ready")
                }
            } else if (snapshot.finished) {
                Text(when (snapshot.match) {
                    NearbyMatch.COOP -> "Solved together ✓"
                    NearbyMatch.SHARED_BOARD_RACE -> when (snapshot.raceStanding?.winner) {
                        seat -> "You win!"
                        null -> "Tie game"
                        else -> "$partner wins"
                    }
                    NearbyMatch.SPEED_DUEL ->
                        if (duel?.winner == seat) "You win!" else "$partner wins"
                }, color = Forest, fontWeight = FontWeight.Bold,
                    maxLines = 2, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(spec.board.cells.toString() + "×" + spec.board.cells,
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        PaperProgress(if (duel != null) ownFound.size else snapshot.foundIds.size,
            puzzle.targets.size)

        if (state.disconnected) {
            Button(onClick = {
                runtime.reconnect(store.profile.identity.displayName)
            }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                Text("Reconnect to game")
            }
        }
        store.warning?.let { warning ->
            Text("Save warning: " + warning, color = Gold,
                style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
        val claimMessage = if (state.role == NearbyRole.GUEST)
            state.claimReceipt?.description() else null
        val visibleFeedback = claimMessage ?: touchFeedback
        if (visibleFeedback.isNotBlank() && started) {
            Text(visibleFeedback, color = Forest,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2)
        }

        if (started) {
            BoxWithConstraints(Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center) {
                // Controls consume ~40dp above the square. Keep the entire
                // fit-to-screen board visible without scrolling the page.
                val side = minOf(maxWidth, (maxHeight - 52.dp).coerceAtLeast(140.dp))
                ZoomableWordGrid(puzzle, allFound, emptyList(), side,
                    onSelect = { from, to ->
                        if (!canPlay) {
                            touchFeedback = "Wait for your partner to connect."
                        } else {
                            val localClaim = WordSearchRules.claim(puzzle, allFound, from, to)
                            when (localClaim.status) {
                                ClaimStatus.INVALID -> touchFeedback =
                                    "That line isn't a listed word. Try its first and last letter."
                                ClaimStatus.ALREADY_FOUND -> touchFeedback =
                                    "That word was already found."
                                ClaimStatus.FOUND -> {
                                    touchFeedback = "Checking word with the host…"
                                    runtime.claim(from, to)
                                }
                            }
                        }
                    },
                    onStep = {},
                    boardFontId = store.effectiveBoardFontId,
                    partnerFoundIds = partnerFound,
                    boardLookId = if (snapshot.match == NearbyMatch.COOP &&
                        !classicLocal) snapshot.boardLookId else "classic")
            }
        } else {
            Box(Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center) {
                Text("Both players tap Ready to reveal the private boards.",
                    color = Forest)
            }
        }

        // One compact word preview replaces an entire long scrolling list.
        if (started) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Words · ${remaining.size} left", color = Forest,
                    fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { wordSheet = true }) {
                    Text("See all ${puzzle.targets.size}")
                }
            }
            val peek = remaining.take(4).map { it.word }
            Text(if (peek.isEmpty()) "All words found!"
                else peek.joinToString("  ·  "),
                color = Muted, style = MaterialTheme.typography.bodySmall,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { chatSheet = true },
                modifier = Modifier.weight(1f)) {
                Text("Chat (${state.chat.size})", maxLines = 1)
            }
            if (snapshot.match == NearbyMatch.SHARED_BOARD_RACE && snapshot.finished) {
                OutlinedButton(onClick = { comparisonSheet = true },
                    modifier = Modifier.weight(1f)) { Text("Compare") }
            }
            if (snapshot.finished) {
                val safelySaved = when (snapshot.match) {
                    NearbyMatch.COOP -> true
                    NearbyMatch.SHARED_BOARD_RACE ->
                        store.profile.raceResults.any { it.roomId == snapshot.roomId }
                    NearbyMatch.SPEED_DUEL ->
                        store.profile.duelResults.any { it.roomId == snapshot.roomId }
                }
                OutlinedButton(onClick = {
                    runCatching { NearbyMatchShare.text(snapshot, seat) }
                        .getOrNull()?.let(onShare)
                }, enabled = safelySaved, modifier = Modifier.weight(1f)) {
                    Text("Share")
                }
            }
        }
    }

    if (wordSheet) ModalBottomSheet(onDismissRequest = { wordSheet = false }) {
        Text("Words to find", Modifier.padding(horizontal = 20.dp),
            color = Forest, style = MaterialTheme.typography.titleLarge)
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 480.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp)) {
            items(puzzle.targets) { target ->
                val label = when {
                    target.id in ownFound -> "✓ ${target.word} · you"
                    target.id in partnerFound -> "✓ ${target.word} · $partner"
                    else -> "○ ${target.word}"
                }
                Text(label, Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = if (target.id in allFound) Muted else Forest,
                    style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    if (chatSheet) ModalBottomSheet(onDismissRequest = { chatSheet = false }) {
        Text("Partner chat", Modifier.padding(horizontal = 20.dp),
            color = Forest, style = MaterialTheme.typography.titleLarge)
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 300.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)) {
            items(state.chat) { line ->
                Text((if (line.seat == seat) "You" else partner) + ": " + line.text,
                    Modifier.padding(vertical = 6.dp), color = Forest)
            }
        }
        state.pendingChat?.let {
            Text(if (it.needsRetry)
                "Message unconfirmed — retry below."
                else "Sending… waiting for confirmation.",
                Modifier.padding(horizontal = 20.dp), color = Muted)
            if (it.needsRetry) TextButton(onClick = { runtime.retryChat() }) {
                Text("Retry message")
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(chatDraft,
                { chatDraft = it.take(NearbyChatPolicy.MAX_CHARS) },
                modifier = Modifier.weight(1f),
                label = { Text("Message") }, maxLines = 2, enabled = connected)
            Button(onClick = {
                val message = NearbyChatPolicy.clean(chatDraft)
                if (message.isNotEmpty() && runtime.sendChat(message)) chatDraft = ""
            }, enabled = connected && state.pendingChat == null &&
                NearbyChatPolicy.clean(chatDraft).isNotBlank()) {
                Text("Send")
            }
        }
    }
    if (roomSheet) AlertDialog(onDismissRequest = { roomSheet = false },
        title = { Text("Room details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(snapshot.match.label + " · " + spec.theme.label + " · " +
                    spec.board.cells + "×" + spec.board.cells + " · " +
                    spec.challenge.label)
                Text(if (connected) "Connected to $partner" else "Partner not connected")
                if (state.role == NearbyRole.HOST) {
                    Text(if (state.requiresCode) "Room code: " + state.code
                        else "Open nearby room")
                    Text("Manual join: " + state.address + ":" + state.port)
                }
                if (snapshot.match == NearbyMatch.COOP &&
                    snapshot.boardLookId != CoopBoardLook.CLASSIC.id) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Classic look on my phone", Modifier.weight(1f))
                        Switch(classicLocal, { classicLocal = it })
                    }
                }
                Text("One finger traces words. Two fingers pinch or move the board. " +
                    "Same Wi-Fi; local room chat is not encrypted.",
                    style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        },
        confirmButton = { TextButton(onClick = { roomSheet = false }) {
            Text("Done")
        } }
    )
    if (comparisonSheet) ModalBottomSheet(
        onDismissRequest = { comparisonSheet = false }) {
        Column(Modifier.fillMaxWidth().heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {
            RacePostMatchCard(snapshot, seat, puzzle,
                saved = store.profile.raceResults.any { it.roomId == snapshot.roomId },
                saveFailed = store.warning != null)
        }
    }
}
