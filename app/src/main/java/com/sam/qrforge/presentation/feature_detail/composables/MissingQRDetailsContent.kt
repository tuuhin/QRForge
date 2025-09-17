package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun MissingQRDetailsContent(
	onBackToHome: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.widthIn(min = 180.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_exclamation),
			contentDescription = "Not found",
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
			modifier = Modifier.size(200.dp),
		)
		Spacer(modifier = Modifier.height(20.dp))
		Text(
			text = stringResource(R.string.qr_content_missing_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.secondary
		)
		Text(
			text = stringResource(R.string.qr_content_missing_desc),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center,
			modifier = Modifier.widthIn(max = 280.dp)
		)
		Spacer(modifier = Modifier.height(8.dp))
		Button(
			onClick = onBackToHome,
			shape = MaterialTheme.shapes.large,
			contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
		) {
			Text(text = stringResource(R.string.action_back_home))
		}
	}
}

@PreviewLightDark
@Composable
private fun MissingQRDetailsContentPreview() = QRForgeTheme {
	MissingQRDetailsContent(
		onBackToHome = {},
		modifier = Modifier.fillMaxSize()
	)
}