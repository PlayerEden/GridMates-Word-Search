package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.CoopSeat
import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import com.playereden.gridmates.wordsearch.core.NearbyCoopSession
import com.playereden.gridmates.wordsearch.core.NearbyMatch

/**
 * Read-only, spoiler-safe nearby result summary. Never sends puzzle seed,
 * found-word IDs, room code/address, chat, or either player's display name.
 * A Duel uses a recipient-scoped verified final host view, not the other
 * player's word-by-word progress.
 */
internal object NearbyMatchShare {
    fun text(snapshot: CoopSnapshot, localSeat: CoopSeat): String {
        require(snapshot.finished && snapshot.guestReserved) {
            "Only finished admitted nearby matches may be shared"
        }
        val puzzle = NearbyCoopSession.validateSnapshot(snapshot)
        val total = puzzle.targets.size
        val headline: String
        val result: String
        when (snapshot.match) {
            NearbyMatch.COOP -> {
                require(snapshot.foundIds.size == total)
                headline = "Nearby Co-op · Puzzle complete"
                result = "We found all $total words together!"
            }
            NearbyMatch.SHARED_BOARD_RACE -> {
                val standing = requireNotNull(snapshot.raceStanding)
                val own = if (localSeat == CoopSeat.HOST)
                    standing.hostPoints else standing.guestPoints
                val partner = if (localSeat == CoopSeat.HOST)
                    standing.guestPoints else standing.hostPoints
                headline = "Shared Board Race · Match complete"
                val label = when {
                    standing.tied -> "A friendly tie!"
                    standing.winner == localSeat -> "I won!"
                    else -> "My friend won!"
                }
                result = "$label · Me $own / Friend $partner · $total shared words"
            }
            NearbyMatch.SPEED_DUEL -> {
                val view = requireNotNull(snapshot.duel)
                require(view.recipient == localSeat)
                headline = "Speed Duel · Match complete"
                result = (if (view.winner == localSeat) "I finished first!"
                    else "My friend finished first!") +
                    " · Me ${view.ownCount} / Friend ${view.opponentCount}" +
                    " · $total words per board"
            }
        }
        return listOf(
            "GridMates: Word Search",
            headline,
            snapshot.spec.theme.label + " · " + snapshot.spec.board.cells +
                "×" + snapshot.spec.board.cells + " · " + snapshot.spec.challenge.label,
            result,
            "Good puzzles. Good company."
        ).joinToString("\n")
    }
}
