package com.sam.qrforge.presentation.feature_scan

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScanQRNavGraph {

	@Serializable
	data object ScanQRRoute : ScanQRNavGraph

	@Serializable
	data object ScanDetailsRoute : ScanQRNavGraph
}