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
    val sentAtMs: Long,
    /** Stable origin request ID for honest delivery confirmation across reconnect. */
    val requestId: String
)

enum class NearbyChatStatus { ACCEPTED, DUPLICATE, EMPTY, TOO_FAST, NOT_CONNECTED, BAD_REQUEST }

data class NearbyChatDecision(val status: NearbyChatStatus, val line: NearbyChatLine? = null)

/** One outstanding guest message, shown until the host confirms its request ID. */
data class PendingNearbyChat(
    val requestId: String,
    val text: String,
    val needsRetry: Boolean = false
)

object NearbyChatDelivery {
    fun confirmed(pending: PendingNearbyChat?, line: NearbyChatLine): Boolean =
        pending != null && line.seat == CoopSeat.GUEST &&
            line.requestId == pending.requestId

    fun confirmedInHistory(pending: PendingNearbyChat?, history: List<NearbyChatLine>): Boolean =
        pending != null && history.any { confirmed(pending, it) }
}

object NearbyChatPolicy {
    const val MAX_CHARS = 140
    const val MAX_HISTORY = 24
    const val MIN_INTERVAL_MS = 1_500L

    fun validRequestId(id: String): Boolean =
        id.length in 1..64 && id.all {
            it in 'a'..'z' || it in 'A'..'Z' ||
                it in '0'..'9' || it == '-' || it == '_'
        }

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
        CoopSeat.HOST to LinkedHashMap<String, NearbyChatLine>(),
        CoopSeat.GUEST to LinkedHashMap<String, NearbyChatLine>()
    )
    private var sequence = 0L

    @Synchronized
    fun history(): List<NearbyChatLine> = messages.toList()

    @Synchronized
    fun add(seat: CoopSeat, requestId: String, raw: String, connected: Boolean,
            nowMs: Long): NearbyChatDecision {
        if (!connected) return NearbyChatDecision(NearbyChatStatus.NOT_CONNECTED)
        if (!NearbyChatPolicy.validRequestId(requestId))
            return NearbyChatDecision(NearbyChatStatus.BAD_REQUEST)
        val seen = recentIds.getValue(seat)
        seen[requestId]?.let { return NearbyChatDecision(NearbyChatStatus.DUPLICATE, it) }
        val clean = NearbyChatPolicy.clean(raw)
        if (clean.isEmpty()) return NearbyChatDecision(NearbyChatStatus.EMPTY)
        val prev = lastTime[seat]
        if (prev != null && nowMs - prev < NearbyChatPolicy.MIN_INTERVAL_MS)
            return NearbyChatDecision(NearbyChatStatus.TOO_FAST)
        sequence++
        val line = NearbyChatLine(sequence, seat, clean, nowMs, requestId)
        messages.addLast(line)
        while (messages.size > NearbyChatPolicy.MAX_HISTORY) messages.removeFirst()
        lastTime[seat] = nowMs
        seen[requestId] = line
        while (seen.size > 128) seen.remove(seen.keys.first())
        return NearbyChatDecision(NearbyChatStatus.ACCEPTED, line)
    }

    companion object {
        fun validateHistory(lines: List<NearbyChatLine>): List<NearbyChatLine> {
            require(lines.size <= NearbyChatPolicy.MAX_HISTORY)
            require(lines.zipWithNext().all { (a, b) -> a.sequence < b.sequence })
            require(lines.map { it.seat to it.requestId }.distinct().size == lines.size)
            require(lines.all {
                it.sequence > 0L && it.sentAtMs >= 0L &&
                    NearbyChatPolicy.validRequestId(it.requestId) &&
                    it.text == NearbyChatPolicy.clean(it.text) &&
                    it.text.isNotEmpty() && it.text.length <= NearbyChatPolicy.MAX_CHARS
            })
            return lines
        }
    }
}
