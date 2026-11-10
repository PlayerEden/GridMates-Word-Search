package com.playereden.gridmates.wordsearch

import android.content.Context

/** Independent local UI preference; never enters the puzzle/save backup schema. */
internal enum class DisplayMode(val label: String) {
    STANDARD("Standard"),
    FULLSCREEN("Fullscreen");

    fun alternate(): DisplayMode = if (this == STANDARD) FULLSCREEN else STANDARD

    companion object {
        // Persisted explicit Standard remains respected after an upgrade.
        // Only users without a choice get the fullscreen-first default.
        fun fromStoredValue(value: String?): DisplayMode =
            entries.firstOrNull { it.name == value } ?: FULLSCREEN
    }
}

internal object DisplayModePreferences {
    private const val FILE = "gridmates_display_preferences"
    private const val KEY = "system_bar_display_mode"

    fun load(context: Context): DisplayMode = DisplayMode.fromStoredValue(
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY, null)
    )

    fun save(context: Context, mode: DisplayMode) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY, mode.name).apply()
    }
}
