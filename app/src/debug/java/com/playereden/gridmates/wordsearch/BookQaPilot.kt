package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * DEBUG/QA BUILD ONLY. This source does NOT exist in release builds.
 *
 * Three real, seeded V2 word-search themes for hands-on phone testing of WS-11.
 * Every volume/page is explicitly a TEST fixture, not licensed/editor-approved
 * book content. These exact IDs are reserved for QA forever and MUST NEVER be
 * reused as published book IDs. Hashes are derived from existing V2 generator
 * solely here; production publisher manifests must instead pin reviewed hashes.
 */
internal object BookQaPilot {
    const val enabled = true

    fun catalog(): BookCatalog {
        fun page(
            name: String, theme: WordTheme, size: PuzzleBoardSize,
            challenge: PuzzleChallenge, seed: Long
        ): BookPage {
            val puzzle = WordSearchGeneratorV2.generate(theme, size, challenge, seed)
            return BookPage(
                id = name, puzzleId = puzzle.id,
                generatorVersion = puzzle.contentVersion,
                lexiconVersion = "qa-fixture-v2",
                boardSize = size, challenge = challenge,
                seed = seed, boardSha256 = BookBoardDigest.sha256(puzzle)
            )
        }
        fun volume(
            prefix: String, display: String, theme: WordTheme, baseSeed: Long
        ): PuzzleBook {
            return PuzzleBook(
                id = "$prefix-qa-volume-one",
                title = "$display · TEST BOOK",
                volume = 1, theme = theme,
                chapters = listOf(
                    BookChapter(
                        "$prefix-qa-chapter-one", "Getting Started · TEST",
                        listOf(
                            page("$prefix-qa-page-one", theme,
                                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED,
                                baseSeed),
                            page("$prefix-qa-page-two", theme,
                                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC,
                                baseSeed + 1L)
                        )
                    ),
                    BookChapter(
                        "$prefix-qa-chapter-two", "A Bigger Challenge · TEST",
                        listOf(
                            page("$prefix-qa-page-three", theme,
                                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC,
                                baseSeed + 2L)
                        )
                    )
                )
            )
        }
        return BookCatalog(listOf(
            volume("woodland", "Woodland", WordTheme.WOODLAND, 171L),
            volume("space", "Space", WordTheme.SPACE, 281L),
            volume("garden", "Garden", WordTheme.GARDEN, 391L)
        ))
    }
}
