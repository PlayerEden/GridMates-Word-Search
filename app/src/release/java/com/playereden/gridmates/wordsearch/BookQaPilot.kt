package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BookCatalog

/**
 * RELEASE BUILD: no unpublished/temporary/test puzzle-book content ships.
 * An approved production manifest gets a separate, explicitly reviewed route.
 */
internal object BookQaPilot {
    const val enabled = false
    fun catalog(): BookCatalog = BookCatalog()
}
