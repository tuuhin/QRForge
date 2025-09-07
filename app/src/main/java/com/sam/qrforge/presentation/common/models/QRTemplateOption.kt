package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

enum class QRTemplateOption {
	BASIC,
	MINIMALISTIC,
	COLOR_LAYERED;

	val localeString: String
		@Composable
		get() = when (this) {
			BASIC -> stringResource(R.string.template_option_basic)
			MINIMALISTIC -> stringResource(R.string.template_option_minimalistic)
			COLOR_LAYERED -> stringResource(R.string.template_option_color_layered)
		}

}