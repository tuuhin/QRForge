package com.sam.qrforge.presentation.common.composables

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.UIEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UIEventsSideEffect(events: () -> Flow<UIEvent>, onNavigateBack: () -> Unit = {}) {

	val context = LocalContext.current
	val lifecycle = LocalLifecycleOwner.current
	val snackBarHostState = LocalSnackBarState.current

	val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

	LaunchedEffect(lifecycle) {
		lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
			events().collectLatest { event ->
				when (event) {
					is UIEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
					is UIEvent.ShowSnackBarWithAction -> {
						val result = snackBarHostState.showSnackbar(
							message = event.message,
							actionLabel = event.actionText,
							duration = SnackbarDuration.Short
						)
						when (result) {
							SnackbarResult.Dismissed -> {}
							SnackbarResult.ActionPerformed -> event.action()
						}
					}

					is UIEvent.ShowToast -> Toast.makeText(
						context,
						event.message,
						Toast.LENGTH_SHORT
					).show()

					UIEvent.NavigateBack -> currentOnNavigateBack()
				}
			}
		}
	}
}