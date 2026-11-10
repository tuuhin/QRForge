package com.sam.qrforge.presentation.navigation.nav_graph

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes : NavKey {

    val analyticsName: String

    @Serializable
    data object HomeRoute : NavRoutes {

        override val analyticsName: String
            get() = "home_route"
    }

    @Serializable
    data object CreateRoute : NavRoutes {

        override val analyticsName: String
            get() = "create_route"
    }

    @Serializable
    data class QRDetailsRoute(val qrId: Long) : NavRoutes {

        override val analyticsName: String
            get() = "qr_details_route"
    }

    @Serializable
    data object ScanRoute : NavRoutes {

        override val analyticsName: String
            get() = "analytics_route"
    }
}
