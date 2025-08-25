package com.sam.qrforge.domain.models

import com.sam.qrforge.domain.enums.QRDataType

data class CreateNewQRModel(
	val title: String,
	val desc: String? = null,
	val content: String = "",
	val format: QRDataType = QRDataType.TYPE_TEXT,
)
