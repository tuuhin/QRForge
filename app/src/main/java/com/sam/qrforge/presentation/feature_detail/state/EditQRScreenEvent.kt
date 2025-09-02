package com.sam.qrforge.presentation.feature_detail.state

sealed interface EditQRScreenEvent {

	data class OnSaveQRTitleChange(val textValue: String) : EditQRScreenEvent
	data class OnSaveQRDescChange(val textValue: String) : EditQRScreenEvent
	data object OnEdit : EditQRScreenEvent
}