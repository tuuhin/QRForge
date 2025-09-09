package com.sam.qrforge.data.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri

class PreviewImageUriContract : ActivityResultContract<String, Unit>() {

	override fun createIntent(context: Context, input: String): Intent {
		val launchIntent = Intent().apply {
			action = Intent.ACTION_VIEW
			setDataAndType(input.toUri(), "image/*")
		}
		return Intent.createChooser(launchIntent, "Select App to Open")
	}

	override fun parseResult(resultCode: Int, intent: Intent?) = Unit

}