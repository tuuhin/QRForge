package com.sam.qrforge.presentation.feature_create.state

import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.ContactsDataModel

data class CreateQRScreenState(
	val showLocationDialog: Boolean = false,
	val lastReadLocation: BaseLocationModel? = null,
	val lastReadContacts: ContactsDataModel? = null,
	val isLocationEnabled: Boolean = false,
	val selectedQRFormat: QRDataType = QRDataType.TYPE_TEXT,
)