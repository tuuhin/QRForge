package com.sam.qrforge.domain.provider

import com.sam.qrforge.domain.models.BaseLocationModel

fun interface LocationProvider {

	suspend fun invoke(): Result<BaseLocationModel>
}