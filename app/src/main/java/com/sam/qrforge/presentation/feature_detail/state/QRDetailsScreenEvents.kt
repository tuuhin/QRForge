package com.sam.qrforge.presentation.feature_detail.state

import androidx.compose.ui.graphics.ImageBitmap
import com.sam.qrforge.domain.models.SavedQRModel

sealed interface QRDetailsScreenEvents {

	data class ToggleIsFavourite(val model: SavedQRModel) : QRDetailsScreenEvents
	data class OnShareQR(val bitmap: ImageBitmap) : QRDetailsScreenEvents
	data object ToggleDeleteDialog : QRDetailsScreenEvents
	data object DeleteCurrentQR : QRDetailsScreenEvents
	data object ActionConnectToWifi : QRDetailsScreenEvents
}