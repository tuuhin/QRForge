package com.sam.qrforge.domain.analytics

enum class AnalyticsEvent(val eventName: String) {
	SHARE_GENERATED_QR("share_item"),
	SCREEN_VIEW("screen_view"),
	GENERATED_QR("generated_item"),
	SAVE_QR("save_qr_success"),
	SCAN_QR("scanned_qr_success"),
	CAMERA_CAPTURE("camera_capture"),
	EXPORT_QR_EVENT("export_qr_result"),
	EXPORT_QR_PREVIEWED("export_qr_viewed"),
	EXPORT_QR_VERIFIED("export_qr_verified"),
	EXPORT_QR_CANCELED("export_qr_cancelled"),
	QR_DELETE("delete_qr"),
	QR_CONTEXT_ACTION_FAILED("context_action_failed"),
	QR_METADATA_UPDATE("present_qr_metadata_update"),
	QR_DETAILS_LOAD("qr_content_details_load"),
	LOAD_ALL_QR("load_all"),
}