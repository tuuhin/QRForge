package com.sam.qrforge.domain.analytics

interface AnalyticsTracker {
	fun logEvent(event: AnalyticsEvent, params: Map<String, Any> = emptyMap())
}