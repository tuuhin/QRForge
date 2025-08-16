package com.sam.qrforge.presentation.common.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow

abstract class AppViewModel : ViewModel() {

	abstract val uiEvents: SharedFlow<UIEvent>
}