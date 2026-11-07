package com.playereden.gridmates.wordsearch

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class BackupDocumentReaderTest {
    @Test fun smallAndExactlyMaxSizeUtf8DocumentsReadSuccessfully() {
        val small = """{"schema":4,"name":"Café"}"""
        assertEquals(small, BackupDocumentReader.readUtf8(
            ByteArrayInputStream(small.toByteArray(Charsets.UTF_8))))
        val exact = ByteArray(BackupDocumentReader.MAX_BYTES) { 'x'.code.toByte() }
        assertEquals(exact.size, BackupDocumentReader.readUtf8(
            ByteArrayInputStream(exact)).length)
    }

    @Test fun oversizedDocumentStopsNearLimitInsteadOfReadingEntireProvider() {
        val limit = BackupDocumentReader.MAX_BYTES
        val source = CountingStream(limit + 20_000_000)
        assertTrue(runCatching { BackupDocumentReader.readUtf8(source) }.isFailure)
        // Never consume the rest of a 22 MB stream just to reject it.
        assertTrue("Read too much from document provider: " + source.consumed,
            source.consumed <= limit + 1)
        assertTrue(source.closed)
    }

    @Test fun exactlyOneByteOverLimitIsRejected() {
        val bytes = ByteArray(BackupDocumentReader.MAX_BYTES + 1) { 'a'.code.toByte() }
        assertTrue(runCatching {
            BackupDocumentReader.readUtf8(ByteArrayInputStream(bytes))
        }.isFailure)
    }

    private class CountingStream(private val length: Int) : InputStream() {
        var consumed = 0
            private set
        var closed = false
            private set
        override fun read(): Int {
            if (consumed == length) return -1
            consumed++
            return 'z'.code
        }
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if (consumed == length) return -1
            val n = minOf(len, length - consumed)
            b.fill('z'.code.toByte(), off, off + n)
            consumed += n
            return n
        }
        override fun close() { closed = true }
    }
}
