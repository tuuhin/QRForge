package com.sam.qrforge.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sam.qrforge.domain.enums.QRDataType
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "SAVED_QR_ENTITY")
data class QRDataEntity(

	@PrimaryKey
	@ColumnInfo(name = "_ID")
	val id: Long? = null,

	@ColumnInfo(name = "TITLE")
	val title: String,

	@ColumnInfo("DESCRIPTION")
	val desc: String? = null,

	@ColumnInfo(name = "CONTENT")
	val content: String,

	@ColumnInfo(name = "CONTENT_TYPE")
	val type: QRDataType = QRDataType.TYPE_TEXT,

	@ColumnInfo(name = "CREATED_AT")
	val createdAt: LocalDateTime,

	@ColumnInfo(name = "MODIFIED_AT")
	val modifiedAt: LocalDateTime,

	@ColumnInfo(name = "IS_FAV")
	val isFavourite: Boolean = false
)
