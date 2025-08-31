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
import androidx.navigation.toRoute
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.qrDetailsRoute(controller: NavController) =
	animatedComposable<NavRoutes.QRDetailsScreen> { entry ->

		val route = entry.toRoute<NavRoutes.QRDetailsScreen>()

		val viewModel = koinViewModel<QRDetailsViewModel>()
		val screenState by viewModel.screenState.collectAsStateWithLifecycle()

		UIEventsSideEffect(viewModel::uiEvents)

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