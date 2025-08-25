package com.sam.qrforge.presentation.feature_home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_home.composables.SavedQRDataList
import com.sam.qrforge.presentation.navigation.animatedComposable
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.homeRoute(controller: NavController) = animatedComposable<NavRoutes.HomeRoute> {

	val viewModel = koinViewModel<HomeViewModel>()

	UIEventsSideEffect(events = viewModel::uiEvents)

	CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
		HomeScreen(
			qrHistory = persistentListOf(),
			onNavigateToCreateNew = dropUnlessResumed { controller.navigate(NavRoutes.CreateRoute) },
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
	qrHistory: ImmutableList<SavedQRModel>,
	modifier: Modifier = Modifier,
	onNavigateToCreateNew: () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(stringResource(R.string.app_name)) },
				scrollBehavior = scrollBehavior
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(onClick = onNavigateToCreateNew) {
				Icon(Icons.Default.Add, contentDescription = null)
				Spacer(modifier = Modifier.width(4.dp))
				Text(text = "New")
			}
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(scPadding)
				.padding(dimensionResource(R.dimen.sc_padding))
		) {
			SavedQRDataList(qrHistory)
		}
	}
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() = QRForgeTheme {
	HomeScreen(qrHistory = PreviewFakes.FAKE_IMMUTABLE_LIST_QR_MODEL)
}