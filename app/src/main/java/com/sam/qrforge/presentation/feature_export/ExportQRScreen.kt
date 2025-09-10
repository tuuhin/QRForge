package com.sam.qrforge.presentation.feature_export

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_export.composable.ExportQRScreenContent
import com.sam.qrforge.presentation.feature_export.composable.ExportScreenTopAppBar
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun ExportQRScreen(
	decoration: QRDecorationOption,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	generatedQR: GeneratedQRUIModel? = null,
	isExportRunning: Boolean = false,
	dimensions: ExportDimensions = ExportDimensions.Medium,
	exportType: ImageMimeTypes = ImageMimeTypes.PNG,
	navigation: @Composable () -> Unit = {}
) {
	val snackBarHostState = LocalSnackBarState.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
	val graphicsLayer = rememberGraphicsLayer()
	val scope = rememberCoroutineScope()

	Scaffold(
		topBar = {
			ExportScreenTopAppBar(
				onBeginExport = {
					scope.launch {
						val bitmap = graphicsLayer.toImageBitmap()
						onEvent(ExportQRScreenEvents.OnExportBitmap(bitmap))
					}
				},
				navigation = navigation,
				scrollBehavior = scrollBehavior
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
			.sharedBoundsWrapper(SharedTransitionKeys.EXPORT_BUTTON_TO_EXPORT_SCREEN),
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
					exportType = exportType,
					onEvent = onEvent,
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