package com.sam.qrforge.data.analytics

import androidx.core.os.bundleOf
import androidx.core.os.toPersistableBundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker

class FirebaseAnalyticsTracker(private val analytics: FirebaseAnalytics) : AnalyticsTracker {

	override fun logEvent(event: AnalyticsEvent, params: Map<AnalyticsParams, Any>) {

		val paramsBundle = params.map { (key, value) -> key.param to value }
			.toMap().toPersistableBundle()

		val bundle = bundleOf().apply {
			putAll(paramsBundle)
		}
		analytics.logEvent(event.eventName, bundle)
	}
}