package com.sam.qrforge.presentation.navigation.nav_graph

import android.content.Intent
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import kotlinx.serialization.serializer

object NavDeepLinks {
    private const val APP_LINK = "app://com.sam.qrforge"

    const val CREATE_NEW_QR_DEEP_LINK = "$APP_LINK/create_new"
    const val SCAN_QR_DEEP_LINK = "$APP_LINK/scan_new"

    private val createMatcher = UriDeepLinkMatcher(
        uriPattern = DeepLinkUri(CREATE_NEW_QR_DEEP_LINK),
        serializer = serializer<NavRoutes.CreateRoute>(),
    )

    private val scanMatcher = UriDeepLinkMatcher(
        uriPattern = DeepLinkUri(SCAN_QR_DEEP_LINK),
        serializer = serializer<NavRoutes.ScanRoute>(),
    )

    fun resolveBackStack(intent: Intent?): NavBackStack<NavKey> {
        if (intent == null) return NavBackStack(NavRoutes.HomeRoute)

        val request = DeepLinkRequest(intent)
        val target: NavKey? = createMatcher.match(request)?.key ?: scanMatcher.match(request)?.key

        return if (target == null) NavBackStack(NavRoutes.HomeRoute)
        else NavBackStack(NavRoutes.HomeRoute, target)
    }
}
