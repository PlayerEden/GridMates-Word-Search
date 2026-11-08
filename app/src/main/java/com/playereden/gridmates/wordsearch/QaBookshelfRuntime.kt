package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog

/**
 * App-local, offline-only preview runtime. The release source set supplies an
 * empty catalog and no stores. On debug/QA builds the files use NEW names and
 * NEVER touch existing Solo schema-5, Daily, Nearby or production book saves.
 */
internal class QaBookshelfRuntime(context: Context) {
    val enabled: Boolean = BookQaPilot.enabled
    val catalog: BookCatalog = if (enabled) BookQaPilot.catalog() else BookCatalog()
    val progressStore: BookLibraryStore? = if (enabled) BookLibraryStore(
        context.applicationContext, catalog,
        "gridmates_wordsearch_ws11_qa_progress_v1.dat"
    ) else null
    val sessionStore: BookPageSessionStore? = if (enabled) BookPageSessionStore(
        context.applicationContext, catalog,
        "gridmates_wordsearch_ws11_qa_sessions_v1.dat"
    ) else null

    fun setForeground(foreground: Boolean) {
        sessionStore?.setForeground(foreground)
    }
}
