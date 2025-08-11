package com.sam.qrforge.feature_generator.domain.models.qr

import android.os.Build
import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
data class QRURLModel(val url: String) : QRDataModel(type = QRDataFormat.TYPE_URL) {

	override fun toQRString(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
		URLEncoder.encode(url, StandardCharsets.UTF_8)
	else URLEncoder.encode(url)

}
