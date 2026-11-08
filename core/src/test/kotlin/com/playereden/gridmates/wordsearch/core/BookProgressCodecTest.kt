package com.playereden.gridmates.wordsearch.core

import java.nio.ByteBuffer
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class BookProgressCodecTest {
    private val woodland = PuzzleBook(
        id = "woodland-volume-one", title = "Woodland Volume I", volume = 1,
        theme = WordTheme.WOODLAND, chapters = listOf(
            BookChapter("woodland-chapter-one", "Trees", listOf(
                page("woodland-page-one", 1L), page("woodland-page-two", 2L)
            )),
            BookChapter("woodland-chapter-two", "Animals", listOf(
                page("woodland-page-three", 3L)
            ))
        )
    )
    private val space = PuzzleBook(
        id = "space-volume-one", title = "Space Volume I", volume = 1,
        theme = WordTheme.SPACE, chapters = listOf(
            BookChapter("space-chapter-one", "Orbits", listOf(
                page("space-page-one", 4L)
            ))
        )
    )
    private val catalog = BookCatalog(listOf(woodland, space))
    private fun page(id: String, seed: Long) = BookPage(
        id, "wordsearch:v2:en:WOODLAND:BOOK_SHORT:CLASSIC:$seed", 2,
        "fixture-en-v1", PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC,
        seed, "a".repeat(64)
    )
    // Synthetic metadata intentionally exercises the codec, NOT actual
    // published books. It cannot award a real completion without exact hashes.
    private fun example() = BookLibraryProgress(
        mapOf(
            "space-volume-one" to BookVolumeProgress(
                "space-volume-one", setOf("space-page-one")
            ),
            "woodland-volume-one" to BookVolumeProgress(
                "woodland-volume-one",
                setOf("woodland-page-two", "woodland-page-one"),
                bookmarkedPageId = "woodland-page-three"
            )
        )
    )
    private inline fun reject(block: () -> Unit) {
        try {
            block()
            fail("Expected corrupt or incompatible progress rejection")
        } catch (_: IllegalArgumentException) { }
    }
    private fun resigned(payload: ByteArray) =
        payload + MessageDigest.getInstance("SHA-256").digest(payload)

    @Test fun emptyCatalogCreatesOnlyAnEmptyJournal() {
        val empty = BookCatalog()
        val bytes = BookProgressCodec.encode(empty, BookLibraryProgress())
        assertTrue(bytes.size < 100)
        assertEquals(BookLibraryProgress(), BookProgressCodec.decode(empty, bytes))
    }

    @Test fun canonicalEncodingIgnoresMapAndSetInsertionOrder() {
        val original = example()
        val reversed = BookLibraryProgress(
            original.volumes.toList().reversed().toMap().mapValues { (_, p) ->
                p.copy(completedPageIds = p.completedPageIds.reversed().toSet())
            }
        )
        val a = BookProgressCodec.encode(catalog, original)
        val b = BookProgressCodec.encode(catalog, reversed)
        assertArrayEquals(a, b)
        assertEquals(original, BookProgressCodec.decode(catalog, a))
        assertEquals("GMWB", String(a.copyOfRange(0, 4), Charsets.US_ASCII))
        assertEquals(BookProgressCodec.VERSION, ByteBuffer.wrap(a, 4, 4).int)
    }

    @Test fun backupRoundTripRetainsCompletionAndResumeIdentity() {
        val restored = BookProgressCodec.decode(catalog, BookProgressCodec.encode(
            catalog, example()
        ))
        assertEquals("woodland-page-three", BookProgressRules.continuePage(
            catalog, restored, "woodland-volume-one"
        )?.id)
        assertEquals(2, BookProgressRules.completedCount(
            catalog, restored, "woodland-volume-one"
        ))
        assertTrue(BookProgressRules.isComplete(catalog, restored, "space-volume-one"))
    }

    @Test fun byteTamperingOrTruncationIsRejected() {
        val original = BookProgressCodec.encode(catalog, example())
        val broken = original.clone()
        broken[15] = (broken[15].toInt() xor 0x01).toByte()
        reject { BookProgressCodec.decode(catalog, broken) }
        reject { BookProgressCodec.decode(catalog, original.copyOf(original.size - 1)) }
        reject { BookProgressCodec.decode(catalog, byteArrayOf()) }
        reject { BookProgressCodec.decode(catalog, ByteArray(BookProgressCodec.MAX_BYTES + 1)) }
    }

    @Test fun unknownFutureVersionCannotBeReadAsOldData() {
        val original = BookProgressCodec.encode(catalog, example())
        val payload = original.copyOf(original.size - 32)
        ByteBuffer.wrap(payload).putInt(4, 2)
        reject { BookProgressCodec.decode(catalog, resigned(payload)) }
    }

    @Test fun unexpectedTrailingFieldsAreNotSilentlyIgnored() {
        val original = BookProgressCodec.encode(catalog, example())
        val payload = original.copyOf(original.size - 32) + byteArrayOf(42)
        reject { BookProgressCodec.decode(catalog, resigned(payload)) }
    }

    @Test fun hugeOrNegativeRecordCountsAreRejectedBeforeAllocation() {
        val original = BookProgressCodec.encode(catalog, example())
        for (count in listOf(-1, Int.MAX_VALUE, 201)) {
            val payload = original.copyOf(original.size - 32)
            ByteBuffer.wrap(payload).putInt(8, count)
            reject { BookProgressCodec.decode(catalog, resigned(payload)) }
        }
    }

    @Test fun missingCatalogOrDeletedBookMustFailClosedNotEraseProgress() {
        val raw = BookProgressCodec.encode(catalog, example())
        reject { BookProgressCodec.decode(BookCatalog(), raw) }
        reject { BookProgressCodec.decode(BookCatalog(listOf(woodland)), raw) }
        assertEquals(example(), BookProgressCodec.decode(catalog, raw))
    }

    @Test fun newBooksDoNotInvalidateExistingLocalBookProgress() {
        val raw = BookProgressCodec.encode(BookCatalog(listOf(woodland)),
            BookLibraryProgress(mapOf(
                "woodland-volume-one" to example().volumes.getValue("woodland-volume-one")
            )))
        val upgraded = BookProgressCodec.decode(catalog, raw)
        assertEquals(2, BookProgressRules.completedCount(catalog, upgraded,
            "woodland-volume-one"))
        assertEquals(0, BookProgressRules.completedCount(catalog, upgraded,
            "space-volume-one"))
    }

    @Test fun incorrectOrCompletedBookmarksNeverRoundTripAsValid() {
        val bad = BookLibraryProgress(mapOf("woodland-volume-one" to
            BookVolumeProgress("woodland-volume-one", setOf("woodland-page-one"),
                "woodland-page-one")))
        reject { BookProgressCodec.encode(catalog, bad) }
        val unknown = BookLibraryProgress(mapOf("woodland-volume-one" to
            BookVolumeProgress("woodland-volume-one", setOf("missing-page"))))
        reject { BookProgressCodec.encode(catalog, unknown) }
    }

    @Test fun completeProgressRemainsIdenticalOnRepeatedBackupExport() {
        val original = example()
        val first = BookProgressCodec.encode(catalog, original)
        val again = BookProgressCodec.encode(catalog,
            BookProgressCodec.decode(catalog, first))
        assertArrayEquals(first, again)
    }
}
