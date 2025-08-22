package com.sam.qrforge.presentation.navigation.nav_graph

import kotlinx.serialization.Serializable

@Serializable
sealed interface CreateNewQRNavGraph {

	@Serializable
	data object CreateNewQRRoute : CreateNewQRNavGraph

	@Serializable
	data object PreviewGeneratedQRRoute : CreateNewQRNavGraph

	@Serializable
	data object SaveGeneratedQRRoute : CreateNewQRNavGraph
}