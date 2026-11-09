package com.sam.qrforge.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.sam.qrforge.data.analytics.FirebaseAnalyticsTracker
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@Module
class FlavourModule {

	@Single
	fun firebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

	@Single
	fun firebaseAnalyticsTracker(analytics: FirebaseAnalytics): AnalyticsTracker =
		FirebaseAnalyticsTracker(analytics = analytics)
}
