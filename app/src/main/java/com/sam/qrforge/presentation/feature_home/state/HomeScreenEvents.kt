package com.sam.qrforge.presentation.feature_home.state

import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.SavedQRModel

sealed interface HomeScreenEvents {

	data class OnFilterQRDataType(val type: QRDataType? = null) : HomeScreenEvents
	data class OnDeleteItem(val model: SavedQRModel) : HomeScreenEvents

}