package com.sam.qrforge.presentation.common.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState

@Composable
fun AppCustomSnackBar(modifier: Modifier = Modifier) {
	val snackBarHostState = LocalSnackBarState.current

	SnackbarHost(
		hostState = snackBarHostState,
		modifier = modifier
	) { data ->
		Snackbar(
			snackbarData = data,
			shape = MaterialTheme.shapes.medium,
			containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
			contentColor = MaterialTheme.colorScheme.onBackground,
			actionColor = MaterialTheme.colorScheme.primary,
		)
	}
}