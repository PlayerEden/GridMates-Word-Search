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
 * Independent "GMWS" partial-page journal. Existing GMWB completed-page
 * backups and the schema-5 Solo profile MUST NOT change format or semantics.
 *
 * All integers are big-endian. The final 32 bytes are SHA-256 for accidental
 * damage detection (not cryptographic authenticity or anti-cheat).
 * v1 = magic(4), version(int), count(int), sorted session records, digest.
 * session = bookId, pageId, puzzleId, boardSha256, elapsed(long),
 *           foundCount(int), sorted found IDs,
 *           hintCount(int), sorted (target ID, level:int).
 */
object BookPageSessionCodec {
    const val VERSION = 1
    const val MAX_BYTES = 1024 * 1024
    private val MAGIC = byteArrayOf('G'.code.toByte(), 'M'.code.toByte(),
        'W'.code.toByte(), 'S'.code.toByte())
    private const val SHA_SIZE = 32
    private val ID = Regex("[a-z][a-z0-9-]{2,63}")
    private val PUZZLE_ID = Regex("[A-Za-z0-9:_-]{15,200}")
    private val TARGET_ID = Regex("[A-Za-z0-9:_-]{1,200}")
    private val HEX = Regex("[0-9a-f]{64}")

    fun encode(catalog: BookCatalog, journal: BookPageSessionJournal): ByteArray {
        BookPageSessionRules.validate(catalog, journal)
        val buffer = ByteArrayOutputStream()
        DataOutputStream(buffer).use { data ->
            data.write(MAGIC)
            data.writeInt(VERSION)
            val ordered = journal.sessions.sortedWith(
                compareBy<BookPageSession>({ it.bookId }, { it.pageId })
            )
            data.writeInt(ordered.size)
            ordered.forEach { state ->
                write(data, state.bookId, ID)
                write(data, state.pageId, ID)
                write(data, state.puzzleId, PUZZLE_ID)
                write(data, state.boardSha256, HEX)
                data.writeLong(state.elapsedSeconds)
                data.writeInt(state.foundIds.size)
                state.foundIds.sorted().forEach { write(data, it, TARGET_ID) }
                data.writeInt(state.hints.size)
                state.hints.toSortedMap().forEach { (target, level) ->
                    write(data, target, TARGET_ID)
                    data.writeInt(level)
                }
            }
        }
        val payload = buffer.toByteArray()
        require(payload.size + SHA_SIZE <= MAX_BYTES) { "Partial book journal too large" }
        return payload + MessageDigest.getInstance("SHA-256").digest(payload)
    }

    fun decode(catalog: BookCatalog, bytes: ByteArray): BookPageSessionJournal {
        require(bytes.size in (4 + 8 + SHA_SIZE)..MAX_BYTES) {
            "Partial book journal is missing, truncated, or oversized"
        }
        val payload = bytes.copyOfRange(0, bytes.size - SHA_SIZE)
        val expected = bytes.copyOfRange(bytes.size - SHA_SIZE, bytes.size)
        val actual = MessageDigest.getInstance("SHA-256").digest(payload)
        require(MessageDigest.isEqual(actual, expected)) { "Book session checksum mismatch" }

        try {
            val data = DataInputStream(ByteArrayInputStream(payload))
            val magic = ByteArray(4)
            data.readFully(magic)
            require(magic.contentEquals(MAGIC)) { "Not a GridMates book page session file" }
            require(data.readInt() == VERSION) { "Unsupported book session format" }
            val count = data.readInt()
            require(count in 0..BookPageSessionRules.MAX_SESSIONS) {
                "Invalid book session count"
            }
            val sessions = ArrayList<BookPageSession>(count)
            repeat(count) {
                val bookId = read(data, ID, 64)
                val pageId = read(data, ID, 64)
                val puzzleId = read(data, PUZZLE_ID, 200)
                val sha = read(data, HEX, 64)
                val elapsed = data.readLong()
                val wordCount = data.readInt()
                require(wordCount in 0..128) { "Invalid found word count" }
                val found = LinkedHashSet<String>()
                repeat(wordCount) {
                    require(found.add(read(data, TARGET_ID, 200))) {
                        "Duplicated found target"
                    }
                }
                val hintCount = data.readInt()
                require(hintCount in 0..128) { "Invalid hint count" }
                val hints = LinkedHashMap<String, Int>()
                repeat(hintCount) {
                    val id = read(data, TARGET_ID, 200)
                    val level = data.readInt()
                    require(hints.put(id, level) == null) { "Duplicated hint target" }
                }
                sessions += BookPageSession(
                    bookId, pageId, puzzleId, sha, found, hints, elapsed
                )
            }
            require(data.available() == 0) { "Unsupported journal trailing data" }
            val result = BookPageSessionJournal(sessions)
            // Never discard unknown pages, unknown words or stale hashes.
            BookPageSessionRules.validate(catalog, result)
            return result
        } catch (e: IOException) {
            throw IllegalArgumentException("Unreadable partial book session journal", e)
        }
    }

    private fun write(data: DataOutputStream, value: String, pattern: Regex) {
        require(pattern.matches(value)) { "Invalid book session field" }
        val encoded = value.toByteArray(Charsets.UTF_8)
        require(encoded.size in 1..200) { "Invalid book session text length" }
        data.writeInt(encoded.size)
        data.write(encoded)
    }

    private fun read(data: DataInputStream, pattern: Regex, max: Int): String {
        val count = data.readInt()
        require(count in 1..max) { "Invalid field size" }
        val bytes = ByteArray(count)
        data.readFully(bytes)
        val text = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString()
        require(pattern.matches(text)) { "Invalid saved book session field" }
        return text
    }
}
