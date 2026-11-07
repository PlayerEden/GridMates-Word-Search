package com.playereden.gridmates.wordsearch.core

/** A completed Speed Duel result, independently scoped from Shared Board Race.
 * Its trusted source is a host-authored recipient-specific final Duel snapshot.
 * The opposite player's individual found words are NEVER recorded or exposed.
 */
data class DuelResult(
    val roomId: String,
    val puzzleId: String,
    val boardFingerprint: String,
    val theme: WordTheme,
    val board: PuzzleBoardSize,
    val challenge: PuzzleChallenge,
    val localSeat: CoopSeat,
    val winner: CoopSeat,
    val ownWords: Int,
    val partnerWords: Int,
    val completedAtMs: Long
) {
    val won: Boolean get() = localSeat == winner
    val wordsRemaining: Int get() = board.targets - ownWords

    fun validate(): DuelResult {
        require(roomId.length in 8..100 &&
            roomId.all { it.isLetterOrDigit() || it == '-' || it == '_' })
        require(puzzleId.length in 10..160 &&
            puzzleId.startsWith("wordsearch:v2:en:" + theme.name + ":" +
                board.name + ":" + challenge.name + ":"))
        require(boardFingerprint.length == 64 &&
            boardFingerprint.all { it in '0'..'9' || it in 'a'..'f' })
        require(board in PuzzleBoardSize.bookPresets)
        require(ownWords in 0..board.targets && partnerWords in 0..board.targets)
        // Exactly one player reached the final word first; the loser cannot
        // also have finished in an already-closed host-serialized match.
        require(if (won) ownWords == board.targets && partnerWords < board.targets
                else partnerWords == board.targets && ownWords < board.targets)
        require(completedAtMs > 0L)
        return this
    }

    companion object {
        fun fromVerifiedSnapshot(snapshot: CoopSnapshot, localSeat: CoopSeat,
                                 completedAtMs: Long): DuelResult {
            require(snapshot.match == NearbyMatch.SPEED_DUEL && snapshot.finished)
            NearbyCoopSession.validateSnapshot(snapshot)
            require(snapshot.guestReserved) { "Duel requires admitted opponent" }
            val view = requireNotNull(snapshot.duel)
            require(view.recipient == localSeat) { "Cannot award other player's view" }
            val winner = requireNotNull(view.winner)
            return DuelResult(
                roomId = snapshot.roomId,
                puzzleId = snapshot.spec.puzzleId,
                boardFingerprint = snapshot.spec.boardFingerprint,
                theme = snapshot.spec.theme,
                board = snapshot.spec.board,
                challenge = snapshot.spec.challenge,
                localSeat = localSeat,
                winner = winner,
                ownWords = view.ownCount,
                partnerWords = view.opponentCount,
                completedAtMs = completedAtMs
            ).validate()
        }
    }
}

data class DuelSummary(val played: Int, val wins: Int, val losses: Int,
                       val totalWordsFound: Int) {
    companion object {
        fun of(results: List<DuelResult>) = DuelSummary(
            played = results.size,
            wins = results.count { it.won },
            losses = results.count { !it.won },
            totalWordsFound = results.sumOf { it.ownWords }
        )
    }
}

object DuelJournalRules {
    /** First saved result for a host room wins. No re-award after reconnect. */
    fun addOnce(saved: List<DuelResult>, candidate: DuelResult): List<DuelResult> {
        candidate.validate()
        if (saved.any { it.roomId == candidate.roomId }) return saved
        require(saved.size < 1_000) { "Speed Duel journal is full" }
        return listOf(candidate) + saved
    }

    fun validateImported(results: List<DuelResult>): List<DuelResult> {
        require(results.size <= 1_000) { "Too many Speed Duel records" }
        require(results.map { it.roomId }.distinct().size == results.size) {
            "Duplicate Speed Duel room in backup"
        }
        results.forEach { it.validate() }
        return results
    }
}
