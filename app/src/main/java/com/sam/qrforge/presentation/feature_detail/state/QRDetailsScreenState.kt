package com.sam.qrforge.presentation.feature_detail.state

import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel

data class QRDetailsScreenState(
	val qrModel: SavedQRModel? = null,
	val isLoading: Boolean = true,
	val generatedModel: GeneratedQRUIModel? = null,
)
