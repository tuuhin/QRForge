package com.sam.qrforge.presentation.feature_scan.screen

import android.Manifest
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
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
	ExperimentalSharedTransitionApi::class, ExperimentalPermissionsApi::class
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
	val layoutDirection = LocalLayoutDirection.current

	val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

	Scaffold(
		topBar = {
			TopAppBar(
				title = { },
				navigationIcon = navigation,
				colors = if (permissionState.status.isGranted)
					TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
				else TopAppBarDefaults.topAppBarColors(),
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier.sharedBoundsWrapper(
			key = SharedTransitionKeys.SCAN_BUTTON_TO_SCAN_SCREEN,
			resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
			clipShape = MaterialTheme.shapes.extraLarge,
			zIndexInOverlay = 2f
		),
	) { scPadding ->
		Crossfade(
			targetState = permissionState.status.isGranted,
			animationSpec = tween(durationMillis = 100, delayMillis = 10, easing = EaseInOut),
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
				permissionStatus = permissionState.status,
				onLaunchPermissionDialog = { permissionState.launchPermissionRequest() },
				onGalleryImageSelected = { uri ->
					onCameraEvent(CameraControllerEvents.OnSelectImageURI(uri))
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