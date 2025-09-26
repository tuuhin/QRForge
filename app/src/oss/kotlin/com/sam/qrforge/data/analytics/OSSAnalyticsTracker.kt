package com.sam.qrforge.data.analytics

import android.util.Log
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsTracker

private const val TAG = "OSS_ANALYTICS"

class OSSAnalyticsTracker : AnalyticsTracker {

	override fun logEvent(event: AnalyticsEvent, params: Map<String, Any>) {
		Log.d(TAG, "Event: ${event.name} $params")
	}
}