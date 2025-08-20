/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Hierarchical memory architecture for enhanced recall and personalization.
 * Got it, love.
 */
package com.sallie.core.memory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * HierarchicalMemorySystem implements Sallie 2.0's advanced memory architecture
 * with episodic, semantic, and emotional memory layers for more human-like recall.
 * 
 * This system builds on Sallie 1.0's MemoryManager while adding multi-layered
 * memory organization, prioritization, and sophisticated retrieval mechanisms.
 */
class HierarchicalMemorySystem {
    
    /**
     * Core memory types in the hierarchical system
     */
    enum class MemoryType {
        EPISODIC,   // Event-based memories tied to specific moments
        SEMANTIC,   // Knowledge and facts independent of specific experiences  
        EMOTIONAL,  // Feelings associated with experiences or knowledge
        PROCEDURAL  // Skills and how to perform tasks
    }
    
    /**
     * Unified memory item structure used across memory types
     */
    data class MemoryItem(
        val id: String,
        val type: MemoryType,
        var content: String,
        var metadata: MutableMap<String, Any> = mutableMapOf(),
        var priority: Int = 50, // 0-100 importance scale
        val created: Long = System.currentTimeMillis(),
        var lastAccessed: Long = System.currentTimeMillis(),
        var accessCount: Int = 0,
        var emotionalValence: Double = 0.0, // -1.0 to 1.0 (negative to positive)
        var emotionalIntensity: Double = 0.5, // 0.0 to 1.0
        var certainty: Double = 1.0, // 0.0 to 1.0
        var connections: MutableSet<String> = mutableSetOf() // IDs of connected memories
    ) {
        fun updateLastAccessed() {
            lastAccessed = System.currentTimeMillis()
            accessCount++
        }
        
        /**
         * Calculate memory salience (likelihood of recall) based on multiple factors
         */
        fun calculateSalience(now: Long = System.currentTimeMillis()): Double {
            val recencyFactor = calculateRecencyFactor(now)
            val emotionalFactor = calculateEmotionalFactor()
            val frequencyFactor = calculateFrequencyFactor()
            val connectionFactor = 1.0 + (connections.size * 0.05) // More connections = more likely to recall
            
            return (priority / 100.0) * recencyFactor * emotionalFactor * frequencyFactor * connectionFactor * certainty
        }
        
        private fun calculateRecencyFactor(now: Long): Double {
            val hoursSinceLastAccess = ChronoUnit.HOURS.between(
                Instant.ofEpochMilli(lastAccessed),
                Instant.ofEpochMilli(now)
            )
            
            // Exponential decay with half-life of ~1 week
            return Math.exp(-0.004 * hoursSinceLastAccess)
        }
        
        private fun calculateEmotionalFactor(): Double {
            // Memories with stronger emotions (positive or negative) are more salient
            return 1.0 + (Math.abs(emotionalValence) * emotionalIntensity)
        }
        
        private fun calculateFrequencyFactor(): Double {
            // More frequently accessed memories have higher salience
            // Log scale to prevent excessive weight for very frequent items
            return 1.0 + (Math.log10(1.0 + accessCount) * 0.2)
        }
    }
    
    /**
     * Memory retrieval query structure
     */
    data class MemoryQuery(
        val searchText: String = "",
        val types: Set<MemoryType> = setOf(),
        val minCertainty: Double = 0.0,
        val emotionalFilter: Pair<Double, Double>? = null, // Range of emotional valence
        val contextTags: Set<String> = setOf(),
        val limit: Int = 10,
        val includeSalience: Boolean = true
    )
    
    /**
     * Memory retrieval result with metadata
     */
    data class MemoryQueryResult(
        val items: List<MemoryItem>,
        val totalMatches: Int,
        val retrievalLatency: Long
    )
    
    // Memory stores for different types
    private val episodicMemories = ConcurrentHashMap<String, MemoryItem>()
    private val semanticMemories = ConcurrentHashMap<String, MemoryItem>()
    private val emotionalMemories = ConcurrentHashMap<String, MemoryItem>()
    private val proceduralMemories = ConcurrentHashMap<String, MemoryItem>()
    
