package com.playereden.gridmates.wordsearch.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.security.MessageDigest

/**
 * WS-11C: bounded, deterministic, platform-neutral local book progress format.
 *
 * This is a NEW, independent file, not part of schema-5 Solo/Daily/Versus saves.
 * Format v1 (all multi-byte integers are big-endian):
 *   "GMWB" / version:int / volumeCount:int /
 *   repeated { bookId:utf8-length+bytes / completedCount:int /
 *       sorted completed pageIds:utf8-length+bytes / hasBookmark:byte /
 *       optional bookmarkId:utf8-length+bytes } /
 *   SHA-256(ALL preceding bytes).
 *
 * No deserialization of arbitrary classes, permissive unknown fields, silent
 * ID cleanup, or auto-creation of unpublished chapter progress.
 * Future incompatible changes MUST introduce another version.
 */
object BookProgressCodec {
    const val VERSION = 1
    const val MAX_BYTES = 1024 * 1024
    private val MAGIC = byteArrayOf('G'.code.toByte(), 'M'.code.toByte(),
        'W'.code.toByte(), 'B'.code.toByte())
    private const val HASH_SIZE = 32
    private const val MAX_VOLUMES = 200
    private const val MAX_PAGES_PER_VOLUME = 12000
    private val ID = Regex("[a-z][a-z0-9-]{2,63}")

    fun encode(catalog: BookCatalog, progress: BookLibraryProgress): ByteArray {
        BookProgressRules.validate(catalog, progress)
        val output = ByteArrayOutputStream()
        DataOutputStream(output).use { stream ->
            stream.write(MAGIC)
            stream.writeInt(VERSION)
            val volumes = progress.volumes.toSortedMap()
            stream.writeInt(volumes.size)
            volumes.forEach { (bookId, state) ->
                writeId(stream, bookId)
                val pages = state.completedPageIds.toSortedSet()
                require(pages.size <= MAX_PAGES_PER_VOLUME) { "Book page count too high" }
                stream.writeInt(pages.size)
                pages.forEach { writeId(stream, it) }
                val bookmark = state.bookmarkedPageId
                stream.writeByte(if (bookmark == null) 0 else 1)
                if (bookmark != null) writeId(stream, bookmark)
            }
        }
        val payload = output.toByteArray()
        require(payload.size + HASH_SIZE <= MAX_BYTES) { "Book progress too large" }
        val signature = MessageDigest.getInstance("SHA-256").digest(payload)
        return payload + signature
    }

    fun decode(catalog: BookCatalog, source: ByteArray): BookLibraryProgress {
        require(source.size in (MAGIC.size + 8 + HASH_SIZE)..MAX_BYTES) {
            "Book progress is empty, truncated or oversized"
        }
        val payload = source.copyOfRange(0, source.size - HASH_SIZE)
        val savedDigest = source.copyOfRange(source.size - HASH_SIZE, source.size)
        val actualDigest = MessageDigest.getInstance("SHA-256").digest(payload)
        require(MessageDigest.isEqual(savedDigest, actualDigest)) {
            "Book progress integrity check failed"
        }
        try {
            val stream = DataInputStream(ByteArrayInputStream(payload))
            val magic = ByteArray(MAGIC.size)
            stream.readFully(magic)
            require(magic.contentEquals(MAGIC)) { "Not a GridMates book progress file" }
            require(stream.readInt() == VERSION) { "Unsupported book progress format" }
            val count = stream.readInt()
            require(count in 0..MAX_VOLUMES) { "Invalid number of books" }
            val decoded = linkedMapOf<String, BookVolumeProgress>()
            repeat(count) {
                val bookId = readId(stream)
                require(bookId !in decoded) { "Duplicate book progress" }
                val pageCount = stream.readInt()
                require(pageCount in 0..MAX_PAGES_PER_VOLUME) { "Invalid page count" }
                val pages = linkedSetOf<String>()
                repeat(pageCount) {
                    val pageId = readId(stream)
                    require(pages.add(pageId)) { "Duplicate completed page" }
                }
                val flag = stream.readUnsignedByte()
                require(flag in 0..1) { "Invalid bookmark marker" }
                val bookmark = if (flag == 1) readId(stream) else null
                decoded[bookId] = BookVolumeProgress(bookId, pages, bookmark)
            }
            require(stream.available() == 0) {
                "Book progress contains unsupported trailing fields"
            }
            val result = BookLibraryProgress(decoded)
            // Crucial: unknown published IDs must cause a safe failure rather
            // than discard saved history when a catalog is temporarily missing.
            BookProgressRules.validate(catalog, result)
            return result
        } catch (e: IOException) {
            throw IllegalArgumentException("Unreadable or truncated book progress", e)
        }
    }

    private fun writeId(output: DataOutputStream, value: String) {
        require(ID.matches(value)) { "Invalid journal identifier" }
        val bytes = value.toByteArray(Charsets.UTF_8)
        require(bytes.size in 3..64) { "Invalid identifier length" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readId(input: DataInputStream): String {
        val size = input.readInt()
        require(size in 3..64) { "Invalid identifier length" }
        val bytes = ByteArray(size)
        input.readFully(bytes)
        val value = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString()
        require(ID.matches(value)) { "Invalid saved journal identifier" }
        return value
    }
}
