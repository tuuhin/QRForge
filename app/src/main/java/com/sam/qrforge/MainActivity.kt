package com.sam.qrforge

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import com.sam.qrforge.data.utils.animateOnExit
import com.sam.qrforge.presentation.navigation.AppNavigation
import com.sam.qrforge.ui.theme.QRForgeTheme

class MainActivity : ComponentActivity() {

	lateinit var navHostController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		// set the splash screen
		val splash = installSplashScreen()
		splash.animateOnExit(splashViewDuration = 300)

		super.onCreate(savedInstanceState)

		//edge to edge
		enableEdgeToEdge()

		setContent {
			QRForgeTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					AppNavigation(
						onNavControllerReady = { controller ->
							navHostController = controller
						},
					)
				}
			}
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		// process the incoming intent if nav controller  is initialized
		if (::navHostController.isInitialized) {
			navHostController.handleDeepLink(intent)
		}
	}

}
