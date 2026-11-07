package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest
import java.util.UUID

/** Who is allowed to submit moves is determined by the trusted host transport. */
enum class CoopSeat { HOST, GUEST }

/** Wire-visible room rules; Speed Duel will use a separate private-board authority. */
enum class NearbyMatch(val label: String) {
    COOP("Co-op"),
    SHARED_BOARD_RACE("Shared Board Race")
}

/** Derived from host-confirmed word ownership; never award the same word twice. */
data class RaceStanding(val hostPoints: Int, val guestPoints: Int, val winner: CoopSeat?) {
    val tied: Boolean get() = winner == null
}

data class CoopPuzzleSpec(
    val theme: WordTheme,
    val board: PuzzleBoardSize,
    val challenge: PuzzleChallenge,
    val seed: Long,
    val puzzleId: String,
    val boardFingerprint: String
) {
    fun regenerate(): WordSearchPuzzle {
        val run = SoloProgressRules.freshV2(theme, board, challenge, seed)
        require(run.puzzleId == puzzleId) { "Incompatible puzzle identity" }
        val puzzle = SoloProgressRules.puzzle(run)
        require(fingerprint(puzzle) == boardFingerprint) { "Incompatible puzzle content" }
        return puzzle
    }

    companion object {
        fun from(run: SoloRun): CoopPuzzleSpec {
            val board = requireNotNull(run.boardSize) { "Co-op requires versioned V2 puzzles" }
            val challenge = requireNotNull(run.challenge) { "Co-op requires V2 challenge" }
            require(run.foundIds.isEmpty() && run.hints.isEmpty()) {
                "Co-op starts a separate clean board, not the player's Solo progress"
            }
            return CoopPuzzleSpec(run.theme, board, challenge, run.seed,
                run.puzzleId, fingerprint(SoloProgressRules.puzzle(run)))
        }

        /** Byte-stable digest of every letter and target path, not just the seed. */
        fun fingerprint(puzzle: WordSearchPuzzle): String {
            val source = buildString {
                append(puzzle.id).append('\n')
                puzzle.rows.forEach { append(it).append('\n') }
                puzzle.targets.forEach { target ->
                    append(target.id).append(':').append(target.word).append(':')
                    target.positions.forEach { append(it.row).append(',').append(it.col).append(';') }
                    append('\n')
                }
            }
            val hash = MessageDigest.getInstance("SHA-256")
                .digest(source.toByteArray(Charsets.UTF_8))
            return hash.joinToString("") { "%02x".format(it.toInt() and 255) }
        }
    }
}

data class CoopWordClaim(val wordId: String, val seat: CoopSeat, val revision: Long)

data class CoopSnapshot(
    val roomId: String,
    val spec: CoopPuzzleSpec,
    val revision: Long,
    val claims: List<CoopWordClaim>,
    val guestReserved: Boolean,
    val guestConnected: Boolean,
    val hostConnected: Boolean,
    val finished: Boolean,
    val match: NearbyMatch = NearbyMatch.COOP
) {
    /** Race result exists only when the authoritative shared board is complete. */
    val raceStanding: RaceStanding? get() {
        if (match != NearbyMatch.SHARED_BOARD_RACE || !finished) return null
        val winner = when {
            hostWords > guestWords -> CoopSeat.HOST
            guestWords > hostWords -> CoopSeat.GUEST
            else -> null
        }
        return RaceStanding(hostWords, guestWords, winner)
    }

    val hostWords: Int get() = claims.count { it.seat == CoopSeat.HOST }
    val guestWords: Int get() = claims.count { it.seat == CoopSeat.GUEST }
    val foundIds: Set<String> get() = claims.map { it.wordId }.toSet()
}

enum class CoopClaimStatus {
    FOUND, ALREADY_FOUND, INVALID_PATH, NOT_READY, WRONG_ROOM, INVALID_REQUEST
}

data class CoopClaimDecision(
    val status: CoopClaimStatus,
    val snapshot: CoopSnapshot,
    val wordId: String? = null,
    /** True only for a repeated request ID from the *same seat*. */
    val retransmitted: Boolean = false
)

/**
 * Deterministic, Android-free host authority. All valid claims are serialized;
 * first valid claim owns a word, so two near-simultaneous phones cannot both
 * award the same target. Race mode interprets the same authoritative
 * ownership as one point per word; co-op remains a separate experience.
 *
 * Caller/transport supplies the authenticated seat. Never accept a seat from
 * an untrusted wire message. No timer automatically ends idle games.
 */
