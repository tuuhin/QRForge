package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class QREmailModel(
	val address: String = "",
	val subject: String? = null,
	val body: String? = null,
	// internal field
	private val isMATMSGFormat: Boolean = false
) : QRContentModel(type = QRDataType.TYPE_EMAIL) {

	override val isValid: Boolean
		get() = address.isNotEmpty()

	override fun toQRString(): String {
		return if (isMATMSGFormat) toMATMSGString() else toMailToString()
	}

	fun toMATMSGString(): String = buildString {
		if (address.isBlank()) return@buildString

		append("MATMSG:TO:${address}")

		subject?.let {
			if (it.isBlank()) return@let
			val encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString())
			append(";SUB:$encodedSubject")
		}

		body?.let {
			if (it.isBlank()) return@let
			val encodedBody = URLEncoder.encode(body, StandardCharsets.UTF_8.toString())
			append(";BODY:$encodedBody")
		}

		append(";;")
	}

	fun toMailToString(): String = buildString {
		append("mailto:")
		append(address)

		// prepare the params
		val params = mutableListOf<String>()
		subject?.let { subject ->
			if (subject.isEmpty()) return@let
			val encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString())
			params.add("subject=$encodedSubject")
		}
		body?.let { body ->
			if (body.isEmpty()) return@let
			val encodedBody = URLEncoder.encode(body, StandardCharsets.UTF_8.toString())
			params.add("body=$encodedBody")
		}
		if (params.isNotEmpty()) {
			append("?")
			append(params.joinToString("&"))
		}
	}

	companion object {

		fun toQRModel(content: String): QREmailModel? {
			return if (content.startsWith("mailto:", ignoreCase = true)) fromMAILTOFormat(content)
			else if (content.startsWith("MATMSG:", ignoreCase = true)) fromMATMSGFormat(content)
			else null
		}

		private fun fromMAILTOFormat(content: String): QREmailModel {
			require(content.startsWith("mailto:", ignoreCase = true))

			val urlWithoutScheme = content.removePrefix("mailto:")

			// Split into address and query part
			val parts = urlWithoutScheme.split("?", limit = 2)
			val address = parts.getOrNull(0)

			var subject: String? = null
			var body: String? = null

			if (parts.size <= 1) return QREmailModel(address = address ?: "")

			val query = parts.getOrElse(1) { "" }
			val params = query.split("&")

			if (params.isEmpty()) return QREmailModel(address = address ?: "")

			for (param in params) {
				val pair = param.split("=", limit = 2)
				if (pair.size != 2) continue

				val key = pair.getOrNull(0)
				val value = try {
					URLDecoder.decode(body, StandardCharsets.UTF_8.toString())
				} catch (_: Exception) {
					pair.getOrNull(1)
				}
				when (key?.lowercase()) {
					"subject" -> subject = value
					"body" -> body = value
				}
			}

			return QREmailModel(
				address = address ?: "",
				subject = subject,
				body = body,
				isMATMSGFormat = false
			)
		}

		private fun fromMATMSGFormat(content: String): QREmailModel {
			require(content.startsWith("MATMSG:", ignoreCase = true))

			val emailData = content.removePrefix("MATMSG:").trim()

			val fields = emailData.split(";")

			var address: String? = null
			var subject: String? = null
			var body: String? = null

			for (field in fields) {
				val trimmedField = field.trim()
				if (trimmedField.isEmpty()) continue

				val keySeparator = trimmedField.indexOf(':')
				if (keySeparator == -1) continue

				val key = trimmedField.take(keySeparator).uppercase()
				val encodedValue = trimmedField.substring(keySeparator + 1)
				val value = try {
					URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString())
				} catch (_: Exception) {
					encodedValue
				}

				when (key) {
					"TO" -> address = value
					"SUB" -> subject = value
					"BODY" -> body = value
				}
			}
			return QREmailModel(
				address = address ?: "",
				subject = subject,
				body = body,
				isMATMSGFormat = true
			)
		}
	}
}