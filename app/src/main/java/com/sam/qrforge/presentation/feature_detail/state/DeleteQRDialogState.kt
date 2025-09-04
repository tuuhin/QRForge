package com.sam.qrforge.presentation.feature_detail.state

data class DeleteQRDialogState(
	val showDialog: Boolean = false,
	val canDeleteItem: Boolean = true
)