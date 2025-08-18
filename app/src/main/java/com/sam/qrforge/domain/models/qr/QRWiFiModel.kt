package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRWiFiModel(
	val ssid: String? = null,
	val password: String? = null,
	val encryption: WifiEncryption = WifiEncryption.WPA,
	val isHidden: Boolean = false,
) : QRContentModel(type = QRDataType.TYPE_WIFI) {

	enum class WifiEncryption {
		WEP, WPA, NO_PASS
	}

	override val isValid: Boolean
		get() = if (encryption == WifiEncryption.NO_PASS) ssid?.isNotEmpty() == true
		else ssid?.isNotEmpty() == true && password?.isNotEmpty() == true

	override fun toQRString(): String {
		return buildString {
			append("WIFI")
			ssid?.let { append("S:$ssid") }
			append(";")
			val encryptionString = when (encryption) {
				WifiEncryption.WEP -> "WEP"
				WifiEncryption.WPA -> "WPA"
				WifiEncryption.NO_PASS -> "nopass"
			}
			append("T:$encryptionString")
			// only add a pass code if Wi-Fi encryption is added
			if (encryption != WifiEncryption.NO_PASS) password?.let { append("P:$password") }
			// no need to add is hidden to false
			if (isHidden) append("H:true")
			append(";;")
		}
	}
}
