package com.playereden.gridmates.wordsearch.core

/** A recipient-scoped view, NOT an open list of both players' discoveries.
 * The host alone owns both private ledgers. Each phone receives only its own
 * found word IDs; the opponent is represented by a count, never by paths.
 */
data class DuelView(
    val revision: Long,
    val recipient: CoopSeat,
    val hostReady: Boolean,
    val guestReady: Boolean,
    val started: Boolean,
    val finished: Boolean,
    val winner: CoopSeat?,
    val hostCount: Int,
    val guestCount: Int,
    val ownFoundIds: Set<String>
) {
    val ownCount: Int get() = if (recipient == CoopSeat.HOST) hostCount else guestCount
    val opponentCount: Int get() = if (recipient == CoopSeat.HOST) guestCount else hostCount
    val ownReady: Boolean get() = if (recipient == CoopSeat.HOST) hostReady else guestReady
    val opponentReady: Boolean get() = if (recipient == CoopSeat.HOST) guestReady else hostReady

    fun validate(puzzle: WordSearchPuzzle, guestReserved: Boolean): DuelView {
        val ids = puzzle.targets.map { it.id }.toSet()
        require(revision >= 0L && hostCount in 0..ids.size && guestCount in 0..ids.size)
        require(ownFoundIds.size == ownCount && ownFoundIds.all { it in ids })
        require(guestReserved || (!guestReady && guestCount == 0))
        require(!started || hostReady && guestReady)
        // The host sends GO atomically with the second Ready; both-Ready
        // without GO is not a legal snapshot and must not pass validation.
        require(started || !(hostReady && guestReady))
        require(started || (hostCount == 0 && guestCount == 0))
        require(finished == (winner != null))
        if (finished) {
            require(started)
            require((if (winner == CoopSeat.HOST) hostCount else guestCount) == ids.size)
        } else {
            require(hostCount < ids.size && guestCount < ids.size)
        }
        return this
    }
}

enum class DuelCommandStatus {
    READY, STARTED, FOUND, ALREADY_FOUND, INVALID_PATH, NOT_STARTED,
    NOT_CONNECTED, FINISHED, INVALID_REQUEST
}

data class DuelDecision(
    val status: DuelCommandStatus,
    val view: DuelView,
    val wordId: String? = null,
    val retransmitted: Boolean = false
)

/** Separate Speed Duel host authority; never calls shared-board ownership. */
class SpeedDuelSession(val puzzle: WordSearchPuzzle) {
    private val hostWords = LinkedHashSet<String>()
    private val guestWords = LinkedHashSet<String>()
    private val replay = mutableMapOf(
        CoopSeat.HOST to LinkedHashMap<String, Pair<DuelCommandStatus, String?>>(),
        CoopSeat.GUEST to LinkedHashMap<String, Pair<DuelCommandStatus, String?>>()
    )
    private var hostReady = false
    private var guestReady = false
    private var started = false
    private var winner: CoopSeat? = null
    private var revision = 0L

    @Synchronized
    fun view(seat: CoopSeat): DuelView = DuelView(
        revision, seat, hostReady, guestReady, started, winner != null,
        winner, hostWords.size, guestWords.size,
        (if (seat == CoopSeat.HOST) hostWords else guestWords).toSet()
    )

    /** Before GO, a disconnected guest must explicitly Ready again. Once
     * started, the match pauses when transport is absent and can resume. */
    @Synchronized
    fun onGuestDisconnected() {
        if (!started && guestReady) {
            guestReady = false
            revision++
        }
    }

    @Synchronized
    fun ready(seat: CoopSeat, bothConnected: Boolean): DuelDecision {
        if (!bothConnected) return DuelDecision(DuelCommandStatus.NOT_CONNECTED, view(seat))
        if (winner != null) return DuelDecision(DuelCommandStatus.FINISHED, view(seat))
        if (started) return DuelDecision(DuelCommandStatus.STARTED, view(seat))
        if (seat == CoopSeat.HOST && !hostReady) {
            hostReady = true
            revision++
        } else if (seat == CoopSeat.GUEST && !guestReady) {
            guestReady = true
            revision++
        }
        if (hostReady && guestReady) {
            started = true
            revision++
        }
        return DuelDecision(if (started) DuelCommandStatus.STARTED else DuelCommandStatus.READY,
            view(seat))
    }

    @Synchronized
    fun submit(seat: CoopSeat, requestId: String, start: GridPoint, end: GridPoint,
               bothConnected: Boolean): DuelDecision {
        if (requestId.length !in 1..64 || requestId.any {
                it.code > 127 || !it.isLetterOrDigit() && it != '-' && it != '_'
            }) return DuelDecision(DuelCommandStatus.INVALID_REQUEST, view(seat))
        val history = replay.getValue(seat)
        history[requestId]?.let { (status, id) ->
            return DuelDecision(status, view(seat), id, true)
        }
        if (!bothConnected) return DuelDecision(DuelCommandStatus.NOT_CONNECTED, view(seat))
        if (!started) return DuelDecision(DuelCommandStatus.NOT_STARTED, view(seat))
        if (winner != null) return DuelDecision(DuelCommandStatus.FINISHED, view(seat))
        val owned = if (seat == CoopSeat.HOST) hostWords else guestWords
        val claim = WordSearchRules.claim(puzzle, owned, start, end)
        val wordId = claim.target?.id
        val status = when (claim.status) {
            ClaimStatus.FOUND -> {
                owned.add(requireNotNull(wordId))
                revision++
                if (owned.size == puzzle.targets.size) winner = seat
                DuelCommandStatus.FOUND
            }
            ClaimStatus.ALREADY_FOUND -> DuelCommandStatus.ALREADY_FOUND
            ClaimStatus.INVALID -> DuelCommandStatus.INVALID_PATH
        }
        history[requestId] = status to wordId
        if (history.size > 256) history.remove(history.keys.first())
        return DuelDecision(status, view(seat), wordId)
    }
}
