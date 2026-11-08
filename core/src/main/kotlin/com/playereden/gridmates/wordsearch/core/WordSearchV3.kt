package com.playereden.gridmates.wordsearch.core

import kotlin.random.Random

/**
 * WS-09E: entirely NEW, version-3 layouts. Never alter frozen v1/v2 vocabulary,
 * placement, Random call order or IDs to expand these boards.
 *
 * All vocabulary is hand-curated and reviewed for single-token display.
 * Frequency tiers are editorial guidance, not a measured English corpus.
 */
object WordLibraryV3 {
    private data class Pack(val familiar: List<String>, val advanced: List<String>)

    private fun parse(words: String): List<String> =
        words.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

    private fun pack(everyday: String, familiar: String, advanced: String): Pack {
        val common = parse(everyday)
        val intermediate = parse(familiar)
        val difficult = parse(advanced)
        val all = common + intermediate + difficult
        require(all.size >= 65 && all.distinct().size == all.size) {
            "V3 word pack must have at least 65 unique entries"
        }
        require(all.all { it.length in 5..16 && it.all { c -> c in 'A'..'Z' } }) {
            "Word Search v3 only supports 5-16 letter A-Z words"
        }
        require((common + intermediate).size >= 50)
        return Pack(common + intermediate, difficult)
    }

    // Intentional separate pack from V2. Avoid editing once v3 saves exist:
    // introduce a distinct versioned generator for later content changes.
    private val packs = mapOf(
        WordTheme.WOODLAND to pack(
            """
            FOREST RABBIT SQUIRREL RACCOON BADGER OTTER BEAVER CHIPMUNK
            HEDGEHOG PORCUPINE WOODPECKER ROBIN BLUEBIRD BLACKBIRD PINECONE
            MUSHROOM ACORN SAPLING MEADOW STREAM RIVERBANK TRAILHEAD
            EVERGREEN SUNLIGHT FOOTPRINT TREEHOUSE WILDFLOWER WILDLIFE
            """,
            """
            CANOPY THICKET UNDERGROWTH NESTLING NUTHATCH KINGFISHER
            NIGHTINGALE SPARROW WARBLER ORIOLE CARDINAL HUMMINGBIRD
            WOODCHUCK GROUNDHOG WILDCAT BOBCAT REINDEER FOXGLOVE BIRCH
            HICKORY REDWOOD ASPEN SPRUCE WILLOW CEDAR POPLAR BEECH
            CHESTNUT SEQUOIA SYCAMORE MAPLEWOOD PINEWOOD BLACKBERRY
            BLUEBERRY SNOWSHOE BROOKSIDE CREEKBED BRAMBLE
            """,
            """
            LICHEN BRACKEN MYCELIUM POLYPHORE BOREALIS AMPHIBIAN
            ARBOREAL CRANBERRY FERNBRACKEN SALAMANDER KESTREL
            KINGLET FLYCATCHER WAXWING EARTHWORM
            """
        ),
        WordTheme.SPACE to pack(
            """
            PLANET ASTEROID METEOR COMET GALAXY NEBULA ROCKET SHUTTLE
            SATELLITE ASTRONAUT SPACECRAFT STARLIGHT MOONLIGHT SUNRISE
            SUNSET ECLIPSE ORBIT JUPITER SATURN MERCURY VENUS PLUTO
            SOLAR GRAVITY CRATER TELESCOPE COSMOS LAUNCH MISSION STATION
            """,
            """
            COSMONAUT INTERSTELLAR SUPERNOVA LIGHTYEAR EXOPLANET
            EQUINOX SOLSTICE MILKYWAY CONSTELLATION AURORA IONOSPHERE
            THRUSTER BOOSTER CAPSULE PAYLOAD ROVER LANDER ORBITER
            MODULE DOCKING REENTRY ATMOSPHERE VACUUM RADIATION
            PHOTON PROTON ELECTRON PLASMA PULSAR QUASAR BLACKHOLE
            REDSHIFT SPECTRUM INFINITY SOLSTICES COMETARY NEUTRON
            """,
            """
            PERIHELION APHELION NEUTRINO HELIOSPHERE MAGNETAR
            SINGULARITY PARALLAX PRECESSION RETROGRADE ORRERY
            MAGNETOSPHERE EXOSPHERE SOLARWIND ASTROMETRY
            """
        ),
        WordTheme.GARDEN to pack(
            """
            FLOWER GARDEN TULIPS DAISIES ROSES PEONIES SUNFLOWER
            LAVENDER MARIGOLD HYDRANGEA BUTTERFLY LADYBUG HONEYBEE
            BLOSSOM SPROUT SEEDLING PETALS ROOTS THYME BASIL
            ROSEMARY WATERING PLANTER MULCH COMPOST HARVEST
            SHOVEL TROWEL GREENHOUSE
            """,
            """
            POLLEN NECTAR ORCHID AZALEA CAMELLIA DAHLIA ZINNIA
            PETUNIA VIOLET GERANIUM JASMINE MAGNOLIA HIBISCUS
            HOLLYHOCK NASTURTIUM DAFFODIL BLUEBELL FOXGLOVE
            PANSIES LILIES MEADOWSWEET PRUNING TRELLIS GARDENER
            TOPSOIL SUNSHINE HUMMINGBIRD IRRIGATION WILDFLOWER
            WINDCHIME WATERLILY PLANTING HEDGEROW SPRINKLER
            PRIMROSE CHRYSANTHEMUM
            """,
            """
            PERENNIAL ANNUAL BOTANICAL BOTANIST RHIZOME STAMEN
            PISTIL CHLOROPHYLL PHOTOSYNTHESIS GERMINATION
            POLLINATOR VERBENA CALENDULA ROOTSTOCK
            """
        )
    )

