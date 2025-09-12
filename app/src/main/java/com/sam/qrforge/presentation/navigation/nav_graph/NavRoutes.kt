package com.sam.qrforge.presentation.navigation.nav_graph

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes {

	@Serializable
	data object HomeRoute : NavRoutes

	@Serializable
	data object CreateRoute : NavRoutes

	@Serializable
	data class QRDetailsRoute(val qrId: Long) : NavRoutes

	@Serializable
	data object ScanRoute : NavRoutes
}