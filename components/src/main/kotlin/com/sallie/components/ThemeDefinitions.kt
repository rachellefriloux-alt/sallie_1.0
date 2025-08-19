package com.sallie.components

import androidx.compose.ui.graphics.Color // Assuming Compose UI Color

// Data class to hold the specific colors for a theme
data class ThemeColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color
    // Add other colors like error, onBackground, onSurface etc. if needed
)

// Function to get the color scheme for a given theme name
fun schemeFor(theme: String): ThemeColors {
    return when (theme) {
        "Grace & Grind" -> ThemeColors(
            primary = Color(0xFF1E88E5),
            secondary = Color(0xFF43A047),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFEEEEEE),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Hustle Legacy" -> ThemeColors(
            primary = Color(0xFFD81B60),
            secondary = Color(0xFF8E24AA),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFEEEEEE),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Soul Care" -> ThemeColors(
            primary = Color(0xFF00ACC1),
            secondary = Color(0xFF00897B),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFEEEEEE),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Midnight Hustle" -> ThemeColors(
            primary = Color(0xFF37474F),
            secondary = Color(0xFF263238),
            background = Color(0xFF212121),
            surface = Color(0xFF303030),
            onPrimary = Color.White,
            onSecondary = Color.White
        )
        "Southern Grit" -> ThemeColors(
            primary = Color(0xFF6B4226),
            secondary = Color(0xFFD9A066),
            background = Color(0xFFF5F0E6),
            surface = Color(0xFF3E2C1C),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Visionary" -> ThemeColors(
            primary = Color(0xFF7C4DFF), // Radiant purple
            secondary = Color(0xFFFFEA00), // Bright yellow
            background = Color(0xFFF3E5F5),
            surface = Color(0xFFEDE7F6),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Guardian" -> ThemeColors(
            primary = Color(0xFF1565C0), // Deep blue
            secondary = Color(0xFF90CAF9), // Light blue
            background = Color(0xFFE3F2FD),
            surface = Color(0xFFBBDEFB),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Mentor" -> ThemeColors(
            primary = Color(0xFF388E3C), // Sage green
            secondary = Color(0xFFA5D6A7), // Light green
            background = Color(0xFFE8F5E9),
            surface = Color(0xFFC8E6C9),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Rebel" -> ThemeColors(
            primary = Color(0xFFD32F2F), // Fiery red
            secondary = Color(0xFFFFA000), // Orange
            background = Color(0xFFFFEBEE),
            surface = Color(0xFFFFCDD2),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Explorer" -> ThemeColors(
            primary = Color(0xFFFF7043), // Sunset orange
            secondary = Color(0xFFFFB74D), // Light orange
            background = Color(0xFFFFF3E0),
            surface = Color(0xFFFFE0B2),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        "Healer" -> ThemeColors(
            primary = Color(0xFFF06292), // Soft pink
            secondary = Color(0xFFBA68C8), // Lavender
            background = Color(0xFFF8BBD0),
            surface = Color(0xFFF3E5F5),
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
        else -> defaultThemeColors() // A default theme
    }
}

fun defaultThemeColors(): ThemeColors = ThemeColors( // Default fallback
    primary = Color(0xFF6200EE), // Default Purple
    secondary = Color(0xFF03DAC5), // Default Teal
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black
)

// List of available theme names
val availableThemes = listOf(
    "Grace & Grind",
    "Hustle Legacy",
    "Soul Care",
    "Midnight Hustle",
    "Southern Grit",
    "Visionary",
    "Guardian",
    "Mentor",
    "Rebel",
    "Explorer",
    "Healer"
)
