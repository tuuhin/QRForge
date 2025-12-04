package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRContentModel

@Composable
fun CreateQRScreenContent(
	content: QRContentModel,
	onSelectType: (QRDataType) -> Unit,
	modifier: Modifier = Modifier,
	onReadCurrentLocation: () -> Unit = {},
	onReadContactsDetails: (String) -> Unit = {},
	onContentUpdate: (QRContentModel) -> Unit,
	isLocationEnabled: Boolean = true,
	lastKnownLocation: BaseLocationModel? = null,
	lastReadContacts: ContactsDataModel? = null,
	contentPadding: PaddingValues = PaddingValues(12.dp)
) {
	val scrollState = rememberScrollState()

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp),
		modifier = modifier
			.padding(contentPadding)
			.verticalScroll(scrollState),
	) {
		QRDataTypeSelector(
			selectedType = content.type,
			onSelectType = onSelectType,
			modifier = Modifier.fillMaxWidth()
		)
		QRContentInputContainer(
			qrContentModel = content,
			isLocationEnabled = isLocationEnabled,
			lastKnownLocation = lastKnownLocation,
			lastReadContacts = lastReadContacts,
			onUseCurrentLocation = onReadCurrentLocation,
			onReadContactsDetails = onReadContactsDetails,
			onContentChange = onContentUpdate,
		)
	}
}