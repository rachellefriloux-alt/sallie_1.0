package com.sallie.components

// Dynamic theming engine
class ThemeManager {
    fun suggestTheme(mood: String): String = when (mood) {
        "energetic" -> "Grace & Grind"
        "focused" -> "Hustle Legacy"
        "calm" -> "Soul Care"
        "night" -> "Midnight Hustle"
        else -> "Default Theme"
    }
}
