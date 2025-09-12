package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GenerateORScanActions(
	onGenerate: () -> Unit,
	onScan: () -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
	buttonShape: Shape = MaterialTheme.shapes.extraLarge,
	generateButtonColor: Color = MaterialTheme.colorScheme.primary,
	scanButtonColor: Color = MaterialTheme.colorScheme.secondary,
) {

	Box(
		modifier = modifier
			.background(
				MaterialTheme.colorScheme.surfaceContainer,
				shape = MaterialTheme.shapes.extraLarge.copy(
					bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize
				),
			)
			.fillMaxWidth()
			.heightIn(100.dp), contentAlignment = Alignment.Center
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			modifier = Modifier
				.padding(contentPadding)
				.windowInsetsPadding(WindowInsets.navigationBars)
		) {
			Button(
				onClick = onGenerate,
				shape = buttonShape,
				colors = ButtonDefaults.buttonColors(containerColor = generateButtonColor),
				contentPadding = PaddingValues(vertical = 16.dp),
				modifier = Modifier
					.weight(1f)
					.sharedBoundsWrapper(SharedTransitionKeys.HOME_SCREEN_TO_CREATE_QR_SCREEN)
			) {
				Icon(
					painter = painterResource(R.drawable.ic_qr_simplified),
					contentDescription = null,
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = stringResource(R.string.generate_qr),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			}
			Button(
				onClick = onScan,
				shape = buttonShape,
				colors = ButtonDefaults.buttonColors(containerColor = scanButtonColor),
				contentPadding = PaddingValues(vertical = 16.dp),
				modifier = Modifier
					.weight(1f)
					.sharedBoundsWrapper(SharedTransitionKeys.SCAN_BUTTON_TO_SCAN_SCREEN)
			) {
				Icon(
					painter = painterResource(R.drawable.ic_scan),
					contentDescription = null,
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = stringResource(R.string.scan_qr),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}