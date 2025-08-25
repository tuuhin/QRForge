package com.sam.qrforge.presentation.feature_export

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExportQRScreen(modifier: Modifier = Modifier) {
	Scaffold { scPadding->
		LazyColumn(contentPadding = scPadding) {  }
	}
}