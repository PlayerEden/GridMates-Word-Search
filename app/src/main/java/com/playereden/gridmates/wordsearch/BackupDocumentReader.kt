package com.playereden.gridmates.wordsearch

import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * The document picker can return an arbitrary stream, including an enormous
 * file or a provider that doesn't report its length. Apply the byte limit
 * BEFORE allocating/decoding text, not after InputStream.readText().
 */
internal object BackupDocumentReader {
    const val MAX_BYTES = 2_000_000

    fun readUtf8(source: InputStream): String = source.use { input ->
        val output = ByteArrayOutputStream(32 * 1024)
        val buffer = ByteArray(8192)
        var used = 0
        while (true) {
            // When at capacity, request one more byte to distinguish an
            // exactly-max-size document from an oversized document.
            val read = input.read(buffer, 0, minOf(buffer.size, MAX_BYTES - used + 1))
            if (read == -1) break
            if (read == 0) {
                val single = input.read()
                if (single == -1) break
                require(used < MAX_BYTES) { "Backup file exceeds 2 MB" }
                output.write(single)
                used++
            } else {
                require(read <= MAX_BYTES - used) { "Backup file exceeds 2 MB" }
                output.write(buffer, 0, read)
                used += read
            }
        }
        output.toString(Charsets.UTF_8.name())
    }
}
