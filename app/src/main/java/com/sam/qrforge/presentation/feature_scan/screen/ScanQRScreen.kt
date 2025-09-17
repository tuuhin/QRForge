package com.sam.qrforge.presentation.feature_scan.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.hasCameraPermission
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_scan.composable.PermissionPlaceHolder
import com.sam.qrforge.presentation.feature_scan.composable.ScanQRScreenContent
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.CameraControlsState
import com.sam.qrforge.presentation.feature_scan.state.ImageAnalysisState
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun ScanQRScreen(
	cameraControl: CameraControlsState,
	cameraCapture: CameraCaptureState,
	imageAnalysisState: ImageAnalysisState,
	onCameraEvent: (CameraControllerEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val layoutDirection = LocalLayoutDirection.current
	val context = LocalContext.current

	var hasPermission by remember { mutableStateOf(context.hasCameraPermission) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { },
				navigationIcon = navigation,
				colors = if (hasPermission) TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
				else TopAppBarDefaults.topAppBarColors(),
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
		modifier = modifier.sharedBoundsWrapper(
			key = SharedTransitionKeys.SCAN_BUTTON_TO_SCAN_SCREEN,
			resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
			clipShape = MaterialTheme.shapes.extraLarge,
			zIndexInOverlay = 2f
		),
	) { scPadding ->
		Crossfade(
			targetState = hasPermission,
			animationSpec = tween(200, easing = FastOutSlowInEasing),
		) { isGranted ->
			if (isGranted) {
				ScanQRScreenContent(
					cameraControlState = cameraControl,
					cameraCaptureState = cameraCapture,
					analyzerState = imageAnalysisState,
					onEvent = onCameraEvent,
					modifier = Modifier.fillMaxSize()
				)
			} else PermissionPlaceHolder(
				onPermissionChanged = { perms -> hasPermission = perms },
				onGalleryImageSelected = { uri ->
					onCameraEvent(
						CameraControllerEvents.OnSelectImageURI(
							uri
						)
					)
				},
				contentPadding = PaddingValues(
					top = scPadding.calculateTopPadding(),
					bottom = scPadding.calculateBottomPadding(),
					start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
					end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
				),
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun ScanQRScreenPreview() = QRForgeTheme {
	ScanQRScreen(
		cameraCapture = CameraCaptureState(),
		cameraControl = CameraControlsState(),
		imageAnalysisState = ImageAnalysisState(),
		onCameraEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}