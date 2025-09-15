package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.feature_scan.state.CaptureType

@Composable
fun CameraCaptureTypePicker(
	onCaptureTypeChange: (CaptureType) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	alwaysShowIcon: Boolean = true,
	selected: CaptureType = CaptureType.AUTO,
) {

	SingleChoiceSegmentedButtonRow(modifier = modifier) {
		CaptureType.entries.forEachIndexed { index, type ->
			SegmentedButton(
				enabled = enabled,
				selected = selected == type,
				onClick = { onCaptureTypeChange(type) },
				icon = {
					AnimatedVisibility(
						visible = alwaysShowIcon || selected == type,
						enter = fadeIn() + expandIn(),
						exit = fadeOut() + shrinkOut(),
					) {
						Icon(
							painter = when (type) {
								CaptureType.MANUAL -> painterResource(R.drawable.ic_manual)
								CaptureType.AUTO -> painterResource(R.drawable.ic_auto)
							},
							contentDescription = type.stringRes,
						)
					}
				},
				label = { Text(text = type.stringRes) },
				shape = SegmentedButtonDefaults.itemShape(
					index = index,
					count = ImageMimeTypes.entries.size
				),
			)
		}
	}
}