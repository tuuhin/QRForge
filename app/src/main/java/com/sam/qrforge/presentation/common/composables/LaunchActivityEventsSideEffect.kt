package com.sam.qrforge.presentation.common.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sam.qrforge.data.contracts.PreviewImageUriContract
import com.sam.qrforge.data.contracts.ShareQRContentContracts
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LaunchActivityEventsSideEffect(eventsFlow: () -> Flow<LaunchActivityEvent>) {

	val lifecycle = LocalLifecycleOwner.current

	val launcherToShare = rememberLauncherForActivityResult(
		contract = ShareQRContentContracts(),
		onResult = {},
	)

	val launcherToPreview = rememberLauncherForActivityResult(
		contract = PreviewImageUriContract(),
		onResult = { },
	)

	LaunchedEffect(lifecycle) {
		lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
			eventsFlow().collectLatest { event ->
				when (event) {
					is LaunchActivityEvent.PreviewImageURI -> launcherToPreview.launch(event.uri)
					is LaunchActivityEvent.ShareImageURI -> launcherToShare.launch(event.uri)
				}
			}
		}
	}
}