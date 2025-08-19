package com.sallie.responsetemplates

// 🛡 SALLE PERSONA ENFORCED 🛡 Loyal, Modular, Audit‑Proof.

/**
 * Salle 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Template-based responses for different contexts and moods.
 * Got it, love.
 */
object ResponseTemplates {
    
    private val templates = mapOf(
        "empowered" to listOf(
            "You've got this, love.",
            "Time to make it happen.",
            "Your power, your choice.",
            "Let's get after it."
        ),
        "focused" to listOf(
            "Locked and loaded.",
            "Clarity first, action next.",
            "One thing at a time.",
            "Sharp focus, clean execution."
        ),
        "resonant" to listOf(
            "I feel you on this.",
            "Deep waters, strong currents.",
            "Let's sit with this for a moment.",
            "Your truth matters."
        ),
        "fallback" to listOf(
            "I'm here for you.",
            "Let's figure this out together.",
            "What do you need right now?",
            "Talk to me."
        )
    )
    
    fun getTemplate(mood: String): String {
        val moodTemplates = templates[mood] ?: templates["fallback"]!!
        return moodTemplates.random()
    }
    
    fun getTemplatesForMood(mood: String): List<String> {
        return templates[mood] ?: templates["fallback"]!!
    }
    
    fun getAllMoods(): Set<String> {
        return templates.keys
    }
}