package com.sam.qrforge.presentation.feature_create.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sam.qrforge.data.contracts.ShareQRContentContracts
import kotlinx.coroutines.flow.Flow

@Composable
fun ShareQREventsSideEffect(eventFlow: () -> Flow<String>) {

	val lifecyleOwner = LocalLifecycleOwner.current

	val launcher = rememberLauncherForActivityResult(
		contract = ShareQRContentContracts(),
		onResult = {},
	)

	LaunchedEffect(lifecyleOwner) {
		lifecyleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			// TODO: Check later if only a single time the intent is launched
			eventFlow().collect { uri ->
				launcher.launch(uri)
			}
		}
	}
}