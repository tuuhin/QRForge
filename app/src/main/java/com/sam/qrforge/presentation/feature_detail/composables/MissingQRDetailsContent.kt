package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

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
		Spacer(modifier = Modifier.height(12.dp))
		Text(
			text = "QR Data Absent",
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.secondary
		)
		Text(
			text = "QR data maybe deleted or the data is not being saved in the database",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center,
		)
		Spacer(modifier = Modifier.height(8.dp))
		Button(
			onClick = onBackToHome,
			shape = MaterialTheme.shapes.large,
			contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
		) {
			Text("Back To Home")
		}
	}
}
