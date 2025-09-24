package com.sam.qrforge.presentation.feature_scan.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_scan.composable.ScanResultsScreenContent
import com.sam.qrforge.presentation.feature_scan.state.ScanResultScreenEvents
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultsScreen(
	content: QRContentModel?,
	onEvent: (ScanResultScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	generatedModel: GeneratedQRUIModel? = null,
	navigation: @Composable () -> Unit = {},
	onNavigateToSave: () -> Unit = {},
) {
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
	val scrollState = rememberScrollState()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text("Scan Results") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					FilledTonalButton(onClick = onNavigateToSave) {
						Text(text = stringResource(R.string.action_save))
					}
				}
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		Crossfade(
			targetState = content != null,
			modifier = Modifier
				.padding(scPadding)
				.padding(horizontal = dimensionResource(R.dimen.sc_padding))
		) { isContentReady ->
			if (isContentReady && content != null)
				ScanResultsScreenContent(
					content = content,
					generatedModel = generatedModel,
					onConnectToWifi = { onEvent(ScanResultScreenEvents.ConnectToWifi) },
					onShare = { bitmap -> onEvent(ScanResultScreenEvents.ShareScannedResults(bitmap)) },
					scrollState = scrollState,
					modifier = Modifier.fillMaxSize()
				)
			else {
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun ScanResultsScreenPreview() = QRForgeTheme {
	ScanResultsScreen(
		content = QRTelephoneModel("1234567890"),
		generatedModel = PreviewFakes.FAKE_GENERATED_UI_MODEL_4,
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}