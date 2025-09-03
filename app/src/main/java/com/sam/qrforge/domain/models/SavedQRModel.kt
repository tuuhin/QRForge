package com.sam.qrforge.domain.models

import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.datetime.LocalDateTime

data class SavedQRModel(
	val id: Long,
	val title: String,
	val desc: String? = null,
	val content: QRContentModel,
	val createdAt: LocalDateTime,
	val modifiedAt: LocalDateTime = createdAt,
	val format: QRDataType = QRDataType.TYPE_TEXT,
	val isFav: Boolean = false,
)
