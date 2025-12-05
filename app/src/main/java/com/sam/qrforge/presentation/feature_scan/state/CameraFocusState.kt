package com.sam.qrforge.presentation.feature_scan.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

@Stable
sealed interface CameraFocusState {

	data object Unspecified : CameraFocusState
	data class Specified(val coordinates: Offset, val status: FocusStatus) : CameraFocusState

	enum class FocusStatus {
		RUNNING,
		SUCCESS,
		FAILURE,
		CANCELLED,
	}
}