package com.playereden.gridmates.wordsearch.core

/** A strictly local profile. Portrait IDs are shared with GridMates: Sudoku,
 * but saves and unlock histories are *not* shared between separate apps.
 */
data class PlayerIdentity(
    val displayName: String = "Player",
    val portraitId: String = "woodland_owl"
)

data class PortraitDefinition(
    val id: String,
    val label: String,
    val requiredCompletions: Int
)

data class PortraitProgress(
    val portrait: PortraitDefinition,
    val unlocked: Boolean,
    val equipped: Boolean
)

object WoodlandCollection {
    // Stable IDs and labels verified against Sudoku
    // design/woodland/packaged-assets.json on feat/gridmates-nearby-animals.
    // Original PNGs still need a safe binary import; never invent replacements.
    val portraits = listOf(
        PortraitDefinition("woodland_owl", "Owl", 0),
        PortraitDefinition("woodland_fox", "Fox", 0),
        PortraitDefinition("woodland_rabbit", "Rabbit", 1),
        PortraitDefinition("woodland_bear", "Bear", 2),
        PortraitDefinition("woodland_badger", "Badger", 3),
        PortraitDefinition("woodland_hedgehog", "Hedgehog", 4),
        PortraitDefinition("woodland_robin", "Robin", 5),
        PortraitDefinition("woodland_deer", "Deer", 7),
        PortraitDefinition("woodland_squirrel", "Squirrel", 10),
        PortraitDefinition("woodland_otter", "Otter", 14),
        PortraitDefinition("woodland_tortoise", "Tortoise", 20),
        PortraitDefinition("woodland_cat", "Cat", 30)
    )
    private val byId = portraits.associateBy { it.id }
    val ids: Set<String> = byId.keys

    fun completedCount(profile: SoloProfile): Int =
        profile.completions + profile.dailyCompletions

    fun unlocked(profile: SoloProfile): Set<String> {
        val count = completedCount(profile)
        return portraits.filter { it.requiredCompletions <= count }.map { it.id }.toSet()
    }

    fun progress(profile: SoloProfile): List<PortraitProgress> {
        val available = unlocked(profile)
        return portraits.map {
            PortraitProgress(it, it.id in available, it.id == profile.identity.portraitId)
        }
    }

    fun sanitizeName(input: String): String {
        val cleaned = input.trim().filterNot(Char::isISOControl).take(24).trim()
        return cleaned.ifEmpty { "Player" }
    }

    fun sanitize(identity: PlayerIdentity, profile: SoloProfile): PlayerIdentity {
        val cleanName = sanitizeName(identity.displayName)
        val chosen = identity.portraitId.takeIf { it in unlocked(profile) } ?: "woodland_owl"
        return PlayerIdentity(cleanName, chosen)
    }

    fun equip(profile: SoloProfile, portraitId: String): SoloProfile? =
        if (portraitId in unlocked(profile)) {
            profile.copy(identity = profile.identity.copy(portraitId = portraitId))
        } else null

    fun remaining(profile: SoloProfile, portraitId: String): Int? {
        val def = byId[portraitId] ?: return null
        return (def.requiredCompletions - completedCount(profile)).coerceAtLeast(0)
    }
}

data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val metric: (SoloProfile) -> Int
)

data class AchievementProgress(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val target: Int
) {
    val unlocked: Boolean get() = current >= target
}

object WordSearchAchievements {
    val definitions = listOf(
        AchievementDefinition("first_puzzle", "First Page", "Complete your first puzzle.", 1) {
            it.completions + it.dailyCompletions
        },
        AchievementDefinition("five_puzzles", "Puzzle Regular", "Complete five puzzles.", 5) {
            it.completions + it.dailyCompletions
        },
        AchievementDefinition("twenty_puzzles", "Puzzle Keeper", "Complete twenty puzzles.", 20) {
            it.completions + it.dailyCompletions
        },
        AchievementDefinition("one_daily", "Today Counts", "Finish a Daily puzzle.", 1) {
            it.dailyCompletions
        },
        AchievementDefinition("seven_dailies", "Seven Good Days", "Finish seven dated puzzles. No streak required.", 7) {
            it.dailyCompletions
        },
        AchievementDefinition("no_hints", "Sharp Eyes", "Finish a Solo puzzle without hints.", 1) {
            it.results.count { result -> !result.assisted }
        },
        AchievementDefinition("hard_solo", "Word Hunter", "Finish a Hard Solo puzzle.", 1) {
            it.results.count { result -> result.difficulty == WordDifficulty.HARD }
        },
        AchievementDefinition("all_themes", "Three Chapters", "Complete a Solo puzzle in Woodland, Space and Garden.", 3) {
            it.results.map { result -> result.theme }.distinct().size
        }
    )

    fun progress(profile: SoloProfile): List<AchievementProgress> =
        definitions.map { def ->
            AchievementProgress(
                def.id, def.title, def.description,
                def.metric(profile).coerceIn(0, def.target), def.target
            )
        }
}
