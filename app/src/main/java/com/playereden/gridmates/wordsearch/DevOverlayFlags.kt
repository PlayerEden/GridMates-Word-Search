package com.playereden.gridmates.wordsearch

/** Internal visual diagnostics. Defaults are off and never affect saved game data. */
internal data class DevOverlayFlags(
    val enabled: Boolean = false,
    val grid: Boolean = true,
    val bounds: Boolean = true,
    val safe: Boolean = true,
    val touch: Boolean = false
)
