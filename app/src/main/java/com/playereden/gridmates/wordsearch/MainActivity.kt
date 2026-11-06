package com.playereden.gridmates.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    private val soloStore by lazy { SoloStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Contrast: dark system indicators over GridMates' light paper background.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            GridMatesTheme {
                GridMatesShell(soloStore)
            }
        }
    }

    override fun onStop() {
        soloStore.flush()
        super.onStop()
    }
}
