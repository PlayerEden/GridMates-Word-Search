package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The theme accent never displaces required page controls for small/large-text UIs. */
class Ws14GPageEmblemLayoutTest {
    @Test fun activeQaPagesShowAccentAtNormalPhoneWidth() {
        assertTrue(shouldShowBookPageHeaderEmblem(true, false, false, 360f, 1f))
        assertTrue(shouldShowBookPageHeaderEmblem(true, false, false, 320f, 1.2f))
    }

    @Test fun releaseOrCrowdedOrAlreadyThemedPagesAvoidExtraDecoration() {
        assertFalse(shouldShowBookPageHeaderEmblem(false, false, false, 360f, 1f))
        assertFalse(shouldShowBookPageHeaderEmblem(true, false, false, 300f, 1f))
        assertFalse(shouldShowBookPageHeaderEmblem(true, false, false, 360f, 2f))
        assertFalse(shouldShowBookPageHeaderEmblem(true, true, false, 360f, 1f))
        assertFalse(shouldShowBookPageHeaderEmblem(true, false, true, 360f, 1f))
    }
}
