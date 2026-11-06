package com.playereden.gridmates.wordsearch

import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Checks the live room owner is kept when Android re-creates an Activity. */
@RunWith(AndroidJUnit4::class)
class NearbyRoomLifecycleTest {
    @get:Rule val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test fun runtimeIdentitySurvivesActivityRecreation() {
        var initial: NearbyCoopRuntime? = null
        activity.scenario.onActivity { screen ->
            initial = ViewModelProvider(screen)[NearbyRoomViewModel::class.java]
                .runtime(screen.applicationContext)
            assertNotNull(initial)
        }
        activity.scenario.recreate()
        activity.scenario.onActivity { screen ->
            val resumed = ViewModelProvider(screen)[NearbyRoomViewModel::class.java]
                .runtime(screen.applicationContext)
            assertSame("Rotation must not close/recreate the nearby room", initial, resumed)
            assertEquals(NearbyRole.NONE, resumed.state.role)
        }
    }
}
