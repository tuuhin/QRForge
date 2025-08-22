package com.sam.qrforge.data.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContract

class PickContactsContract : ActivityResultContract<Unit, Uri?>() {

	override fun createIntent(context: Context, input: Unit) =
		Intent(Intent.ACTION_PICK).apply {
			type = ContactsContract.Contacts.CONTENT_TYPE
		}

	override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
		return if (resultCode == Activity.RESULT_OK) intent?.data else null
	}
}