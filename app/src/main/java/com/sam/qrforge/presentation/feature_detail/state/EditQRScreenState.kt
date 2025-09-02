package com.sam.qrforge.presentation.feature_detail.state

data class EditQRScreenState(
	val title: String = "",
	val desc: String = "",
	val isError: Boolean = false,
)