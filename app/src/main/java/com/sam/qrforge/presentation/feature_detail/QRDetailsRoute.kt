package com.sam.qrforge.presentation.feature_detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.sam.qrforge.presentation.common.composables.ShareQREventsSideEffect
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.feature_detail.screens.QRDetailsScreen
import com.sam.qrforge.presentation.feature_detail.screens.QREditScreen
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.presentation.navigation.nav_graph.QRDetailsNavGraph
import org.koin.compose.viewmodel.sharedKoinViewModel

fun NavGraphBuilder.qrDetailsRoute(controller: NavController) =
	navigation<NavRoutes.QRDetailsScreen>(startDestination = QRDetailsNavGraph.QRDetailsRoute) {

		composable<QRDetailsNavGraph.QRDetailsRoute> { entry ->
			val route = entry.savedStateHandle.toRoute<NavRoutes.QRDetailsScreen>()

			val viewModel = entry.sharedKoinViewModel<QRDetailsViewModel>(controller)
			val screenState by viewModel.screenState.collectAsStateWithLifecycle()

			UIEventsSideEffect(viewModel::uiEvents)
			ShareQREventsSideEffect(viewModel::shareQREvent)

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				QRDetailsScreen(
					qrId = route.qrId,
					state = screenState,
					onEvent = viewModel::onEvent,
					onNavigateToHome = dropUnlessResumed {
						controller.navigate(NavRoutes.HomeRoute) {
							popUpTo<NavRoutes.HomeRoute>()
						}
					},
					onNavigateToEdit = dropUnlessResumed {
						controller.navigate(QRDetailsNavGraph.EditQRDetailsRoute)
					},
					navigation = {
						if (controller.previousBackStackEntry != null)
							IconButton(onClick = dropUnlessResumed { controller.popBackStack() }) {
								Icon(
									imageVector = Icons.AutoMirrored.Filled.ArrowBack,
									contentDescription = "Back Arrow"
								)
							}
					},
				)
			}
		}

		composable<QRDetailsNavGraph.EditQRDetailsRoute> { entry ->

			val viewModel = entry.sharedKoinViewModel<QRDetailsViewModel>(controller)

			UIEventsSideEffect(
				events = viewModel::uiEvents,
				onNavigateBack = dropUnlessResumed { controller.popBackStack() },
			)

			ShareQREventsSideEffect(viewModel::shareQREvent)

			val screenState by viewModel.editState.collectAsStateWithLifecycle()

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				QREditScreen(
					state = screenState,
					onEvent = viewModel::onEditEvent,
					navigation = {
						if (controller.previousBackStackEntry != null)
							IconButton(onClick = dropUnlessResumed { controller.popBackStack() }) {
								Icon(
									imageVector = Icons.AutoMirrored.Filled.ArrowBack,
									contentDescription = "Back Arrow"
								)
							}
					},
				)
			}
		}
	}