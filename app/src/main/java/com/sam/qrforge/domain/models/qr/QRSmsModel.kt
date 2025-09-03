package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class QRSmsModel(
	val phoneNumber: String? = null,
	val message: String? = null,
) : QRContentModel(type = QRDataType.TYPE_SMS) {

	override val isValid: Boolean
		get() {
			val numberOk =
				phoneNumber?.isNotEmpty() == true && telephonePattern.matches(phoneNumber)
			val isMessageGood = message?.isNotEmpty() == true

			return numberOk && isMessageGood
		}

	override fun toQRString(): String = buildString {
		append("SMSTO:")
		append(phoneNumber)
		message?.let {
			val encodedMessage = try {
				URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
			} catch (_: Exception) {
				message
			}
			append(":$encodedMessage")
		}
	}

	companion object {
		fun toQRModel(content: String): QRSmsModel? {
			if (!content.startsWith("SMSTO:", ignoreCase = true)) return null

			val noScheme = content.removePrefix("SMSTO:")
			val parts = noScheme.split(":", limit = 2)

			val phone = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
			val message = parts.getOrNull(1)?.let { message ->
				try {
					URLDecoder.decode(message, StandardCharsets.UTF_8.toString())
				} catch (_: Exception) {
					message
				}.takeIf { it.isNotBlank() }
			}

			return QRSmsModel(phoneNumber = phone, message = message)
		}

	}
}