    // Memory indices for faster retrieval
    private val contentIndex = ConcurrentHashMap<String, MutableSet<String>>()
    private val tagIndex = ConcurrentHashMap<String, MutableSet<String>>()
    private val timeIndex = sortedMapOf<Long, MutableSet<String>>()
    private val emotionIndex = ConcurrentHashMap<String, MutableSet<String>>() // Emotion categories -> memory IDs
    
    /**
     * Store a new memory item in the appropriate memory store
     */
    fun storeMemory(item: MemoryItem): String {
        val store = getStoreForType(item.type)
        store[item.id] = item
        
        // Update indices
        updateContentIndex(item)
        updateTagIndex(item)
        updateTimeIndex(item)
        updateEmotionIndex(item)
        
        return item.id
    }
    
    /**
     * Retrieve a memory by ID
     */
    fun getMemory(id: String): MemoryItem? {
        for (store in listOf(episodicMemories, semanticMemories, emotionalMemories, proceduralMemories)) {
            store[id]?.let { 
                it.updateLastAccessed()
                return it
            }
        }
        return null
    }
    
    /**
     * Search for memories matching the query
     */
    fun searchMemories(query: MemoryQuery): MemoryQueryResult {
        val startTime = System.currentTimeMillis()
        
        // Determine which stores to search based on query
        val storesToSearch = if (query.types.isEmpty()) {
            listOf(episodicMemories, semanticMemories, emotionalMemories, proceduralMemories)
        } else {
            query.types.map { getStoreForType(it) }
        }
        
        // Collect candidate memories
        val candidates = mutableListOf<MemoryItem>()
        
        // First try indexed search if we have search text
        if (query.searchText.isNotEmpty()) {
            val keywords = query.searchText.lowercase().split(Regex("\\s+"))
            val matchingIds = mutableSetOf<String>()
            
            for (keyword in keywords) {
                contentIndex.keys
                    .filter { it.contains(keyword) }
                    .forEach { key -> 
                        contentIndex[key]?.let { matchingIds.addAll(it) }
                    }
            }
            
            for (id in matchingIds) {
                getMemory(id)?.let { candidates.add(it) }
            }
        } else {
            // If no search text, collect all memories from relevant stores
            for (store in storesToSearch) {
                candidates.addAll(store.values)
            }
        }
        
        // Apply filters
        val filtered = candidates.filter { item ->
            (query.minCertainty <= 0.0 || item.certainty >= query.minCertainty) &&
            (query.emotionalFilter == null || 
                (item.emotionalValence >= query.emotionalFilter.first && 
                 item.emotionalValence <= query.emotionalFilter.second)) &&
            (query.contextTags.isEmpty() || 
                query.contextTags.any { tag -> item.metadata["tags"]?.toString()?.contains(tag) == true })
        }
        
        // Sort by salience if requested
        val sorted = if (query.includeSalience) {
            filtered.sortedByDescending { it.calculateSalience() }
        } else {
            filtered
        }
        
        // Take top results
        val results = sorted.take(query.limit)
        
        // Update last accessed for returned items
        results.forEach { it.updateLastAccessed() }
        
        return MemoryQueryResult(
            items = results,
            totalMatches = filtered.size,
            retrievalLatency = System.currentTimeMillis() - startTime
        )
    }
    
    /**
     * Connect two memory items, creating associative links
     */
    fun connectMemories(sourceId: String, targetId: String) {
        val source = getMemory(sourceId)
        val target = getMemory(targetId)
        
        if (source != null && target != null) {
            source.connections.add(targetId)
            target.connections.add(sourceId)
            
            // Update the stores
            storeMemory(source)
            storeMemory(target)
        }
    }
    
