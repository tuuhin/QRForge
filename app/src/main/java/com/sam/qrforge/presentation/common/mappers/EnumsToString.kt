package com.sam.qrforge.presentation.common.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes

val ExportDimensions.localeString: String
	@Composable
	get() = when (this) {
		ExportDimensions.Small -> stringResource(R.string.dimension_option_small)
		ExportDimensions.Medium -> stringResource(R.string.dimension_option_medium)
		ExportDimensions.Large -> stringResource(R.string.dimension_option_large)
		ExportDimensions.ExtraLarge -> stringResource(R.string.dimension_option_extra_large)
	}

val ImageMimeTypes.localeString: String
	@Composable
	get() = when (this) {
		ImageMimeTypes.JPEG -> "JPEG"
		ImageMimeTypes.PNG -> "PNG"
	}