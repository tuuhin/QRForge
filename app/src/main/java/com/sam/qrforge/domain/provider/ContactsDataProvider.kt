package com.sam.qrforge.domain.provider

import com.sam.qrforge.domain.models.ContactsDataModel

fun interface ContactsDataProvider {

	suspend fun invoke(uri: String): Result<ContactsDataModel>
}