    /**
     * Consolidate memories (simulate sleep-based memory consolidation)
     * This reinforces important memories and weakens less significant ones
     */
    fun consolidateMemories() {
        val now = System.currentTimeMillis()
        
        // Process each memory store
        for (store in listOf(episodicMemories, semanticMemories, emotionalMemories, proceduralMemories)) {
            for (item in store.values) {
                val salience = item.calculateSalience(now)
                
                // Reinforce important and recent memories
                if (salience > 0.7) {
                    item.priority = Math.min(100, (item.priority * 1.05).toInt())
                    item.certainty = Math.min(1.0, item.certainty * 1.02)
                }
                
                // Weaken old, rarely accessed memories
                val daysSinceAccess = ChronoUnit.DAYS.between(
                    Instant.ofEpochMilli(item.lastAccessed),
                    Instant.ofEpochMilli(now)
                )
                
                if (daysSinceAccess > 30 && item.accessCount < 3 && salience < 0.3) {
                    item.priority = Math.max(1, (item.priority * 0.95).toInt())
                    item.certainty = Math.max(0.1, item.certainty * 0.98)
                }
            }
        }
    }
    
    /**
     * Get related memories based on connections and content similarity
     */
    fun getRelatedMemories(memoryId: String, limit: Int = 5): List<MemoryItem> {
        val memory = getMemory(memoryId) ?: return emptyList()
        
        // First get directly connected memories
        val connected = memory.connections
            .mapNotNull { getMemory(it) }
            .sortedByDescending { it.calculateSalience() }
        
        // If we have enough connected memories, return them
        if (connected.size >= limit) {
            return connected.take(limit)
        }
        
        // Otherwise, find content-similar memories
        val keywords = memory.content.lowercase().split(Regex("\\s+"))
            .filter { it.length > 3 }
            .take(5)
        
        val contentSimilar = keywords
            .flatMap { keyword -> 
                contentIndex.keys
                    .filter { it.contains(keyword) }
                    .flatMap { key -> contentIndex[key] ?: emptySet() }
            }
            .distinct()
            .mapNotNull { getMemory(it) }
            .filter { it.id != memoryId && !memory.connections.contains(it.id) }
            .sortedByDescending { it.calculateSalience() }
        
        // Combine connected and content-similar, prioritizing connected
        return (connected + contentSimilar).distinct().take(limit)
    }
    
    /**
     * Clean up old, low-salience memories to prevent unlimited growth
     */
    fun cleanupMemories(maxMemories: Int = 10000, minSalience: Double = 0.1) {
        val allMemories = (episodicMemories.values + semanticMemories.values + 
                         emotionalMemories.values + proceduralMemories.values)
                        .sortedBy { it.calculateSalience() }
        
        // If we're over the limit, remove low salience memories
        if (allMemories.size > maxMemories) {
            val toRemove = allMemories
                .filter { it.calculateSalience() < minSalience }
                .take(allMemories.size - maxMemories)
            
            for (item in toRemove) {
                removeMemory(item.id)
            }
        }
    }
    
    /**
     * Remove a memory from the system
     */
    private fun removeMemory(id: String) {
        // Remove from all stores
        episodicMemories.remove(id)
        semanticMemories.remove(id)
        emotionalMemories.remove(id)
        proceduralMemories.remove(id)
        
        // Remove from indices
        for (index in contentIndex.values) {
            index.remove(id)
        }
        
        for (index in tagIndex.values) {
            index.remove(id)
        }
        
        for (index in timeIndex.values) {
            index.remove(id)
        }
        
        for (index in emotionIndex.values) {
            index.remove(id)
        }
        
        // Remove connections in other memories
        for (store in listOf(episodicMemories, semanticMemories, emotionalMemories, proceduralMemories)) {
            for (memory in store.values) {
                memory.connections.remove(id)
            }
        }
    }
    
    /**
     * Get the appropriate store for a given memory type
     */
    private fun getStoreForType(type: MemoryType): ConcurrentHashMap<String, MemoryItem> {
        return when (type) {
            MemoryType.EPISODIC -> episodicMemories
            MemoryType.SEMANTIC -> semanticMemories
            MemoryType.EMOTIONAL -> emotionalMemories
            MemoryType.PROCEDURAL -> proceduralMemories
        }
    }
    
    /**
     * Update the content index with keywords from the memory
     */
    private fun updateContentIndex(item: MemoryItem) {
        val keywords = item.content.lowercase().split(Regex("\\s+"))
            .filter { it.length > 3 }
        
        for (keyword in keywords) {
            contentIndex.computeIfAbsent(keyword) { mutableSetOf() }.add(item.id)
        }
    }
    
