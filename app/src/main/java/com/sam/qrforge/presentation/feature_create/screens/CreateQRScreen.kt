package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.feature_create.composables.CreateQRBottomBarAction
import com.sam.qrforge.presentation.feature_create.composables.QRContentInputContainer
import com.sam.qrforge.presentation.feature_create.composables.QRDataTypeSelector
import com.sam.qrforge.presentation.feature_create.state.CreateQREvents
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQRScreen(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	onEvent: (CreateQREvents) -> Unit = {},
	navigation: @Composable () -> Unit = {},
	onPreviewQR: () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val layoutDirection = LocalLayoutDirection.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = "Create QR Code") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
			)
		},
		bottomBar = {
			CreateQRBottomBarAction(
				showBottomBar = content.isValid,
				onGenerateQR = onPreviewQR
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		LazyColumn(
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier
				.fillMaxSize()
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			item {
				QRDataTypeSelector(
					selectedType = content.type,
					onSelectType = { onEvent(CreateQREvents.OnQRDataTypeChange(it)) },
					modifier = Modifier.fillMaxWidth()
				)
			}
			item {
				QRContentInputContainer(
					selectedType = content.type,
					content = content,
					onUseCurrentLocation = { onEvent(CreateQREvents.CheckLastKnownLocation) },
					onReadContactsDetails = { uri -> onEvent(CreateQREvents.CheckContactsDetails(uri)) },
					onContentChange = { content -> onEvent(CreateQREvents.OnUpdateQRContent(content)) },
				)
			}
		}
	}
}

private class QRContentPreviewParams : CollectionPreviewParameterProvider<QRContentModel>(
	listOf(
		QRPlainTextModel("Hello"),
		QRTelephoneModel("0000000000"),
		QRGeoPointModel(23.0, 67.0)
	)
)

@PreviewLightDark
@Composable
private fun CreateQRScreenPreview(
	@PreviewParameter(QRContentPreviewParams::class)
	content: QRContentModel,
) = QRForgeTheme {
	CreateQRScreen(
		content = content,
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}