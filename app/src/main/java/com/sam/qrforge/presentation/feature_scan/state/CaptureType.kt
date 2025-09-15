package com.sam.qrforge.presentation.feature_scan.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

enum class CaptureType {
	MANUAL,
	AUTO;

	val stringRes: String
		@Composable
		get() = when (this) {
			MANUAL -> stringResource(R.string.camera_capture_type_manual)
			AUTO -> stringResource(R.string.camera_capture_type_auto)
		}
}