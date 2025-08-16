package com.sam.qrforge.presentation.common.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackBarState = compositionLocalOf { SnackbarHostState() }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScopeProvider = compositionLocalOf<SharedTransitionScope?> { null }

val LocalSharedTransitionVisibilityScopeProvider =
	compositionLocalOf<AnimatedVisibilityScope?> { null }