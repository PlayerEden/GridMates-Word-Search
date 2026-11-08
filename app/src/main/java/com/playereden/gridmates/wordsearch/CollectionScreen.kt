package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.AchievementProgress
import com.playereden.gridmates.wordsearch.core.BoardFontCollection
import com.playereden.gridmates.wordsearch.core.BoardFontProgress
import com.playereden.gridmates.wordsearch.core.PortraitProgress
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements

@Composable
internal fun CollectionScreen(store: SoloStore) {
    val profile = store.profile
    val portraits = remember(profile) { WoodlandCollection.progress(profile) }
    val achievements = remember(profile) { WordSearchAchievements.progress(profile) }
    val equipped = portraits.firstOrNull { it.equipped }
    val equippedName = equipped?.portrait?.label ?: "Owl"
    val equippedId = equipped?.portrait?.id ?: "woodland_owl"
    val context = LocalContext.current
    val originalImagesAvailable = remember(context) {
        WoodlandCollection.portraits.all { portrait ->
            context.resources.getIdentifier(
                portrait.id, "drawable", context.packageName
            ) != 0
        }
    }
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var draftName by remember(profile.identity.displayName) {
        mutableStateOf(profile.identity.displayName)
    }
    var collectionTab by rememberSaveable { mutableStateOf("Portraits") }
    var portraitPage by rememberSaveable { mutableIntStateOf(0) }
    var achievementPage by rememberSaveable { mutableIntStateOf(0) }
    val compactGallery = LocalConfiguration.current.screenWidthDp < 350 ||
        LocalDensity.current.fontScale >= 1.3f
    val portraitPageSize = if (compactGallery) 2 else 4
    val achievementPageSize = if (compactGallery) 2 else 4
    var licenseFontId by rememberSaveable { mutableStateOf<String?>(null) }
    val completed = WoodlandCollection.completedCount(profile)
    val nextPortrait = portraits.firstOrNull { !it.unlocked }?.portrait
    val fonts = remember(profile, store.selectedBoardFontId) {
        BoardFontCollection.progress(profile, store.selectedBoardFontId)
    }

    if (licenseFontId != null) {
        val fontId = requireNotNull(licenseFontId)
        val label = BoardFontCollection.definitions.firstOrNull { it.id == fontId }?.label
            ?: "Typeface"
        val notice = remember(fontId) {
            runCatching {
                context.assets.open("font_licenses/${fontId}_ofl.txt")
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
            }.getOrElse { "License notice could not be opened." }
        }
        AlertDialog(
            onDismissRequest = { licenseFontId = null },
            title = { Text("$label · Open Font License") },
            text = {
                Column(Modifier.heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text(notice, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { licenseFontId = null }) { Text("Close") }
            }
        )
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Your local profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your name is saved on this device and shown to a partner " +
                        "during Nearby Co-op. No account is needed.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it.take(24) },
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

    PaperEyebrow("Your profile & rewards")
    Text("Your Collection", color = Forest,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold)
    Text("Collect more portraits and fonts as you finish puzzles. Everything is free.",
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (label in listOf("Portraits", "Fonts", "Achievements")) {
            FilterChip(
                selected = collectionTab == label,
                onClick = { collectionTab = label },
                label = { Text(label) }
            )
        }
    }

    // The selected collection tab must open to its content immediately,
    // not below a full-page profile header. Keep profile/dossier in Portraits.
    if (collectionTab == "Portraits") {
        PaperPanel(Modifier.fillMaxWidth()) {
            PaperEyebrow("Player profile")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                PortraitBadge(equippedId, equippedName, 48)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(profile.identity.displayName, color = Forest,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(equippedName + " · Current portrait", color = Muted,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
            OutlinedButton(onClick = {
                draftName = profile.identity.displayName
                showNameDialog = true
            }, modifier = Modifier.fillMaxWidth()) { Text("Edit display name") }
        }



    }

    if (collectionTab == "Fonts") {
        Text(fonts.count { it.unlocked }.toString() + " / " +
            fonts.size + " board fonts unlocked",
            color = Forest, style = MaterialTheme.typography.titleMedium)
        Text("All styles are free. Earn fonts through achievements, then equip " +
            "one to change your puzzle letters. The readable default is always available.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = if (maxWidth < 350.dp ||
                LocalDensity.current.fontScale >= 1.3f) 1 else 2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fonts.chunked(columns).forEach { pair ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { item ->
                            BoardFontCard(item, Modifier.weight(1f),
                                onEquip = { store.equipBoardFont(item.font.id) },
                                onLicense = { licenseFontId = item.font.id })
                        }
                        if (pair.size < columns) Box(Modifier.weight(1f))
                    }
                }
            }
        }
        Text("Every font is bundled for offline use under its own Open Font License. " +
            "Tap License to read the full copyright and redistribution terms.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    } else if (collectionTab == "Achievements") {
        Text(achievements.count { it.unlocked }.toString() +
            " / " + achievements.size + " achievements",
            color = Forest, style = MaterialTheme.typography.titleMedium)
        Text("Achievements reflect your saved Solo and Daily progress.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        val orderedAchievements = achievements.sortedWith(
            compareBy<AchievementProgress> { it.unlocked }.thenBy { it.target - it.current })
        val pages = ((orderedAchievements.size + achievementPageSize - 1) /
            achievementPageSize).coerceAtLeast(1)
        val current = achievementPage.coerceIn(0, pages - 1)
        // Put paging ahead of the long card stack: on narrow phones the
        // controls must remain discoverable immediately after changing tabs.
        CollectionPager(current, pages, id = "achievements", onChange = { achievementPage = it })
        orderedAchievements.drop(current * achievementPageSize).take(achievementPageSize)
            .forEach { item -> AchievementCard(item) }
    } else {
        Text(portraits.count { it.unlocked }.toString() +
            " / " + portraits.size + " Woodland portraits available",
            color = Forest, style = MaterialTheme.typography.titleMedium)
        Text(
            if (originalImagesAvailable) {
                "Choose a Woodland companion. More portraits unlock as you play."
            } else {
                "Some portrait artwork is unavailable in this build. " +
                    "Letter badges will appear instead."
            },
            color = Muted, style = MaterialTheme.typography.bodySmall
        )
        val portraitPages = ((portraits.size + portraitPageSize - 1) /
            portraitPageSize).coerceAtLeast(1)
        CollectionPager(portraitPage.coerceIn(0, portraitPages - 1),
            portraitPages, id = "portraits", onChange = { portraitPage = it })
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = if (maxWidth < 350.dp ||
                LocalDensity.current.fontScale >= 1.3f) 1 else 2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val current = portraitPage.coerceIn(0, ((portraits.size + portraitPageSize - 1) / portraitPageSize - 1).coerceAtLeast(0))
                portraits.drop(current * portraitPageSize).take(portraitPageSize)
                    .chunked(columns).forEach { pair ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { item ->
                            PortraitCard(
                                item,
                                WoodlandCollection.remaining(profile, item.portrait.id) ?: 0,
                                Modifier.weight(1f),
                                onEquip = { store.equipPortrait(item.portrait.id) }
                            )
                        }
                        if (pair.size < columns) Box(Modifier.weight(1f))
                    }
                }
            }
        }
        Text("Unlocks depend on completed Solo and Daily puzzles. " +
            "No random purchases, streak deadlines or paid portraits.",
            color = Muted, style = MaterialTheme.typography.bodySmall)

        // History/progress is still available, but after the actual gallery.
        // The portrait pager and equip choices come first on a phone.
        PaperPanel(Modifier.fillMaxWidth()) {
            PaperEyebrow("Collection progress")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(portraits.count { it.unlocked }.toString() + " of " +
                        portraits.size + " portraits", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text(achievements.count { it.unlocked }.toString() + " of " +
                        achievements.size + " achievements earned",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            PaperProgress(portraits.count { it.unlocked }, portraits.size)
            Text(
                if (nextPortrait == null) "You have earned every Woodland portrait."
                else "Next portrait: " + nextPortrait.label + " · " +
                    (nextPortrait.requiredCompletions - completed).coerceAtLeast(0) +
                    " more completed " + (if (nextPortrait.requiredCompletions - completed == 1)
                        "puzzle" else "puzzles") + " to unlock.",
                color = Muted, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BoardFontCard(
    item: BoardFontProgress,
    modifier: Modifier,
    onEquip: () -> Unit,
    onLicense: () -> Unit
) {
    Card(modifier,
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, if (item.equipped) Forest else Color(0xFFDFE0D6))
    ) {
        Column(Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BoardFontPreview(item.font.id)
            Text(item.font.label, color = Forest,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2)
            Text(when {
                item.equipped -> "Equipped"
                item.unlocked -> "Unlocked · Free"
                else -> "Earn: " + (item.achievementName ?: "Achievement")
            }, color = Muted, style = MaterialTheme.typography.labelMedium)
            Text(item.font.description, color = Muted,
                style = MaterialTheme.typography.bodySmall)
            TextButton(
                onClick = onEquip,
                enabled = item.unlocked && !item.equipped,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (item.equipped) "Selected" else
                    if (item.unlocked) "Equip" else "Locked")
            }
            TextButton(onClick = onLicense, modifier = Modifier.fillMaxWidth()) {
                Text("License")
            }
        }
    }
}

@Composable
private fun PortraitBadge(
    id: String, label: String, sizeDp: Int, unlocked: Boolean = true
) {
    val context = LocalContext.current
    val resource = remember(context, id) {
        context.resources.getIdentifier(id, "drawable", context.packageName)
    }
    if (resource == 0) {
        PortraitMonogram(label, sizeDp)
    } else {
        Box(
            Modifier.size(sizeDp.dp).clip(CircleShape)
                .background(ColorForestPale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(resource),
                contentDescription = "$label Woodland portrait",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().alpha(if (unlocked) 1f else 0.45f)
            )
        }
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
    Card(modifier,
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, if (item.equipped) Forest else Color(0xFFDFE0D6))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            PortraitBadge(item.portrait.id, item.portrait.label, 54, item.unlocked)
            Text(item.portrait.label, color = Forest, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                when {
                    item.equipped -> "Equipped"
                    item.unlocked -> "Unlocked"
                    else -> remaining.toString() + " more " +
                        (if (remaining == 1) "puzzle" else "puzzles") + " to unlock"
                },
                color = Muted, style = MaterialTheme.typography.labelMedium,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            if (!item.unlocked && item.portrait.requiredCompletions > 0) {
                val completed = (item.portrait.requiredCompletions - remaining).coerceAtLeast(0)
                PaperProgress(completed, item.portrait.requiredCompletions)
            }
            TextButton(onClick = onEquip, enabled = item.unlocked && !item.equipped) {
                Text(if (item.equipped) "Selected" else if (item.unlocked) "Equip" else "Locked")
            }
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementProgress) {
    PaperPanel(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(item.title, fontWeight = FontWeight.SemiBold, color = Forest,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f))
            Text(if (item.unlocked) "Earned" else
                item.current.toString() + " / " + item.target,
                color = if (item.unlocked) Forest else Muted,
                style = MaterialTheme.typography.labelMedium)
        }
        Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Muted)
        PaperProgress(item.current, item.target)
        if (!item.unlocked) {
            Text((item.target - item.current).coerceAtLeast(0).toString() +
                " more to earn this achievement",
                color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Compact but complete gallery navigation: no rewards are hidden or discarded. */
@Composable
private fun CollectionPager(page: Int, pageCount: Int, id: String, onChange: (Int) -> Unit) {
    if (pageCount <= 1) return
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { onChange(page - 1) }, enabled = page > 0,
            modifier = Modifier.heightIn(min = 48.dp).testTag("$id-previous")) {
            Text("Previous")
        }
        Text("Page ${page + 1} of $pageCount",
            color = Muted, style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { onChange(page + 1) }, enabled = page < pageCount - 1,
            modifier = Modifier.heightIn(min = 48.dp).testTag("$id-next")) {
            Text("Next")
        }
    }
}
