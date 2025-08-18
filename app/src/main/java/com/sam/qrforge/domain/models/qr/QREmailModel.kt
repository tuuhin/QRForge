package com.sam.qrforge.domain.models.qr

import android.os.Build
import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
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
			subject?.let {
				val encodedSubject = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
					URLEncoder.encode(subject, StandardCharsets.UTF_8)
				else
					URLEncoder.encode(subject)
				params.add("subject=$encodedSubject")
			}
			body?.let {
				val encodedSubject = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
					URLEncoder.encode(subject, StandardCharsets.UTF_8)
				else
					URLEncoder.encode(subject)
				params.add("body=$encodedSubject")
			}
			if (params.isNotEmpty()) {
				append("?")
				append(params.joinToString("&"))
			}
		}
	}
}