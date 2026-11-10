package com.sam.qrforge.runtime

import android.app.Application
import com.sam.qrforge.di.KoinTestDependencyContainer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level
import org.koin.dsl.KoinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class QRForgeTestRunner : Application(), KoinStartup {

    override fun onKoinStartup(): KoinConfiguration = koinConfiguration<KoinTestDependencyContainer> {
        androidContext(this@QRForgeTestRunner)
        androidLogger(level = Level.DEBUG)
    }
}
