package com.sam.qrforge.presentation.feature_home.state

import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel

data class SelectableQRModel(
	val qrModel: SavedQRModel,
	val isSelectable: Boolean = false,
	val isSelected: Boolean = false,
	val generated: GeneratedQRUIModel? = null,
)
