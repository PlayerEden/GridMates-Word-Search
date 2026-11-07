package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.CoopSeat
import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import com.playereden.gridmates.wordsearch.core.NearbyMatch

/**
 * More visible table-mate presence without extra packets, timers or private
 * progress leakage. Every number comes from the existing verified snapshot.
 */
@Composable
internal fun NearbyPlayerPresenceCard(
    state: NearbyUiState, snapshot: CoopSnapshot, localSeat: CoopSeat, total: Int
) {
    val connected = snapshot.guestConnected && snapshot.hostConnected && !state.disconnected
    val remote = if (state.role == NearbyRole.HOST) state.guestName else state.hostName
    val friendName = remote.replace(Regex("\\s+"), " ").trim().take(24)
        .ifBlank { "Friend" }
    val duel = snapshot.duel
    val me = duel?.ownCount ?: snapshot.claims.count { it.seat == localSeat }
    val friend = duel?.opponentCount ?: snapshot.claims.count { it.seat != localSeat }
    val small = LocalConfiguration.current.screenWidthDp < 360 ||
        LocalDensity.current.fontScale >= 1.3f
    val activity = when {
        snapshot.finished && snapshot.match == NearbyMatch.COOP ->
            "You solved this board together."
        snapshot.finished -> "Match complete. Compare your results below."
        !connected && state.role == NearbyRole.GUEST ->
            "Connection paused. Reconnect to continue this game."
        !connected -> "The room is open. Invite someone to join on Wi-Fi."
        duel != null && !duel.started ->
            if (duel.opponentReady) "Your friend is ready."
            else "Your friend is getting ready."
        duel != null -> "You each have a private board. Only totals are shared."
        snapshot.claims.isEmpty() -> "Your puzzle is ready. Find the first word together!"
        else -> "You're both making progress on this puzzle."
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PaperEyebrow("Playing with someone nearby")
            Text(if (connected) "Connected · $friendName is here"
                 else "$friendName · not connected",
                color = if (connected) Forest else Muted,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium)
            if (small) {
                PresencePerson("YOU", "$me / $total", Modifier.fillMaxWidth())
                PresencePerson("FRIEND · $friendName", "$friend / $total", Modifier.fillMaxWidth())
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PresencePerson("YOU", "$me / $total", Modifier.weight(1f))
                    PresencePerson("FRIEND · $friendName", "$friend / $total", Modifier.weight(1f))
                }
            }
            Text(activity, color = Muted, style = MaterialTheme.typography.bodySmall)
            if (snapshot.match == NearbyMatch.COOP) {
                Text("${snapshot.foundIds.size} of $total shared words found",
                    color = Forest, style = MaterialTheme.typography.bodySmall)
            } else if (snapshot.match == NearbyMatch.SPEED_DUEL) {
                Text("Your friend cannot see which specific words you've found.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PresencePerson(label: String, amount: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = Muted, style = MaterialTheme.typography.labelMedium)
        Text(amount, color = Forest, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
    }
}
