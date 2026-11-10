package com.sam.qrforge.presentation.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionScopeProvider
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import com.sam.qrforge.presentation.navigation.scene.BottomSheetSceneStrategy
import com.sam.qrforge.presentation.navigation.util.NavDestinationBuilder
import org.koin.compose.getKoin


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    linkedBackStack: List<NavKey>? = null,
) {
    // Seeded with the deep link, so there is no Home flash
    val backStack = rememberNavBackStack(NavRoutes.HomeRoute)

    // Only handles deep links arriving while running (onNewIntent)
    LaunchedEffect(linkedBackStack) {
        if (linkedBackStack != null && linkedBackStack != backStack.toList()) {
            backStack.clear()
            backStack.addAll(linkedBackStack)
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    val defaultSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val defaultEffectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val fastSpatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val fastEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()

    val koin = getKoin()
    val destinations = remember(koin) { koin.getAll<NavDestinationBuilder>() }

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSnackBarState provides snackBarHostState,
            LocalSharedTransitionScopeProvider provides this,
        ) {
            NavDisplay(
                backStack = backStack,
                modifier = modifier,
                sharedTransitionScope = this,
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
                    destinations.forEach { builder ->
                        builder.apply { navEntry(backStack) }
                    }
                },
            )
        }
    }
}
