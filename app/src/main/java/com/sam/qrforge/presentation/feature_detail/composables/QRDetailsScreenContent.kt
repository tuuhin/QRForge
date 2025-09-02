package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.AnimatedBasicQRContent
import com.sam.qrforge.presentation.common.composables.QRContentStringCard
import com.sam.qrforge.presentation.common.composables.QRContentTypeChip
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.PLAIN_DATE_TIME
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@Composable
fun QRDetailsScreenContent(
	savedContent: SavedQRModel,
	onShare: (ImageBitmap) -> Unit,
	onExport: () -> Unit,
	modifier: Modifier = Modifier,
	generatedModel: GeneratedQRUIModel? = null,
) {
	val graphicsLayer = rememberGraphicsLayer()
	val scope = rememberCoroutineScope()
	val scrollState = rememberScrollState()

	Column(
		modifier = modifier.verticalScroll(scrollState),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		QRContentTypeChip(type = savedContent.format)
		AnimatedBasicQRContent(
			generated = generatedModel,
			graphicsLayer = { graphicsLayer },
		)
		QRCommonActions(
			showActions = generatedModel != null,
			type = savedContent.format,
			onShare = {
				scope.launch {
					val bitmap = graphicsLayer.toImageBitmap()
					onShare(bitmap)
				}
			},
			onExport = onExport,
			onAction = {},
		)
		Spacer(modifier = Modifier.height(2.dp))
		savedContent.desc?.let { desc ->
			QRDescriptionCard(
				desc,
				modifier = Modifier.fillMaxWidth()
			)
		}
		QRContentStringCard(
			contentString = savedContent.content,
			onContentCopy = {},
		)
		Text(
			text = buildString {
				append("Last Updated: ")
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