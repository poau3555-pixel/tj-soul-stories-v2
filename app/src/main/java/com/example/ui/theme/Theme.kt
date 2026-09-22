package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CandyColorScheme = darkColorScheme(
    primary = PrimaryPink,
    onPrimary = Color.White,
    secondary = CandyOrange,
    onSecondary = Color.Black,
    tertiary = CandyYellow,
    onTertiary = Color.Black,
    background = BoardBackground,
    onBackground = Color.White,
    surface = SurfaceCard,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF4A2574),
    onSurfaceVariant = Color(0xFFFFD54F),
    outline = Color(0xFFFFC107)
)

@Composable
fun CandyCrushTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CandyColorScheme,
        typography = Typography,
        content = content
    )
}
