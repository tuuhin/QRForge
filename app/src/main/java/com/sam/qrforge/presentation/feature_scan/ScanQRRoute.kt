package com.sam.qrforge.presentation.feature_scan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.feature_scan.screen.ScanQRScreen
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraViewmodel
import com.sam.qrforge.presentation.feature_scan.viewmodel.ScanQRViewModel
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.merge
import org.koin.compose.viewmodel.sharedKoinViewModel

fun NavGraphBuilder.scanRoute(controller: NavController) =
	navigation<NavRoutes.ScanRoute>(startDestination = ScanQRNavGraph.ScanQRRoute) {

		animatedComposable<ScanQRNavGraph.ScanQRRoute> { backStack ->

			val viewModel = backStack.sharedKoinViewModel<ScanQRViewModel>(controller)

			val cameraViewModel = viewModel<CameraViewmodel>()

			UIEventsSideEffect(
				events = { merge(viewModel.uiEvents, cameraViewModel.uiEvents) }
			)

			val surfaceRequest by cameraViewModel.surfaceRequest.collectAsStateWithLifecycle()
			val focusState by cameraViewModel.focusState.collectAsStateWithLifecycle()
			val zoomState by cameraViewModel.zoomLevel.collectAsStateWithLifecycle()
			val isFlashEnabled by cameraViewModel.isTorchEnabled.collectAsStateWithLifecycle()
			val captureType by cameraViewModel.captureType.collectAsStateWithLifecycle()

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				ScanQRScreen(
					surfaceRequest = surfaceRequest,
					zoomLevel = zoomState,
					focusState = focusState,
					isFlashEnabled = isFlashEnabled,
					captureType = captureType,
					onEvent = cameraViewModel::onEvent,
					navigation = {
						if (controller.previousBackStackEntry != null)
							FilledTonalIconButton(onClick = dropUnlessResumed { controller.popBackStack() }) {
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