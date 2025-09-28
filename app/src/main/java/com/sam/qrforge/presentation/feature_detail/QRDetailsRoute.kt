package com.sam.qrforge.presentation.feature_detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.presentation.common.composables.LaunchActivityEventsSideEffect
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.feature_detail.screens.QRDetailsScreen
import com.sam.qrforge.presentation.feature_detail.screens.QREditScreen
import com.sam.qrforge.presentation.feature_export.ExportQRScreen
import com.sam.qrforge.presentation.feature_export.ExportQRViewModel
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.fadeAnimatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.viewmodel.sharedKoinViewModel

fun NavGraphBuilder.qrDetailsRoute(controller: NavController) =
	navigation<NavRoutes.QRDetailsRoute>(startDestination = QRDetailsNavGraph.DetailsRoute) {

		fadeAnimatedComposable<QRDetailsNavGraph.DetailsRoute> { entry ->

			val analytics = koinInject<AnalyticsTracker>()

			LaunchedEffect(Unit) {
				analytics.logEvent(
					AnalyticsEvent.SCREEN_VIEW,
					mapOf(AnalyticsParams.SCREEN_NAME to "qr_details_screen")
				)
			}

			val route = entry.savedStateHandle.toRoute<NavRoutes.QRDetailsRoute>()

			val viewModel = entry.sharedKoinViewModel<QRDetailsViewModel>(controller)
			val screenState by viewModel.screenState.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				events = viewModel::uiEvents,
				onNavigateBack = { controller.popBackStack() },
			)
			LaunchActivityEventsSideEffect(viewModel::activityEvents)

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
						controller.navigate(QRDetailsNavGraph.EditDetailsRoute)
					},
					onNavigateToExport = dropUnlessResumed {
						controller.navigate(QRDetailsNavGraph.ExportRoute)
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

		animatedComposable<QRDetailsNavGraph.EditDetailsRoute> { entry ->

			val analytics = koinInject<AnalyticsTracker>()

			LaunchedEffect(Unit) {
				analytics.logEvent(
					AnalyticsEvent.SCREEN_VIEW,
					mapOf(AnalyticsParams.SCREEN_NAME to "edit_qr_metadata_screen")
				)
			}

			val viewModel = entry.sharedKoinViewModel<QRDetailsViewModel>(controller)
			val screenState by viewModel.editState.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				events = viewModel::uiEvents,
				onNavigateBack = dropUnlessResumed { controller.popBackStack() },
			)

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

		animatedComposable<QRDetailsNavGraph.ExportRoute> { entry ->

			val analytics = koinInject<AnalyticsTracker>()

			LaunchedEffect(Unit) {
				analytics.logEvent(
					AnalyticsEvent.SCREEN_VIEW,
					mapOf(
						AnalyticsParams.SCREEN_NAME to "export_qr_route",
						AnalyticsParams.PREVIOUS_SCREEN_NAME to "qr_details_screen"
					)
				)
			}

			val detailsViewModel = entry.sharedKoinViewModel<QRDetailsViewModel>(controller)
			val screenState by detailsViewModel.screenState.collectAsStateWithLifecycle()

			val viewModel = koinViewModel<ExportQRViewModel>()

			val decoration by viewModel.selectedDecoration.collectAsStateWithLifecycle()
			val exportState by viewModel.exportScreenState.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				events = { merge(detailsViewModel.uiEvents, viewModel.uiEvents) },
				onNavigateBack = dropUnlessResumed { controller.popBackStack() },
			)

			LaunchActivityEventsSideEffect(viewModel::activityEvents)

			CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
				ExportQRScreen(
					decoration = decoration,
					state = exportState,
					generatedQR = screenState.generatedModel,
					onEvent = viewModel::onEvent,
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