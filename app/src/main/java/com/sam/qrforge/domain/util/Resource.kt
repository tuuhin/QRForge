package com.sam.qrforge.domain.util

sealed class Resource<out S, out E> {

	data class Success<S, E>(
		val data: S,
		val message: String? = null
	) : Resource<S, E>()

	data object Loading : Resource<Nothing, Nothing>()

	data class Error<S, E : Exception>(
		val error: E,
		val message: String? = null
	) : Resource<S, E>()
}