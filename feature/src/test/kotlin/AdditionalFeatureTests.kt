package com.sallie.feature

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdditionalFeatureTests {
    @Test
    fun testSituationAnalyzerConflict() {
        val analyzer = SituationAnalyzer()
        val res = analyzer.analyzeSituation("We have a conflict and urgent deadline")
        assertTrue(res.tags.contains("conflict"))
        assertTrue(res.tags.contains("urgency"))
        assertTrue(res.emotionalWeight >= 10)
    }

    @Test
    fun testBiasInterceptorMitigation() {
        val bi = BiasInterceptor()
        val assessment = bi.interceptBias("They should always do this stereotype")
        assertTrue(assessment.score >= 5)
        assertTrue(assessment.flags.isNotEmpty())
        assertTrue(assessment.mitigated.contains("Mitigated"))
    }

    @Test
    fun testDignityProtocolsAudit() {
        val dp = DignityProtocols()
        dp.enforceProtocol("redact-sensitive", "User requested redaction")
        dp.logDecision("session-start")
        val audit = dp.audit()
        assertEquals(2, audit.size)
        assertTrue(dp.offerRollback("redact-sensitive").contains("Rollback available"))
    }

    @Test
    fun testSituationAnalyzerEmptyInput() {
        val analyzer = SituationAnalyzer()
        val res = analyzer.analyzeSituation("")
        assertTrue(res.tags.isEmpty())
        assertEquals(0, res.emotionalWeight)
    }

    @Test
    fun testTaskOrchestratorHighStressBudgeting() {
        val orchestrator = TaskOrchestrator()
        orchestrator.addTask("a", "A", urgency = 5, importance = 5, stressImpact = 6, effortMinutes = 5)
        orchestrator.addTask("b", "B", urgency = 4, importance = 4, stressImpact = 2, effortMinutes = 30)
        val selected = orchestrator.selectTasks(maxStress = 3, maxTotalMinutes = 20)
        assertTrue(selected.none { it.stressImpact > 3 })
        assertTrue(selected.sumOf { it.estimatedMinutes } <= 20)
    }

    @Test
    fun testCustomRoutineInactiveRun() {
        val routines = CustomRoutineManager()
        routines.createRoutine("Night", listOf("Shutdown", "Journal"))
        routines.deactivate("Night")
        val (r, outputs) = routines.runRoutine("Night")
        assertTrue(outputs.first().contains("inactive"))
        assertEquals(false, r?.active)
    }
}
