package com.sam.qrforge.presentation.feature_detail.state

import com.sam.qrforge.domain.models.SavedQRModel

sealed interface QRDetailsScreenEvents {

	data class ToggleIsFavourite(val model: SavedQRModel) : QRDetailsScreenEvents
	data object OnShareQR : QRDetailsScreenEvents
}