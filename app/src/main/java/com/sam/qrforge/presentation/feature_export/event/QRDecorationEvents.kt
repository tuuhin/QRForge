package com.sam.qrforge.presentation.feature_export.event

import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption

sealed interface QRDecorationEvents {
	data class OnDecorationChange(val decoration: QRDecorationOption) : QRDecorationEvents
	data class OnQRTemplateChange(val template: QRTemplateOption) : QRDecorationEvents
}