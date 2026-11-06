package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * These assertions are compiled in CI, but need a device/emulator for runtime
 * execution. The CI workflow independently checks the 12 actual APK entries.
 */
class ApprovedWoodlandAssetTest {
    @Test fun approvedOriginalsAreAvailableAsPackagedAndroidDrawables() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val originals = listOf(
            "woodland_owl", "woodland_fox", "woodland_rabbit",
            "woodland_bear", "woodland_badger", "woodland_hedgehog",
            "woodland_robin", "woodland_deer", "woodland_squirrel",
            "woodland_otter", "woodland_tortoise", "woodland_cat"
        )
        assertEquals(12, originals.distinct().size)
        for (name in originals) {
            val resId = context.resources.getIdentifier(
                name, "drawable", context.packageName
            )
            assertTrue("Missing portrait resource: $name", resId != 0)
            val input = context.resources.openRawResource(resId)
            input.use { stream ->
                val header = ByteArray(8)
                val len = stream.read(header)
                assertEquals("PNG header should be readable for $name", 8, len)
                assertTrue(
                    "$name must be a PNG image",
                    header.contentEquals(
                        byteArrayOf(
                            -119, 80, 78, 71, 13, 10, 26, 10
                        )
                    )
                )
            }
        }
    }
}