    fun count(theme: WordTheme): Int =
        packs.getValue(theme).let { it.familiar.size + it.advanced.size }

    /** Prevent forced duplicate occurrences from selected substring pairs. */
    internal fun words(
        theme: WordTheme, board: PuzzleBoardSize,
        challenge: PuzzleChallenge, seed: Long, attempt: Int
    ): List<String> {
        require(board in PuzzleBoardSize.largePresets)
        val pack = packs.getValue(theme)
        val rng = Random(seed xor (theme.ordinal.toLong() shl 29) xor
            (board.ordinal.toLong() shl 18) xor
            (challenge.ordinal.toLong() shl 9) xor
            (attempt.toLong() * 0x52A85L) xor 0x33D091L)
        val rare = if (challenge == PuzzleChallenge.RELAXED) emptyList()
            else pack.advanced.shuffled(rng).take(
                if (challenge == PuzzleChallenge.CLASSIC) 3 else 6
            )
        val eligible = if (challenge == PuzzleChallenge.RELAXED) pack.familiar
            else pack.familiar + pack.advanced
        val selected = mutableListOf<String>()
        for (word in rare + eligible.shuffled(rng)) {
            if (selected.none { it == word || it.contains(word) || word.contains(it) }) {
                selected += word
            }
            if (selected.size == board.targets) break
        }
        require(selected.size == board.targets) {
            "Curated v3 pack cannot supply ${board.targets} unambiguous target words"
        }
        return selected.sortedByDescending { it.length }
    }
}

/** Bounded near-constant-time placement sampling rather than materializing
 * every path in a 30×30×8 grid for every one of 44 words. */
object WordSearchGeneratorV3 {
    const val CONTENT_VERSION = 3
    private data class Direction(val dr: Int, val dc: Int)
    private val easy = listOf(Direction(0, 1), Direction(1, 0))
    private val classic = listOf(
        Direction(0, 1), Direction(1, 0), Direction(1, 1),
        Direction(1, -1), Direction(0, -1), Direction(-1, 0)
    )
    private val all = (-1..1).flatMap { r ->
        (-1..1).mapNotNull { c ->
            if (r == 0 && c == 0) null else Direction(r, c)
        }
    }
    private const val FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"

