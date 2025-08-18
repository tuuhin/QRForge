package com.sam.qrforge.domain.models.qr

import android.os.Build
import android.util.Patterns
import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
data class QRURLModel(val url: String? = "") : QRContentModel(type = QRDataType.TYPE_URL) {

	override val isValid: Boolean
		get() = url?.isNotEmpty() == true && Patterns.WEB_URL.matcher(url).matches()

	override fun toQRString(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
		URLEncoder.encode(url, StandardCharsets.UTF_8)
	else URLEncoder.encode(url)

}
