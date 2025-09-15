package com.sam.qrforge.presentation.feature_scan

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.feature_scan.screen.ScanQRScreen
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraViewModel
import com.sam.qrforge.presentation.feature_scan.viewmodel.ScanQRViewModel
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.viewmodel.sharedKoinViewModel

fun NavGraphBuilder.scanRoute(controller: NavController) =
	navigation<NavRoutes.ScanRoute>(startDestination = ScanQRNavGraph.ScanQRRoute) {

		animatedComposable<ScanQRNavGraph.ScanQRRoute> { backStack ->

			val viewModel = backStack.sharedKoinViewModel<ScanQRViewModel>(controller)

			val cameraViewModel = koinViewModel<CameraViewModel>()

			UIEventsSideEffect(
				events = { merge(viewModel.uiEvents, cameraViewModel.uiEvents) }
			)

			val cameraControlState by cameraViewModel.cameraControlState.collectAsStateWithLifecycle()
			val cameraCaptureState by cameraViewModel.cameraCaptureState.collectAsStateWithLifecycle()
			val analyzerState by cameraViewModel.imageAnalyzerState.collectAsStateWithLifecycle()

			val lifecycle = LocalLifecycleOwner.current

			LaunchedEffect(lifecycle, cameraViewModel) {
				// first mode is found
				cameraViewModel.analysisResult
					.collectLatest { viewModel.onGenerate(it) }
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
	}