package com.sam.qrforge.data.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker

class FirebaseAnalyticsTracker(private val analytics: FirebaseAnalytics) : AnalyticsTracker {

	override fun logEvent(event: AnalyticsEvent, params: Map<AnalyticsParams, Any>) {

		val bundle = bundleOf()
		for ((key, value) in params) {
			when (value) {
				is String -> bundle.putString(key.param, value)
				is Int -> bundle.putInt(key.param, value)
				is Long -> bundle.putLong(key.param, value)
				is Double -> bundle.putDouble(key.param, value)
				is Float -> bundle.putFloat(key.param, value)
				is Boolean -> bundle.putBoolean(key.param, value)
				// send plain to string version
				else -> bundle.putString(key.param, value.toString())
			}
		}
		analytics.logEvent(event.eventName, bundle)
	}
}