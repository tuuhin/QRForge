package com.sam.qrforge.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionScopeProvider
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.feature_create.createNewQRRoute
import com.sam.qrforge.presentation.feature_detail.qrDetailsRoute
import com.sam.qrforge.presentation.feature_home.homeRoute
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
	modifier: Modifier = Modifier,
	onNavControllerReady: (NavController) -> Unit = {},
) {

	val navController = rememberNavController()
	val currentOnNavControllerReady by rememberUpdatedState(onNavControllerReady)

	LaunchedEffect(navController) {
		currentOnNavControllerReady(navController)
	}

	val snackBarHostState = remember { SnackbarHostState() }

	SharedTransitionLayout(modifier = modifier) {
		CompositionLocalProvider(
			LocalSnackBarState provides snackBarHostState,
			LocalSharedTransitionScopeProvider provides this
		) {
			NavHost(
				navController = navController,
				startDestination = NavRoutes.HomeRoute
			) {
				homeRoute(controller = navController)
				qrDetailsRoute(controller = navController)
				createNewQRRoute(controller = navController)
			}
		}
	}
}