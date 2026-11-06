package com.playereden.gridmates.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private val soloStore by lazy { SoloStore(applicationContext) }
    private val nearbyRooms by lazy { ViewModelProvider(this)[NearbyRoomViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Contrast: dark system indicators over GridMates' light paper background.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            GridMatesTheme {
                GridMatesShell(soloStore, nearbyRooms)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        soloStore.setForeground(true)
    }

    override fun onStop() {
        // Compose's LaunchedEffect can survive the Activity moving to STOPPED.
        // Never count background time as active puzzle play.
        soloStore.setForeground(false)
        super.onStop()
    }
}
