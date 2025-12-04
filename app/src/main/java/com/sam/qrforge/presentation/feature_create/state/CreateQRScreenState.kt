package com.sam.qrforge.presentation.feature_create.state

import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel

data class CreateQRScreenState(
	val showLocationDialog: Boolean = false,
	val lastReadLocation: BaseLocationModel? = null,
	val lastReadContacts: ContactsDataModel? = null,
	val isLocationEnabled: Boolean = false,
	val qrContent: QRContentModel = QRPlainTextModel()
)