package com.sam.qrforge.presentation.feature_export

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_export.composable.ExportQRScreenContent
import com.sam.qrforge.presentation.feature_export.state.ExportDimensions
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportQRScreen(
	decoration: QRDecorationOption,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	generatedQR: GeneratedQRUIModel? = null,
	isExportRunning: Boolean = false,
	dimensions: ExportDimensions = ExportDimensions.Medium,
	navigation: @Composable () -> Unit = {}
) {
	val snackBarHostState = LocalSnackBarState.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
	val graphicsLayer = rememberGraphicsLayer()
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.qr_editor_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					OutlinedButton(
						onClick = {
							scope.launch {
								val bitmap = graphicsLayer.toImageBitmap()
								// export bitmap
								onEvent(ExportQRScreenEvents.OnExportBitmap(bitmap))
							}
						},
						shape = MaterialTheme.shapes.extraLarge,
						enabled = !isExportRunning,
						contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp)
					) {
						Crossfade(targetState = isExportRunning) { isExport ->
							if (!isExport) Text(text = stringResource(R.string.action_export))
							else Text(text = stringResource(R.string.action_exporting))
						}
					}
				}
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
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		Crossfade(
			targetState = generatedQR != null,
			modifier = Modifier.padding(scPadding)
		) { isReady ->
			if (isReady && generatedQR != null) {
				ExportQRScreenContent(
					generatedQR = generatedQR,
					isExportRunning = isExportRunning,
					dimensions = dimensions,
					onDecorationEvent = onEvent,
					graphicsLayer = { graphicsLayer },
					contentPadding = PaddingValues(dimensionResource(R.dimen.sc_padding)),
					decoration = decoration,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun ExportQRScreenPreview() = QRForgeTheme {
	ExportQRScreen(
		decoration = QRDecorationOption.QRDecorationOptionBasic(),
		onEvent = {},
		generatedQR = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}