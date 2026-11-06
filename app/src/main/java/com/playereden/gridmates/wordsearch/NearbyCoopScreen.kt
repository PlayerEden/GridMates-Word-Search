package com.playereden.gridmates.wordsearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.*

/**
 * No account, server, or QR step: host on a trusted Wi-Fi, partner selects
 * the nearby room. Manual address is a visible fallback only when needed.
 * We keep this in an isolated screen so Solo/Daily are unchanged.
 */
@Composable
internal fun NearbyCoopScreen(store: SoloStore, rooms: NearbyRoomViewModel, onBack: () -> Unit) {
    val ctx = LocalContext.current.applicationContext
    val runtime = remember(rooms) { rooms.runtime(ctx) }
    BackHandler { onBack() }
    val state = runtime.state
    var theme by rememberSaveable { mutableStateOf(WordTheme.WOODLAND.name) }
    var board by rememberSaveable { mutableStateOf(PuzzleBoardSize.BOOK_CLASSIC.name) }
    var challenge by rememberSaveable { mutableStateOf(PuzzleChallenge.CLASSIC.name) }
    var showJoin by rememberSaveable { mutableStateOf(false) }
    var manual by rememberSaveable { mutableStateOf(false) }
    var manualAddress by rememberSaveable { mutableStateOf("") }
    var manualPort by rememberSaveable { mutableStateOf("") }
    var pin by rememberSaveable { mutableStateOf("") }
    var selectedRoom by remember { mutableStateOf<NearbyListing?>(null) }
    var chatOpen by rememberSaveable { mutableStateOf(false) }
    var chatDraft by rememberSaveable { mutableStateOf("") }
    val ownName = store.profile.identity.displayName.trim().ifBlank { "Player" }
    val snapshot = state.snapshot

    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("← Back to Play") }
        Text("Play Together", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = Forest)
        Text("One word search, two phones. Same Wi-Fi, no sign-in.",
            color = Muted, style = MaterialTheme.typography.bodyMedium)

        if (state.message.isNotBlank()) {
            Card(Modifier.fillMaxWidth()) {
                Text(state.message, Modifier.padding(12.dp),
                    color = Forest, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())

        if (snapshot == null && state.role == NearbyRole.NONE) {
            state.recovery?.let {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Resume interrupted join",
                            color = Forest, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Text("Your phone saved its room credential. Retry the same " +
                            "host without starting over.",
                            style = MaterialTheme.typography.bodySmall, color = Muted)
                        Button(onClick = { runtime.retryInterruptedJoin(ownName) },
                            enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                            Text("Retry joining this game")
                        }
                    }
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Start a shared puzzle",
                        color = Forest, style = MaterialTheme.typography.titleLarge)
                    Text("Pick a puzzle, then your partner taps it under Nearby games.",
                        color = Muted)
                    Text("Theme", fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        WordTheme.entries.forEach { entry ->
                            FilterChip(selected = theme == entry.name,
                                onClick = { theme = entry.name },
                                label = { Text(entry.label, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Text("Board", fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        PuzzleBoardSize.bookPresets.forEach { entry ->
                            FilterChip(selected = board == entry.name,
                                onClick = { board = entry.name },
                                label = { Text(entry.cells.toString() + "×" + entry.cells,
                                    style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Text("Challenge", fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        PuzzleChallenge.entries.forEach { entry ->
                            FilterChip(selected = challenge == entry.name,
                                onClick = { challenge = entry.name },
                                label = { Text(entry.label,
                                    style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("Require a room code", fontWeight = FontWeight.Medium)
                            Text("Optional. Off by default; your choice is remembered.",
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = runtime.requireCodePreference,
                            onCheckedChange = runtime::updateRequireCodePreference)
                    }
                    Button(onClick = {
                        runtime.host(ownName, WordTheme.valueOf(theme),
                            PuzzleBoardSize.valueOf(board), PuzzleChallenge.valueOf(challenge))
                    }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                        Text("Host a game")
                    }
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Join someone", color = Forest,
                        style = MaterialTheme.typography.titleLarge)
                    Text("Ask your partner to host first.",
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {
                        showJoin = true
                        runtime.search()
                    }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                        Text(if (showJoin) "Find nearby games" else "Join nearby game")
                    }
                    if (showJoin) {
                        Row(Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (state.scanning) "Looking on your Wi-Fi…" else "Nearby rooms",
                                color = Muted, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { runtime.refreshSearch() },
                                enabled = !state.busy) { Text("Refresh") }
                        }
                        if (state.listings.isEmpty()) {
                            Text("No rooms found yet. Check that both phones use the same Wi-Fi.",
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        state.listings.forEach { entry ->
                            OutlinedButton(onClick = {
                                selectedRoom = entry
                                pin = ""
                                if (!entry.requiresCode) {
                                    runtime.join(entry.address, entry.port, entry.roomId, "",
                                        ownName)
                                }
                            }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.fillMaxWidth()) {
                                    Text(entry.name)
                                    Text(if (entry.requiresCode) "Code needed" else "Tap to join",
                                        style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        TextButton(onClick = { manual = !manual }) {
                            Text(if (manual) "Hide manual entry" else "Join manually instead")
                        }
                        if (manual) {
                            OutlinedTextField(manualAddress, { manualAddress = it.trim().take(80) },
                                label = { Text("Host Wi-Fi address") },
                                placeholder = { Text("192.168.1.24") },
                                singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(manualPort, { manualPort = it.filter(Char::isDigit).take(5) },
                                label = { Text("Port from host's screen") },
                                singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(pin, { pin = it.filter(Char::isDigit).take(6) },
                                label = { Text("Room code (only if required)") },
                                singleLine = true, modifier = Modifier.fillMaxWidth())
                            Button(onClick = {
                                val port = manualPort.toIntOrNull() ?: 0
                                runtime.join(manualAddress, port, "", pin, ownName)
                            }, enabled = !state.busy && (manualPort.toIntOrNull() ?: 0) in 1..65535 &&
                                manualAddress.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()) { Text("Join with address") }
                        }
                    }
                }
            }
            Text("Nearby rooms work on trusted local networks. Guest Wi-Fi may block devices " +
                "from seeing each other. No messages or puzzles are sent to an internet server.",
                style = MaterialTheme.typography.bodySmall, color = Muted)
        } else if (snapshot != null) {
            val spec = snapshot.spec
            val puzzle = remember(spec.puzzleId, spec.boardFingerprint) { spec.regenerate() }
            val localSeat = if (state.role == NearbyRole.HOST) CoopSeat.HOST else CoopSeat.GUEST
            val ownFound = snapshot.claims.filter { it.seat == localSeat }
                .map { it.wordId }.toSet()
            val partnerFound = snapshot.claims.filter { it.seat != localSeat }
                .map { it.wordId }.toSet()
            val connected = snapshot.guestConnected && snapshot.hostConnected && !state.disconnected
            val lastPartnerWord = snapshot.claims.lastOrNull { it.seat != localSeat }
                ?.wordId?.let { id -> puzzle.targets.firstOrNull { it.id == id }?.word }
            val boardDp = (LocalConfiguration.current.screenWidthDp - 36)
                .coerceIn(180, 440).dp

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (snapshot.finished) "Solved together!" else
                        if (connected) "Finding words together" else "Waiting for your partner",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = Forest)
                    Text(spec.theme.label + " · " + spec.board.cells + "×" + spec.board.cells +
                        " · " + spec.challenge.label, color = Muted)
                    Text(snapshot.foundIds.size.toString() + " / " + puzzle.targets.size +
                        " found  ·  You: " + ownFound.size +
                        "  ·  Partner: " + partnerFound.size, color = Forest,
                        fontWeight = FontWeight.SemiBold)
                    Text(if (connected) "● Partner connected" else "○ Partner offline / not joined",
                        color = if (connected) Forest else Muted)
                    if (lastPartnerWord != null) {
                        Text("Partner most recently found: " + lastPartnerWord,
                            color = Forest, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (state.role == NearbyRole.HOST) {
                        Text("Host: " + state.address + ":" + state.port,
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                        Text(if (state.requiresCode) "Join code: " + state.code
                            else "Open room · no code needed",
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    if (state.disconnected) {
                        Button(onClick = { runtime.reconnect(ownName) },
                            enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                            Text("Reconnect to game")
                        }
                    }
                    Text("Green = your finds · Blue = partner's finds. " +
                        "First valid selection counts for the shared board.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Partner chat", color = Forest,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = { chatOpen = !chatOpen }) {
                            Text(if (chatOpen) "Hide chat" else
                                "Open chat (" + state.chat.size + ")")
                        }
                    }
                    val recent = state.chat.lastOrNull()
                    if (!chatOpen && recent != null) {
                        val sender = if (recent.seat == localSeat) "You" else "Partner"
                        Text(sender + ": " + recent.text,
                            color = Muted, style = MaterialTheme.typography.bodySmall,
                            maxLines = 2)
                    }
                    if (chatOpen) {
                        if (state.chat.isEmpty()) {
                            Text("No messages yet. Say hello to your partner.",
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        state.chat.takeLast(5).forEach { line ->
                            val sender = if (line.seat == localSeat) "You" else
                                if (state.role == NearbyRole.HOST)
                                    state.guestName.ifBlank { "Partner" }
                                else state.hostName.ifBlank { "Partner" }
                            Text(sender + ": " + line.text,
                                color = if (line.seat == localSeat) Forest else Muted,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        state.pendingChat?.let { pending ->
                            Text("You: " + pending.text,
                                color = Forest, style = MaterialTheme.typography.bodyMedium)
                            Text(if (pending.needsRetry)
                                "Not confirmed — your partner may already have this. Retry safely."
                            else "Sending · awaiting host confirmation…",
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                            if (pending.needsRetry) {
                                OutlinedButton(onClick = { runtime.retryChat() },
                                    enabled = connected && !state.busy) {
                                    Text("Retry message")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = chatDraft,
                                onValueChange = { chatDraft = it.take(NearbyChatPolicy.MAX_CHARS) },
                                label = { Text("Message") },
                                placeholder = { Text("Say hello…") },
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                enabled = connected
                            )
                            Button(
                                onClick = {
                                    val text = NearbyChatPolicy.clean(chatDraft)
                                    if (text.isNotBlank()) {
                                        if (runtime.sendChat(text)) chatDraft = ""
                                    }
                                },
                                enabled = connected && state.pendingChat == null &&
                                    NearbyChatPolicy.clean(chatDraft).isNotBlank()
                            ) { Text("Send") }
                        }
                        Text("Only the last 24 room messages are kept while the host remains open. " +
                            "Chat is shared on your Wi-Fi, not encrypted or stored online.",
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ZoomableWordGrid(puzzle, snapshot.foundIds, emptyList(), boardDp,
                    onSelect = { from, to ->
                        if (connected && !snapshot.finished) runtime.claim(from, to)
                    }, onStep = {}, boardFontId = store.effectiveBoardFontId,
                    partnerFoundIds = partnerFound)
            }
            Text("Words to find", color = Forest,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            puzzle.targets.chunked(3).forEach { items ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { target ->
                        val label = when {
                            target.id in ownFound -> "✓ " + target.word + " (you)"
                            target.id in partnerFound -> "✓ " + target.word + " (partner)"
                            else -> target.word
                        }
                        Text(label, Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (target.id in snapshot.foundIds) Forest else Muted)
                    }
                    repeat(3 - items.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Text(if (snapshot.finished)
                "Every word has been found. The room stays here until you leave."
            else "Your Solo and Daily puzzles remain separate. A lost Wi-Fi connection " +
                "doesn't erase the current host's shared progress.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }

    val protected = selectedRoom
    if (protected != null && protected.requiresCode && state.role == NearbyRole.NONE) {
        AlertDialog(onDismissRequest = { selectedRoom = null },
            title = { Text("Enter room code") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(protected.name)
                    OutlinedTextField(pin, { pin = it.filter(Char::isDigit).take(6) },
                        label = { Text("Six-digit code") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    runtime.join(protected.address, protected.port, protected.roomId,
                        pin, ownName)
                    selectedRoom = null
                }, enabled = pin.length == 6 && !state.busy) { Text("Join game") }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoom = null }) { Text("Cancel") }
            })
    }
}
