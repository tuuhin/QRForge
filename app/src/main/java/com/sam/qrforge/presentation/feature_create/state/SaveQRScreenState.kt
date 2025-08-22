package com.sam.qrforge.presentation.feature_create.state

import androidx.compose.ui.text.input.TextFieldValue

data class SaveQRScreenState(
	val title: TextFieldValue = TextFieldValue(),
	val desc: TextFieldValue = TextFieldValue(),
)