package com.sam.qrforge.presentation.feature_scan.composable

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.applicationSettingsIntent
import com.sam.qrforge.presentation.common.utils.sharedTransitionSkipChildSize
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionPlaceHolder(
	permissionStatus: PermissionStatus,
	modifier: Modifier = Modifier,
	onLaunchPermissionDialog: () -> Unit,
	onGalleryImageSelected: (String) -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(12.dp)
) {
	val context = LocalContext.current

	val pickVisualLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = { pickerURI -> pickerURI?.let { onGalleryImageSelected(it.toString()) } },
	)

	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.padding(contentPadding)
	) {
		Image(
			painter = painterResource(R.drawable.ic_scanner),
			contentDescription = "Scanner",
			colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
			modifier = Modifier
				.size(180.dp)
				.rotate(-12f)
		)
		Spacer(modifier = Modifier.height(24.dp))
		Column(
			modifier = Modifier
				.fillMaxWidth(.85f)
				.sharedTransitionSkipChildSize(),
			verticalArrangement = Arrangement.spacedBy(6.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (permissionStatus.shouldShowRationale) {
				Text(
					text = stringResource(R.string.camera_permission_denied_title),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.primary
				)
				Text(
					text = stringResource(R.string.permission_denied_text),
					style = MaterialTheme.typography.labelLarge,
					textAlign = TextAlign.Center,
				)
			} else {
				Text(
					text = stringResource(R.string.camera_permission_title),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.primary
				)
				Text(
					text = stringResource(id = R.string.camera_permission_text),
					style = MaterialTheme.typography.labelLarge,
					textAlign = TextAlign.Center,
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))
		Button(
			onClick = {
				if (!permissionStatus.shouldShowRationale) onLaunchPermissionDialog()
				else try {
					context.startActivity(context.applicationSettingsIntent)
				} catch (_: ActivityNotFoundException) {
				}
			},
			shape = MaterialTheme.shapes.large,
			contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
		) {
			if (permissionStatus.shouldShowRationale) {
				Icon(
					painterResource(R.drawable.ic_settings),
					contentDescription = "Settings",
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = stringResource(R.string.open_app_settings),
					style = MaterialTheme.typography.titleSmall,
				)
			} else {
				Icon(
					painter = painterResource(id = R.drawable.ic_camera),
					contentDescription = null,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = stringResource(id = R.string.allow_permission_title),
					style = MaterialTheme.typography.titleSmall,
				)
			}
		}
		Spacer(modifier = Modifier.height(2.dp))
		Button(
			onClick = {
				val request = PickVisualMediaRequest.Builder()
					.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
					.setDefaultTab(ActivityResultContracts.PickVisualMedia.DefaultTab.PhotosTab)
					.build()
				pickVisualLauncher.launch(request)
			},
			colors = ButtonDefaults.buttonColors(
				contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
				containerColor = MaterialTheme.colorScheme.secondaryContainer
			),
			shape = MaterialTheme.shapes.large,
		) {
			Text(
				text = stringResource(R.string.scan_qr_option_select_from_gallery),
				style = MaterialTheme.typography.titleSmall,
				textAlign = TextAlign.Center,
			)
		}
	}
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewLightDark
@Composable
private fun MissingPermissionPlaceHolderPreview() = QRForgeTheme {
	Surface {
		PermissionPlaceHolder(
			permissionStatus = PermissionStatus.Denied(false),
			onLaunchPermissionDialog = {},
			onGalleryImageSelected = {},
			modifier = Modifier.fillMaxSize()
		)
	}
}