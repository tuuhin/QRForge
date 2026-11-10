package com.sam.qrforge.presentation.feature_home

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.presentation.navigation.util.NavDestinationBuilder
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.annotation.Single

@Single(binds = [NavDestinationBuilder::class])
class HomeNavEntry : NavDestinationBuilder {

    override fun EntryProviderScope<NavKey>.navEntry(backStack: NavBackStack<NavKey>) =
        entry<NavRoutes.HomeRoute> { route ->

            val analyticsTracker = koinInject<AnalyticsTracker>()

            SideEffect(Unit) {
                analyticsTracker.logEvent(
                    AnalyticsEvent.SCREEN_VIEW,
                    mapOf(AnalyticsParams.SCREEN_NAME to route.analyticsName),
                )
            }

            val viewModel = koinViewModel<HomeViewModel>()
            val state by viewModel.homeScreenState.collectAsStateWithLifecycle()

            UIEventsSideEffect(events = viewModel::uiEvents)

            val lifecycle = LocalLifecycleOwner.current
            val lifecycleState by lifecycle.lifecycle.currentStateAsState()

            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToCreateNew = dropUnlessResumed { backStack.add(NavRoutes.CreateRoute) },
                onNavigateToScanner = dropUnlessResumed { backStack.add(NavRoutes.ScanRoute) },
                onNavigateToItemDetailed = { item ->
                    if (lifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
                        backStack.add(NavRoutes.QRDetailsRoute(item.id))
                    }
                },
            )
        }
}
