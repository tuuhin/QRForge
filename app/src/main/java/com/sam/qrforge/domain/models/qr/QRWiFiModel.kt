package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

	//	WIFI:S:<SSID>;T:<WEP|WPA|blank>;P:<PASSWORD>;H:<true|false|blank>;;
	override fun toQRString(): String {

		return buildString {
			append("WIFI:")
			ssid?.let {
				val encoded = try {
					URLEncoder.encode(ssid, StandardCharsets.UTF_8.toString())
				} catch (_: Exception) {
					ssid
				}
				append("S:$encoded;")
			}
			val encryptionString = when (encryption) {
				WifiEncryption.WEP -> "WEP"
				WifiEncryption.WPA -> "WPA"
				WifiEncryption.NO_PASS -> "nopass"
			}
			append("T:$encryptionString")
			append(";")
			// only add a pass code if Wi-Fi encryption is added
			if (encryption != WifiEncryption.NO_PASS) password?.let {
				val encoded = try {
					URLEncoder.encode(password, StandardCharsets.UTF_8.toString())
				} catch (_: Exception) {
					password
				}
				append("P:$encoded;")
			}
			// no need to add is hidden to false
			append("H:$isHidden")
			append(";;")
		}
	}

	companion object {
		fun toQRModel(content: String): QRWiFiModel? {
			if (!content.startsWith("WIFI:", ignoreCase = true)) return null

			val content = content.removePrefix("WIFI:").removeSuffix(";;")
			val parts = content.split(";").filter { it.isNotBlank() }

			var ssid: String? = null
			var password: String? = null
			var encryption = WifiEncryption.WPA
			var hidden = false

			for (part in parts) {
				when {
					part.startsWith("S:") -> {
						val encodedSSID = part.removePrefix("S:")
						ssid = try {
							URLDecoder.decode(encodedSSID, StandardCharsets.UTF_8.toString())
						} catch (_: Exception) {
							encodedSSID
						}
					}

					part.startsWith("T:") -> {
						encryption = when (part.removePrefix("T:")) {
							"WEP" -> WifiEncryption.WEP
							"WPA" -> WifiEncryption.WPA
							"nopass" -> WifiEncryption.NO_PASS
							else -> WifiEncryption.WPA
						}
					}

					part.startsWith("P:") -> {
						val encodedPassword = part.removePrefix("P:")
						password = try {
							URLDecoder.decode(encodedPassword, StandardCharsets.UTF_8.toString())
						} catch (_: Exception) {
							encodedPassword
						}
					}
					part.startsWith("H:") -> hidden = part.removePrefix("H:").lowercase() == "true"
				}
			}

			return QRWiFiModel(
				ssid = ssid,
				password = password,
				encryption = encryption,
				isHidden = hidden
			)
		}
	}
}
