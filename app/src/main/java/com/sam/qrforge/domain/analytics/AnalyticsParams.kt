package com.sam.qrforge.domain.analytics

enum class AnalyticsParams(val param: String) {
	SCREEN_NAME("screen_name"),
	PREVIOUS_SCREEN_NAME("previous_screen_name"),
	GENERATED_QR_TYPE("qr_type"),
	QR_IS_SCANNED("is_qr_scanned"),
	ERROR_NAME("error_name"),
	QR_SCAN_MODE("scan_mode"),
	QR_SCAN_AUTO_CAPTURE("is_auto_capture"),
	IS_SUCCESSFUL("is_successful"),
	EXPORT_QR_TEMPLATE("current_template"),
	ERROR_WRONG_ID("qr_metadata_wrong_id")
}