package com.playereden.gridmates.wordsearch

import android.content.Context
import androidx.lifecycle.ViewModel

/**
 * Owns a single nearby room across Activity recreation. A rotation or
 * transient background visit must not discard its live socket/session.
 *
 * Leaving Nearby intentionally closes it; a killed app process still ends
 * the room (no persistent host service or cloud backend is promised).
 */
internal class NearbyRoomViewModel : ViewModel() {
    private var activeRoom: NearbyCoopRuntime? = null

    @Synchronized
    fun runtime(context: Context): NearbyCoopRuntime {
        activeRoom?.let { return it }
        return NearbyCoopRuntime(context.applicationContext).also { activeRoom = it }
    }

    @Synchronized
    fun leaveRoom() {
        activeRoom?.close()
        activeRoom = null
    }

    override fun onCleared() {
        leaveRoom()
        super.onCleared()
    }
}
