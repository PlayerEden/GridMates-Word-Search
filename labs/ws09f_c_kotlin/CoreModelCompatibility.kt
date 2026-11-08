package ws09fc.lab

import com.playereden.gridmates.wordsearch.core.*
import java.io.File
import java.security.MessageDigest

/**
 * Core-model compatibility harness only.
 * Explicitly compile with the real WordSearch.kt / V2.kt / V3.kt and the
 * isolated lab engine; this file is NOT under core/main or Android/app.
 *
 * V4 is NOT routed through SoloProgressRules or exposed in New Puzzle.
 */
private fun asCore(theme: WordTheme, challenge: PuzzleChallenge, seed: Long,
                   experimental: V4EngineLab.Puzzle): WordSearchPuzzle {
    require(experimental.id.startsWith("wordsearch:v4:LAB:en:"))
    require(experimental.size in setOf(40, 50))
    val converted = experimental.targets.map { target ->
        WordTarget(target.word, target.word, target.cells.map {
            GridPoint(it.row, it.col)
        })
    }
    return WordSearchPuzzle(
        id = experimental.id,
        seed = seed,
        contentVersion = 4,
        theme = theme,
        difficulty = challenge.legacyDifficulty,
        rows = experimental.rows,
        targets = converted
    )
}

private fun exactFingerprint(p: WordSearchPuzzle): String {
    val canonical = buildString {
        append(p.id).append('\n')
        append(p.contentVersion).append('\n')
        p.rows.forEach { append(it).append('\n') }
        append("--targets--\n")
        p.targets.forEach { t ->
            append(t.id).append('|').append(t.word).append('|')
            append(t.positions.joinToString(";") { point ->
                point.row.toString() + "," + point.col.toString()
            })
            append('\n')
        }
    }
    return MessageDigest.getInstance("SHA-256")
        .digest(canonical.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
}

private fun verifyHistoryIsFrozen() {
    // These literals come from previously passed independent core goldens.
    // Never calculate expected hashes by calling the generator under test.
    val historical = listOf(
        Triple("v1-woodland-easy", WordSearchGenerator.generate(
            WordTheme.WOODLAND, WordDifficulty.EASY, 42L),
            "d2a14dac7c4a5172cc82784550a5dae6e02c70828658d299c73e55440cbec8be"),
        Triple("v2-woodland-bookclassic", WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 42L),
            "75f96dab9a80714d393abc071a125c2c7477f4961f4d8ada2dbb8a18eb97f42c"),
        Triple("v2-garden-bookbig", WordSearchGeneratorV2.generate(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.CLASSIC, 76124L),
            "b72723ecf722c966073ad084eb0e5d628d110aca9dee176e7f664db96810226d"),
        Triple("v3-woodland-grand", WordSearchGeneratorV3.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_GRAND,
            PuzzleChallenge.RELAXED, 42L),
            "b0de609f74c15b1d6631d497d14e467645654488ca1b9c330b37362553494d17"),
        Triple("v3-space-giant", WordSearchGeneratorV3.generate(
            WordTheme.SPACE, PuzzleBoardSize.BOOK_GIANT,
            PuzzleChallenge.CLASSIC, 20261108L),
            "ea668c622cc0923111b2504d34331c9823e07576bfda367678fe09e1353a4509"),
        Triple("v3-garden-giant", WordSearchGeneratorV3.generate(
            WordTheme.GARDEN, PuzzleBoardSize.BOOK_GIANT,
            PuzzleChallenge.EXPERT, 76124L),
            "5493e412fcb5cc589cd82e688738b2a9171b0689753e2b2e849e2e867e845250")
    )
    for ((name, puzzle, expected) in historical) {
        check(exactFingerprint(puzzle) == expected) {
            "STOP: frozen historical rows / targets changed for " + name
        }
    }
    check(PuzzleBoardSize.entries.map { it.name } == listOf(
        "COMPACT", "STANDARD", "LARGE", "BOOK_SHORT", "BOOK_CLASSIC",
        "BOOK_BIG", "BOOK_GRAND", "BOOK_GIANT"
    )) { "STOP: saved puzzle enum name / ordinal changed" }
    check(PuzzleBoardSize.largePresets.map { it.cells } == listOf(25, 30))
    check(PuzzleBoardSize.soloPresets.none { it.cells >= 40 })
    check(WordSearchGenerator.CONTENT_VERSION == 1)
    check(WordSearchGeneratorV2.CONTENT_VERSION == 2)
    check(WordSearchGeneratorV3.CONTENT_VERSION == 3)
    println("PASS 6 independent historical v1/v2/v3 literal goldens; exact legacy board ordinals retained.")
}

fun main(args: Array<String>) {
    verifyHistoryIsFrozen()
    val fixture = args.getOrNull(0) ?: "labs/ws09f_c_kotlin/candidates_test_only.txt"
    val pools = File(fixture).readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .associate { line ->
            val parts = line.split("|", limit = 2)
            require(parts.size == 2)
            parts[0] to parts[1].split(" ").filter { it.isNotBlank() }
        }
    check(pools.keys == setOf("WOODLAND", "SPACE", "GARDEN"))
    val seeds = listOf(11L, 42L, 2026L, 0L, 1L, 7L, 19L, 77L, 144L, 999L)
    var boards = 0
    var foundChecks = 0
    for (theme in WordTheme.entries) {
        for (size in listOf(40, 50)) {
            for (mode in V4EngineLab.Mode.entries) {
                val difficulty = PuzzleChallenge.valueOf(mode.name)
                for (seed in seeds) {
                    val raw = V4EngineLab.generate(theme.name, size, mode, seed,
                        pools.getValue(theme.name))
                    V4EngineLab.verify(raw, mode)
                    val p = asCore(theme, difficulty, seed, raw)
                    check(p.id == raw.id && p.id.startsWith("wordsearch:v4:LAB:"))
                    check(p.contentVersion == 4)
                    check(p.theme == theme)
                    check(p.difficulty == difficulty.legacyDifficulty)
                    check(p.size == size)
                    check(p.rows == raw.rows && p.targets.size == (if (size == 40) 70 else 105))
                    // Independently exercise the actual core's find/duplicate/reverse
                    // and completion semantics for every experimental saved path.
                    var found: Set<String> = emptySet()
                    for (t in p.targets) {
                        check(t.word == t.positions.map { p.letter(it) }.joinToString(""))
                        check(WordSearchRules.line(t.positions.first(), t.positions.last(), size)
                            == t.positions)
                        val first = t.positions.first()
                        val last = t.positions.last()
                        check(WordSearchRules.claim(p, found, first, last).status ==
                            ClaimStatus.FOUND)
                        found = found + t.id
                        check(WordSearchRules.claim(p, found, last, first).status ==
                            ClaimStatus.ALREADY_FOUND)
                        foundChecks++
                    }
                    check(WordSearchRules.complete(p, found))
                    check(!WordSearchRules.complete(p, found - p.targets.first().id))
                    check(WordSearchRules.claim(p, found,
                        GridPoint(-1, 0), GridPoint(0, 0)).status ==
                        ClaimStatus.INVALID)
                    boards++
                }
            }
        }
    }
    println("PASS " + boards + "/180 real WordSearchPuzzle model conversions, " +
        foundChecks + " forward/reverse FOUND/ALREADY_FOUND path checks, " +
        "and completion/invalid-selection assertions.")
    println("NO Android source, UI, saved schema, shipping content or production routing changed.")
}
