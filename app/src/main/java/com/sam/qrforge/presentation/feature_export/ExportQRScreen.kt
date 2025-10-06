package com.sam.qrforge.presentation.feature_export

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.applicationSettingsIntent
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_export.composable.ExportQRBottomSheet
import com.sam.qrforge.presentation.feature_export.composable.ExportQRScreenContent
import com.sam.qrforge.presentation.feature_export.composable.ExportRunningBackHandler
import com.sam.qrforge.presentation.feature_export.composable.ExportScreenTopAppBar
import com.sam.qrforge.presentation.feature_export.composable.StoragePermissionDialog
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenState
import com.sam.qrforge.presentation.feature_export.state.VerificationState
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalPermissionsApi::class
)
@Composable
fun ExportQRScreen(
	state: ExportQRScreenState,
	decoration: QRDecorationOption,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	generatedQR: GeneratedQRUIModel? = null,
	navigation: @Composable () -> Unit = {}
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	val captureLayer = CanvasCaptureLayer.rememberCaptureLayer()


	var showPermissionDialog by remember { mutableStateOf(false) }
	val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

	StoragePermissionDialog(
		showDialog = showPermissionDialog,
		status = permissionState.status,
		onShowLauncher = { permissionState.launchPermissionRequest() },
		onShowSettings = {
			try {
				context.startActivity(context.applicationSettingsIntent)
			} finally {
				showPermissionDialog = false
			}
		},
		onDismissDialog = { showPermissionDialog = false },
	)

	ExportQRBottomSheet(
		showSheet = state.verificationState == VerificationState.VERIFIED,
		selectedExportType = state.selectedMimeType,
		selectedDimension = state.exportDimensions,
		isExportRunning = !state.canExport,
		onCancelExport = { onEvent(ExportQRScreenEvents.OnResetVerify) },
		onBeginExport = {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && !permissionState.status.isGranted) {
				showPermissionDialog = true
			} else onEvent(ExportQRScreenEvents.OnExportBitmap)
		},
		onExportTypeChange = { onEvent(ExportQRScreenEvents.OnExportMimeTypeChange(it)) },
		onDimensionChange = { onEvent(ExportQRScreenEvents.OnExportDimensionChange(it)) },
	)

	ExportRunningBackHandler(
		isExportRunning = state.verificationState == VerificationState.VERIFYING,
		onCancelExport = { onEvent(ExportQRScreenEvents.OnCancelExport) },
	)

	Scaffold(
		topBar = {
			ExportScreenTopAppBar(
				onBeginVerify = {
					scope.launch {
						val bitmap = captureLayer.captureBitmap()
						if (bitmap == null) return@launch
						onEvent(ExportQRScreenEvents.OnVerifyBitmap(bitmap))
					}
				},
				enabled = state.canVerify,
				navigation = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		Crossfade(
			targetState = generatedQR != null,
			modifier = Modifier.padding(scPadding)
		) { isReady ->
			if (isReady && generatedQR != null) {
				Column(modifier = Modifier.fillMaxSize()) {
					AnimatedVisibility(
						visible = state.verificationState == VerificationState.VERIFYING,
						enter = slideInHorizontally() + fadeIn(),
						exit = slideOutHorizontally() + fadeOut(),
					) {
						LinearProgressIndicator(
							strokeCap = StrokeCap.Round,
							modifier = Modifier.fillMaxWidth()
						)
					}
					ExportQRScreenContent(
						generatedQR = generatedQR,
						showFaultyQRWarning = state.verificationState == VerificationState.FAILED,
						onEvent = onEvent,
						captureLayer = captureLayer,
						contentPadding = PaddingValues(all = dimensionResource(R.dimen.sc_padding)),
						decoration = decoration,
						modifier = Modifier.weight(1f)
					)
				}
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun ExportQRScreenPreview() = QRForgeTheme {
	ExportQRScreen(
		state = ExportQRScreenState(),
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