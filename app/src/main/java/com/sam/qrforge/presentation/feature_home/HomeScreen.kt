package com.sam.qrforge.presentation.feature_home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sam.qrforge.R
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.slideUpToReveal
import com.sam.qrforge.presentation.feature_home.composables.FilterListBottomSheet
import com.sam.qrforge.presentation.feature_home.composables.GenerateORScanActions
import com.sam.qrforge.presentation.feature_home.composables.HomeScreenContent
import com.sam.qrforge.presentation.feature_home.composables.HomeScreenTopAppBar
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.HomeScreenState
import com.sam.qrforge.presentation.navigation.fadeAnimatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

fun NavGraphBuilder.homeRoute(
	controller: NavController
) = fadeAnimatedComposable<NavRoutes.HomeRoute> {

	val analyticsLogger = koinInject<AnalyticsTracker>()
	LaunchedEffect(Unit) {
		analyticsLogger.logEvent(
			AnalyticsEvent.SCREEN_VIEW,
			mapOf(AnalyticsParams.SCREEN_NAME to "home_screen")
		)
	}

	val viewModel = koinViewModel<HomeViewModel>()
	val state by viewModel.homeScreenState.collectAsStateWithLifecycle()

	UIEventsSideEffect(events = viewModel::uiEvents)

	val lifecycle = LocalLifecycleOwner.current
	val lifecycleState by lifecycle.lifecycle.currentStateAsState()

	CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
		HomeScreen(
			state = state,
			onEvent = viewModel::onEvent,
			onNavigateToCreateNew = dropUnlessResumed { controller.navigate(NavRoutes.CreateRoute) },
			onNavigateToScanner = dropUnlessResumed { controller.navigate(NavRoutes.ScanRoute) },
			onNavigateToItemDetailed = { item ->
				if (lifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
					controller.navigate(NavRoutes.QRDetailsRoute(item.id))
				}
			},
		)
	}
}

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
private fun HomeScreen(
	state: HomeScreenState,
	onEvent: (HomeScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	onNavigateToCreateNew: () -> Unit = {},
	onNavigateToItemDetailed: (SavedQRModel) -> Unit = {},
	onNavigateToScanner: () -> Unit = {},
) {
	val layoutDirection = LocalLayoutDirection.current
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()
	var showBottomSheet by remember { mutableStateOf(false) }

	val showToggleFavouriteOption by remember(state.filteredQRList) {
		derivedStateOf { state.hasFavouriteItem }
	}

	val showFilterOptions by remember(state) {
		derivedStateOf { state.showFilterOption }
	}

	FilterListBottomSheet(
		showSheet = showBottomSheet,
		onDismissSheet = { showBottomSheet = false },
		filterState = state.filterState,
		hasItems = showFilterOptions,
		showToggleFavOption = showToggleFavouriteOption,
		sheetState = sheetState,
		onEvent = onEvent,
	)

	Scaffold(
		topBar = {
			HomeScreenTopAppBar(
				onFilter = {
					scope.launch { sheetState.show() }
						.invokeOnCompletion { showBottomSheet = true }
				},
				scrollBehavior = scrollBehavior
			)
		},
		bottomBar = {
			GenerateORScanActions(
				onGenerate = onNavigateToCreateNew,
				onScan = onNavigateToScanner,
				modifier = Modifier.slideUpToReveal()
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		HomeScreenContent(
			generatedQR = state.filteredQRList,
			filterState = state.filterState,
			isContentReady = state.isContentLoaded,
			onEvent = onEvent,
			onSelectItem = onNavigateToItemDetailed,
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding(),
				bottom = dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier.fillMaxSize(),
		)
	}
}

private class HomeScreenPreviewParams : CollectionPreviewParameterProvider<HomeScreenState>(
	listOf(
		HomeScreenState(savedQRList = PreviewFakes.FAKE_IMMUTABLE_LIST_QR_MODEL),
		HomeScreenState()
	)
)

@PreviewLightDark
@Composable
private fun HomeScreenPreview(
	@PreviewParameter(HomeScreenPreviewParams::class)
	state: HomeScreenState,
) = QRForgeTheme {
	HomeScreen(
		state = state,
		onEvent = {}
	)
}