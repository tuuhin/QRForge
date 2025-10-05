package com.sam.qrforge.domain.provider

import com.sam.qrforge.domain.models.BaseLocationModel
import kotlinx.coroutines.flow.Flow

interface LocationProvider {

	val locationEnabledFlow: Flow<Boolean>

	suspend fun readLastLocation(): Result<BaseLocationModel>

	suspend fun readCurrentLocation(isPreciseLocation: Boolean = false): Result<BaseLocationModel>
}