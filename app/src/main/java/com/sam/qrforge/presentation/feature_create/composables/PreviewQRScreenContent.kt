package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.AnimatedBasicQRContent
import com.sam.qrforge.presentation.common.composables.QRContentStringCard
import com.sam.qrforge.presentation.common.composables.QRContentTypeChip
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import kotlinx.coroutines.launch

@Composable
fun PreviewQRScreenContent(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	onShareContent: (ImageBitmap) -> Unit = {},
	onExportContent: () -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {

	val graphicsLayer = rememberGraphicsLayer()
	val scope = rememberCoroutineScope()

	Column(
		modifier = modifier.padding(contentPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Spacer(modifier = Modifier.height(20.dp))
		QRContentTypeChip(content.type)
		AnimatedBasicQRContent(
			generated = generated,
			graphicsLayer = { graphicsLayer },
		)
		ExportShareActions(
			actionEnabled = generated != null,
			onShare = { scope.launch { onShareContent(graphicsLayer.toImageBitmap()) } },
			onExport = onExportContent,
		)
		QRContentStringCard(contentString = content.toQRString())
	}
}
