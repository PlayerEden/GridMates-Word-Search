package com.playereden.gridmates.wordsearch.core

/**
 * Completed, host-verified Shared Board Race result stored in the player's
 * existing atomic SoloProfile JSON. Room ID (unique per hosted game) is the
 * deduplication key. Do not persist unconfirmed or partial live progress.
 */
data class RaceResult(
    val roomId: String,
    val puzzleId: String,
    val boardFingerprint: String,
    val theme: WordTheme,
    val board: PuzzleBoardSize,
    val challenge: PuzzleChallenge,
    val localSeat: CoopSeat,
    val ownWords: Int,
    val partnerWords: Int,
    val completedAtMs: Long
) {
    val totalWords: Int get() = ownWords + partnerWords
    val outcome: RaceOutcome get() = when {
        ownWords > partnerWords -> RaceOutcome.WIN
        ownWords < partnerWords -> RaceOutcome.LOSS
        else -> RaceOutcome.TIE
    }
    val lead: Int get() = ownWords - partnerWords

    /** Validate imported or stored records without regenerating historical boards. */
    fun validate(): RaceResult {
        require(roomId.length in 8..100 && roomId.all { it.isLetterOrDigit() || it == '-' || it == '_' })
        require(puzzleId.length in 10..160 &&
            puzzleId.startsWith("wordsearch:v2:en:${theme.name}:${board.name}:${challenge.name}:"))
        require(boardFingerprint.length == 64 &&
            boardFingerprint.all { it in '0'..'9' || it in 'a'..'f' })
        require(board in PuzzleBoardSize.bookPresets) { "Unsupported race board" }
        require(ownWords >= 0 && partnerWords >= 0 && totalWords == board.targets)
        require(completedAtMs > 0L)
        return this
    }

    companion object {
        /** Never trust a result supplied by a guest as a bare score packet. */
        fun fromVerifiedSnapshot(snapshot: CoopSnapshot, localSeat: CoopSeat,
                                 completedAtMs: Long): RaceResult {
            require(snapshot.match == NearbyMatch.SHARED_BOARD_RACE && snapshot.finished)
            NearbyCoopSession.validateSnapshot(snapshot)
            require(snapshot.guestReserved) { "A race requires an admitted opponent" }
            return RaceResult(
                roomId = snapshot.roomId,
                puzzleId = snapshot.spec.puzzleId,
                boardFingerprint = snapshot.spec.boardFingerprint,
                theme = snapshot.spec.theme,
                board = snapshot.spec.board,
                challenge = snapshot.spec.challenge,
                localSeat = localSeat,
                ownWords = snapshot.claims.count { it.seat == localSeat },
                partnerWords = snapshot.claims.count { it.seat != localSeat },
                completedAtMs = completedAtMs
            ).validate()
        }
    }
}

enum class RaceOutcome { WIN, LOSS, TIE }

data class RaceSummary(val played: Int, val wins: Int, val losses: Int, val ties: Int,
                       val totalClaimed: Int, val totalOpponentClaimed: Int) {
    companion object {
        fun of(records: List<RaceResult>) = RaceSummary(
            played = records.size,
            wins = records.count { it.outcome == RaceOutcome.WIN },
            losses = records.count { it.outcome == RaceOutcome.LOSS },
            ties = records.count { it.outcome == RaceOutcome.TIE },
            totalClaimed = records.sumOf { it.ownWords },
            totalOpponentClaimed = records.sumOf { it.partnerWords }
        )
    }
}

object RaceJournalRules {
    /** First valid, persisted result for a room is immutable across reconnects. */
    fun addOnce(records: List<RaceResult>, candidate: RaceResult): List<RaceResult> {
        candidate.validate()
        if (records.any { it.roomId == candidate.roomId }) return records
        require(records.size < 1_000) { "Versus journal is full; back up results" }
        return listOf(candidate) + records
    }

    fun validateImported(records: List<RaceResult>): List<RaceResult> {
        require(records.size <= 1_000) { "Too many race results" }
        require(records.map { it.roomId }.distinct().size == records.size) {
            "Duplicate race room in backup"
        }
        records.forEach { it.validate() }
        return records
    }
}
