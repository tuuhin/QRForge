package com.sam.qrforge.presentation.navigation.nav_graph

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes {

	@Serializable
	data object HomeRoute : NavRoutes

	@Serializable
	data object CreateRoute : NavRoutes
}