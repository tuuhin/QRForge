package com.sam.qrforge.presentation.feature_create

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CreateNewQRNavGraph : NavKey {

    val analyticsScreenName: String

    @Serializable
    data object CreateNewRoute : CreateNewQRNavGraph {

        override val analyticsScreenName: String
            get() = "generate_qr_screen"
    }

    @Serializable
    data object PreviewNewRoute : CreateNewQRNavGraph {

        override val analyticsScreenName: String
            get() = "preview_generated_qr_screen"
    }

    @Serializable
    data object SaveRoute : CreateNewQRNavGraph {

        override val analyticsScreenName: String
            get() = "save_generated_qr_screen"
    }

    @Serializable
    data object ExportRoute : CreateNewQRNavGraph {

        override val analyticsScreenName: String
            get() = "export_qr_screen_from_create"
    }
}
