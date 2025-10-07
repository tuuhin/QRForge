package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRContentModel

@Composable
fun QRContentInputContainer(
	qrContentType: QRDataType,
	onContentChange: (QRContentModel) -> Unit,
	onUseCurrentLocation: () -> Unit,
	onReadContactsDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	isLocationEnabled: Boolean = true,
	lastKnownLocation: BaseLocationModel? = null,
	lastReadContacts: ContactsDataModel? = null,
) {

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
			targetState = qrContentType,
			transitionSpec = {
				slideInVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + expandIn(
					expandFrom = Alignment.TopCenter,
					animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
				) togetherWith
						slideOutVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + shrinkOut(
					shrinkTowards = Alignment.BottomCenter,
					animationSpec = tween(durationMillis = 400, easing = EaseInOut)
				) using SizeTransform(clip = false)
			},
			contentAlignment = Alignment.Center,
			modifier = Modifier.fillMaxWidth()
		) { type ->
			when (type) {
				QRDataType.TYPE_EMAIL -> QRFormatEmailInput(onStateChange = onContentChange)
				QRDataType.TYPE_TEXT -> QRFormatTextInput(onStateChange = onContentChange)
				QRDataType.TYPE_URL -> QRFormatURLInput(onStateChange = onContentChange)
				QRDataType.TYPE_WIFI -> QRFormatWifiInput(onContentChange = onContentChange)
				QRDataType.TYPE_GEO -> QRFormatGeoInput(
					onStateChange = onContentChange,
					lastKnownLocation = lastKnownLocation,
					isLocationEnabled = isLocationEnabled,
					onUseLastKnownLocation = onUseCurrentLocation,
				)

				QRDataType.TYPE_PHONE -> QRFormatPhoneInput(
					onStateChange = onContentChange,
					onSelectContacts = onReadContactsDetails,
					readContactsModel = lastReadContacts
				)

				QRDataType.TYPE_SMS -> QRFormatSMSInput(
					onStateChange = onContentChange,
					onSelectContacts = onReadContactsDetails,
					readContactsModel = lastReadContacts
				)
			}
		}
	}
}