class NearbyCoopSession(
    run: SoloRun,
    val roomId: String = UUID.randomUUID().toString(),
    val match: NearbyMatch = NearbyMatch.COOP
) {
    val spec: CoopPuzzleSpec = CoopPuzzleSpec.from(run)
    val puzzle: WordSearchPuzzle = spec.regenerate()
    private val owned = LinkedHashMap<String, CoopWordClaim>()
    private val commandHistory = mutableMapOf(
        CoopSeat.HOST to LinkedHashMap<String, Pair<CoopClaimStatus, String?>>(),
        CoopSeat.GUEST to LinkedHashMap<String, Pair<CoopClaimStatus, String?>>()
    )
    private var guestReserved = false
    private var guestConnected = false
    private var hostConnected = true
    private var revision = 0L

    @Synchronized
    fun setHostConnected(connected: Boolean): CoopSnapshot {
        hostConnected = connected
        return snapshot()
    }

    /** A reserved guest can reconnect without erasing any previous discoveries. */
    @Synchronized
    fun admitOrRejoinGuest(): CoopSnapshot {
        guestReserved = true
        guestConnected = true
        return snapshot()
    }

    @Synchronized
    fun guestDisconnected(): CoopSnapshot {
        guestConnected = false
        return snapshot()
    }

    @Synchronized
    fun snapshot(): CoopSnapshot = CoopSnapshot(
        roomId, spec, revision, owned.values.toList(),
        guestReserved, guestConnected, hostConnected,
        WordSearchRules.complete(puzzle, owned.keys), match
    )

    @Synchronized
    fun submit(seat: CoopSeat, requestRoom: String, requestId: String,
               start: GridPoint, end: GridPoint): CoopClaimDecision {
        if (requestRoom != roomId)
            return CoopClaimDecision(CoopClaimStatus.WRONG_ROOM, snapshot())
        if (requestId.length !in 1..64 ||
            requestId.any { !it.isLetterOrDigit() && it != '-' && it != '_' } ||
            requestId.any { it.code > 127 })
            return CoopClaimDecision(CoopClaimStatus.INVALID_REQUEST, snapshot())
        val history = commandHistory.getValue(seat)
        history[requestId]?.let { (status, target) ->
            return CoopClaimDecision(status, snapshot(), target, retransmitted = true)
        }
        if (!hostConnected || !guestConnected)
            return CoopClaimDecision(CoopClaimStatus.NOT_READY, snapshot())

        val result = WordSearchRules.claim(puzzle, owned.keys, start, end)
        val wordId = result.target?.id
        val status = when (result.status) {
            ClaimStatus.FOUND -> {
                revision++
                owned[requireNotNull(wordId)] = CoopWordClaim(wordId, seat, revision)
                CoopClaimStatus.FOUND
            }
            ClaimStatus.ALREADY_FOUND -> CoopClaimStatus.ALREADY_FOUND
            ClaimStatus.INVALID -> CoopClaimStatus.INVALID_PATH
        }
        // Do not remember NOT_READY. A retry after reconnect must be possible.
        history[requestId] = status to wordId
        if (history.size > 256) history.remove(history.keys.first())
        return CoopClaimDecision(status, snapshot(), wordId)
    }

    companion object {
        /**
         * Guest-side safety gate before presenting received data.
         * Rejects corrupt, out-of-order and impossible host snapshots.
         * Callers also compare room IDs and do not downgrade an accepted revision.
         */
        fun validateSnapshot(received: CoopSnapshot): WordSearchPuzzle {
            require(received.roomId.length in 8..100)
            val puzzle = received.spec.regenerate()
            require(received.revision >= 0L)
            require(received.claims.size <= puzzle.targets.size)
            val legalIds = puzzle.targets.map { it.id }.toSet()
            require(received.claims.map { it.wordId }.distinct().size == received.claims.size)
            require(received.claims.all { it.wordId in legalIds && it.revision in 1..received.revision })
            require(received.claims.map { it.revision }.distinct().size == received.claims.size)
            require(received.claims.sortedBy { it.revision } == received.claims)
            require(received.claims.size.toLong() == received.revision)
            require(received.claims.none { it.seat == CoopSeat.GUEST } || received.guestReserved)
            require(received.finished == WordSearchRules.complete(puzzle, received.foundIds))
            return puzzle
        }
    }
}
