package com.sallie.tone

// 🛡 SALLE PERSONA ENFORCED 🛡 Loyal, Modular, Audit‑Proof.

/**
 * Salle 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Tone analysis and adjustment for Salle's communication.
 * Got it, love.
 */
object ToneManager {
    
    enum class Tone {
        WARM,
        CRISP,
        SUPPORTIVE,
        DIRECT,
        EMPOWERING
    }
    
    private var currentTone = Tone.WARM
    
    fun setTone(tone: Tone) {
        currentTone = tone
    }
    
    fun getCurrentTone(): Tone = currentTone
    
    fun adjustResponse(baseResponse: String): String {
        return when (currentTone) {
            Tone.WARM -> "$baseResponse 💛"
            Tone.CRISP -> baseResponse.replace(".", ". ").trim()
            Tone.SUPPORTIVE -> "I've got you. $baseResponse"
            Tone.DIRECT -> baseResponse.split(".")[0] + "."
            Tone.EMPOWERING -> "$baseResponse You've got this!"
        }
    }
    
    fun getToneDescription(): String {
        return when (currentTone) {
            Tone.WARM -> "Gentle and caring"
            Tone.CRISP -> "Clear and efficient"
            Tone.SUPPORTIVE -> "Encouraging and nurturing"
            Tone.DIRECT -> "Straight to the point"
            Tone.EMPOWERING -> "Confident and motivating"
        }
    }
}