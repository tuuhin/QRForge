package com.sam.qrforge.di

import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(
	includes = [
		CoreModule::class,
		MLKitModule::class,
		PresentationModule::class,
		FlavourModule::class
	]
)
class AppModule


@KoinApplication(modules = [AppModule::class])
class KoinDependencyContainer