package com.playereden.gridmates.wordsearch

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private val soloStore by lazy { SoloStore(applicationContext) }
    private val nearbyRooms by lazy { ViewModelProvider(this)[NearbyRoomViewModel::class.java] }
    private val qaBookshelf by lazy { QaBookshelfRuntime(applicationContext) }

    private var displayMode by mutableStateOf(DisplayMode.FULLSCREEN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Drawing parchment behind transparent bars works on older Android too.
        // Android 15 (targetSdk 35) otherwise forces edge-to-edge anyway.
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For three-button navigation this removes the opaque contrast scrim.
            // The dark system icons remain legible against our pale parchment.
            window.isNavigationBarContrastEnforced = false
        }
        displayMode = DisplayModePreferences.load(applicationContext)
        applyDisplayMode(displayMode)

        setContent {
            GridMatesTheme {
                GridMatesShell(
                    soloStore, nearbyRooms, qaBookshelf,
                    displayMode = displayMode,
                    onDisplayModeChange = { requested ->
                        if (requested != displayMode) {
                            displayMode = requested
                            DisplayModePreferences.save(applicationContext, requested)
                            applyDisplayMode(requested)
                        }
                    }
                )
            }
        }
    }

    private fun applyDisplayMode(mode: DisplayMode) {
        val bars = WindowCompat.getInsetsController(window, window.decorView)
        bars.isAppearanceLightStatusBars = true
        bars.isAppearanceLightNavigationBars = true
        if (mode == DisplayMode.FULLSCREEN) {
            bars.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            bars.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            bars.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            bars.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Restore fullscreen after an external document picker or app switch.
        // Transient swipe-revealed bars do not change window focus.
        if (hasFocus) applyDisplayMode(displayMode)
    }

    override fun onStart() {
        super.onStart()
        soloStore.setForeground(true)
        qaBookshelf.setForeground(true)
    }

    override fun onStop() {
        // Compose's LaunchedEffect can survive the Activity moving to STOPPED.
        // Never count background time as active puzzle play.
        soloStore.setForeground(false)
        qaBookshelf.setForeground(false)
        super.onStop()
    }
}
