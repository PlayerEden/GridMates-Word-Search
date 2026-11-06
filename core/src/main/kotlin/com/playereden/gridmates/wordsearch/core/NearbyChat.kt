package com.playereden.gridmates.wordsearch.core

/**
 * Bounded, ephemeral chat for one trusted nearby two-person room.
 * The host decides message order and replays recent history on reconnect;
 * chat never writes to Solo/Daily profiles or permanent Android backup.
 */
data class NearbyChatLine(
    val sequence: Long,
    val seat: CoopSeat,
    val text: String,
    val sentAtMs: Long
)

enum class NearbyChatStatus { ACCEPTED, DUPLICATE, EMPTY, TOO_FAST, NOT_CONNECTED, BAD_REQUEST }

data class NearbyChatDecision(val status: NearbyChatStatus, val line: NearbyChatLine? = null)

object NearbyChatPolicy {
    const val MAX_CHARS = 140
    const val MAX_HISTORY = 24
    const val MIN_INTERVAL_MS = 1_500L

    fun clean(raw: String): String = raw.asSequence()
        .filterNot { it.isISOControl() && !it.isWhitespace() }
        .joinToString("")
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(MAX_CHARS)
}

class NearbyChatLog {
    private val messages = ArrayDeque<NearbyChatLine>()
    private val lastTime = mutableMapOf<CoopSeat, Long>()
    private val recentIds = mutableMapOf(
        CoopSeat.HOST to LinkedHashSet<String>(),
        CoopSeat.GUEST to LinkedHashSet<String>()
    )
    private var sequence = 0L

    @Synchronized
    fun history(): List<NearbyChatLine> = messages.toList()

    @Synchronized
    fun add(seat: CoopSeat, requestId: String, raw: String, connected: Boolean,
            nowMs: Long): NearbyChatDecision {
        if (!connected) return NearbyChatDecision(NearbyChatStatus.NOT_CONNECTED)
        if (requestId.length !in 1..64 ||
            requestId.any { !(it in 'a'..'z' || it in 'A'..'Z' ||
                it in '0'..'9' || it == '-' || it == '_') })
            return NearbyChatDecision(NearbyChatStatus.BAD_REQUEST)
        val seen = recentIds.getValue(seat)
        if (requestId in seen) return NearbyChatDecision(NearbyChatStatus.DUPLICATE)
        val clean = NearbyChatPolicy.clean(raw)
        if (clean.isEmpty()) return NearbyChatDecision(NearbyChatStatus.EMPTY)
        val prev = lastTime[seat]
        if (prev != null && nowMs - prev < NearbyChatPolicy.MIN_INTERVAL_MS)
            return NearbyChatDecision(NearbyChatStatus.TOO_FAST)
        sequence++
        val line = NearbyChatLine(sequence, seat, clean, nowMs)
        messages.addLast(line)
        while (messages.size > NearbyChatPolicy.MAX_HISTORY) messages.removeFirst()
        lastTime[seat] = nowMs
        seen.add(requestId)
        while (seen.size > 128) seen.remove(seen.first())
        return NearbyChatDecision(NearbyChatStatus.ACCEPTED, line)
    }

    companion object {
        fun validateHistory(lines: List<NearbyChatLine>): List<NearbyChatLine> {
            require(lines.size <= NearbyChatPolicy.MAX_HISTORY)
            require(lines.zipWithNext().all { (a, b) -> a.sequence < b.sequence })
            require(lines.all {
                it.sequence > 0L && it.sentAtMs >= 0L &&
                    it.text == NearbyChatPolicy.clean(it.text) &&
                    it.text.isNotEmpty() && it.text.length <= NearbyChatPolicy.MAX_CHARS
            })
            return lines
        }
    }
}
