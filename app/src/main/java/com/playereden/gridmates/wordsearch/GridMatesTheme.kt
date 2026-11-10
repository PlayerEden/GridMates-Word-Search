package com.playereden.gridmates.wordsearch

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

// WS-16U design tokens: source-pinned, locally bundled OFL fonts.
 // Literata is the GridMates storybook headline; Atkinson is the accessible UI.
 // No remote font service is required when the APK is installed.
internal val GridMatesStoryTypeface = FontFamily(Font(R.font.gm_literata))
internal val GridMatesControlTypeface = FontFamily(Font(R.font.gm_atkinson))

private val paperbackTypography = Typography(
    headlineLarge = TextStyle(fontFamily = GridMatesStoryTypeface, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = GridMatesStoryTypeface, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = GridMatesStoryTypeface, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = GridMatesStoryTypeface, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = GridMatesControlTypeface, fontSize = 16.sp,
        lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = GridMatesControlTypeface, fontSize = 14.sp,
        lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = GridMatesControlTypeface, fontSize = 12.sp,
        lineHeight = 18.sp),
    titleSmall = TextStyle(fontFamily = GridMatesControlTypeface,
        fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = GridMatesControlTypeface,
        fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = GridMatesControlTypeface,
        fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = GridMatesControlTypeface,
        fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
)

@Composable
internal fun GridMatesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = gridMatesScheme,
        typography = paperbackTypography,
        content = content
    )
}
