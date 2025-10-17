package com.sam.qrforge.presentation.feature_scan.composable

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sam.qrforge.data.contracts.QRIntentActionContracts
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.AnimatedBasicQRContent
import com.sam.qrforge.presentation.common.composables.QRContentStringCard
import com.sam.qrforge.presentation.common.composables.QRContentTypeChip
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import kotlinx.coroutines.launch

@Composable
fun ScanResultsScreenContent(
	content: QRContentModel,
	onShare: (ImageBitmap) -> Unit,
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
		Spacer(modifier = Modifier.height(12.dp))
		QRContentTypeChip(type = content.type)
		AnimatedBasicQRContent(
			generated = generatedModel,
			captureLayer = captureLayer,
		)
		ScanQRActions(
			type = content.type,
			hasAssociatedAction = content.type != QRDataType.TYPE_TEXT,
			onShare = {
				scope.launch {
					val bitmap = captureLayer.captureBitmap()
					if (bitmap == null) return@launch
					onShare(bitmap)
				}
			},
			onAction = {
				if (content.type == QRDataType.TYPE_WIFI) onConnectToWifi()
				else if (content.type != QRDataType.TYPE_TEXT)
					try {
						launcher.launch(content)
					} catch (_: Exception) {
						val message = "Cannot complete Action"
						Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
					}
			},
		)
		QRContentStringCard(contentString = content.toQRString())
	}
}