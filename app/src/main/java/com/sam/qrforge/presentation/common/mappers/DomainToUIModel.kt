package com.sam.qrforge.presentation.common.mappers

import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel

fun GeneratedQRModel.toUIModel(): GeneratedQRUIModel {
	return GeneratedQRUIModel(
		dimension = widthInPx,
		margin = marginBlocks,
		boolArray = bits
	)
}