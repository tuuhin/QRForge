package com.sam.qrforge.data.analytics

import android.util.Log
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker

private const val TAG = "LOCAL_ANALYTICS"

class OSSAnalyticsTracker : AnalyticsTracker {

	override fun logEvent(event: AnalyticsEvent, params: Map<AnalyticsParams, Any>) {
		val paramsList = params.map { (key, value) -> key.param to value }.toList()
		Log.d(TAG, "Event: ${event.eventName} $paramsList")
	}
}