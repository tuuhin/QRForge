package com.sam.qrforge.presentation.feature_detail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface QRDetailsNavGraph : NavKey {

    val analyticsScreenName: String

    @Serializable
    data object DetailsRoute : QRDetailsNavGraph {

        override val analyticsScreenName: String
            get() = "qr_details_screen"
    }

    @Serializable
    data object EditDetailsRoute : QRDetailsNavGraph {

        override val analyticsScreenName: String
            get() = "edit_qr_metadata_screen"
    }

    @Serializable
    data object ExportRoute : QRDetailsNavGraph {

        override val analyticsScreenName: String
            get() = "export_qr_route_from_details"
    }
}
