package com.sam.qrforge.presentation.feature_scan

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ScanQRNavGraph : NavKey {

    val analyticsScreenName: String

    @Serializable
    data object ScanQRRoute : ScanQRNavGraph {

        override val analyticsScreenName: String
            get() = "scan_qr_screen"
    }

    @Serializable
    data object ScanResultsRoute : ScanQRNavGraph {

        override val analyticsScreenName: String
            get() = "scan_results_screen"
    }

    @Serializable
    data object SaveResultDialog : ScanQRNavGraph {
        override val analyticsScreenName: String
            get() = "save_scan_results_dialog"
    }
}
