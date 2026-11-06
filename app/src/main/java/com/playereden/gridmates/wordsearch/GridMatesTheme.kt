package com.playereden.gridmates.wordsearch

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val Cream = Color(0xFFF4F3EB)
internal val Paper = Color(0xFFFFFDF8)
internal val Forest = Color(0xFF214F3F)
internal val Ink = Color(0xFF263D36)
internal val Muted = Color(0xFF55665C)
internal val Gold = Color(0xFF8A6729)

private val gridMatesScheme = lightColorScheme(
    primary = Forest,
    onPrimary = Paper,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    secondary = Gold,
    onSecondary = Paper,
    outline = Muted,
    primaryContainer = Color(0xFFD9E7D9),
    onPrimaryContainer = Forest,
    secondaryContainer = Color(0xFFF1E4C1),
    onSecondaryContainer = Ink,
    tertiary = Gold,
    onTertiary = Paper,
    tertiaryContainer = Color(0xFFF5E9CD),
    onTertiaryContainer = Ink,
    surfaceVariant = Cream,
    onSurfaceVariant = Ink,
    surfaceContainer = Paper,
    surfaceContainerHigh = Cream,
    surfaceContainerLow = Paper
)

@Composable
internal fun GridMatesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = gridMatesScheme,
        typography = Typography(),
        content = content
    )
}
