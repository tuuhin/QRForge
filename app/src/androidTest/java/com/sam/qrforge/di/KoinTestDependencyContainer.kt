package com.sam.qrforge.di

import org.koin.core.annotation.KoinApplication

@KoinApplication(modules = [AppModule::class, TestModule::class])
class KoinTestDependencyContainer
