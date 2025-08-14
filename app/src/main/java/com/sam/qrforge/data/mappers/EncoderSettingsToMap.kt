package com.sam.qrforge.data.mappers

import com.google.zxing.EncodeHintType
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.QRCode
import com.sam.qrforge.domain.enums.ErrorTolerance
import com.sam.qrforge.domain.models.EncoderSettings

fun EncoderSettings.toEncoderHints(): HashMap<EncodeHintType, Any> {
	val errorCorrection = when (tolerance) {
		ErrorTolerance.LOW -> ErrorCorrectionLevel.L
		ErrorTolerance.MEDIUM -> ErrorCorrectionLevel.M
		ErrorTolerance.QUARTILE -> ErrorCorrectionLevel.Q
		ErrorTolerance.HIGH -> ErrorCorrectionLevel.H
	}
	// start from 2
	val version = if (qrCodeVersion in 2..10) qrCodeVersion else 2
	val maskPattern = if (maskPattern in 0..<QRCode.NUM_MASK_PATTERNS) maskPattern else 0

	return hashMapOf(
		EncodeHintType.ERROR_CORRECTION to errorCorrection,
		EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name(),
		EncodeHintType.DATA_MATRIX_SHAPE to SymbolShapeHint.FORCE_SQUARE,
		EncodeHintType.MARGIN to margin,
		EncodeHintType.QR_VERSION to version,
		EncodeHintType.QR_MASK_PATTERN to maskPattern
	)
}