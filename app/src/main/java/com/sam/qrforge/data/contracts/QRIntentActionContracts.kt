package com.sam.qrforge.data.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.models.qr.QRURLModel

class QRIntentActionContracts : ActivityResultContract<QRContentModel, Int>() {

	override fun createIntent(context: Context, input: QRContentModel): Intent {
		return when (input) {
			is QREmailModel -> Intent(Intent.ACTION_SENDTO).apply {
				data = "mailto:".toUri()
				putExtra(Intent.EXTRA_EMAIL, arrayOf(input.address))
				input.subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
				input.body?.let { putExtra(Intent.EXTRA_TEXT, it) }
			}

			is QRSmsModel -> Intent(Intent.ACTION_SEND).apply {
				data = "smsto:${input.phoneNumber}".toUri()
				input.message?.let { putExtra("sms_body", it) }
			}

			is QRGeoPointModel -> Intent(Intent.ACTION_VIEW, input.toQRString().toUri())
			is QRTelephoneModel -> Intent(Intent.ACTION_DIAL, input.toQRString().toUri())
			is QRURLModel -> Intent(Intent.ACTION_VIEW, input.toQRString().toUri())
			else -> Intent()
		}
	}

	override fun parseResult(resultCode: Int, intent: Intent?): Int = resultCode

}