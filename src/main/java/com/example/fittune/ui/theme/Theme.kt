package com.example.fittune.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Updated Dark Theme Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50), // Green for primary
    onPrimary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

// Updated Light Theme Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // Green for primary
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF), // White for background
    surface = Color(0xFFF5F5F5),
    onSurface = Color.Black
)

@Composable
fun FitTuneTheme(darkMode: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkMode) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
