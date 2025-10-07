package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.common.utils.sharedTransitionSkipChildSize
import com.sam.qrforge.presentation.feature_create.composables.CreateQRScreenContent
import com.sam.qrforge.presentation.feature_create.composables.ReadingLocationDialog
import com.sam.qrforge.presentation.feature_create.composables.ShowGeneratedQRButton
import com.sam.qrforge.presentation.feature_create.state.CreateQREvents
import com.sam.qrforge.presentation.feature_create.state.CreateQRScreenState
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun CreateQRScreen(
	state: CreateQRScreenState,
	modifier: Modifier = Modifier,
	isContentValid: Boolean = false,
	onEvent: (CreateQREvents) -> Unit = {},
	navigation: @Composable () -> Unit = {},
	onPreviewQR: () -> Unit = {},
) {
	val layoutDirection = LocalLayoutDirection.current
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


	ReadingLocationDialog(
		showDialog = state.showLocationDialog,
		onDismiss = { onEvent(CreateQREvents.CancelReadCurrentLocation) },
	)

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.create_qr_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				modifier = Modifier.sharedTransitionSkipChildSize()
			)
		},
		bottomBar = {
			ShowGeneratedQRButton(
				showButton = isContentValid,
				onGenerateQR = {
					onEvent(CreateQREvents.OnPreviewQR)
					onPreviewQR()
				},
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(
				key = SharedTransitionKeys.HOME_SCREEN_TO_CREATE_QR_SCREEN,
				resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
				clipShape = MaterialTheme.shapes.extraLarge,
			)
	) { scPadding ->
		CreateQRScreenContent(
			contentFormat = state.selectedQRFormat,
			isLocationEnabled = state.isLocationEnabled,
			lastReadContacts = state.lastReadContacts,
			lastKnownLocation = state.lastReadLocation,
			onSelectType = { onEvent(CreateQREvents.OnQRDataTypeChange(it)) },
			onContentUpdate = { onEvent(CreateQREvents.OnUpdateQRContent(it)) },
			onReadCurrentLocation = { onEvent(CreateQREvents.CheckLastKnownLocation) },
			onReadContactsDetails = { onEvent(CreateQREvents.CheckContactsDetails(it)) },
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier
				.fillMaxSize()
				.imePadding(),
		)
	}
}

private class QRContentTypePreviewParams : CollectionPreviewParameterProvider<QRDataType>(
	listOf(
		QRDataType.TYPE_TEXT,
		QRDataType.TYPE_GEO,
		QRDataType.TYPE_PHONE,
	)
)

@PreviewLightDark
@Composable
private fun CreateQRScreenPreview(
	@PreviewParameter(QRContentTypePreviewParams::class)
	format: QRDataType,
) = QRForgeTheme {
	CreateQRScreen(
		state = CreateQRScreenState(selectedQRFormat = format),
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}