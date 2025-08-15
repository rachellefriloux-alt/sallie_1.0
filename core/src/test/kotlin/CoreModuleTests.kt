package com.sallie.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoreModuleTests {
    @Test
    fun testEmotionalContextManagerMoodHistory() {
        val ecm = EmotionalContextManager()
        ecm.updateMood("calm")
        ecm.updateMood("focused")
        assertEquals("focused", ecm.currentMood)
        assertTrue(ecm.getMoodHistory().size >= 2)
    }

    @Test
    fun testMemoryManagerRecall() {
        val mm = MemoryManager()
        mm.remember("project", "Sallie")
        assertEquals("Sallie", mm.recall("project"))
        assertTrue(mm.getMemoryHistory().isNotEmpty())
    }

    @Test
    fun testGoalAlignerAddsGoals() {
        val ga = GoalAligner()
        ga.addGoal("Ship MVP")
        ga.addGoal("Refactor core")
        assertTrue(ga.getGoals().size == 2)
        assertEquals(2, ga.getGoalHistory().size)
    }

    @Test
    fun testPatternRecognizerHistory() {
        val pr = PatternRecognizer()
        val pattern = pr.recognizePattern(listOf("focus", "deep work"))
        assertTrue(pattern.contains("Pattern recognized"))
        assertEquals(1, pr.getPatternHistory().size)
    }
}
