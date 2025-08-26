package com.sam.qrforge.presentation.feature_home.state

import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel

data class SavedAndGeneratedQRModel(
	val qrModel: SavedQRModel,
	val uiModel: GeneratedQRUIModel? = null,
)
