package com.sam.qrforge.domain.facade

import androidx.camera.core.ImageAnalysis
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.coroutines.flow.Flow

interface QRImageAnalyzer : ImageAnalysis.Analyzer {

	val resultAnalysis: Flow<Result<QRContentModel>>

	fun cleanUp()
}