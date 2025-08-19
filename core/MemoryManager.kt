package com.sallie.core

// Persistent memory using Firestore (placeholder)
class MemoryManager {
    data class MemoryItem(
        val key: String,
        var value: String,
        var priority: Int = 50, // 0..100 user / system assigned importance
        val created: Long = System.currentTimeMillis(),
        var lastAccess: Long = System.currentTimeMillis()
    ) {
        fun ageMillis(now: Long = System.currentTimeMillis()) = now - created
        fun sinceLastAccess(now: Long = System.currentTimeMillis()) = now - lastAccess
        fun decayFactor(now: Long = System.currentTimeMillis()): Double {
            // Exponential decay based on age; half-life ~ 7 days
            val halfLifeMs = 7 * 24 * 60 * 60 * 1000.0
            val decay = Math.pow(0.5, ageMillis(now) / halfLifeMs)
            return decay
        }
        fun effectiveScore(now: Long = System.currentTimeMillis()): Double {
            // Blend priority with freshness (recent accesses boost)
            val freshnessBoost = if (sinceLastAccess(now) < 6 * 60 * 60 * 1000) 1.15 else 1.0
            return priority * decayFactor(now) * freshnessBoost
        }
    }

    private val quickCapture = mutableListOf<String>()
    private val personalContext = mutableMapOf<String, MemoryItem>()
    private val memoryHistory = mutableListOf<MemoryItem>()

    // Configuration
    var maxItems: Int = 500
    var pruneTarget: Int = 450

    fun remember(key: String, value: String, priority: Int = 50) {
        val clamped = priority.coerceIn(0,100)
        val existing = personalContext[key]
        if (existing != null) {
            existing.value = value
            existing.priority = clamped
            existing.lastAccess = System.currentTimeMillis()
            memoryHistory.add(existing.copy())
        } else {
            val item = MemoryItem(key, value, clamped)
            personalContext[key] = item
            memoryHistory.add(item.copy())
        }
        enforceCapacity()
    }

    fun boost(key: String, delta: Int = 5) {
        personalContext[key]?.let {
            it.priority = (it.priority + delta).coerceIn(0,100)
            it.lastAccess = System.currentTimeMillis()
        }
    }

    fun recall(key: String): String? {
        val item = personalContext[key]
        if (item != null) item.lastAccess = System.currentTimeMillis()
        return item?.value
    }

    fun addQuickCapture(item: String) {
        quickCapture.add(item)
        // Potential future classification of capture importance.
    }
    fun getQuickCaptures(): List<String> = quickCapture
    fun getMemoryHistory(): List<Pair<String, String>> = memoryHistory
    fun fetchRecentMemories(limit: Int): List<String> = memoryHistory.takeLast(limit).map { "${it.first}=${it.second}" }
    fun getMemoryHistory(): List<MemoryItem> = memoryHistory

    fun topMemories(limit: Int = 10): List<MemoryItem> {
        val now = System.currentTimeMillis()
        return personalContext.values
            .sortedByDescending { it.effectiveScore(now) }
            .take(limit)
    }

    fun pruneAged(now: Long = System.currentTimeMillis()) {
        if (personalContext.size <= maxItems) return
        val sorted = personalContext.values.sortedBy { it.effectiveScore(now) }
        val toRemove = personalContext.size - pruneTarget
        sorted.take(toRemove).forEach { personalContext.remove(it.key) }
    }

    private fun enforceCapacity() {
        if (personalContext.size > maxItems) pruneAged()
    }
}
