package com.sallie.feature

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class FeatureModuleTests {
    @Test
    fun testTaskOrchestratorSelection() {
        val orchestrator = TaskOrchestrator()
        orchestrator.addTask("t1", "Low task", importance = 1, urgency = 1, effortMinutes = 5)
        orchestrator.addTask("t2", "High task", importance = 5, urgency = 5, effortMinutes = 10)
        val selected = orchestrator.selectTasks(maxTotalMinutes = 15)
        assertEquals(1, selected.size)
        assertEquals("t2", selected.first().id)
    }

    @Test
    fun testMessageDraftManager() {
        val manager = MessageDraftManager()
        val draft = manager.createDraft("Hello there")
        assertTrue(draft.content.contains("Hello"))
        val edited = manager.editDraft(draft.id, "Updated content")
        assertEquals("Updated content", edited?.content)
        val toned = manager.adjustTone(draft.id, "friendly")
        assertNotNull(toned)
        assertTrue(toned.content.startsWith("[FRIENDLY]"))
    }

    @Test
    fun testCustomRoutineManager() {
        val crm = CustomRoutineManager()
        val routine = crm.createRoutine("Morning", listOf("Stretch", "Water"))
        assertEquals(2, routine.steps.size)
        crm.addStep("Morning", "Breathe")
        val (r, outputs) = crm.runRoutine("Morning")
        assertEquals(3, r?.steps?.size)
        assertEquals(3, outputs.size)
    }

    @Test
    fun testDeviceControlPermissions() {
        val dcm = DeviceControlManager()
        dcm.revokePermission("call")
    val result = dcm.makeCall("Alex") // should be blocked
    assertTrue(result.contains("blocked") && result.contains("permission"))
        dcm.grantPermission("call")
        val ok = dcm.makeCall("Alex")
        assertTrue(ok.contains("executed"))
        assertTrue(dcm.getHistory().isNotEmpty())
    }
}
