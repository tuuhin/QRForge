package com.sam.qrforge.presentation.feature_create.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.data.contracts.PickContactsContract
import com.sam.qrforge.data.utils.hasCoarseLocationPermission
import com.sam.qrforge.data.utils.hasReadContactsPermission
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel

@Composable
fun QRContentInputContainer(
	content: QRContentModel,
	onContentChange: (QRContentModel) -> Unit,
	onUseCurrentLocation: () -> Unit,
	onReadContactsDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	isLocationEnabled: Boolean = true,
) {
	val context = LocalContext.current

	val currentOnReadContactsDetails by rememberUpdatedState(onReadContactsDetails)

	var hasContactsPermission by remember { mutableStateOf(context.hasReadContactsPermission) }
	var hasLocationPermission by remember { mutableStateOf(context.hasCoarseLocationPermission) }

	val permissionsLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions(),
		onResult = { perms ->
			if (perms.containsKey(Manifest.permission.READ_CONTACTS)) {
				hasContactsPermission =
					perms.getOrDefault(Manifest.permission.READ_CONTACTS, false)
			}
			// we want any of the permissions to be granted
			if (perms.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION) ||
				perms.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)
			) {
				hasLocationPermission =
					perms.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ||
							perms.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
			}
		}
	)

	val readContactsLauncher = rememberLauncherForActivityResult(
		contract = PickContactsContract(),
		onResult = { uri ->
			val uriString = uri?.toString() ?: return@rememberLauncherForActivityResult
			currentOnReadContactsDetails(uriString)
		},
	)

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
			targetState = content.type,
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
					initialState = (content as? QRGeoPointModel) ?: QRGeoPointModel(),
					onStateChange = onContentChange,
					isLocationEnabled = isLocationEnabled,
					onUseLastKnownLocation = {
						if (hasLocationPermission) onUseCurrentLocation()
						else permissionsLauncher.launch(
							arrayOf(
								Manifest.permission.ACCESS_COARSE_LOCATION,
								Manifest.permission.ACCESS_FINE_LOCATION
							)
						)
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
					onOpenContacts = {
						if (hasContactsPermission) readContactsLauncher.launch(Unit)
						else permissionsLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
					},
				)
			}
		}
	}
}