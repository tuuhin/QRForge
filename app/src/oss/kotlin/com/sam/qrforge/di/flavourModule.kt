package com.sam.qrforge.di

import com.sam.qrforge.data.analytics.OSSAnalyticsTracker
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val flavourModule = module {
	singleOf(::OSSAnalyticsTracker).bind<AnalyticsTracker>()
}