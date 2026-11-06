package com.playereden.gridmates.wordsearch.core

/** Free, evidence-backed cosmetic typography. No storefront or hidden currency. */
data class BoardFontDefinition(
    val id: String,
    val label: String,
    val description: String,
    val requiredAchievementId: String? = null
)

data class BoardFontProgress(
    val font: BoardFontDefinition,
    val unlocked: Boolean,
    val equipped: Boolean,
    val achievementName: String?
)

/**
 * Ownership is derived from durable Solo/Daily records already saved in the
 * profile. A restored save automatically earns past achievements; a repeated
 * completion never grants duplicate inventory. The selected cosmetic ID is
 * kept as a small Android device preference, not part of the puzzle seed.
 */
object BoardFontCollection {
    const val DEFAULT_ID = "atkinson"

    val definitions: List<BoardFontDefinition> = listOf(
        BoardFontDefinition("atkinson", "Readable Print",
            "Distinct, carefully centered letters. Always available."),
        BoardFontDefinition("plex", "Classic Typewriter",
            "An even-width typewriter feel.", "first_puzzle"),
        BoardFontDefinition("dmmono", "Modern Print",
            "Clean, understated letterforms.", "five_puzzles"),
        BoardFontDefinition("patrick", "Pencil Notes",
            "Friendly handwriting for a paper-puzzle mood.", "one_daily")
    )
    private val byId = definitions.associateBy { it.id }

    fun unlocked(profile: SoloProfile): Set<String> {
        val earned = WordSearchAchievements.progress(profile)
            .filter { it.unlocked }.map { it.id }.toSet()
        return definitions.filter {
            it.requiredAchievementId == null || it.requiredAchievementId in earned
        }.map { it.id }.toSet()
    }

    fun allowed(profile: SoloProfile, id: String): Boolean =
        id in unlocked(profile)

    fun effective(profile: SoloProfile, requested: String?): String =
        requested?.takeIf { allowed(profile, it) } ?: DEFAULT_ID

    fun progress(profile: SoloProfile, requestedId: String?): List<BoardFontProgress> {
        val open = unlocked(profile)
        val equipped = effective(profile, requestedId)
        val achievements = WordSearchAchievements.definitions.associateBy { it.id }
        return definitions.map { definition ->
            BoardFontProgress(
                definition,
                definition.id in open,
                definition.id == equipped,
                definition.requiredAchievementId?.let { achievements[it]?.title }
            )
        }
    }
}
