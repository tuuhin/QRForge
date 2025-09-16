package com.sam.qrforge.presentation.common.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Applies a slide and fade animation for enter and exit transitions within a [AnimatedVisibilityScope].
 */
@Composable
fun Modifier.slideUpToReveal(): Modifier {
	val visibilityScope = LocalSharedTransitionVisibilityScopeProvider.current ?: return this
	return with(visibilityScope) {
		this@slideUpToReveal.animateEnterExit(
			enter = fadeIn() + slideInVertically { height -> height },
			exit = fadeOut() + slideOutVertically { height -> height },
		)

	}
}