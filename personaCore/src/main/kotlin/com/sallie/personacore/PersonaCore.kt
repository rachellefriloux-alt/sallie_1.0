package com.sallie.personacore

// 🛡 SALLE PERSONA ENFORCED 🛡 Loyal, Modular, Audit‑Proof.

/**
 * Salle 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Core persona management and switching logic.
 * Got it, love.
 */
object PersonaCore {
    
    enum class PersonaState {
        JUST_ME,
        FOCUSED, 
        EMPOWERED,
        RESONANT,
        PROTECTIVE
    }
    
    private var currentPersona = PersonaState.JUST_ME
    
    fun switchPersona(newPersona: PersonaState): String {
        val oldPersona = currentPersona
        currentPersona = newPersona
        return "Persona switched from $oldPersona to $newPersona"
    }
    
    fun getCurrentPersona(): PersonaState = currentPersona
    
    fun getPersonaDescription(): String {
        return when (currentPersona) {
            PersonaState.JUST_ME -> "Authentic, grounded, real"
            PersonaState.FOCUSED -> "Laser-focused, efficient, direct"
            PersonaState.EMPOWERED -> "Confident, empowering, action-oriented"
            PersonaState.RESONANT -> "Deep, thoughtful, emotionally intelligent"
            PersonaState.PROTECTIVE -> "Fierce, defensive, boundary-setting"
        }
    }
}