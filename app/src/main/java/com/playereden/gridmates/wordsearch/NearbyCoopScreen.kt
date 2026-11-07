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
import androidx.compose.ui.platform.LocalDensity
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
    val state = runtime.state
    var exitPrompt by rememberSaveable { mutableStateOf(false) }
    val requestExit = {
        if (state.role == NearbyRole.NONE && !state.busy && state.recovery == null) onBack()
        else exitPrompt = true
    }
    BackHandler { requestExit() }
    var match by rememberSaveable { mutableStateOf(NearbyMatch.COOP.name) }
    var theme by rememberSaveable { mutableStateOf(WordTheme.WOODLAND.name) }
    var board by rememberSaveable { mutableStateOf(PuzzleBoardSize.BOOK_CLASSIC.name) }
    var challenge by rememberSaveable { mutableStateOf(PuzzleChallenge.CLASSIC.name) }
    var preferredBoardLook by rememberSaveable { mutableStateOf(CoopBoardLook.CLASSIC.id) }
    var showAppearanceOptions by rememberSaveable { mutableStateOf(false) }
    var showHostOptions by rememberSaveable { mutableStateOf(false) }
    var classicOnThisPhone by rememberSaveable { mutableStateOf(false) }
    var showJoin by rememberSaveable { mutableStateOf(false) }
    var manual by rememberSaveable { mutableStateOf(false) }
    var manualAddress by rememberSaveable { mutableStateOf("") }
    var manualPort by rememberSaveable { mutableStateOf("") }
    var pin by rememberSaveable { mutableStateOf("") }
    var selectedRoom by remember { mutableStateOf<NearbyListing?>(null) }
    var chatOpen by rememberSaveable { mutableStateOf(false) }
    var roomDetailsExpanded by rememberSaveable { mutableStateOf(false) }
    var chatDraft by rememberSaveable { mutableStateOf("") }
    var shareText by rememberSaveable { mutableStateOf<String?>(null) }
    val ownName = store.profile.identity.displayName.trim().ifBlank { "Player" }
    val snapshot = state.snapshot

    // Final, host-authoritative snapshots enter their own atomic journal.
    // A Duel's revision lives in the recipient-specific DuelView rather than
    // the co-op revision. Replays/rotation/rejoin never increment totals twice.
    LaunchedEffect(snapshot?.roomId, snapshot?.revision, snapshot?.duel?.revision,
        snapshot?.finished, state.role) {
        val final = snapshot ?: return@LaunchedEffect
        if (!final.finished || state.role == NearbyRole.NONE) return@LaunchedEffect
        val seat = if (state.role == NearbyRole.HOST) CoopSeat.HOST else CoopSeat.GUEST
        when (final.match) {
            NearbyMatch.SHARED_BOARD_RACE -> store.recordRace(final, seat)
            NearbyMatch.SPEED_DUEL -> store.recordDuel(final, seat)
            NearbyMatch.COOP -> Unit
        }
    }

    if (snapshot != null) {
        NearbyActiveMatchScreen(store, runtime, state,
            onBack = requestExit,
            onShare = { shareText = it })
    } else {
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = requestExit) { Text("← Back to Play") }
        PaperEyebrow("GridMates · Nearby Play")
        Text("Play Together", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = Forest)
        Text("Host a game or join a friend on the same Wi-Fi.",
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
                    Text("Host a nearby game",
                        color = Forest, style = MaterialTheme.typography.titleLarge)
                    Text("Game mode", fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NearbyMatch.entries.forEach { entry ->
                            FilterChip(selected = match == entry.name,
                                onClick = { match = entry.name },
                                label = { Text(when (entry) {
                                    NearbyMatch.COOP -> "Co-op"
                                    NearbyMatch.SHARED_BOARD_RACE -> "Race"
                                    NearbyMatch.SPEED_DUEL -> "Duel"
                                }) })
                        }
                    }

                    Text(when (match) {
                        NearbyMatch.SPEED_DUEL.name -> "Duel · Private puzzles, Ready / GO"
                        NearbyMatch.SHARED_BOARD_RACE.name -> "Race · First to claim each word scores"
                        else -> "Co-op · Find words together"
                    }, color = Muted, style = MaterialTheme.typography.bodySmall)
                    Button(onClick = {
                        runtime.host(ownName, WordTheme.valueOf(theme),
                            PuzzleBoardSize.valueOf(board), PuzzleChallenge.valueOf(challenge),
                            NearbyMatch.valueOf(match), preferredBoardLook)
                    }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                        Text(when (match) {
                            NearbyMatch.SPEED_DUEL.name -> "Host Speed Duel"
                            NearbyMatch.SHARED_BOARD_RACE.name -> "Host Shared Board Race"
                            else -> "Host Co-op game"
                        })
                    }
                    OutlinedButton(onClick = {
                        showJoin = true
                        runtime.search()
                    }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                        Text("Join nearby game")
                    }
                    TextButton(onClick = { showHostOptions = !showHostOptions },
                        modifier = Modifier.fillMaxWidth()) {
                        Text(if (showHostOptions) "Hide puzzle & room options"
                            else "Puzzle & room options · " +
                                PuzzleBoardSize.valueOf(board).cells + "×" +
                                PuzzleBoardSize.valueOf(board).cells)
                    }
                    if (showHostOptions) {
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
                    }

                }
            }
            if (showJoin) Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nearby games", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
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
                                        ownName, preferredBoardLook)
                                }
                            }, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.fillMaxWidth()) {
                                    Text(entry.name)
                                    Text(entry.match.label + " · " +
                                        if (entry.requiresCode) "Code needed" else "Tap to join",
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
                                runtime.join(manualAddress, port, "", pin, ownName,
                                    preferredBoardLook)
                            }, enabled = !state.busy && (manualPort.toIntOrNull() ?: 0) in 1..65535 &&
                                manualAddress.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()) { Text("Join with address") }
                        }

                    TextButton(onClick = { showJoin = false; manual = false }) {
                        Text("Close game finder")
                    }
                }
            }
            TextButton(onClick = { showAppearanceOptions = !showAppearanceOptions },
                modifier = Modifier.fillMaxWidth()) {
                Text(if (showAppearanceOptions) "Hide board appearance options"
                    else "Board appearance · " + CoopBoardLook.safe(preferredBoardLook).label +
                        " (optional)")
            }
            if (showAppearanceOptions) Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Co-op board appearance",
                        color = Forest, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium)
                    Text("Optional free looks. You and your partner each choose before joining; " +
                        "the shared preview uses a consistent display priority. No cosmetic " +
                        "is bought, earned or transferred.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CoopBoardLook.entries.forEach { look ->
                            FilterChip(
                                selected = preferredBoardLook == look.id,
                                onClick = { preferredBoardLook = look.id },
                                label = { Text(look.label) })
                        }
                    }
                    Text("Co-op only. Race and Speed Duel keep their normal board. " +
                        "The room look stays fixed after the first partner joins.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("Nearby rooms work on trusted local networks. Guest Wi-Fi may block devices " +
                "from seeing each other. No messages or puzzles are sent to an internet server.",
                style = MaterialTheme.typography.bodySmall, color = Muted)

        }
    }
    }

    if (exitPrompt) {
        AlertDialog(
            onDismissRequest = { exitPrompt = false },
            title = { Text(if (state.role == NearbyRole.HOST) "Close this shared game?"
                else "Leave Play Together?") },
            text = { Text(
                if (state.role == NearbyRole.HOST) {
                    "Your partner will be disconnected. This nearby game and its chat " +
                        "end when you close the room. Your Solo and Daily puzzles are safe."
                } else if (state.role == NearbyRole.GUEST) {
                    "Leaving will disconnect you from your partner. " +
                        "Your Solo and Daily puzzles are not affected."
                } else {
                    "Leave the nearby game setup? Your connection attempt will be closed."
                }
            ) },
            confirmButton = {
                TextButton(onClick = { exitPrompt = false; onBack() }) {
                    Text(if (state.role == NearbyRole.HOST) "Close room" else "Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { exitPrompt = false }) { Text("Keep playing") }
            }
        )
    }

    shareText?.let { summary ->
        ShareResultPreview(summary = summary, onDismiss = { shareText = null })
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
                        pin, ownName, preferredBoardLook)
                    selectedRoom = null
                }, enabled = pin.length == 6 && !state.busy) { Text("Join game") }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoom = null }) { Text("Cancel") }
            })
    }
}
