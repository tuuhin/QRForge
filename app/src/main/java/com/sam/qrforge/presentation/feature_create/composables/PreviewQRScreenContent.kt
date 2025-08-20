package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.QRTemplatePicker
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes

@Composable
fun PreviewQRScreenContent(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	onDecorationChange: (QRDecorationOption) -> Unit,
	onTemplateChange: (QRTemplateOption) -> Unit = {},
	templateDecoration: QRDecorationOption = QRDecorationOptionBasic(),
	generated: GeneratedQRUIModel? = null,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	LazyColumn(
		contentPadding = contentPadding,
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		stickyHeader {
			GeneratedQRWithActions(
				generated = generated,
				templateDecoration = templateDecoration
			)
		}
		item {
			QRContentStringCard(content = content)
		}
		item {
			QRTemplatePicker(
				model = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
				selectedTemplate = templateDecoration.templateType,
				onTemplateChange = onTemplateChange,
				modifier = Modifier.fillMaxWidth()
			)
		}
		item {
			EditQRDecoration(
				decoration = templateDecoration,
				onDecorationChange = onDecorationChange,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}
}

@Composable
private fun QRContentStringCard(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(12.dp),
) {

	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier
				.heightIn(80.dp)
				.padding(contentPadding),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = "Content",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				Text(
					text = content.toQRString(),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
			}
			Button(
				onClick = {

				},
				modifier = Modifier.size(36.dp),
				contentPadding = PaddingValues(all = 8.dp),
				shape = MaterialTheme.shapes.medium,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer,
					contentColor = MaterialTheme.colorScheme.onSecondaryContainer
				)
			) {
				Icon(
					painter = painterResource(R.drawable.ic_copy),
					contentDescription = "Action Copy",
				)
			}
		}
	}
}
