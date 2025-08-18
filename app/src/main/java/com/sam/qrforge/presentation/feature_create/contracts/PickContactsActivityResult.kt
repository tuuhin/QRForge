package com.sam.qrforge.presentation.feature_create.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContract

class PickContactsActivityResult : ActivityResultContract<Unit, Uri?>() {

	override fun createIntent(context: Context, input: Unit) =
		Intent(Intent.ACTION_PICK).apply {
			type = ContactsContract.Contacts.CONTENT_TYPE
		}

	override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
		return if (resultCode == Activity.RESULT_OK) intent?.data else null
	}
}