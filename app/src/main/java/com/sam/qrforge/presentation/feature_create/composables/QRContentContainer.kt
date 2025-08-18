package com.sam.qrforge.presentation.feature_create.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import com.sam.qrforge.data.utils.hasLocationPermission
import com.sam.qrforge.data.utils.hasReadContactsPermission
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.presentation.feature_create.contracts.PickContactsActivityResult

@Composable
fun QRContentContainer(
	content: QRContentModel,
	onContentChange: (QRContentModel) -> Unit,
	onUseCurrentLocation: () -> Unit,
	onReadContactsDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	selectedType: QRDataType = QRDataType.TYPE_TEXT,
) {
	val context = LocalContext.current

	var hasContactsPermission by remember { mutableStateOf(context.hasReadContactsPermission) }
	var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission) }

	val permissionsLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions(),
		onResult = { perms ->
			if (perms.containsKey(Manifest.permission.READ_CONTACTS)) {
				hasContactsPermission =
					perms.getOrDefault(Manifest.permission.READ_CONTACTS, false)
			}
			if (perms.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)) {
				hasLocationPermission =
					perms.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
			}
		}
	)

	val readContactsLauncher = rememberLauncherForActivityResult(
		contract = PickContactsActivityResult(),
		onResult = { uri -> onReadContactsDetails(uri.toString()) },
	)

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Text(
			text = "QR Content",
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary
		)
		Text(
			text = "Enter content based on the selected type",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface
		)
		Spacer(modifier = Modifier.height(8.dp))
		Crossfade(
			targetState = selectedType,
			modifier = modifier
				.fillMaxWidth()
				.animateContentSize()
		) { type ->
			when (type) {
				QRDataType.TYPE_EMAIL -> QRFormatEmailInput(onStateChange = onContentChange)

				QRDataType.TYPE_TEXT, QRDataType.TYPE_URL -> QRFormatTextInput(onStateChange = onContentChange)
				QRDataType.TYPE_WIFI -> QRFormatWifiInput(onContentChange = onContentChange)
				QRDataType.TYPE_GEO -> QRFormatGeoInput(
					initialState = (content as? QRGeoPointModel) ?: QRGeoPointModel(),
					onStateChange = onContentChange,
					onUseCurrentLocation = dropUnlessResumed {
						if (hasLocationPermission) onUseCurrentLocation()
						else permissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
					},
				)

				QRDataType.TYPE_PHONE -> QRFormatPhoneInput(
					initialState = (content as? QRTelephoneModel) ?: QRTelephoneModel(),
					onStateChange = onContentChange,
					onSelectContacts = {
						if (hasContactsPermission) readContactsLauncher.launch(Unit)
						else permissionsLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
					},
				)

				QRDataType.TYPE_SMS -> QRFormatSMSInput(
					initialState = (content as? QRSmsModel) ?: QRSmsModel(),
					onStateChange = onContentChange,
					onOpenContacts = dropUnlessResumed {
						if (hasContactsPermission) readContactsLauncher.launch(Unit)
						else permissionsLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
					},
				)
			}
		}
	}
}