    fun puzzleId(theme: WordTheme, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge, seed: Long): String {
        require(board in PuzzleBoardSize.largePresets)
        return "wordsearch:v3:en:${theme.name}:${board.name}:${challenge.name}:$seed"
    }

    fun generate(theme: WordTheme, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge, seed: Long): WordSearchPuzzle {
        require(board in PuzzleBoardSize.largePresets) {
            "Only new 25×25/30×30 boards use v3"
        }
        val directions = when (challenge) {
            PuzzleChallenge.RELAXED -> easy
            PuzzleChallenge.CLASSIC -> classic
            PuzzleChallenge.EXPERT -> all
        }
        val size = board.cells
        val id = puzzleId(theme, board, challenge, seed)
        repeat(24) { attempt ->
            val rng = Random(seed xor 0x51AF09E20B7DL xor
                ((theme.ordinal + 1L) shl 34) xor
                ((board.ordinal + 1L) shl 20) xor
                ((challenge.ordinal + 1L) shl 12) xor attempt.toLong())
            val words = WordLibraryV3.words(theme, board, challenge, seed, attempt)
            val grid = Array(size) { CharArray(size) { ' ' } }
            val targets = mutableListOf<WordTarget>()
            var allPlaced = true
            for (word in words) {
                var firstFeasible: List<GridPoint>? = null
                var preferred: List<GridPoint>? = null
                repeat(120) {
                    val dir = directions[rng.nextInt(directions.size)]
                    val rowFirst = if (dir.dr < 0) word.length - 1 else 0
                    val rowEnd = if (dir.dr > 0) size - word.length + 1 else size
                    val colFirst = if (dir.dc < 0) word.length - 1 else 0
                    val colEnd = if (dir.dc > 0) size - word.length + 1 else size
                    val row = rng.nextInt(rowFirst, rowEnd)
                    val col = rng.nextInt(colFirst, colEnd)
                    var overlap = 0
                    var fits = true
                    for (i in word.indices) {
                        val current = grid[row + i * dir.dr][col + i * dir.dc]
                        if (current != ' ' && current != word[i]) {
                            fits = false
                            break
                        }
                        if (current == word[i]) overlap++
                    }
                    if (fits) {
                        val path = List(word.length) { i ->
                            GridPoint(row + i * dir.dr, col + i * dir.dc)
                        }
                        if (firstFeasible == null) firstFeasible = path
                        if (overlap > 0 && preferred == null) preferred = path
                    }
                }
                val chosen = if (challenge == PuzzleChallenge.RELAXED)
                    firstFeasible else preferred ?: firstFeasible
                if (chosen == null) {
                    allPlaced = false
                    break
                }
                chosen.forEachIndexed { i, p -> grid[p.row][p.col] = word[i] }
                targets += WordTarget(word, word, chosen)
            }
            if (!allPlaced) return@repeat
            for (r in 0 until size) for (c in 0 until size)
                if (grid[r][c] == ' ') grid[r][c] = FILLER[rng.nextInt(FILLER.length)]
            if (targets.any { occurrences(grid, it.word) != 1 }) return@repeat
            return WordSearchPuzzle(
                id, seed, CONTENT_VERSION, theme, challenge.legacyDifficulty,
                grid.map { it.concatToString() }, targets
            )
        }
        throw IllegalStateException("Could not build unique v3 ${board.name} puzzle")
    }

    /** Fast first-letter rejection and strict forward+reverse occurrence count. */
    private fun occurrences(grid: Array<CharArray>, word: String): Int {
        val size = grid.size
        var matches = 0
        for (r in 0 until size) for (c in 0 until size) {
            if (grid[r][c] != word[0]) continue
            for (dir in all) {
                val lastR = r + (word.length - 1) * dir.dr
                val lastC = c + (word.length - 1) * dir.dc
                if (lastR !in 0 until size || lastC !in 0 until size) continue
                var good = true
                for (i in 1 until word.length) {
                    if (grid[r + i * dir.dr][c + i * dir.dc] != word[i]) {
                        good = false
                        break
                    }
                }
                if (good && ++matches > 1) return matches
            }
        }
        return matches
    }
}
