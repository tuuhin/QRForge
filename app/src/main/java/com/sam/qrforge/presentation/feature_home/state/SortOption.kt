package com.sam.qrforge.presentation.feature_home.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

enum class SortOption {
	NAME,
	CREATED;

	val stringRes: String
		@Composable
		get() = when (this) {
			NAME -> stringResource(R.string.sort_option_name)
			CREATED -> stringResource(R.string.sort_option_date_created)
		}
}