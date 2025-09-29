package com.sam.qrforge

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.compose.runtime.ExperimentalComposeRuntimeApi
import com.sam.qrforge.di.appModule
import com.sam.qrforge.di.flavourModule
import com.sam.qrforge.di.mlKitModule
import com.sam.qrforge.di.presentationModule
import com.sam.qrforge.domain.provider.AppShortcutProvider
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

@OptIn(
	KoinExperimentalAPI::class,
	ExperimentalComposeRuntimeApi::class
)
class QRForgeApp : Application(), KoinStartup {

	private val _shortcutManager by inject<AppShortcutProvider>()

	override fun onCreate() {
		super.onCreate()
		Composer.setDiagnosticStackTraceEnabled(enabled = BuildConfig.DEBUG)
		_shortcutManager.setShortcuts()
	}

	override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
		androidContext(this@QRForgeApp)
		androidLogger()
		modules(appModule + presentationModule + mlKitModule + flavourModule)
	}

}