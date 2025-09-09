package com.sam.qrforge.presentation.common.utils

sealed interface LaunchActivityEvent {
	data class PreviewImageURI(val uri: String) : LaunchActivityEvent
	data class ShareImageURI(val uri: String) : LaunchActivityEvent
}