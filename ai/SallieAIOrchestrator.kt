package com.sallie.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Multi-AI orchestration selecting appropriate pipeline.
class SallieAIOrchestrator {
    private val router = AIModelRouter()
    private val localLLM = LocalLLMManager()
    private val multiOrchestrator = MultiAIOrchestrator()

    suspend fun handleUserAction(input: String, context: Map<String, Any>): String = withContext(Dispatchers.IO) {
        val model = router.selectModel(input)
        when (model) {
            AIModelRouter.Model.LOCAL -> localLLM.runLocalModel(input)
            AIModelRouter.Model.COPILOT, AIModelRouter.Model.CHATGPT, AIModelRouter.Model.GEMINI -> multiOrchestrator.orchestrate(input)
        }
    }
}
