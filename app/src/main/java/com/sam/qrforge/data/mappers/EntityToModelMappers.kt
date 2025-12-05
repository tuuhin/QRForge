package com.sam.qrforge.data.mappers

import android.util.Patterns
import com.sam.qrforge.data.database.entity.QRDataEntity
import com.sam.qrforge.domain.models.CreateNewQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.models.qr.QRURLModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun String.toQRModel(): QRContentModel {
	val result = when {
		startsWith("mailto:") || startsWith("MATMSG:") -> QREmailModel.toQRModel(this)
		startsWith("geo:") -> QRGeoPointModel.toQRModel(this)
		startsWith("SMSTO:") -> QRSmsModel.toQRModel(this)
		startsWith("WIFI:") -> QRWiFiModel.toQRModel(this)
		startsWith("tel:") -> QRTelephoneModel.toQRModel(this)
		Patterns.WEB_URL.matcher(this).matches() -> QRURLModel.toQRModel(this)
		else -> QRPlainTextModel.toQRModel(this)
	}
	return result ?: QRPlainTextModel(this)
}

@OptIn(ExperimentalTime::class)
fun CreateNewQRModel.toEntity() = QRDataEntity(
	title = title,
	desc = desc,
	content = content,
	type = format,
	createdAt = Clock.System.now().toLocalDateTime(
		TimeZone.currentSystemDefault()
	),
	modifiedAt = Clock.System.now().toLocalDateTime(
		TimeZone.currentSystemDefault()
	),
)

fun SavedQRModel.toEntity() = QRDataEntity(
	id = id,
	title = title,
	desc = desc,
	content = content.toQRString(),
	type = format,
	createdAt = createdAt,
	modifiedAt = modifiedAt,
	isFavourite = isFav,
)

fun QRDataEntity.toModel() = SavedQRModel(
	id = id ?: -1L,
	title = title,
	desc = desc,
	content = content.toQRModel(),
	format = type,
	createdAt = createdAt,
	modifiedAt = modifiedAt,
	isFav = isFavourite
)