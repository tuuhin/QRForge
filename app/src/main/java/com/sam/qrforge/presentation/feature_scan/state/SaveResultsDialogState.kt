package com.sam.qrforge.presentation.feature_scan.state

data class SaveResultsDialogState(
	val titleText: String = "",
	val isSaveEnabled: Boolean = false,
	val errorString: String? = null,
) {
	val hasError: Boolean
		get() = errorString?.isNotBlank() == true
}