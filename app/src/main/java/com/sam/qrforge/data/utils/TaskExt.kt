package com.sam.qrforge.data.utils

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
	addOnSuccessListener { result ->
		if (cont.isActive) cont.resume(result)
	}
	addOnFailureListener { exception ->
		if (cont.isActive) cont.resumeWithException(exception)
	}
	addOnCanceledListener {
		if (cont.isActive) cont.cancel()
	}
}
