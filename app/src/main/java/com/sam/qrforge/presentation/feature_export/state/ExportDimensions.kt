package com.sam.qrforge.presentation.feature_export.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

enum class ExportDimensions(val sizeInPx: Int) {
	Small(200),
	Medium(400),
	Large(800),
	ExtraLarge(1200);

	val localeString: String
		@Composable
		get() = when (this) {
			Small -> stringResource(R.string.dimension_option_small)
			Medium -> stringResource(R.string.dimension_option_medium)
			Large -> stringResource(R.string.dimension_option_large)
			ExtraLarge -> stringResource(R.string.dimension_option_extra_large)
		}
}