package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.AchievementProgress
import com.playereden.gridmates.wordsearch.core.PortraitProgress
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements

@Composable
internal fun CollectionScreen(store: SoloStore) {
    val profile = store.profile
    val portraits = remember(profile) { WoodlandCollection.progress(profile) }
    val achievements = remember(profile) { WordSearchAchievements.progress(profile) }
    val equippedName = portraits.firstOrNull { it.equipped }?.portrait?.label ?: "Owl"
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var draftName by remember(profile.identity.displayName) {
        mutableStateOf(profile.identity.displayName)
    }
    var showAchievements by rememberSaveable { mutableStateOf(false) }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Your local profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This name stays inside Word Search on this device. " +
                        "It is not shared with Sudoku or other players.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it.take(48) },
                        label = { Text("Display name") },
                        singleLine = true,
                        supportingText = { Text("Up to 24 characters") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    store.updateDisplayName(draftName)
                    showNameDialog = false
                }) { Text("Save name") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
            }
        )
    }

    Text("Your Collection", color = Forest,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold)
    Text("Little treasures from the puzzles you've finished.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            // Intentional readable placeholder until the approved Sudoku PNGs
            // can be imported without replacing them with approximations.
            PortraitMonogram(equippedName, 64)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(profile.identity.displayName, color = Forest,
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(equippedName + " · Woodland portrait",
                    color = Muted, style = MaterialTheme.typography.bodyMedium)
                Text((profile.completions + profile.dailyCompletions).toString() +
                    " puzzles completed · " + profile.dailyCompletions + " Daily",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = {
                draftName = profile.identity.displayName
                showNameDialog = true
            }) { Text("Edit") }
        }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (showAchievements) {
            OutlinedButton(onClick = { showAchievements = false },
                modifier = Modifier.weight(1f)) { Text("Portraits") }
            Button(onClick = { showAchievements = true },
                modifier = Modifier.weight(1f)) { Text("Achievements") }
        } else {
            Button(onClick = { showAchievements = false },
                modifier = Modifier.weight(1f)) { Text("Portraits") }
            OutlinedButton(onClick = { showAchievements = true },
                modifier = Modifier.weight(1f)) { Text("Achievements") }
        }
    }

    if (showAchievements) {
        Text(achievements.count { it.unlocked }.toString() +
            " / " + achievements.size + " achievements",
            color = Forest, style = MaterialTheme.typography.titleMedium)
        achievements.forEach { item -> AchievementCard(item) }
    } else {
        Text(portraits.count { it.unlocked }.toString() +
            " / " + portraits.size + " Woodland portraits available",
            color = Forest, style = MaterialTheme.typography.titleMedium)
        Text("The original Woodland artwork is reserved for this collection; " +
            "simple letter badges are temporary previews until the approved PNGs are imported.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        portraits.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { item ->
                    PortraitCard(
                        item,
                        WoodlandCollection.remaining(profile, item.portrait.id) ?: 0,
                        Modifier.weight(1f),
                        onEquip = { store.equipPortrait(item.portrait.id) }
                    )
                }
                if (pair.size == 1) Box(Modifier.weight(1f))
            }
        }
        Text("Unlocks depend on completed Solo and Daily puzzles. " +
            "No random purchases, streak deadlines or paid portraits.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PortraitMonogram(label: String, sizeDp: Int) {
    Box(
        Modifier.size(sizeDp.dp).background(
            if (label == "Owl") ColorForestPale else ColorGoldPale,
            CircleShape
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(label.take(1).uppercase(), color = Forest,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
    }
}

private val ColorForestPale = androidx.compose.ui.graphics.Color(0xFFD9E7D9)
private val ColorGoldPale = androidx.compose.ui.graphics.Color(0xFFF3E3C3)

@Composable
private fun PortraitCard(
    item: PortraitProgress,
    remaining: Int,
    modifier: Modifier,
    onEquip: () -> Unit
) {
    Card(modifier) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            PortraitMonogram(item.portrait.label, 54)
            Text(item.portrait.label, color = Forest, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                when {
                    item.equipped -> "Equipped"
                    item.unlocked -> "Unlocked"
                    else -> remaining.toString() + " more to unlock"
                },
                color = Muted, style = MaterialTheme.typography.labelMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            TextButton(onClick = onEquip, enabled = item.unlocked && !item.equipped) {
                Text(if (item.equipped) "Selected" else if (item.unlocked) "Equip" else "Locked")
            }
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementProgress) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.title, fontWeight = FontWeight.SemiBold, color = Forest)
                Text(if (item.unlocked) "Earned" else
                    item.current.toString() + "/" + item.target,
                    color = if (item.unlocked) Forest else Muted,
                    style = MaterialTheme.typography.labelMedium)
            }
            Text(item.description, style = MaterialTheme.typography.bodySmall, color = Muted)
            LinearProgressIndicator(
                progress = { item.current.toFloat() / item.target.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = Forest,
                trackColor = ColorForestPale
            )
        }
    }
}
