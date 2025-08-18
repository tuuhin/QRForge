package com.sam.qrforge.domain.enums

import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.models.qr.QRURLModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel

enum class QRDataType {
	TYPE_EMAIL,
	TYPE_PHONE,
	TYPE_GEO,
	TYPE_URL,
	TYPE_WIFI,
	TYPE_TEXT,
	TYPE_SMS;

	fun toNewModel(): QRContentModel {
		return when (this) {
			TYPE_EMAIL -> QREmailModel()
			TYPE_PHONE -> QRTelephoneModel()
			TYPE_GEO -> QRGeoPointModel()
			TYPE_URL -> QRURLModel()
			TYPE_WIFI -> QRWiFiModel()
			TYPE_TEXT -> QRPlainTextModel()
			TYPE_SMS -> QRSmsModel()
		}
	}
}