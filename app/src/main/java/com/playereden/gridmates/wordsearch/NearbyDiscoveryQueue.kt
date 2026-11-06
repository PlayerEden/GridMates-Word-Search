package com.playereden.gridmates.wordsearch

/**
 * Legacy Android DNS-SD permits only one in-flight resolve on many devices.
 * Queue discovered service names, dedupe repeats, and never resurrect a lost
 * room when an asynchronous resolution returns after onServiceLost.
 *
 * The queue owns no Android resources. The runtime owns the NSD callbacks.
 */
internal class NearbyDiscoveryQueue(private val maxLive: Int = 32) {
    private val live = LinkedHashSet<String>()
    private val waiting = ArrayDeque<String>()
    private val tickets = mutableMapOf<String, Long>()
    private var nextTicket = 0L
    private var active: String? = null

    fun found(name: String): String? {
        if (name.isBlank() || name.length > 120 || live.size >= maxLive ||
            !live.add(name)) return null
        tickets[name] = ++nextTicket
        waiting.addLast(name)
        return startNext()
    }

    fun lost(name: String) {
        live.remove(name)
        tickets.remove(name)
        waiting.removeAll { it == name }
        // A resolve already in progress must complete before another starts.
    }

    fun finished(name: String): String? {
        if (active != name) return null
        active = null
        return startNext()
    }

    fun isLive(name: String): Boolean = name in live

    /** Distinguish a late resolution from a new ad with the same name. */
    fun ticket(name: String): Long? = tickets[name]

    fun reset() {
        live.clear()
        tickets.clear()
        waiting.clear()
        active = null
    }

    private fun startNext(): String? {
        if (active != null) return null
        while (waiting.isNotEmpty()) {
            val name = waiting.removeFirst()
            if (name in live) {
                active = name
                return name
            }
        }
        return null
    }
}
