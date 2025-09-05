package com.sam.qrforge.presentation.feature_home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_home.composables.HomeScreenContent
import com.sam.qrforge.presentation.feature_home.composables.HomeScreenTopAppBar
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.homeRoute(controller: NavController) = animatedComposable<NavRoutes.HomeRoute> {

	val viewModel = koinViewModel<HomeViewModel>()
	val qrHistory by viewModel.savedQR.collectAsStateWithLifecycle()
	val selectedQRType by viewModel.typeFilter.collectAsStateWithLifecycle()
	val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

	UIEventsSideEffect(events = viewModel::uiEvents)

	val lifecycle = LocalLifecycleOwner.current
	val lifecycleState by lifecycle.lifecycle.currentStateAsState()

	CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
		HomeScreen(
			qrHistory = qrHistory,
			selectedTypeFilter = selectedQRType,
			isContentReady = !isLoading,
			onEvent = viewModel::onEvent,
			onNavigateToCreateNew = dropUnlessResumed { controller.navigate(NavRoutes.CreateRoute) },
			onNavigateToItemDetailed = { item ->
				if (lifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
					controller.navigate(NavRoutes.QRDetailsRoute(item.id))
				}
			}
		)
	}
}

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
private fun HomeScreen(
	qrHistory: ImmutableList<SavedAndGeneratedQRModel>,
	onEvent: (HomeScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	selectedTypeFilter: QRDataType? = null,
	isContentReady: Boolean = true,
	onNavigateToCreateNew: () -> Unit = {},
	onNavigateToItemDetailed: (SavedQRModel) -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val layoutDirection = LocalLayoutDirection.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			HomeScreenTopAppBar(scrollBehavior = scrollBehavior)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = onNavigateToCreateNew,
				icon = {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = "Create"
					)
				},
				text = { Text(text = stringResource(R.string.action_create)) },
				modifier = Modifier.sharedBoundsWrapper(SharedTransitionKeys.HOME_SCREEN_TO_CREATE_QR_SCREEN)
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
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		HomeScreenContent(
			generatedQR = qrHistory,
			selectedQRType = selectedTypeFilter,
			isContentReady = isContentReady,
			onEvent = onEvent,
			onSelectItem = onNavigateToItemDetailed,
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier.fillMaxSize(),
		)
	}
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = QRForgeTheme {
	HomeScreen(
		qrHistory = PreviewFakes.FAKE_IMMUTABLE_LIST_QR_MODEL,
		onEvent = {},
	)
}