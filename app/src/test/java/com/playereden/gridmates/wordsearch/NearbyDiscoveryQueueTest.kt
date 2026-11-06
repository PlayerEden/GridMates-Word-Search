package com.playereden.gridmates.wordsearch

import org.junit.Assert.*
import org.junit.Test

class NearbyDiscoveryQueueTest {
    @Test fun resolvesSeriallyWithoutOverlappingRequests() {
        val queue = NearbyDiscoveryQueue()
        assertEquals("a", queue.found("a"))
        assertNull(queue.found("a"))
        assertNull(queue.found("b"))
        assertNull(queue.found("c"))
        assertEquals("b", queue.finished("a"))
        assertEquals("c", queue.finished("b"))
        assertNull(queue.finished("c"))
    }

    @Test fun serviceLostWhileResolvingCannotResurrectRoom() {
        val queue = NearbyDiscoveryQueue()
        assertEquals("old", queue.found("old"))
        assertNull(queue.found("next"))
        queue.lost("old")
        assertFalse(queue.isLive("old"))
        assertTrue(queue.isLive("next"))
        assertEquals("next", queue.finished("old"))
        assertNull(queue.finished("old"))
        queue.lost("next")
        assertNull(queue.finished("next"))
    }

    @Test fun lostPendingRoomIsSkippedAndNewAdvertisementCanReturn() {
        val queue = NearbyDiscoveryQueue()
        assertEquals("busy", queue.found("busy"))
        assertNull(queue.found("stale"))
        queue.lost("stale")
        assertNull(queue.found("fresh"))
        assertEquals("fresh", queue.finished("busy"))
        assertNull(queue.finished("fresh"))
        assertEquals("stale", queue.found("stale"))
    }

    @Test fun resetIgnoresOldAsyncCallbacksAndCapsDiscoveryFlood() {
        val queue = NearbyDiscoveryQueue(2)
        assertEquals("one", queue.found("one"))
        assertNull(queue.found("two"))
        assertNull(queue.found("three"))
        assertFalse(queue.isLive("three"))
        queue.reset()
        assertNull(queue.finished("one"))
        assertEquals("new", queue.found("new"))
        assertNull(queue.finished("one"))
        assertNull(queue.finished("new"))
    }

    @Test fun rejectBlankAndOversizeDiscoveryNames() {
        val queue = NearbyDiscoveryQueue()
        assertNull(queue.found(""))
        assertNull(queue.found("x".repeat(121)))
        assertEquals("hello", queue.found("hello"))
    }
}
