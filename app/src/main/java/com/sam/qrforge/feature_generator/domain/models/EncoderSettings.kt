package com.sam.qrforge.feature_generator.domain.models

import com.sam.qrforge.feature_generator.domain.enums.ErrorTolerance

data class EncoderSettings(
	val tolerance: ErrorTolerance = ErrorTolerance.LOW,
	val qrCodeVersion: Int = 1,
	val maskPattern: Int = 0,
	val margin: Int = 0,
)
