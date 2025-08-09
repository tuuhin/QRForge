package com.sam.qrforge

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class QRForgeApp : Application(), KoinStartup {

	override fun onKoinStartup(): KoinConfiguration {
		return koinConfiguration {
			androidContext(this@QRForgeApp)
			androidLogger()
		}
	}
}