package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.LargeBoardViewport
import kotlin.math.roundToInt

/**
 * An overview of ONE board, not multiple puzzles. 10-cell "regions" are
 * optional navigation labels: a target may cross any region boundary.
 */
@Composable
internal fun LargeBoardRegionDialog(
    cells: Int,
    viewport: LargeBoardViewport,
    sidePx: Float,
    onJumpToRegion: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val count = LargeBoardViewport.regionCount(cells)
    var selectedRow by remember(cells) { mutableIntStateOf(0) }
    var selectedCol by remember(cells) { mutableIntStateOf(0) }
    val rect = viewport.overview(sidePx)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Board map", color = Forest, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("This is one continuous ${cells}×${cells} puzzle. " +
                    "Tap the map to jump to a section, or choose a row and column. " +
                    "Words can cross section lines.", color = Muted)
                Canvas(Modifier.fillMaxWidth().widthIn(max = 280.dp)
                    .aspectRatio(1f)
                    .border(1.dp, Forest)
                    .testTag("large-board-region-map")
                    .pointerInput(cells) {
                        detectTapGestures { tap ->
                            LargeBoardViewport.tappedRegion(
                                tap.x, tap.y, size.width.toFloat(), cells
                            )?.let { (r, c) -> onJumpToRegion(r, c) }
                        }
                    }
                    .semantics {
                        contentDescription = "Overview of ${count} by ${count} " +
                            "regions; the highlighted rectangle shows the visible area. " +
                            "Use row and column controls below for accessible navigation."
                    }
                ) {
                    drawRect(Color(0xFFFAF9F2))
                    val step = size.width / count
                    for (index in 1 until count) {
                        val p = step * index
                        drawLine(Color(0xFFC0CCBF), Offset(p,0f), Offset(p,size.height),
                            strokeWidth = 1.dp.toPx())
                        drawLine(Color(0xFFC0CCBF), Offset(0f,p), Offset(size.width,p),
                            strokeWidth = 1.dp.toPx())
                    }
                    drawRect(Color(0x44D4B05A),
                        topLeft = Offset(size.width * rect.left, size.height * rect.top),
                        size = Size(size.width * (rect.right - rect.left),
                            size.height * (rect.bottom - rect.top)))
                    drawRect(Forest,
                        topLeft = Offset(size.width * rect.left, size.height * rect.top),
                        size = Size(size.width * (rect.right - rect.left),
                            size.height * (rect.bottom - rect.top)),
                        style = Stroke(2.dp.toPx()))
                }
                Text("Go directly to a section", color = Forest,
                    fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var rowsExpanded by remember { mutableStateOf(false) }
                    var colsExpanded by remember { mutableStateOf(false) }
                    Box(Modifier.weight(1f)) {
                        TextButton(onClick = { rowsExpanded = true }) {
                            Text("Row ${('A'.code + selectedRow).toChar()}")
                        }
                        DropdownMenu(expanded = rowsExpanded,
                            onDismissRequest = { rowsExpanded = false }) {
                            for (row in 0 until count) {
                                DropdownMenuItem(text = {
                                    Text("Row ${('A'.code + row).toChar()}")
                                }, onClick = {
                                    selectedRow = row
                                    rowsExpanded = false
                                })
                            }
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        TextButton(onClick = { colsExpanded = true }) {
                            Text("Column ${selectedCol + 1}")
                        }
                        DropdownMenu(expanded = colsExpanded,
                            onDismissRequest = { colsExpanded = false }) {
                            for (col in 0 until count) {
                                DropdownMenuItem(text = {
                                    Text("Column ${col + 1}")
                                }, onClick = {
                                    selectedCol = col
                                    colsExpanded = false
                                })
                            }
                        }
                    }
                }
                Button(
                    onClick = { onJumpToRegion(selectedRow, selectedCol) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Jump to section") }
                Text("Section size: up to 10×10 cells. Your puzzle is not divided.",
                    color = Muted)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close map") }
        }
    )
}
