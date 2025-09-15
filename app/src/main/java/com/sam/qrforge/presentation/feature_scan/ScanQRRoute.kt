package com.sam.qrforge.presentation.feature_scan

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.LaunchActivityEventsSideEffect
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.feature_scan.screen.ScanQRScreen
import com.sam.qrforge.presentation.feature_scan.screen.ScanResultsScreen
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.ScanResultScreenEvents
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraViewModel
import com.sam.qrforge.presentation.feature_scan.viewmodel.ScanQRViewModel
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.viewmodel.sharedKoinViewModel

fun NavGraphBuilder.scanRoute(controller: NavController) =
	navigation<NavRoutes.ScanRoute>(startDestination = ScanQRNavGraph.ScanQRRoute) {

		animatedComposable<ScanQRNavGraph.ScanQRRoute> { backStack ->

			val viewModel = backStack.sharedKoinViewModel<ScanQRViewModel>(controller)

			val cameraViewModel = koinViewModel<CameraViewModel>()

			UIEventsSideEffect(events = cameraViewModel::uiEvents)

			val cameraControlState by cameraViewModel.cameraControlState.collectAsStateWithLifecycle()
			val cameraCaptureState by cameraViewModel.cameraCaptureState.collectAsStateWithLifecycle()
			val analyzerState by cameraViewModel.imageAnalyzerState.collectAsStateWithLifecycle()

			NavigateToResultsSideEffect(analysis = cameraViewModel::analysisResult) { model ->
				// TODO: Fix this section
				viewModel.onEvent(ScanResultScreenEvents.GenerateQR(model))
				cameraViewModel.onCameraEvents(CameraControllerEvents.OnClearAnalysisResult)
				controller.navigate(ScanQRNavGraph.ScanResultsRoute)
			}

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				ScanQRScreen(
					cameraControl = cameraControlState,
					cameraCapture = cameraCaptureState,
					imageAnalysisState = analyzerState,
					onCameraEvent = cameraViewModel::onCameraEvents,
					navigation = {
						if (controller.previousBackStackEntry != null)
							FilledTonalIconButton(
								onClick = dropUnlessResumed { controller.popBackStack() },
								modifier = Modifier.offset(x = dimensionResource(R.dimen.sc_padding))
							) {
								Icon(
									imageVector = Icons.AutoMirrored.Filled.ArrowBack,
									contentDescription = "Back Arrow"
								)
							}
					}
				)
			}
		}

		animatedComposable<ScanQRNavGraph.ScanResultsRoute> { backStack ->

			val viewModel = backStack.sharedKoinViewModel<ScanQRViewModel>(controller)

			val generatedUI by viewModel.generated.collectAsStateWithLifecycle()
			val content by viewModel.qrContent.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				events = viewModel::uiEvents,
				onNavigateBack = { controller.popBackStack() },
			)

			LaunchActivityEventsSideEffect(eventsFlow = viewModel::activityEvents)

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				ScanResultsScreen(
					content = content,
					onEvent = viewModel::onEvent,
					generatedModel = generatedUI,
					navigation = {
						if (controller.previousBackStackEntry != null)
							IconButton(onClick = dropUnlessResumed { controller.popBackStack() }) {
								Icon(
									imageVector = Icons.AutoMirrored.Filled.ArrowBack,
									contentDescription = "Back Arrow"
								)
							}
					}
				)
			}
		}
	}

@Composable
private fun NavigateToResultsSideEffect(
	analysis: () -> Flow<QRContentModel?>,
	onNavigate: (QRContentModel) -> Unit,
) {
	val currentOnNavigate by rememberUpdatedState(onNavigate)
	val lifecycleOwner = LocalLifecycleOwner.current

	LaunchedEffect(lifecycleOwner) {
		lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			analysis()
				.collectLatest { model ->
					model?.let { currentOnNavigate(it) }
				}
		}
	}
}