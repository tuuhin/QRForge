package com.sam.qrforge.domain.analytics

interface AnalyticsTracker {
	fun logEvent(event: AnalyticsEvent, params: Map<AnalyticsParams, Any> = emptyMap())
}