    /**
     * Update the tag index for the memory
     */
    private fun updateTagIndex(item: MemoryItem) {
        val tags = item.metadata["tags"] as? List<String> ?: return
        
        for (tag in tags) {
            tagIndex.computeIfAbsent(tag) { mutableSetOf() }.add(item.id)
        }
    }
    
    /**
     * Update the time index for chronological retrieval
     */
    private fun updateTimeIndex(item: MemoryItem) {
        val timeKey = item.created / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000) // Round to day
        timeIndex.computeIfAbsent(timeKey) { mutableSetOf() }.add(item.id)
    }
    
    /**
     * Update the emotion index for emotional filtering
     */
    private fun updateEmotionIndex(item: MemoryItem) {
        // Map emotional valence to categories
        val categories = when {
            item.emotionalValence > 0.7 -> listOf("very_positive")
            item.emotionalValence > 0.3 -> listOf("positive")
            item.emotionalValence > -0.3 -> listOf("neutral")
            item.emotionalValence > -0.7 -> listOf("negative")
            else -> listOf("very_negative")
        }
        
        // Map emotional intensity
        val intensityCategory = when {
            item.emotionalIntensity > 0.7 -> "high_intensity"
            item.emotionalIntensity > 0.3 -> "medium_intensity"
            else -> "low_intensity"
        }
        
        val allCategories = categories + intensityCategory
        
        for (category in allCategories) {
            emotionIndex.computeIfAbsent(category) { mutableSetOf() }.add(item.id)
        }
    }
    
    /**
     * Generate a unique ID for a memory
     */
    fun generateMemoryId(): String {
        return "mem_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
    }
    
    /**
     * Create a memory factory method for episodic memories (event-based)
     */
    fun createEpisodicMemory(
        content: String,
        priority: Int = 50,
        emotionalValence: Double = 0.0,
        emotionalIntensity: Double = 0.5,
        metadata: Map<String, Any> = emptyMap()
    ): MemoryItem {
        val id = generateMemoryId()
        return MemoryItem(
            id = id,
            type = MemoryType.EPISODIC,
            content = content,
            priority = priority,
            emotionalValence = emotionalValence,
            emotionalIntensity = emotionalIntensity,
            metadata = metadata.toMutableMap()
        )
    }
    
    /**
     * Create a memory factory method for semantic memories (knowledge/facts)
     */
    fun createSemanticMemory(
        content: String,
        certainty: Double = 1.0,
        priority: Int = 50,
        metadata: Map<String, Any> = emptyMap()
    ): MemoryItem {
        val id = generateMemoryId()
        return MemoryItem(
            id = id,
            type = MemoryType.SEMANTIC,
            content = content,
            priority = priority,
            certainty = certainty,
            metadata = metadata.toMutableMap()
        )
    }
    
    /**
     * Create a memory factory method for emotional memories
     */
    fun createEmotionalMemory(
        content: String,
        emotionalValence: Double,
        emotionalIntensity: Double,
        priority: Int = 60, // Emotional memories typically higher priority
        metadata: Map<String, Any> = emptyMap()
    ): MemoryItem {
        val id = generateMemoryId()
        return MemoryItem(
            id = id,
            type = MemoryType.EMOTIONAL,
            content = content,
            priority = priority,
            emotionalValence = emotionalValence,
            emotionalIntensity = emotionalIntensity,
            metadata = metadata.toMutableMap()
        )
    }
    
    /**
     * Create a memory factory method for procedural memories (skills/abilities)
     */
    fun createProceduralMemory(
        content: String,
        priority: Int = 50,
        proficiency: Double = 0.5, // 0.0-1.0 skill level
        metadata: Map<String, Any> = emptyMap()
    ): MemoryItem {
        val id = generateMemoryId()
        val metadataWithProficiency = metadata.toMutableMap().apply {
            this["proficiency"] = proficiency
        }
        
        return MemoryItem(
            id = id,
            type = MemoryType.PROCEDURAL,
            content = content,
            priority = priority,
            metadata = metadataWithProficiency
        )
    }
}
