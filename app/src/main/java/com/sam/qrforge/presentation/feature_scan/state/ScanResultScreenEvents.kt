package com.sam.qrforge.presentation.feature_scan.state

import androidx.compose.ui.graphics.ImageBitmap
import com.sam.qrforge.domain.models.qr.QRContentModel

sealed interface ScanResultScreenEvents {
	data class GenerateQR(val content: QRContentModel) : ScanResultScreenEvents

	data class ShareScannedResults(val bitmap: ImageBitmap) : ScanResultScreenEvents
	data object ConnectToWifi : ScanResultScreenEvents

	data object OnSaveItem : ScanResultScreenEvents
	data class OnUpdateTitle(val title: String) : ScanResultScreenEvents
}