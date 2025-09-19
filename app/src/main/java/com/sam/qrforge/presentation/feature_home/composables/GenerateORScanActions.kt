package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.contentColorFor
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
import com.sam.qrforge.presentation.common.utils.sharedTransitionRenderInOverlay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GenerateORScanActions(
	onGenerate: () -> Unit,
	onScan: () -> Unit,
	modifier: Modifier = Modifier,
	buttonShape: Shape = MaterialTheme.shapes.extraLarge,
	generateButtonColor: Color = MaterialTheme.colorScheme.primary,
	scanButtonColor: Color = MaterialTheme.colorScheme.secondary,
) {
	val clipShape = MaterialTheme.shapes.extraLarge.copy(
		bottomStart = ZeroCornerSize,
		bottomEnd = ZeroCornerSize
	)

	Box(
		modifier = modifier
			.sharedTransitionRenderInOverlay(1f)
			.fillMaxWidth()
			.heightIn(100.dp)
			.background(
				color = MaterialTheme.colorScheme.surfaceContainerHigh,
				shape = clipShape,
			),
		contentAlignment = Alignment.Center
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.windowInsetsPadding(WindowInsets.navigationBars)
				.padding(horizontal = 12.dp, vertical = 4.dp)
		) {
			Button(
				onClick = onGenerate,
				shape = buttonShape,
				colors = ButtonDefaults.buttonColors(
					containerColor = generateButtonColor,
					contentColor = contentColorFor(generateButtonColor)
				),
				contentPadding = PaddingValues(vertical = 16.dp),
				modifier = Modifier
					.weight(1f)
					.sharedBoundsWrapper(
						key = SharedTransitionKeys.HOME_SCREEN_TO_CREATE_QR_SCREEN,
						resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
						clipShape = MaterialTheme.shapes.extraLarge,
						zIndexInOverlay = 2f,
					),
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
				colors = ButtonDefaults.buttonColors(
					containerColor = scanButtonColor,
					contentColor = contentColorFor(scanButtonColor)
				),
				contentPadding = PaddingValues(vertical = 16.dp),
				modifier = Modifier
					.weight(1f)
					.sharedBoundsWrapper(
						key = SharedTransitionKeys.SCAN_BUTTON_TO_SCAN_SCREEN,
						resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
						clipShape = MaterialTheme.shapes.extraLarge,
						zIndexInOverlay = 2f,
					),
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