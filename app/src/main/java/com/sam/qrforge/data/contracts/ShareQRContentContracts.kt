package com.sam.qrforge.data.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import com.sam.qrforge.R

class ShareQRContentContracts : ActivityResultContract<String, Boolean>() {

	override fun createIntent(context: Context, input: String): Intent {
		val intent = Intent().apply {
			action = Intent.ACTION_SEND
			setDataAndType(input.toUri(), "image/png")
			putExtra(Intent.EXTRA_STREAM, input.toUri())
			flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
		}
		return Intent.createChooser(intent, context.getString(R.string.share_qr_chooser_title))
	}

	override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
		return resultCode == Activity.RESULT_OK
	}
}