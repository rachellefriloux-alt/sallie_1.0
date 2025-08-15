package com.sallie.launcher

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.sallie.feature.TaskOrchestrator.Task
import com.sallie.feature.FeatureRegistry

class SallieViewModel(app: Application) : AndroidViewModel(app) {
    private val system = SallieSystem()
    private val persistence = SalliePersistence(app.applicationContext)
    // Conversation & silence tracking additions
    private val _conversation = MutableStateFlow<List<ConversationEntry>>(emptyList())
    val conversation: StateFlow<List<ConversationEntry>> = _conversation
    private var lastSpeechTs: Long = 0L
    private val silenceTimeoutMs = 3000L
    private val _asrError = MutableStateFlow<String?>(null)
    val asrError: StateFlow<String?> = _asrError
    private var lastRmsEmitMs: Long = 0L
    private val platformASR = PlatformASR(
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
        }
    )
    private val platformTTS = PlatformTTS(app) { st -> appendLog(st) }

    private val _mood = MutableStateFlow(system.emotionalContext.currentMood)
    val mood: StateFlow<String> = _mood

    private val _fatigue = MutableStateFlow(system.emotionalContext.fatigueLevel)
    val fatigue: StateFlow<Int> = _fatigue

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

    private fun appendLog(entry: String) {
        _log.value = (_log.value + "${System.currentTimeMillis()%100000} | $entry").takeLast(60)
    }

    fun appendLogExternal(entry: String) = appendLog(entry)

    private fun generateSallieReply(userText: String): String {
        // Simple heuristic using core engines
        val mood = system.emotionalContext.currentMood
        val memorySnippet = system.memoryManager.fetchRecentMemories(1).joinToString().ifBlank { "none" }
        val insight = system.insightSynthesizer.synthesize(userText, memorySnippet)
        return "($mood) ${insight.take(120)}"
    }

    init {
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

    fun refreshMetrics() { _featureMetrics.value = FeatureRegistry.summary(); appendLog("Metrics refreshed") }

    fun toggleListening() {
        val asr = system.asrManager
        if (_listening.value) {
            platformASR.stop(); asr.stopListening(); _listening.value = false; appendLog("ASR stopped")
        } else {
            asr.startListening(); platformASR.start(); _listening.value = true; lastSpeechTs = System.currentTimeMillis(); appendLog("ASR started")
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

    fun clearAsrError() { _asrError.value = null }

    private fun checkSilence() {
        if (_listening.value) {
            val now = System.currentTimeMillis()
            if (now - lastSpeechTs > silenceTimeoutMs && system.asrManager.currentPartial().isBlank()) {
                platformASR.stop(); system.asrManager.stopListening(); _listening.value = false
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
        limit: Int? = null
    ): String = persistence.exportConversationJson(conversation.value, speaker, startTs, endTs, limit)
}
