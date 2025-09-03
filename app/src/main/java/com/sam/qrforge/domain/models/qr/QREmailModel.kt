package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class QREmailModel(
	val address: String = "",
	val subject: String? = null,
	val body: String? = null
) : QRContentModel(type = QRDataType.TYPE_EMAIL) {

	override val isValid: Boolean
		get() = address.isNotEmpty()

	override fun toQRString(): String {
		return buildString {
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
	}

	companion object {

		fun toQRModel(content: String): QREmailModel? {

			if (!content.startsWith("mailto:", ignoreCase = true)) return null

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
				body = body
			)
		}
	}
}