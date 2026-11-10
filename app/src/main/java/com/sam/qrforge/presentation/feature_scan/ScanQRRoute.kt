package com.sam.qrforge.presentation.feature_scan

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.repeatOnLifecycle
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
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.LaunchActivityEventsSideEffect
import com.sam.qrforge.presentation.common.composables.UIEventsSideEffect
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionScopeProvider
import com.sam.qrforge.presentation.feature_scan.composable.SaveResultsDialogContent
import com.sam.qrforge.presentation.feature_scan.screen.ScanQRScreen
import com.sam.qrforge.presentation.feature_scan.screen.ScanResultsScreen
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.ScanResultScreenEvents
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraViewModel
import com.sam.qrforge.presentation.feature_scan.viewmodel.ScanQRViewModel
import com.sam.qrforge.presentation.navigation.composable.NavigationBackArrow
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.presentation.navigation.scene.BottomSheetSceneStrategy
import com.sam.qrforge.presentation.navigation.util.NavDestinationBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.Single

@Single(binds = [NavDestinationBuilder::class])
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class ScanQRRoute : NavDestinationBuilder {

    override fun EntryProviderScope<NavKey>.navEntry(backStack: NavBackStack<NavKey>) =
        entry<NavRoutes.ScanRoute> { rootRoute ->

            val nestedBackStack = rememberNavBackStack(ScanQRNavGraph.ScanQRRoute)
            val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

            val defaultSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
            val defaultEffectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
            val fastSpatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
            val fastEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()

            val viewModel = koinViewModel<ScanQRViewModel>()
            val analytics = koinInject<AnalyticsTracker>()

            val currentRoute by remember(nestedBackStack) {
                derivedStateOf { nestedBackStack.lastOrNull() }
            }

            SideEffect(currentRoute, rootRoute) {
                val route = (currentRoute as? ScanQRNavGraph) ?: return@SideEffect
                analytics.logEvent(
                    AnalyticsEvent.SCREEN_VIEW,
                    mapOf(
                        AnalyticsParams.SCREEN_NAME to route.analyticsScreenName,
                        AnalyticsParams.SCREEN_GROUP to rootRoute.analyticsName,
                    ),
                )
            }

            NavDisplay(
                backStack = nestedBackStack,
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

                    entry<ScanQRNavGraph.ScanQRRoute> {

                        val cameraViewModel = koinViewModel<CameraViewModel>()

                        UIEventsSideEffect(events = cameraViewModel::uiEvents)

                        val cameraControlState by cameraViewModel.cameraControlState.collectAsStateWithLifecycle()
                        val cameraCaptureState by cameraViewModel.cameraCaptureState.collectAsStateWithLifecycle()
                        val analyzerState by cameraViewModel.imageAnalyzerState.collectAsStateWithLifecycle()

                        NavigateToResultsSideEffect(
                            analysis = cameraViewModel::analysisResult,
                            onClearAnalysis = { cameraViewModel.onCameraEvents(CameraControllerEvents.OnClearAnalysisResult) },
                            onNavigate = { model ->
                                viewModel.onEvent(ScanResultScreenEvents.GenerateQR(model))
                                nestedBackStack.add(ScanQRNavGraph.ScanResultsRoute)
                            },
                        )

                        ScanQRScreen(
                            cameraControl = cameraControlState,
                            cameraCapture = cameraCaptureState,
                            imageAnalysisState = analyzerState,
                            onCameraEvent = cameraViewModel::onCameraEvents,
                            navigation = { NavigationBackArrow(backStack = backStack) },
                        )
                    }

                    entry<ScanQRNavGraph.ScanResultsRoute> {

                        val generatedUI by viewModel.generated.collectAsStateWithLifecycle()
                        val content by viewModel.qrContent.collectAsStateWithLifecycle()

                        UIEventsSideEffect(
                            events = viewModel::uiEvents,
                            onNavigateBack = { nestedBackStack.removeLastOrNull() },
                        )

                        LaunchActivityEventsSideEffect(eventsFlow = viewModel::activityEvents)

                        ScanResultsScreen(
                            content = content,
                            onEvent = viewModel::onEvent,
                            generatedModel = generatedUI,
                            onNavigateToSave = dropUnlessResumed { nestedBackStack.add(ScanQRNavGraph.SaveResultDialog) },
                            navigation = {
                                NavigationBackArrow(backStack = nestedBackStack)
                            },
                        )
                    }

                    entry<ScanQRNavGraph.SaveResultDialog>(
                        metadata = DialogSceneStrategy.dialog(dialogProperties = DialogProperties(dismissOnBackPress = false)),
                    ) {

                        val saveDialogState by viewModel.saveDialogState.collectAsStateWithLifecycle()

                        UIEventsSideEffect(
                            events = viewModel::uiEvents,
                            onNavigateBack = {
                                // special base in which we move to home screen
                                val index = backStack.indexOfLast { it is NavRoutes.HomeRoute }
                                if (index != -1 && index + 1 < backStack.size) {
                                    backStack.subList(index + 1, backStack.size).clear()
                                }
                            },
                        )

                        SaveResultsDialogContent(
                            state = saveDialogState,
                            onEvent = viewModel::onEvent,
                            onDismissDialog = dropUnlessResumed { nestedBackStack.removeLastOrNull() },
                        )
                    }
                },
            )
        }
}


@Composable
private fun NavigateToResultsSideEffect(
    analysis: () -> Flow<QRContentModel?>,
    onNavigate: (QRContentModel) -> Unit,
    onClearAnalysis: () -> Unit,
) {
    val currentOnNavigate by rememberUpdatedState(onNavigate)
    val currentOnClearAnalysis by rememberUpdatedState(onClearAnalysis)
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            analysis()
                .collectLatest { model ->
                    model?.let {
                        // navigate to details
                        currentOnNavigate(it)
                        // clear analysis result
                        currentOnClearAnalysis()
                    }
                }
        }
    }
}
