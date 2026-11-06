package com.playereden.gridmates.wordsearch

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Android-side logic tests; they cannot replace a two-phone NSD test. */
@RunWith(AndroidJUnit4::class)
class NearbyDiscoveryAndroidTest {
    @Test fun repeatedAdvertisementsAreResolvedOnceAndOldRoomsStayLost() {
        val queue = NearbyDiscoveryQueue()
        assertEquals("Woodland room", queue.found("Woodland room"))
        assertNull(queue.found("Woodland room"))
        assertNull(queue.found("Garden room"))
        queue.lost("Woodland room")
        assertFalse(queue.isLive("Woodland room"))
        assertEquals("Garden room", queue.finished("Woodland room"))
        assertTrue(queue.isLive("Garden room"))
        assertNull(queue.finished("Garden room"))
    }

    @Test fun refreshingResetsPendingResolversAndOldRepliesAreIgnored() {
        val queue = NearbyDiscoveryQueue()
        assertEquals("old", queue.found("old"))
        assertNull(queue.found("next-old"))
        queue.reset()
        assertFalse(queue.isLive("old"))
        assertNull(queue.finished("old"))
        assertEquals("new", queue.found("new"))
        assertNull(queue.finished("old"))
        assertNull(queue.finished("new"))
    }

    @Test fun interruptedManualJoinRemembersActualOfferedRoomEndpoint() {
        val recovery = NearbyJoinRecovery("room-actual", "192.168.1.20", 30220, "123456")
        // This identity survives the screen's rotation-retained ViewModel;
        // it is not an additional account, server or permanent cloud save.
        assertEquals("room-actual", recovery.roomId)
        assertEquals("192.168.1.20", recovery.address)
        assertEquals(30220, recovery.port)
        assertEquals("123456", recovery.code)
    }
}
