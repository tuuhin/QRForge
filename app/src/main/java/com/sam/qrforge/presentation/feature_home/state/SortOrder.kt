package com.sam.qrforge.presentation.feature_home.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

enum class SortOrder {
	ASC,
	DESC;

	val stringRes: String
		@Composable
		get() = when (this) {
			ASC -> stringResource(R.string.sort_order_asc)
			DESC -> stringResource(R.string.sort_order_desc)
		}
}