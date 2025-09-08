package com.sam.qrforge.domain.provider

import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.flow.Flow

fun interface WIFIConnectionProvider {

	fun connectToWifi(
		ssid: String,
		passphrase: String?,
		isHidden: Boolean,
	): Flow<Resource<Boolean, Exception>>
}