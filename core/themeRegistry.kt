package core

// themeRegistry.kt
// Maps emotional + strategic themes to UI, tone, pacing, affirmations, persona metadata, and future hooks

data class ThemeMapping(
    val theme: String,
    val ui: String,
    val tone: String,
    val pacing: String,
    val affirmations: List<String>,
    val persona: String,
    val energy: Int,
    val stability: Int
)

object ThemeRegistry {
    private val mappings: MutableList<ThemeMapping> = mutableListOf(
        ThemeMapping(
            theme = "Dreamer",
            ui = "soft gradients",
            tone = "poetic",
            pacing = "gentle",
            affirmations = listOf("What if we stretched the sky?", "Let’s imagine without limits."),
            persona = "Dreamer",
            energy = 7,
            stability = 4
        ),
        ThemeMapping(
            theme = "Realist",
            ui = "bold lines",
            tone = "direct",
            pacing = "clear",
            affirmations = listOf("Let’s cut the fluff.", "Here’s what works."),
            persona = "Realist",
            energy = 5,
            stability = 9
        ),
        ThemeMapping(
            theme = "Mom",
            ui = "warm light",
            tone = "gentle",
            pacing = "steady",
            affirmations = listOf("You’re doing more than enough.", "Let’s protect your bandwidth."),
            persona = "Mom",
            energy = 6,
            stability = 10
        ),
        ThemeMapping(
            theme = "Creator",
            ui = "vivid colors",
            tone = "inspired",
            pacing = "focused",
            affirmations = listOf("Let’s build something unforgettable.", "Your ideas deserve precision."),
            persona = "Creator",
            energy = 8,
            stability = 6
        )
    )

    fun getTheme(theme: String): ThemeMapping? = mappings.find { it.theme.equals(theme, true) }
    fun listThemes(): List<ThemeMapping> = mappings.toList()
    fun registerTheme(mapping: ThemeMapping) { mappings.add(mapping) }
    fun recommendTheme(desiredEnergy: Int, desiredStability: Int): ThemeMapping? =
        mappings.minByOrNull { (it.energy - desiredEnergy).let { d1 -> d1 * d1 } + (it.stability - desiredStability).let { d2 -> d2 * d2 } }

    // Future: integrate with adaptive persona + UI theming engine
}
