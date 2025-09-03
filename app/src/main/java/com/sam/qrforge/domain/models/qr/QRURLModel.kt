package com.sam.qrforge.domain.models.qr

import android.util.Patterns
import com.sam.qrforge.domain.enums.QRDataType

@Suppress("DEPRECATION")
data class QRURLModel(
	val url: String = ""
) : QRContentModel(type = QRDataType.TYPE_URL) {

	override val isValid: Boolean
		get() = url.isNotEmpty() && Patterns.WEB_URL.matcher(url).matches()

	override fun toQRString(): String = url

	companion object {
		fun toQRModel(content: String): QRURLModel? {
			if (content.isBlank()) return null
			if (!Patterns.WEB_URL.matcher(content).matches()) return null
			return QRURLModel(content)
		}
	}

}
