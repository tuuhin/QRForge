package com.sam.qrforge.data.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
	addOnSuccessListener { result ->
		if (cont.isActive) cont.resume(result, null)
	}
	addOnFailureListener { exception ->
		if (cont.isActive) cont.resumeWithException(exception)
	}
	addOnCanceledListener {
		if (cont.isActive) cont.cancel()
	}
}