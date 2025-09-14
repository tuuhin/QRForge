package com.sam.qrforge.presentation.common.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class CoroutineLifecycleOwner(coroutineContext: CoroutineContext) : LifecycleOwner {

	private val lifecycleRegistry = LifecycleRegistry(this)

	override val lifecycle: Lifecycle
		get() = lifecycleRegistry

	init {
		if (coroutineContext[Job]?.isActive == true) {
			lifecycleRegistry.currentState = Lifecycle.State.RESUMED
			coroutineContext[Job]?.invokeOnCompletion {
				lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
			}
		} else {
			lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
		}
	}
}