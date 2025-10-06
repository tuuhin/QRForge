package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreenTopAppBar(
	onBeginVerify: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = false,
	navigation: @Composable () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null
) {
	TopAppBar(
		title = { Text(text = stringResource(R.string.qr_editor_title)) },
		actions = {
			OutlinedButton(
				onClick = onBeginVerify,
				shape = MaterialTheme.shapes.extraLarge,
				colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
				enabled = enabled,
			) {
				Text(
					text = stringResource(R.string.action_verify),
					style = MaterialTheme.typography.titleSmall
				)
			}
			Spacer(modifier = Modifier.width(2.dp))
		},
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		modifier = modifier,
	)
}
