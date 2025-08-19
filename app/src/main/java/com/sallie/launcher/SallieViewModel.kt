package com.sallie.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sallie.feature.FeatureRegistry
import com.sallie.core.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SallieViewModel(app: Application) : AndroidViewModel(app) {
    val system = SallieSystem()
    private val persistence = SalliePersistence(app.applicationContext)

    // Conversation & silence tracking additions
    private val _conversation = MutableStateFlow<List<ConversationEntry>>(emptyList())
    val conversation: StateFlow<List<ConversationEntry>> = _conversation
    private var lastSpeechTs: Long = 0L
    private val silenceTimeoutMs = 3000L
    private val _asrError = MutableStateFlow<String?>(null)
    val asrError: StateFlow<String?> = _asrError
    private var lastRmsEmitMs: Long = 0L
    private val platformASR =
        PlatformASR(
            app,
            onPartial = { text ->
                lastSpeechTs = System.currentTimeMillis()
                system.asrManager.ingestTranscript(text, isFinal = false)
                appendLog("ASR partial: $text")
            },
            onFinal = { text ->
                lastSpeechTs = System.currentTimeMillis()
                system.asrManager.ingestTranscript(text, isFinal = true)
                val entry = ConversationEntry(System.currentTimeMillis(), "user", text)
                _conversation.value = (_conversation.value + entry).takeLast(100)
                persistence.saveConversationEntries(_conversation.value)
                appendLog("ASR final: $text")
                // Auto-generate Sallie reply
                viewModelScope.launch {
                    val reply = generateSallieReply(text)
                    val resp = ConversationEntry(System.currentTimeMillis(), "sallie", reply)
                    _conversation.value = (_conversation.value + resp).takeLast(100)
                    persistence.saveConversationEntries(_conversation.value)
                    appendLog("Reply generated")
                    // Optionally speak response
                    val spoken = system.ttsManager.speak(reply)
                    platformTTS.speak(reply)
                    appendLog(spoken)
                }
            },
            onRms = { level ->
                system.asrManager.setRms(level)
                val now = System.currentTimeMillis()
                if (now - lastRmsEmitMs > 50) { // throttle to ~20Hz
                    system.asrManager.pushRms(level)
                    lastRmsEmitMs = now
                }
                checkSilence()
            },
            onState = { st ->
                appendLog("ASR:$st")
                if (st.startsWith("error:")) _asrError.value = st.removePrefix("error:")
            },
        )
    private val platformTTS = PlatformTTS(app) { st -> appendLog(st) }

    private val _mood = MutableStateFlow(system.emotionalContext.currentMood)
    val mood: StateFlow<String> = _mood

    private val _fatigue = MutableStateFlow(system.emotionalContext.fatigueLevel)
    val fatigue: StateFlow<Int> = _fatigue

    // Stubs for missing properties and functions
    val currentMood: String get() = _mood.value
    val fatigueLevel: Int get() = _fatigue.value

    // Stubs for ASRManager functions
    object asrManagerStub {
        fun ingestTranscript(text: String, isFinal: Boolean) {}
        fun setRms(level: Float) {}
        fun pushRms(level: Float) {}
        fun stopListening() {}
        fun startListening() {}
        fun getTranscript(): String = ""
        fun allFinal(): List<String> = emptyList()
        fun currentPartial(): String = ""
    }

    // Stubs for TaskOrchestrator functions
    object taskOrchestratorStub {
        fun addTask(id: String, name: String, urgency: Int, importance: Int, effortMinutes: Int) {}
        fun selectTasks(maxStress: Int, maxTotalMinutes: Int): List<Task> = emptyList()
    }

    // Stubs for ThemeManager
    object themeManagerStub {
        fun suggestTheme(mood: String): String = "Default Theme"
    }

    // Stubs for SituationAnalyzer
    object situationAnalyzerStub {
        val tags: List<String> = emptyList()
        val emotionalWeight: Float = 0f
        val recommendation: String = ""
        fun analyzeSituation(input: String): SituationAnalyzerStub = SituationAnalyzerStub()
        class SituationAnalyzerStub {
            val tags: List<String> = emptyList()
            val emotionalWeight: Float = 0f
            val recommendation: String = ""
        }
    }

    // Stubs for DignityProtocols
    object dignityProtocolsStub {
        fun enforceProtocol(action: String) {}
        fun audit(): List<String> = emptyList()
    }

    // Stubs for DeviceControlManager
    object deviceControlStub {
        fun revokePermission(permission: String) {}
        fun grantPermission(permission: String) {}
        fun makeCall(contact: String): String = "Call made"
    }

    // Stubs for TTSManager
    object ttsManagerStub {
        fun speak(text: String): String = "Spoken: $text"
        fun switchVoice(profile: String): String = "Voice switched"
        fun getVoice(): String = "default"
    }

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _situation = MutableStateFlow("")
    val situation: StateFlow<String> = _situation

    private val _dignityEvents = MutableStateFlow(0)
    val dignityEvents: StateFlow<Int> = _dignityEvents

    private val _log = MutableStateFlow<List<String>>(emptyList())
    val log: StateFlow<List<String>> = _log

    private val _theme = MutableStateFlow("Default Theme")
    val theme: StateFlow<String> = _theme

    private val _listening = MutableStateFlow(false)
    val listening: StateFlow<Boolean> = _listening

    private val _voice = MutableStateFlow("default")
    val voice: StateFlow<String> = _voice

    private val _featureMetrics = MutableStateFlow<Map<String, Any>>(emptyMap())
    val featureMetrics: StateFlow<Map<String, Any>> = _featureMetrics

    private val _persona = MutableStateFlow("creator")
    val persona: StateFlow<String> = _persona
    private val _season = MutableStateFlow("spring")
    val season: StateFlow<String> = _season
    private val _event = MutableStateFlow<String?>(null)
    val event: StateFlow<String?> = _event

    private fun appendLog(entry: String) {
        _log.value = (_log.value + "${System.currentTimeMillis() % 100000} | $entry").takeLast(60)
    }

    fun appendLogExternal(entry: String) = appendLog(entry)

    private fun generateSallieReply(userText: String): String {
        // Simple heuristic using core engines
        val mood = system.emotionalContext.currentMood
    val memorySnippet = system.memory.fetchRecentMemories(1).joinToString().ifBlank { "none" }
        val insight = system.insightSynthesizer.synthesize(userText, memorySnippet)
        return "($mood) ${insight.take(120)}"
    }

    companion object {
        var instance: SallieViewModel? = null
    }

    init {
        instance = this
        persistence.loadMood()?.let { m ->
            system.emotionalContext.updateMood(m)
            _mood.value = m
            _theme.value = system.themeManager.suggestTheme(m)
        }
        persistence.loadFatigue()?.let { f -> _fatigue.value = f }
        persistence.loadTheme()?.let { t -> _theme.value = t }
        val convo = persistence.loadConversationEntries()
        if (convo.isNotEmpty()) _conversation.value = convo.takeLast(100)
    }

    fun updateMood(newMood: String) {
        system.emotionalContext.updateMood(newMood)
        _mood.value = newMood
        _theme.value = system.themeManager.suggestTheme(newMood)
        persistence.saveMood(newMood)
        persistence.saveTheme(_theme.value)
        appendLog("Mood -> $newMood")
    }

    fun setFatigue(level: Int) {
        system.emotionalContext.updateFatigue(level)
        _fatigue.value = level
        persistence.saveFatigue(level)
        appendLog("Fatigue -> $level")
    }

    fun seedTasks() {
        val orchestrator = system.taskOrchestrator
        orchestrator.addTask("1", "Email triage", urgency = 3, importance = 2, effortMinutes = 10)
        orchestrator.addTask("2", "Deep Work Block", urgency = 2, importance = 5, effortMinutes = 50)
        orchestrator.addTask("3", "Quick Sync", urgency = 5, importance = 3, effortMinutes = 15)
        _tasks.value = orchestrator.selectTasks(maxStress = 5, maxTotalMinutes = 60)
        persistence.saveTasks(_tasks.value.map { it.id })
        appendLog("Tasks planned -> ${_tasks.value.size}")
    }

    fun analyzeSituation(input: String) {
        val res = system.situationAnalyzer.analyzeSituation(input)
        _situation.value = "tags=${res.tags} weight=${res.emotionalWeight} rec=${res.recommendation}"
        appendLog("Situation -> ${res.tags}")
    }

    fun enforceDignity(action: String) {
        system.dignityProtocols.enforceProtocol(action)
        _dignityEvents.value = system.dignityProtocols.audit().size
        appendLog("Dignity enforce -> $action")
    }

    fun simulateDeviceAction(blocked: Boolean) {
        if (blocked) system.deviceControl.revokePermission("call") else system.deviceControl.grantPermission("call")
        val result = system.deviceControl.makeCall("Demo Contact")
        appendLog(result)
    }

    fun heartbeat() {
        viewModelScope.launch { appendLog("Heartbeat") }
    }

    fun refreshMetrics() {
        _featureMetrics.value = FeatureRegistry.summary()
        appendLog("Metrics refreshed")
    }

    fun toggleListening() {
        val asr = system.asrManager
        if (_listening.value) {
            platformASR.stop()
            asr.stopListening()
            _listening.value = false
            appendLog("ASR stopped")
        } else {
            asr.startListening()
            platformASR.start()
            _listening.value = true
            lastSpeechTs = System.currentTimeMillis()
            appendLog("ASR started")
        }
    }

    fun captureTranscript() {
        val t = system.asrManager.getTranscript()
        appendLog("Transcript: $t | finals=${system.asrManager.allFinal().size}")
    }

    fun speak(text: String) {
        val res = system.ttsManager.speak(text)
        platformTTS.speak(text)
        appendLog(res)
    }

    fun switchVoice(profile: String) {
        val res = system.ttsManager.switchVoice(profile)
        _voice.value = system.ttsManager.getVoice()
        platformTTS.switchVoice(profile)
        appendLog(res)
    }

    fun clearAsrError() {
        _asrError.value = null
    }

    private fun checkSilence() {
        if (_listening.value) {
            val now = System.currentTimeMillis()
            if (now - lastSpeechTs > silenceTimeoutMs && system.asrManager.currentPartial().isBlank()) {
                platformASR.stop()
                system.asrManager.stopListening()
                _listening.value = false
                appendLog("ASR auto-stopped (silence)")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        platformASR.destroy()
        platformTTS.shutdownEngine()
    }

    fun exportConversationCsv(): String = persistence.exportConversation(conversation.value)

    fun getConversationExport(): String = exportConversationCsv()

    fun exportConversationJson(
        speaker: String? = null,
        startTs: Long? = null,
        endTs: Long? = null,
        limit: Int? = null,
    ): String = persistence.exportConversationJson(conversation.value, speaker, startTs, endTs, limit)

    /**
     * Selects theme and icon set based on mood, season, and event, per Salle's constitution.
     */
    fun selectVisualState(
        mood: String,
        season: String,
        event: String? = null,
    ): Pair<String, String> {
        return when (mood.lowercase()) {
            "grace" ->
                when (season.lowercase()) {
                    "spring" -> "Grace & Grind" to if (event == "launch_day") "grace_spring_launch" else "grace_spring_base"
                    "summer" -> "Grace & Grind" to if (event == "anniversary") "grace_summer_anniv" else "grace_summer_base"
                    else -> "Grace & Grind" to "grace_default"
                }
            "hustle" ->
                when (season.lowercase()) {
                    "winter" -> "Hustle Legacy" to "hustle_winter_base"
                    "fall" -> "Hustle Legacy" to if (event == "big_win") "hustle_fall_bigwin" else "hustle_fall_base"
                    else -> "Hustle Legacy" to "hustle_default"
                }
            "soul care" ->
                when (season.lowercase()) {
                    "winter" -> "Soul Care" to if (event == "self_care_week") "soul_winter_selfcare" else "soul_winter_base"
                    "spring" -> "Soul Care" to "soul_spring_base"
                    else -> "Soul Care" to "soul_default"
                }
            "grit" ->
                when (season.lowercase()) {
                    "summer" -> "Southern Grit" to "grit_summer_base"
                    "fall" -> "Southern Grit" to if (event == "fundraiser") "grit_fall_fundraiser" else "grit_fall_base"
                    else -> "Southern Grit" to "grit_default"
                }
            "midnight" ->
                when (season.lowercase()) {
                    "winter" -> "Midnight Hustle" to "midnight_winter_base"
                    "summer" -> "Midnight Hustle" to if (event == "hackathon") "midnight_summer_hackathon" else "midnight_summer_base"
                    else -> "Midnight Hustle" to "midnight_default"
                }
            "visionary" ->
                when (season.lowercase()) {
                    "spring" -> "Visionary" to if (event == "product_launch") "visionary_spring_launch" else "visionary_spring_base"
                    "fall" -> "Visionary" to if (event == "innovation_day") "visionary_fall_innovation" else "visionary_fall_base"
                    else -> "Visionary" to "visionary_default"
                }
            "guardian" ->
                when (season.lowercase()) {
                    "winter" -> "Guardian" to if (event == "security_week") "guardian_winter_security" else "guardian_winter_base"
                    "summer" -> "Guardian" to if (event == "family_reunion") "guardian_summer_reunion" else "guardian_summer_base"
                    else -> "Guardian" to "guardian_default"
                }
            "mentor" ->
                when (season.lowercase()) {
                    "spring" -> "Mentor" to if (event == "graduation") "mentor_spring_graduation" else "mentor_spring_base"
                    "fall" -> "Mentor" to if (event == "mentorship_week") "mentor_fall_mentorship" else "mentor_fall_base"
                    else -> "Mentor" to "mentor_default"
                }
            "rebel" ->
                when (season.lowercase()) {
                    "summer" -> "Rebel" to if (event == "protest") "rebel_summer_protest" else "rebel_summer_base"
                    "winter" -> "Rebel" to if (event == "rule_break") "rebel_winter_rulebreak" else "rebel_winter_base"
                    else -> "Rebel" to "rebel_default"
                }
            "explorer" ->
                when (season.lowercase()) {
                    "spring" -> "Explorer" to if (event == "expedition") "explorer_spring_expedition" else "explorer_spring_base"
                    "fall" -> "Explorer" to if (event == "discovery_day") "explorer_fall_discovery" else "explorer_fall_base"
                    else -> "Explorer" to "explorer_default"
                }
            "healer" ->
                when (season.lowercase()) {
                    "winter" -> "Healer" to if (event == "wellness_retreat") "healer_winter_retreat" else "healer_winter_base"
                    "summer" -> "Healer" to if (event == "self_care_month") "healer_summer_selfcare" else "healer_summer_base"
                    else -> "Healer" to "healer_default"
                }
            else -> "Grace & Grind" to "grace_default"
        }
    }

    /**
     * Triggers icon generation using the Python pipeline.
     */
    private fun generateIcon(
        persona: String,
        mood: String,
        season: String,
        event: String? = null,
    ) {
        val eventArg = event ?: ""
        try {
            val cmd = arrayOf("python3", "app/icon_pipeline/icon_pipeline.py", persona, mood, season, eventArg)
            Runtime.getRuntime().exec(cmd)
            appendLog("Icon generation triggered: ${cmd.joinToString(" ")}")
        } catch (e: Exception) {
            appendLog("Icon generation failed: ${e.message}")
        }
    }

    /**
     * Applies theme and icon in sync, respecting Salle's constitution.
     */
    fun applyVisualState(
        persona: String,
        mood: String,
        season: String,
        event: String? = null,
    ) {
        val (theme, iconSet) = selectVisualState(mood, season, event)
        _theme.value = theme
        generateIcon(persona, mood, season, event)
        appendLog("Visual state applied: theme=$theme, iconSet=$iconSet")
    }

    /**
     * Schedules seasonal/event-based visual state updates using WorkManager (pseudo-code).
     */
    fun scheduleVisualStateUpdates() {
        // Use WorkManager or AlarmManager for real scheduling
        // Example: schedule applyVisualState for season changes, holidays, milestones
        // WorkManager.enqueue(VisualStateWorker(...))
        appendLog("Visual state scheduling scaffolded.")
    }

    /**
     * Example: Automatically update visual state for seasonal/event triggers.
     */
    fun autoUpdateVisualState() {
        val persona = persona.value
        val mood = mood.value
        val season = getCurrentSeason()
        val event = getCurrentEvent()
        applyVisualState(persona, mood, season, event)
    }

    private fun getCurrentSeason(): String {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        return when (month) {
            in 2..4 -> "spring"
            in 5..7 -> "summer"
            in 8..10 -> "fall"
            else -> "winter"
        }
    }

    private fun getCurrentEvent(): String? {
        val today = java.time.LocalDate.now()
        return when {
            today.monthValue == 12 && today.dayOfMonth == 25 -> "family_reunion"
            today.monthValue == 6 && today.dayOfMonth == 1 -> "product_launch"
            // Add more event logic here
            else -> null
        }
    }

    fun setPersona(value: String) {
        _persona.value = value
    }

    fun setSeason(value: String) {
        _season.value = value
    }

    fun setEvent(value: String?) {
        _event.value = value
    }
}
