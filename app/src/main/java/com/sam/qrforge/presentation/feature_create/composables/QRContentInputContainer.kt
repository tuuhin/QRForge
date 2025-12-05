package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.models.qr.QRURLModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel

@Composable
fun QRContentInputContainer(
	qrContentModel: QRContentModel,
	onContentChange: (QRContentModel) -> Unit,
	onUseCurrentLocation: () -> Unit,
	onReadContactsDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	isLocationEnabled: Boolean = true,
	lastKnownLocation: BaseLocationModel? = null,
	lastReadContacts: ContactsDataModel? = null,
) {
	// remembers the content model until the content type change
	// initial state is only applied when the screen renders first
	val initialState = remember(qrContentModel.type) { qrContentModel }

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		Text(
			text = stringResource(R.string.select_qr_content_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
		)
		Text(
			text = stringResource(R.string.select_qr_content_desc),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(10.dp))
		AnimatedContent(
			targetState = qrContentModel.type,
			transitionSpec = {
				fadeIn(
					initialAlpha = .4f,
					animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
				) + scaleIn(
					initialScale = 0.9f,
					animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
				) togetherWith fadeOut(
					targetAlpha = .4f,
					animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
				) + scaleOut(
					targetScale = 0.9f,
					animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
				)
			},
			contentAlignment = Alignment.Center,
			modifier = Modifier.fillMaxWidth()
		) { type ->
			when (type) {
				QRDataType.TYPE_EMAIL -> QRFormatEmailInput(
					onStateChange = onContentChange,
					initialState = initialState as? QREmailModel ?: QREmailModel()
				)

				QRDataType.TYPE_TEXT -> QRFormatTextInput(
					onStateChange = onContentChange,
					initialState = initialState as? QRPlainTextModel ?: QRPlainTextModel()
				)

				QRDataType.TYPE_URL -> QRFormatURLInput(
					onStateChange = onContentChange,
					initialState = initialState as? QRURLModel ?: QRURLModel()
				)

				QRDataType.TYPE_WIFI -> QRFormatWifiInput(
					onContentChange = onContentChange,
					initialState = initialState as? QRWiFiModel ?: QRWiFiModel()
				)

				QRDataType.TYPE_GEO -> QRFormatGeoInput(
					onStateChange = onContentChange,
					lastKnownLocation = lastKnownLocation,
					isLocationEnabled = isLocationEnabled,
					onUseLastKnownLocation = onUseCurrentLocation,
					initialState = initialState as? QRGeoPointModel ?: QRGeoPointModel()
				)

				QRDataType.TYPE_PHONE -> QRFormatPhoneInput(
					onStateChange = onContentChange,
					onSelectContacts = onReadContactsDetails,
					readContactsModel = lastReadContacts,
					initialState = initialState as? QRTelephoneModel ?: QRTelephoneModel()
				)

				QRDataType.TYPE_SMS -> QRFormatSMSInput(
					onStateChange = onContentChange,
					onSelectContacts = onReadContactsDetails,
					readContactsModel = lastReadContacts,
					initialState = initialState as? QRSmsModel ?: QRSmsModel()
				)
			}
		}
	}
}