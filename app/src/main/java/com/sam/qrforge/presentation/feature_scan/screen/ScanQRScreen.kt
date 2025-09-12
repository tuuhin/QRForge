package com.sam.qrforge.presentation.feature_scan.screen

import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.hasCameraPermission
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_scan.composable.AndroidCameraView
import com.sam.qrforge.presentation.feature_scan.composable.CameraWithControls
import com.sam.qrforge.presentation.feature_scan.composable.PermissionPlaceHolder
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.CameraFocusState
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.presentation.feature_scan.state.CaptureType
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun ScanQRScreen(
	onEvent: (CameraControllerEvents) -> Unit,
	modifier: Modifier = Modifier,
	surfaceRequest: SurfaceRequest? = null,
	focusState: CameraFocusState = CameraFocusState.Unspecified,
	zoomLevel: CameraZoomState = CameraZoomState(),
	captureType: CaptureType = CaptureType.AUTO,
	isFlashEnabled: Boolean = false,
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
				colors = if (hasPermission)
					TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
		modifier = modifier.sharedBoundsWrapper(SharedTransitionKeys.SCAN_BUTTON_TO_SCAN_SCREEN)
	) { scPadding ->
		Crossfade(
			targetState = hasPermission,
			animationSpec = tween(200, easing = FastOutSlowInEasing),
		) { isGranted ->
			if (isGranted) {
				CameraWithControls(
					cameraContent = {
						CameraContent(
							surfaceRequest = surfaceRequest,
							focusState = focusState,
							onEvent = onEvent,
							modifier = Modifier.matchParentSize()
						)
					},
					zoomState = zoomLevel,
					captureType = captureType,
					isFlashOn = isFlashEnabled,
					onZoomChange = { onEvent(CameraControllerEvents.OnZoomLevelChange(it)) },
					onToggleFlash = { onEvent(CameraControllerEvents.ToggleFlash) },
					onCaptureTypeChange = { onEvent(CameraControllerEvents.OnChangeCaptureMode(it)) },
					modifier = Modifier.fillMaxSize(),
				)
			} else PermissionPlaceHolder(
				onPermissionChanged = { hasPermission = it },
				contentPadding = PaddingValues(
					top = scPadding.calculateTopPadding(),
					bottom = scPadding.calculateBottomPadding(),
					start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
					end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
				),
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

@Composable
fun CameraContent(
	surfaceRequest: SurfaceRequest?,
	onEvent: (CameraControllerEvents) -> Unit,
	modifier: Modifier = Modifier,
	focusState: CameraFocusState = CameraFocusState.Unspecified,
) {

	val lifecycleOwner = LocalLifecycleOwner.current
	val isInspectionMode = LocalInspectionMode.current

	LifecycleStartEffect(key1 = Unit, lifecycleOwner = lifecycleOwner) {
		onEvent(CameraControllerEvents.BindCamera)

		onStopOrDispose {
			onEvent(CameraControllerEvents.UnBindCamera)
		}
	}

	if (isInspectionMode) {
		Box(
			modifier = modifier
				.background(MaterialTheme.colorScheme.surfaceContainerHighest)
		)
	} else surfaceRequest?.let { request ->
		AndroidCameraView(
			surfaceRequest = request,
			focusState = focusState,
			onRelativeScaleChange = { zoomFraction ->
				onEvent(CameraControllerEvents.OnZoomLevelChange(zoomFraction, true))
			},
			tapToFocus = { focusOffset -> onEvent(CameraControllerEvents.TapToFocus(focusOffset)) },
			modifier = modifier,
		)
	}
}

@PreviewLightDark
@Composable
private fun ScanQRScreenPreview() = QRForgeTheme {
	ScanQRScreen(
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}