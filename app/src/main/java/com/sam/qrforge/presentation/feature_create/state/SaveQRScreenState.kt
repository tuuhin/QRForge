package com.sam.qrforge.presentation.feature_create.state

data class SaveQRScreenState(
	val title: String = "",
	val isError: Boolean = false,
	val desc: String = "",
)