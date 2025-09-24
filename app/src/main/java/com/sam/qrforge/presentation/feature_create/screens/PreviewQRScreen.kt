package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_create.composables.PreviewQRScreenContent
import com.sam.qrforge.presentation.feature_create.state.CreateQREvents
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun PreviewQRScreen(
	content: QRContentModel,
	onEvent: (CreateQREvents) -> Unit,
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	navigation: @Composable () -> Unit = {},
	onNavigateToSave: () -> Unit = {},
	onNavigateToExport: () -> Unit = {},
) {
	val layoutDirection = LocalLayoutDirection.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.preview_qr_screen)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					FilledTonalButton(
						onClick = onNavigateToSave,
						shape = MaterialTheme.shapes.extraLarge,
					) {
						Text(text = stringResource(R.string.action_save))
					}
					Spacer(modifier = Modifier.width(4.dp))
				},
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		PreviewQRScreenContent(
			content = content,
			generated = generated,
			onExportContent = onNavigateToExport,
			onShareContent = { bitmap -> onEvent(CreateQREvents.ShareGeneratedQR(bitmap)) },
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier.fillMaxSize()
		)
	}
}

@PreviewLightDark
@Composable
private fun PreviewQRScreenPreview() = QRForgeTheme {
	PreviewQRScreen(
		generated = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
		content = QRPlainTextModel("Hello"),
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}