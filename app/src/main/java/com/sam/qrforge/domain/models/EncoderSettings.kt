package com.sam.qrforge.domain.models

import com.sam.qrforge.domain.enums.ErrorTolerance

data class EncoderSettings(
	val tolerance: ErrorTolerance = ErrorTolerance.LOW,
	val qrCodeVersion: Int = 1,
	val maskPattern: Int = 0,
	val margin: Int = 0,
)
