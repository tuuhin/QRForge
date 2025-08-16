package com.sam.qrforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sam.qrforge.presentation.navigation.AppNavigation
import com.sam.qrforge.ui.theme.QRForgeTheme

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		// set the splash screen
		installSplashScreen()

		super.onCreate(savedInstanceState)

		//edge to edge
		enableEdgeToEdge()

		setContent {
			QRForgeTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					AppNavigation()
				}
			}
		}
	}
}
