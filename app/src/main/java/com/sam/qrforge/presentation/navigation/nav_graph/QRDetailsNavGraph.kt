package com.sam.qrforge.presentation.navigation.nav_graph

import kotlinx.serialization.Serializable

@Serializable
sealed interface QRDetailsNavGraph {

	@Serializable
	data object QRDetailsRoute : QRDetailsNavGraph

	@Serializable
	data object EditQRDetailsRoute : QRDetailsNavGraph
}