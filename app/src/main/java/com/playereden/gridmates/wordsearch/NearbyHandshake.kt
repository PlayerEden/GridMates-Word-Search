package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.CoopSnapshot
import org.json.JSONObject

/**
 * A guest's seat is not reserved until a credential saved on the guest
 * device has been acknowledged. The final message proves host admission.
 *
 * This helper checks the handshake; the actual host must hold its
 * single-offer gate until the acknowledged admission is committed.
 */
internal object NearbyHandshake {
    fun receipt(room: String, token: String): JSONObject =
        JSONObject().put("type", "accept").put("room", room).put("token", token)

    fun accepts(type: String, room: String, token: String,
                expectedRoom: String, expectedToken: String): Boolean =
        type == "accept" && expectedRoom.isNotBlank() && room == expectedRoom &&
            expectedToken.length == 40 && token == expectedToken

    fun matchesFinal(offered: CoopSnapshot, joined: CoopSnapshot): Boolean =
        offered.roomId == joined.roomId && offered.spec == joined.spec &&
            offered.match == joined.match &&
            joined.guestReserved && joined.guestConnected &&
            joined.revision >= offered.revision &&
            joined.claims.take(offered.claims.size) == offered.claims
}
