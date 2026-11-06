package com.playereden.gridmates.wordsearch

import android.graphics.drawable.AdaptiveIconDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Protects the install/launcher experience on modern Android. */
@RunWith(AndroidJUnit4::class)
class PaperbackLauncherAndroidTest {
    @Test fun launcherUsesAdaptiveIconInsteadOfBareTestDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val icon = context.packageManager.getApplicationIcon(context.packageName)
        assertTrue("Word Search should have a masked adaptive launcher icon",
            icon is AdaptiveIconDrawable)
        assertEquals("Word Search",
            context.packageManager.getApplicationLabel(context.applicationInfo).toString())
    }
}
