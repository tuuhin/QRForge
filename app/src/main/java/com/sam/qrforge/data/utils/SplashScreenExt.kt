package com.sam.qrforge.data.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen

fun SplashScreen.animateOnExit(
	splashViewDuration: Long = 600L,
	onAnimationEnd: () -> Unit = {}
) {
	setOnExitAnimationListener { screenView ->
		// do all the animation is a reverse way
		val iconInterpolator = AccelerateDecelerateInterpolator()
		val viewInterpolator = AccelerateInterpolator()

		val iconScaleXAnimation = ObjectAnimator
			.ofFloat(screenView.iconView, View.SCALE_X, 1f, 0.5f)
			.apply {
				this.interpolator = iconInterpolator
				this.duration = screenView.iconAnimationDurationMillis
			}

		val iconScaleYAnimation = ObjectAnimator
			.ofFloat(screenView.iconView, View.SCALE_Y, 1f, 0.5f)
			.apply {
				this.interpolator = iconInterpolator
				this.duration = screenView.iconAnimationDurationMillis
			}

		val iconTranslateYAnimation = ObjectAnimator
			.ofFloat(screenView.iconView, View.TRANSLATION_Y, 0.0f, -8.0f)
			.apply {
				this.interpolator = iconInterpolator
				this.duration = screenView.iconAnimationDurationMillis
			}

		val viewFadeAnimation = ObjectAnimator
			.ofFloat(screenView.view, View.ALPHA, 1.0f, .3f)
			.apply {
				this.interpolator = viewInterpolator
				this.duration = splashViewDuration
			}

		val viewTranslateAnimation = ObjectAnimator.ofFloat(
			screenView.view,
			View.TRANSLATION_Y,
			0f,
			screenView.view.height.toFloat()
		).apply {
			this.interpolator = viewInterpolator
			this.duration = splashViewDuration
		}

		// animations associated with the splash view
		val viewAnimatorSet = AnimatorSet().apply {
			playTogether(viewFadeAnimation, viewTranslateAnimation)
			doOnEnd {
				screenView.remove()
				onAnimationEnd()
			}
		}
		// animation associated with icon
		val iconAnimatorSet = AnimatorSet().apply {
			playTogether(
				iconScaleXAnimation,
				iconScaleYAnimation,
				iconTranslateYAnimation
			)
			doOnEnd { viewAnimatorSet.start() }
		}
		// start the animation
		iconAnimatorSet.start()
	}
}