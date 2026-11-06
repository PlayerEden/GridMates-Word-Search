package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class NearbyChatTest {
    @Test fun sanitizesWhitespaceControlCharactersAndCapsMessages() {
        assertEquals("Hey friend!", NearbyChatPolicy.clean("  Hey\n\t friend!  "))
        assertEquals("Hi there", NearbyChatPolicy.clean("Hi\u0000 there"))
        assertEquals(140, NearbyChatPolicy.clean("x".repeat(800)).length)
        assertEquals("", NearbyChatPolicy.clean("\t  \n"))
    }

    @Test fun hostOrdersChatSeparatelyFromPuzzleRevisionsAndDeduplicates() {
        val log = NearbyChatLog()
        assertEquals(NearbyChatStatus.NOT_CONNECTED,
            log.add(CoopSeat.HOST, "m1", "Hello", false, 1_000L).status)
        val first = log.add(CoopSeat.HOST, "m1", "Hello", true, 1_000L)
        assertEquals(NearbyChatStatus.ACCEPTED, first.status)
        assertEquals("Hello", first.line?.text)
        assertEquals(NearbyChatStatus.DUPLICATE,
            log.add(CoopSeat.HOST, "m1", "Duplicate", true, 3_000L).status)
        val second = log.add(CoopSeat.GUEST, "m1", "Here!", true, 1_001L)
        assertEquals(NearbyChatStatus.ACCEPTED, second.status)
        assertEquals(2L, second.line?.sequence)
        assertEquals(listOf("Hello", "Here!"), log.history().map { it.text })
    }

    @Test fun throttlesFloodRetainsLast24AndAllowsReconnectHistory() {
        val log = NearbyChatLog()
        assertEquals(NearbyChatStatus.EMPTY,
            log.add(CoopSeat.GUEST, "empty", "  ", true, 2_000L).status)
        assertEquals(NearbyChatStatus.BAD_REQUEST,
            log.add(CoopSeat.GUEST, "bad id", "Oops", true, 2_000L).status)
        assertEquals(NearbyChatStatus.ACCEPTED,
            log.add(CoopSeat.GUEST, "one", "First", true, 2_000L).status)
        assertEquals(NearbyChatStatus.TOO_FAST,
            log.add(CoopSeat.GUEST, "two", "Too quick", true, 2_005L).status)
        assertEquals(NearbyChatStatus.ACCEPTED,
            log.add(CoopSeat.GUEST, "two", "Next", true, 3_500L).status)
        for (i in 0 until 28) {
            assertEquals(NearbyChatStatus.ACCEPTED,
                log.add(CoopSeat.GUEST, "id_$i", "Word $i", true, 5_000L + 1_600L * i).status)
        }
        val history = NearbyChatLog.validateHistory(log.history())
        assertEquals(24, history.size)
        assertEquals("Word 27", history.last().text)
        assertEquals("Word 4", history.first().text)
        assertFalse(history.any { it.text == "First" })
    }

    @Test fun rejectsMaliciousOrImpossibleReplayedHistory() {
        val good = NearbyChatLine(1, CoopSeat.HOST, "Hi", 100, "id-one")
        assertEquals(listOf(good), NearbyChatLog.validateHistory(listOf(good)))
        assertTrue(runCatching { NearbyChatLog.validateHistory(listOf(good,good)) }.isFailure)
        assertTrue(runCatching {
            NearbyChatLog.validateHistory(listOf(good.copy(text = "bad\u0000")))
        }.isFailure)
        assertTrue(runCatching {
            NearbyChatLog.validateHistory(listOf(good.copy(text = "x".repeat(141))))
        }.isFailure)
    }
    @Test fun retryingSameAcceptedIdReturnsOriginalReceiptEvenAfterRateWindow() {
        val log = NearbyChatLog()
        val accepted = log.add(CoopSeat.GUEST, "stable-123", "Hello!", true, 4_000)
        assertEquals(NearbyChatStatus.ACCEPTED, accepted.status)
        val repeated = log.add(CoopSeat.GUEST, "stable-123", "Different text", true, 40_000)
        assertEquals(NearbyChatStatus.DUPLICATE, repeated.status)
        assertEquals(accepted.line, repeated.line)
        assertEquals(1, log.history().size)
        assertEquals("Hello!", log.history().single().text)
    }

    @Test fun guestPendingClearsOnlyOnMatchingConfirmedOriginId() {
        val pending = PendingNearbyChat("guest-12", "Hey")
        val hostLine = NearbyChatLine(1, CoopSeat.HOST, "Hey", 1000, "guest-12")
        val another = NearbyChatLine(2, CoopSeat.GUEST, "Other", 1100, "guest-13")
        val matching = NearbyChatLine(3, CoopSeat.GUEST, "Hey", 1200, "guest-12")
        assertFalse(NearbyChatDelivery.confirmed(pending, hostLine))
        assertFalse(NearbyChatDelivery.confirmed(pending, another))
        assertTrue(NearbyChatDelivery.confirmed(pending, matching))
        assertFalse(NearbyChatDelivery.confirmedInHistory(pending, listOf(hostLine, another)))
        assertTrue(NearbyChatDelivery.confirmedInHistory(pending, listOf(another, matching)))
        assertFalse(NearbyChatDelivery.confirmedInHistory(null, listOf(matching)))
    }

    @Test fun malformedRequestIdsDoNotEnterChatHistory() {
        val log = NearbyChatLog()
        for (bad in listOf("", "contains space", "!bad", "x".repeat(65))) {
            assertEquals(NearbyChatStatus.BAD_REQUEST,
                log.add(CoopSeat.HOST, bad, "Test", true, 2_000).status)
        }
        assertTrue(log.history().isEmpty())
        assertTrue(runCatching {
            NearbyChatLog.validateHistory(listOf(
                NearbyChatLine(1, CoopSeat.HOST, "hello", 1000, "bad id")))
        }.isFailure)
    }

}
