package com.sam.qrforge.presentation.feature_detail.composables

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.data.contracts.QRIntentActionContracts
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.AnimatedBasicQRContent
import com.sam.qrforge.presentation.common.composables.QRContentStringCard
import com.sam.qrforge.presentation.common.composables.QRContentTypeChip
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.PLAIN_DATE_TIME
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedElementWrapper
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun QRDetailsScreenContent(
	savedContent: SavedQRModel,
	onShare: (ImageBitmap) -> Unit,
	onExport: () -> Unit,
	modifier: Modifier = Modifier,
	onConnectToWifi: () -> Unit = {},
	generatedModel: GeneratedQRUIModel? = null,
	scrollState: ScrollState = rememberScrollState()
) {
	val captureLayer = CanvasCaptureLayer.rememberCaptureLayer()
	val scope = rememberCoroutineScope()

	val context = LocalContext.current

	val launcher = rememberLauncherForActivityResult(
		contract = QRIntentActionContracts(),
		onResult = {},
	)

	Column(
		modifier = modifier.verticalScroll(scrollState),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		QRContentTypeChip(
			type = savedContent.format,
			modifier = Modifier.sharedElementWrapper(
				key = SharedTransitionKeys.sharedElementContentTypeCard(savedContent.id)
			)
		)
		AnimatedBasicQRContent(
			generated = generatedModel,
			captureLayer = captureLayer,
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.sharedElementWrapper(
				key = SharedTransitionKeys.sharedElementQRCodeItemToDetail(savedContent.id),
				placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
				clipShape = MaterialTheme.shapes.medium
			)
		)
		QRCommonActions(
			isQRReady = generatedModel != null,
			type = savedContent.format,
			hasAssociatedAction = savedContent.format != QRDataType.TYPE_TEXT,
			onShare = {
				scope.launch {
					val bitmap = captureLayer.captureBitmap()
					if (bitmap == null) return@launch
					onShare(bitmap)
				}
			},
			onExport = onExport,
			onAction = {
				if (savedContent.format == QRDataType.TYPE_WIFI) onConnectToWifi()
				else if (savedContent.format != QRDataType.TYPE_TEXT) try {
					launcher.launch(savedContent.content)
				} catch (_: Exception) {
					val message = "Cannot complete Action"
					Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
				}
			},
		)
		Spacer(modifier = Modifier.height(2.dp))
		savedContent.desc?.let { desc ->
			QRDescriptionCard(
				text = desc,
				modifier = Modifier.fillMaxWidth()
			)
		}
		QRContentStringCard(contentString = savedContent.content.toQRString())
		Text(
			text = buildString {
				append(stringResource(R.string.last_updated_text))
				append(" ")
				append(savedContent.modifiedAt.format(LocalDateTime.Formats.PLAIN_DATE_TIME))
			},
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.tertiary,
			fontWeight = FontWeight.Normal,
		)
	}
}

@Composable
private fun QRDescriptionCard(
	text: String,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
) {
	var isExpanded by remember { mutableStateOf(false) }

	ElevatedCard(
		shape = shape,
		onClick = { isExpanded = !isExpanded },
		modifier = modifier.animateContentSize(
			animationSpec = tween(durationMillis = 100, easing = EaseInOutBack)
		),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = stringResource(R.string.qr_description_text),
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.secondary
			)
			Text(
				text = text,
				maxLines = if (isExpanded) Int.MAX_VALUE else 2,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}