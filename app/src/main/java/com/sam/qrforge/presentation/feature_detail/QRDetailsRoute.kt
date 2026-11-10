package com.sam.qrforge.presentation.feature_detail

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.presentation.common.composables.LaunchActivityEventsSideEffect
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionScopeProvider
import com.sam.qrforge.presentation.feature_detail.screens.QRDetailsScreen
import com.sam.qrforge.presentation.feature_detail.screens.QREditScreen
import com.sam.qrforge.presentation.feature_export.ExportQRScreen
import com.sam.qrforge.presentation.feature_export.ExportQRViewModel
import com.sam.qrforge.presentation.navigation.composable.NavigationBackArrow
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.presentation.navigation.scene.BottomSheetSceneStrategy
import com.sam.qrforge.presentation.navigation.util.NavDestinationBuilder
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class QRDetailsNavDestination : NavDestinationBuilder {

    override fun EntryProviderScope<NavKey>.navEntry(backStack: NavBackStack<NavKey>) =
        entry<NavRoutes.QRDetailsRoute> { rootRoute ->

            val nestedNavStack = rememberNavBackStack(QRDetailsNavGraph.DetailsRoute)
            val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

            val defaultSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
            val defaultEffectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
            val fastSpatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
            val fastEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()

            val viewModel = koinViewModel<QRDetailsViewModel>()
            val analytics = koinInject<AnalyticsTracker>()

            val currentRoute by remember(nestedNavStack) {
                derivedStateOf { nestedNavStack.lastOrNull() }
            }

            SideEffect(currentRoute, rootRoute) {
                val route = (currentRoute as? QRDetailsNavGraph) ?: return@SideEffect
                analytics.logEvent(
                    AnalyticsEvent.SCREEN_VIEW,
                    mapOf(
                        AnalyticsParams.SCREEN_NAME to route.analyticsScreenName,
                        AnalyticsParams.SCREEN_GROUP to rootRoute.analyticsName,
                    ),
                )
            }

            NavDisplay(
                backStack = nestedNavStack,
                sharedTransitionScope = LocalSharedTransitionScopeProvider.current,
                sceneStrategies = listOf(
                    DialogSceneStrategy(),
                    BottomSheetSceneStrategy(),
                    listDetailStrategy,
                ),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = {
                    scaleIn(
                        animationSpec = defaultSpatialSpec,
                        initialScale = 0.9f,
                    ) + fadeIn(animationSpec = defaultEffectsSpec) togetherWith
                        scaleOut(
                            animationSpec = fastSpatialSpec,
                            targetScale = 1.1f,
                        ) + fadeOut(fastEffectsSpec)
                },
                popTransitionSpec = {
                    scaleIn(
                        animationSpec = defaultSpatialSpec,
                        initialScale = 1.1f,
                    ) + fadeIn(animationSpec = defaultEffectsSpec) togetherWith
                        scaleOut(
                            animationSpec = fastSpatialSpec,
                            targetScale = 0.9f,
                        ) + fadeOut(fastEffectsSpec)
                },
                predictivePopTransitionSpec = {
                    scaleIn(
                        animationSpec = defaultSpatialSpec,
                        initialScale = 1.1f,
                    ) + fadeIn(animationSpec = defaultEffectsSpec) togetherWith
                        scaleOut(
                            animationSpec = fastSpatialSpec,
                            targetScale = 0.9f,
                        ) + fadeOut(fastEffectsSpec)
                },
                contentAlignment = Alignment.Center,
                entryProvider = entryProvider {

                    entry<QRDetailsNavGraph.DetailsRoute> {

                        val screenState by viewModel.screenState.collectAsStateWithLifecycle()

                        UIEventsSideEffect(
                            events = viewModel::uiEvents,
                            onNavigateBack = { nestedNavStack.removeLastOrNull() },
                        )

                        LaunchActivityEventsSideEffect(viewModel::activityEvents)

                        QRDetailsScreen(
                            qrId = rootRoute.qrId,
                            state = screenState,
                            onEvent = viewModel::onEvent,
                            onNavigateToHome = dropUnlessResumed {
                                val index = backStack.indexOfLast { it is NavRoutes.HomeRoute }
                                if (index != -1 && index + 1 < backStack.size) {
                                    backStack.subList(index + 1, backStack.size).clear()
                                }
                            },
                            onNavigateToEdit = dropUnlessResumed {
                                nestedNavStack.add(QRDetailsNavGraph.EditDetailsRoute)
                            },
                            onNavigateToExport = dropUnlessResumed {
                                nestedNavStack.add(QRDetailsNavGraph.ExportRoute)
                            },
                            navigation = { NavigationBackArrow(backStack = backStack) },
                        )
                    }

                    entry<QRDetailsNavGraph.EditDetailsRoute> {

                        val screenState by viewModel.editState.collectAsStateWithLifecycle()

                        UIEventsSideEffect(
                            events = viewModel::uiEvents,
                            onNavigateBack = dropUnlessResumed { nestedNavStack.removeLastOrNull() },
                        )

                        QREditScreen(
                            state = screenState,
                            onEvent = viewModel::onEditEvent,
                            navigation = { NavigationBackArrow(nestedNavStack) },
                        )
                    }

                    entry<QRDetailsNavGraph.ExportRoute> {

                        val screenState by viewModel.screenState.collectAsStateWithLifecycle()

                        val exportViewmodel = koinViewModel<ExportQRViewModel>()
                        val decoration by exportViewmodel.selectedDecoration.collectAsStateWithLifecycle()
                        val exportState by exportViewmodel.exportScreenState.collectAsStateWithLifecycle()

                        UIEventsSideEffect(
                            events = { merge(exportViewmodel.uiEvents, exportViewmodel.uiEvents) },
                            onNavigateBack = dropUnlessResumed { nestedNavStack.removeLastOrNull() },
                        )

                        LaunchActivityEventsSideEffect(exportViewmodel::activityEvents)

                        ExportQRScreen(
                            decoration = decoration,
                            state = exportState,
                            generatedQR = screenState.generatedModel,
                            onEvent = exportViewmodel::onEvent,
                            navigation = { NavigationBackArrow(nestedNavStack) },
                        )
                    }
                },
            )
        }
}
