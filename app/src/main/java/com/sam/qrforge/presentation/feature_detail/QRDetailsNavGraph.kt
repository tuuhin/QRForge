package com.sam.qrforge.presentation.feature_detail

import kotlinx.serialization.Serializable

@Serializable
sealed interface QRDetailsNavGraph {

	@Serializable
	data object DetailsRoute : QRDetailsNavGraph

	@Serializable
	data object EditDetailsRoute : QRDetailsNavGraph

	@Serializable
	data object ExportRoute : QRDetailsNavGraph
}