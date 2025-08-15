package com.sallie.core

// Persistent memory using Firestore (placeholder)
class MemoryManager {
    private val quickCapture = mutableListOf<String>()
    private val personalContext = mutableMapOf<String, String>()
    private val memoryHistory = mutableListOf<Pair<String, String>>()

    fun remember(key: String, value: String) {
        personalContext[key] = value
        memoryHistory.add(Pair(key, value))
        // Future: sync to cloud, analytics, emotional triggers
    }

    fun recall(key: String): String? = personalContext[key]
    fun addQuickCapture(item: String) {
        quickCapture.add(item)
        // Future: context-aware quick capture, sync
    }
    fun getQuickCaptures(): List<String> = quickCapture
    fun getMemoryHistory(): List<Pair<String, String>> = memoryHistory
    fun fetchRecentMemories(limit: Int): List<String> = memoryHistory.takeLast(limit).map { "${it.first}=${it.second}" }

    // Future: add hooks for AI, device, cloud integrations
}
