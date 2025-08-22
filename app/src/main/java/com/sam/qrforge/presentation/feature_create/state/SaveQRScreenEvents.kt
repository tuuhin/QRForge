package com.sam.qrforge.presentation.feature_create.state

import androidx.compose.ui.text.input.TextFieldValue

sealed interface SaveQRScreenEvents {

	data class OnSaveQRTitleChange(val textValue: TextFieldValue) : SaveQRScreenEvents
	data class OnSaveQRDescChange(val textValue: TextFieldValue) : SaveQRScreenEvents
	data object OnSave : SaveQRScreenEvents
}