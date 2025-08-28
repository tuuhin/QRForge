package com.sam.qrforge.presentation.common.utils

sealed interface UIEvent {

	data class ShowSnackBar(val message: String) : UIEvent
	data class ShowSnackBarWithAction(
		val message: String,
		val actionText: String,
		val action: () -> Unit
	) : UIEvent

	data class ShowToast(val message: String) : UIEvent
	data object NavigateBack : UIEvent
}