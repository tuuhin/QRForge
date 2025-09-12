package com.sam.qrforge.presentation.feature_scan.composable

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun PermissionPlaceHolder(
	onPermissionChanged: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(12.dp)
) {

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { isGranted -> onPermissionChanged(isGranted) },
	)

	Box(
		modifier = modifier.padding(contentPadding),
		contentAlignment = Alignment.Center,
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Image(
				painter = painterResource(R.drawable.ic_scanner),
				contentDescription = "Scanner",
				colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
				modifier = Modifier
					.size(200.dp)
					.rotate(-10f)
			)
			Text(
				text = stringResource(id = R.string.camera_permission_text),
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center,
			)
			Button(
				onClick = { launcher.launch(Manifest.permission.CAMERA) },
				shape = MaterialTheme.shapes.large,
				contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
			) {
				Icon(
					painter = painterResource(id = R.drawable.ic_camera),
					contentDescription = null
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = stringResource(id = R.string.allow_permission_title),
					style = MaterialTheme.typography.titleMedium,
					textAlign = TextAlign.Center,
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun MissingPermissionPlaceHolderPreview() = QRForgeTheme {
	Surface {
		PermissionPlaceHolder(
			onPermissionChanged = {},
			modifier = Modifier.fillMaxSize()
		)
	}
}