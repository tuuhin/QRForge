package com.sam.qrforge.data.mappers

import com.sam.qrforge.data.database.entity.QRDataEntity
import com.sam.qrforge.domain.models.CreateNewQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
	content = content,
	type = format,
	createdAt = createdAt,
	modifiedAt = modifiedAt,
	isFavourite = isFav,
)

fun QRDataEntity.toModel() = SavedQRModel(
	id = id ?: -1L,
	title = title,
	desc = desc,
	content = content,
	format = type,
	createdAt = createdAt,
	modifiedAt = modifiedAt,
	isFav = isFavourite
)