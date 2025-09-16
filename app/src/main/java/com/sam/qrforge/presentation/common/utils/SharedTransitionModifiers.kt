@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sam.qrforge.presentation.common.utils

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale

private val NormalSpring = spring(
	stiffness = StiffnessMediumLow,
	visibilityThreshold = Rect.VisibilityThreshold
)


fun Modifier.sharedElementWrapper(
	key: Any,
	renderInOverlayDuringTransition: Boolean = true,
	zIndexInOverlay: Float = 0f,
	placeHolderSize: SharedTransitionScope.PlaceHolderSize = SharedTransitionScope.PlaceHolderSize.contentSize,
	boundsTransform: BoundsTransform = BoundsTransform { _, _ -> NormalSpring },
	clipShape: Shape = RectangleShape,
	keepChildPositions: Boolean = false,
) = composed {
	val transitionScope = LocalSharedTransitionScopeProvider.current ?: return@composed Modifier
	val visibilityScope =
		LocalSharedTransitionVisibilityScopeProvider.current ?: return@composed Modifier

	with(transitionScope) {
		val state = rememberSharedContentState(key)

		val otherModifier = if (keepChildPositions) Modifier.skipToLookaheadSize()
		else Modifier

		this@sharedElementWrapper
			.sharedElement(
				sharedContentState = state,
				animatedVisibilityScope = visibilityScope,
				renderInOverlayDuringTransition = renderInOverlayDuringTransition,
				zIndexInOverlay = zIndexInOverlay,
				placeHolderSize = placeHolderSize,
				boundsTransform = boundsTransform,
				clipInOverlayDuringTransition = OverlayClip(clipShape)
			)
			.then(otherModifier)
	}
}

fun Modifier.sharedBoundsWrapper(
	key: Any,
	enter: EnterTransition = fadeIn(),
	exit: ExitTransition = fadeOut(),
	renderInOverlayDuringTransition: Boolean = true,
	resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Alignment.Center),
	zIndexInOverlay: Float = 0f,
	placeHolderSize: SharedTransitionScope.PlaceHolderSize = SharedTransitionScope.PlaceHolderSize.contentSize,
	boundsTransform: BoundsTransform = BoundsTransform { _, _ -> NormalSpring },
	clipShape: Shape = RectangleShape,
	keepChildSize: Boolean = false,
) = composed {

	val transitionScope = LocalSharedTransitionScopeProvider.current ?: return@composed Modifier
	val visibilityScope =
		LocalSharedTransitionVisibilityScopeProvider.current ?: return@composed Modifier

	with(transitionScope) {

		val state = rememberSharedContentState(key)

		val otherModifier = if (keepChildSize) Modifier.skipToLookaheadSize()
		else Modifier

		this@sharedBoundsWrapper
			.sharedBounds(
				sharedContentState = state,
				animatedVisibilityScope = visibilityScope,
				exit = exit,
				enter = enter,
				boundsTransform = boundsTransform,
				renderInOverlayDuringTransition = renderInOverlayDuringTransition,
				zIndexInOverlay = zIndexInOverlay,
				placeHolderSize = placeHolderSize,
				resizeMode = resizeMode,
				clipInOverlayDuringTransition = OverlayClip(clipShape)
			)
			.then(otherModifier)
	}
}

@Composable
fun Modifier.sharedTransitionSkipChildSize(): Modifier {
	val transitionScope = LocalSharedTransitionScopeProvider.current ?: return this

	return with(transitionScope) {
		this@sharedTransitionSkipChildSize.skipToLookaheadSize()
	}
}

@Composable
fun Modifier.sharedTransitionRenderInOverlay(zIndexInOverlay: Float): Modifier {
	val transitionScope = LocalSharedTransitionScopeProvider.current ?: return this
	return with(transitionScope) {
		this@sharedTransitionRenderInOverlay
			.renderInSharedTransitionScopeOverlay(zIndexInOverlay = zIndexInOverlay)
	}
}