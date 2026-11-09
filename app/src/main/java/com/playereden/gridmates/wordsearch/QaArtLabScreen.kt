package com.playereden.gridmates.wordsearch

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * WS-12G — owner-review-only Art Lab in the signed QA build.
 *
 * This loads the exact deterministic WS-12F contact sheet from a bundled
 * LOCAL HTML + SVG asset directory. JS, remote network and navigation are
 * disabled; no backend or model generation is required on the phone.
 * It never replaces existing Bookshelf cards, Collections or puzzle visuals.
 *
 * Its launcher is gated to BookQaPilot.enabled (debug source set only);
 * release has no launcher and no art preview assets.
 */
@Composable
internal fun QaArtLabScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    var showingWs14 by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val packaged = remember(context) {
        runCatching {
            context.assets.open("ws12/review.html").use { it.read() >= 0 }
        }.getOrDefault(false)
    }
    Column(
        Modifier.fillMaxSize().background(Cream),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp).heightIn(min = 48.dp)
        ) { Text("Back to Play") }
        TextButton(
            onClick = { showingWs14 = !showingWs14 },
            modifier = Modifier.padding(start = 8.dp).heightIn(min = 48.dp)
        ) {
            Text(if (showingWs14) "Back to WS-12 lab" else "Review WS-14 assets")
        }
        if (showingWs14) {
            QaWs14ArtworkReview()
        } else if (!packaged) {
            Text("Art Lab preview not packaged in this APK.",
                color = Forest,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 18.dp))
            Text("The existing Word Search and Puzzle Books are unaffected.",
                color = Muted,
                modifier = Modifier.padding(horizontal = 18.dp))
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = false
                        settings.domStorageEnabled = false
                        settings.blockNetworkLoads = true
                        settings.allowContentAccess = false
                        settings.allowFileAccess = true
                        settings.setSupportZoom(false)
                        isVerticalScrollBarEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?, request: WebResourceRequest?
                            ): Boolean = request?.url?.toString()
                                ?.startsWith("file:///android_asset/ws12/") != true
                            @Deprecated("Legacy WebView API")
                            override fun shouldOverrideUrlLoading(
                                view: WebView?, url: String?
                            ): Boolean = url?.startsWith(
                                "file:///android_asset/ws12/"
                            ) != true
                        }
                        loadUrl("file:///android_asset/ws12/review.html")
                    }
                }
            )
        }
    }
}
