package com.sam.qrforge.presentation.feature_detail.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.feature_detail.composables.QREditScreenContent
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenEvent
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenState
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QREditScreen(
	state: EditQRScreenState,
	onEvent: (EditQRScreenEvent) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {
	val snackBarHostState = LocalSnackBarState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.edit_qr_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					TextButton(onClick = { onEvent(EditQRScreenEvent.OnEdit) }) {
						Text(stringResource(R.string.action_save))
					}
				},
			)
		},
		snackbarHost = {
			SnackbarHost(snackBarHostState) { data ->
				Snackbar(
					snackbarData = data,
					shape = MaterialTheme.shapes.large,
					containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
					contentColor = MaterialTheme.colorScheme.onBackground,
					actionContentColor = MaterialTheme.colorScheme.primary,
				)
			}
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		QREditScreenContent(
			state = state,
			onEvent = onEvent,
			contentPadding = scPadding,
			modifier = Modifier
				.padding(dimensionResource(R.dimen.sc_padding))
				.fillMaxSize()
		)
	}
}

@PreviewLightDark
@Composable
private fun QREditScreenPreview() = QRForgeTheme {
	QREditScreen(
		state = EditQRScreenState(),
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}