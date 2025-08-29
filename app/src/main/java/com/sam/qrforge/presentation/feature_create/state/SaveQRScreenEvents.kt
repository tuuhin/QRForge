package com.sam.qrforge.presentation.feature_create.state

sealed interface SaveQRScreenEvents {

	data class OnSaveQRTitleChange(val textValue: String) : SaveQRScreenEvents
	data class OnSaveQRDescChange(val textValue: String) : SaveQRScreenEvents
	data object OnSave : SaveQRScreenEvents
}