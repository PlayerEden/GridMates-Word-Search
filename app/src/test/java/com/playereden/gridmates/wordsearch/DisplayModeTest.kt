package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayModeTest {
    @Test fun fullscreenIsFirstInstallDefaultButStoredStandardStillWorks() {
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.fromStoredValue(null))
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.fromStoredValue(""))
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.fromStoredValue("INVALID"))
        assertEquals(DisplayMode.STANDARD, DisplayMode.fromStoredValue("STANDARD"))
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.fromStoredValue("FULLSCREEN"))
    }

    @Test fun alternatingModesReturnsToOriginal() {
        assertEquals(DisplayMode.FULLSCREEN, DisplayMode.STANDARD.alternate())
        assertEquals(DisplayMode.STANDARD, DisplayMode.FULLSCREEN.alternate())
        assertEquals(DisplayMode.STANDARD, DisplayMode.STANDARD.alternate().alternate())
    }
}
