package com.sam.qrforge.presentation.feature_detail.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_detail.composables.QREditScreenContent
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenEvent
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenState

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
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
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = { onEvent(EditQRScreenEvent.OnEdit) },
				shape = MaterialTheme.shapes.large,
				icon = {
					Icon(
						painter = painterResource(R.drawable.ic_save),
						contentDescription = "Save edited changes"
					)
				},
				text = { Text(text = stringResource(R.string.action_save)) },
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
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(SharedTransitionKeys.QR_DETAILS_SCREEN_TO_EDIT_SCREEN)
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
