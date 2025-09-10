package com.sam.qrforge.presentation.feature_create

import kotlinx.serialization.Serializable

@Serializable
sealed interface CreateNewQRNavGraph {

	@Serializable
	data object CreateNewRoute : CreateNewQRNavGraph

	@Serializable
	data object PreviewNewRoute : CreateNewQRNavGraph

	@Serializable
	data object SaveRoute : CreateNewQRNavGraph

	@Serializable
	data object ExportRoute : CreateNewQRNavGraph
}