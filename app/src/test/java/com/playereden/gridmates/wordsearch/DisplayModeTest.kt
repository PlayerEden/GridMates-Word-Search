package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayModeTest {
    @Test fun parseStoredDisplayModeAlwaysHasSafeStandardFallback() {
        assertEquals(DisplayMode.STANDARD, DisplayMode.fromStoredValue(null))
        assertEquals(DisplayMode.STANDARD, DisplayMode.fromStoredValue(""))
        assertEquals(DisplayMode.STANDARD, DisplayMode.fromStoredValue("INVALID"))
        assertEquals(DisplayMode.STANDARD, DisplayMode.fromStoredValue("STANDARD"))
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.fromStoredValue("FULLSCREEN"))
    }

    @Test fun alternatingModesReturnsToOriginal() {
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.STANDARD.alternate())
        assertEquals(DisplayMode.STANDARD, DisplayMode.FULLSCREEN.alternate())
        assertEquals(DisplayMode.STANDARD, DisplayMode.STANDARD.alternate().alternate())
    }
}
