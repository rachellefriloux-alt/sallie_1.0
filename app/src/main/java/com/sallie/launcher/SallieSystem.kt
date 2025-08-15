package com.sallie.launcher

import com.sallie.ai.SallieAIOrchestrator
import com.sallie.ai.AIModelRouter
import com.sallie.ai.MultiAIOrchestrator
import com.sallie.ai.RuntimeSwitcher
import com.sallie.ai.ConsentManager
import com.sallie.ai.EmotionalFallbackScripts
import com.sallie.ai.LocalLLMManager
import com.sallie.core.*
import com.sallie.feature.*
import com.sallie.components.*

// Main system orchestrator for Sallie
class SallieSystem {
    val aiOrchestrator = SallieAIOrchestrator()
    val modelRouter = AIModelRouter()
    val multiAI = MultiAIOrchestrator()
    val runtimeSwitcher = RuntimeSwitcher()
    val consentManager = ConsentManager()
    val fallbackScripts = EmotionalFallbackScripts()
    val localLLM = LocalLLMManager()
    val emotionalContext = EmotionalContextManager()
    val memoryManager = MemoryManager()
    val philosophy = PhilosophyEngine()
    val selfEvolution = SelfEvolutionEngine()
    val trust = TrustEngine()
    val goalAligner = GoalAligner()
    val legacyArchitect = LegacyArchitect()
    val insightSynthesizer = InsightSynthesizer()
    val patternRecognizer = PatternRecognizer()
    val personalityBalancer = PersonalityBalancer()
    val proactiveGuidance = ProactiveGuidance()
    val voicePersonaManager = VoicePersonaManager()
    val toneAdjuster = ToneAdjuster()
    val taskOrchestrator = TaskOrchestrator()
    val deviceControl = DeviceControlManager()
    val customRoutine = CustomRoutineManager()
    val navigation = NavigationManager()
    val upgradeMatrix = UpgradeMatrix()
    val emotionalBackup = EmotionalBackupManager()
    val triggerResponseMap = TriggerResponseMap()
    val valuesReflector = ValuesReflector()
    val dignityProtocols = DignityProtocols()
    val situationAnalyzer = SituationAnalyzer()
    val biasInterceptor = BiasInterceptor()
    val allyshipManifesto = AllyshipManifesto()
    val impactLog = ImpactLog()
    val asrManager = ASRManager()
    val ttsManager = TTSManager()
    val documentProtector = DocumentProtector()
    val stateMonitor = StateMonitor()
    val messageDraftManager = MessageDraftManager()
    val toneEngine = ToneEngine()
    val themeManager = ThemeManager()
    val voicePersona = VoicePersona()
    val emotionMeter = EmotionMeter()
}
