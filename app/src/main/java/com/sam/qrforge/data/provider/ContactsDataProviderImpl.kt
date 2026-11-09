package com.sam.qrforge.data.provider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import com.sam.qrforge.data.utils.hasReadContactsPermission
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.exception.ContactsPermissionMissingException
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class ContactsDataProviderImpl(
    private val context: Context,
    private val dispatchers: PlatformCoroutineDispatchers,
) : ContactsDataProvider {

	private val secondProjection: Array<String>
		get() = arrayOf(
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.HAS_PHONE_NUMBER,
		)

	private val primaryProjection: Array<String>
		get() = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)


	override suspend fun invoke(uri: String): Result<ContactsDataModel> {
		if (!context.hasReadContactsPermission)
			return Result.failure(ContactsPermissionMissingException())

		val contactsUri = ContactsContract.Contacts
			.getLookupUri(context.contentResolver, uri.toUri())

        return withContext(dispatchers.io) {
			try {
				val result = context.contentResolver
					.query(contactsUri, secondProjection, null, null)
					?.use { cursor -> cursorToContactsModel(cursor) }
					?: return@withContext Result.failure(Exception("Unable to process info"))
				Result.success(result)
			} catch (e: Exception) {
                if (e is CancellationException) throw e
				e.printStackTrace()
				Result.failure(e)
			}
		}
	}

	private fun cursorToContactsModel(cursor: Cursor): ContactsDataModel? {
		if (!cursor.moveToFirst()) return null

		val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
		val displayNameColumn = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
		val hasNumberColumn =
			cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)

		val id = cursor.getLong(idColumn)
		val displayName = cursor.getString(displayNameColumn)
		val hasPhNumber = cursor.getString(hasNumberColumn)

		if (hasPhNumber == "0") return null

		val phNumber = readPhoneNumber(id) ?: return null
		val strippedPhNumber = phNumber.trim().replace("\\s+".toRegex(), "")

		return ContactsDataModel(
			displayName = displayName,
			phoneNumber = strippedPhNumber,
		)
	}

	private fun readPhoneNumber(id: Long): String? {

        val args = Bundle().apply {
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            )
            putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf("$id"))
        }

		return context.contentResolver.query(
			ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			primaryProjection,
			args,
			null
		)?.use { cursor ->
			if (!cursor.moveToFirst()) return@use null
			val phColumn =
				cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
			cursor.getStringOrNull(phColumn)
		}
	}
}
