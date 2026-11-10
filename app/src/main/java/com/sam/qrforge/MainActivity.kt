package com.sam.qrforge

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavKey
import com.sam.qrforge.data.utils.animateOnExit
import com.sam.qrforge.presentation.navigation.AppNavigation
import com.sam.qrforge.presentation.navigation.nav_graph.NavDeepLinks
import com.sam.qrforge.ui.theme.QRForgeTheme

class MainActivity : ComponentActivity() {

    private var deepLinkStack by mutableStateOf<List<NavKey>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the splash screen
        val splash = installSplashScreen()
        splash.animateOnExit(splashViewDuration = 300)

        super.onCreate(savedInstanceState)

        //edge to edge
        enableEdgeToEdge()

        // Only on a fresh launch; on recreation the saved back stack should win
        if (savedInstanceState == null) handleDeepLink(intent)

        setContent {
            QRForgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation(
                        linkedBackStack = deepLinkStack,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        // if the intent is null skip it
        if (intent == null) return
        val resolved = NavDeepLinks.resolveBackStack(intent).toList()
        if (resolved.size > 1) deepLinkStack = resolved
    }
}
