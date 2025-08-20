package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewQRCodeTopAppBar(
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onSaveQR: () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null
) {

	var showMenu by remember { mutableStateOf(false) }

	MediumTopAppBar(
		title = { Text(text = "Preview QR Code") },
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		actions = {
			TextButton(onClick = onSaveQR) {
				Text("Save")
			}
			Box {
				IconButton(onClick = { showMenu = true }) {
					Icon(
						imageVector = Icons.Default.MoreVert,
						contentDescription = "More options"
					)
					DropdownMenu(
						expanded = showMenu,
						shape = MaterialTheme.shapes.medium,
						onDismissRequest = { showMenu = false },
					) {

					}
				}
			}
		},
		modifier = modifier,
	)
}