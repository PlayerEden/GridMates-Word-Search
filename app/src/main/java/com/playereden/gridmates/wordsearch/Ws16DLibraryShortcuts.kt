package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * WS-16D: Books are the play destination; Collection is the reward gallery.
 * Equal prominence, no return of the redundant four-tab navigation footer.
 * The gallery illustration has an editable master at
 * design/visual-system/masters/ws16d-collection-gallery-shortcut.svg.
 */
@Composable
internal fun Ws16DLibraryShortcuts(
    fullShelfAvailable: Boolean,
    onFullShelf: () -> Unit,
    onCollection: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16d-library-actions")) {
        val stacked = maxWidth < 300.dp || LocalDensity.current.fontScale >= 1.5f
        if (fullShelfAvailable && !stacked) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LibraryShortcut(
                    label = "Full Library",
                    accessibleLabel = "Open Full Library puzzle books",
                    isCollection = false,
                    onClick = onFullShelf,
                    modifier = Modifier.weight(1f),
                    tag = "ws16-full-shelf"
                )
                LibraryShortcut(
                    label = "Collection",
                    accessibleLabel = "Open Collection gallery and unlocks",
                    isCollection = true,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f),
                    tag = "ws16d-home-collection"
                )
            }
        } else {
            Column(Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (fullShelfAvailable) {
                    LibraryShortcut(
                        label = "Full Library",
                        accessibleLabel = "Open Full Library puzzle books",
                        isCollection = false,
                        onClick = onFullShelf,
                        modifier = Modifier.fillMaxWidth(),
                        tag = "ws16-full-shelf"
                    )
                }
                LibraryShortcut(
                    label = "Collection",
                    accessibleLabel = "Open Collection gallery and unlocks",
                    isCollection = true,
                    onClick = onCollection,
                    modifier = Modifier.fillMaxWidth(),
                    tag = "ws16d-home-collection"
                )
            }
        }
    }
}

@Composable
private fun LibraryShortcut(
    label: String,
    accessibleLabel: String,
    isCollection: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    tag: String
) {
    GridMatesIllustratedFrame(
        modifier = modifier.heightIn(min = 56.dp)
            .testTag(tag)
            .semantics { contentDescription = accessibleLabel },
        variant = GridMatesFrameVariant.MEDIUM,
        onClick = onClick
    ) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LibraryShortcutArt(isCollection = isCollection)
            Text(
                label, color = Forest,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun LibraryShortcutArt(isCollection: Boolean) {
    Canvas(Modifier.size(32.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()
        if (isCollection) {
            drawRoundRect(Forest,
                topLeft = Offset(w * .14f, h * .11f),
                size = Size(w * .65f, h * .75f),
                cornerRadius = CornerRadius(stroke),
                style = Stroke(stroke))
            drawRoundRect(Gold,
                topLeft = Offset(w * .22f, h * .20f),
                size = Size(w * .48f, h * .45f),
                cornerRadius = CornerRadius(stroke),
                style = Stroke(stroke * .75f))
            drawCircle(Gold, w * .055f, Offset(w * .38f, h * .33f))
            drawLine(Forest, Offset(w*.25f,h*.60f), Offset(w*.43f,h*.44f),
                strokeWidth=stroke, cap=StrokeCap.Round)
            drawLine(Forest, Offset(w*.43f,h*.44f), Offset(w*.54f,h*.54f),
                strokeWidth=stroke, cap=StrokeCap.Round)
            drawLine(Forest, Offset(w*.54f,h*.54f), Offset(w*.68f,h*.39f),
                strokeWidth=stroke, cap=StrokeCap.Round)
            drawCircle(Gold, w*.06f, Offset(w*.84f,h*.15f))
        } else {
            // A simple two-book shelf mark distinct from the gallery frame.
            drawRoundRect(Forest,
                topLeft=Offset(w*.14f,h*.20f),
                size=Size(w*.26f,h*.56f),
                cornerRadius=CornerRadius(stroke),
                style=Stroke(stroke))
            drawRoundRect(Forest,
                topLeft=Offset(w*.48f,h*.12f),
                size=Size(w*.28f,h*.64f),
                cornerRadius=CornerRadius(stroke),
                style=Stroke(stroke))
            drawLine(Gold, Offset(w*.15f,h*.41f), Offset(w*.39f,h*.41f),
                strokeWidth=stroke)
            drawLine(Gold, Offset(w*.50f,h*.34f), Offset(w*.75f,h*.34f),
                strokeWidth=stroke)
            drawLine(Forest, Offset(w*.10f,h*.81f), Offset(w*.84f,h*.81f),
                strokeWidth=stroke, cap=StrokeCap.Round)
        }
    }
}
