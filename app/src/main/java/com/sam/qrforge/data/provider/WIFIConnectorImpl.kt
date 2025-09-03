@file:Suppress("DEPRECATION")

package com.sam.qrforge.data.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.sam.qrforge.domain.provider.WIFIConnector
import com.sam.qrforge.domain.provider.exception.NetworkUnAvailableException
import com.sam.qrforge.domain.provider.exception.WIFINotEnabledException
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

private const val TAG = "WIFI_CONNECTIVITY_PROVIDER"

class WIFIConnectorImpl(private val context: Context) : WIFIConnector {

	private val _connectivityManager by lazy { context.getSystemService<ConnectivityManager>() }
	private val _wifiManager by lazy { context.getSystemService<WifiManager>() }

	private val _isWifiEnabled: Boolean
		get() = _wifiManager?.isWifiEnabled ?: false

	override fun connectToWifi(ssid: String, passphrase: String?, isHidden: Boolean)
			: Flow<Resource<Boolean, Exception>> =
		if (Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.Q)
			connect(ssid, passphrase, isHidden)
		else connectBelowAPI30(ssid, passphrase)


	@RequiresApi(Build.VERSION_CODES.Q)
	fun connect(ssid: String, password: String?, isHidden: Boolean = false)
			: Flow<Resource<Boolean, Exception>> {

		val specifier = WifiNetworkSpecifier.Builder()
			.setSsid(ssid)
			.setIsHiddenSsid(isHidden)

		// set password
		password?.let(specifier::setWpa2Passphrase)

		val request = NetworkRequest.Builder()
			.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
			.setNetworkSpecifier(specifier.build())
			.build()

		return callbackFlow {
			trySend(Resource.Loading)

			if (!_isWifiEnabled) {
				trySend(Resource.Error(WIFINotEnabledException()))
				return@callbackFlow awaitClose { }
			}

			val callback = object : ConnectivityManager.NetworkCallback() {

				override fun onAvailable(network: Network) {
					super.onAvailable(network)
					Log.d(TAG, "NETWORK READY")
					// bind to the network
					val result = _connectivityManager?.bindProcessToNetwork(network) ?: false
					trySend(Resource.Success(result))
				}

				override fun onUnavailable() {
					super.onUnavailable()
					Log.d(TAG, "NETWORK UNAVAILABLE")
					trySend(Resource.Error(NetworkUnAvailableException()))
					close()
				}
			}

			Log.d(TAG, "NETWORK CALLBACK ADDED")
			_connectivityManager?.requestNetwork(request, callback, 10_000)

			awaitClose {
				Log.d(TAG, "CLOSING NETWORK CALLBACK")
				_connectivityManager?.unregisterNetworkCallback(callback)
			}
		}
	}

	fun connectBelowAPI30(ssid: String, password: String?): Flow<Resource<Boolean, Exception>> {
		return flow {

			if (!_isWifiEnabled) {
				emit(Resource.Error(WIFINotEnabledException()))
				return@flow
			}

			val config = WifiConfiguration().apply {
				SSID = ssid
				password?.let { preSharedKey = password }
			}
			emit(Resource.Loading)
			try {
				val networkId = _wifiManager?.addNetwork(config) ?: -1
				_wifiManager?.disconnect()
				_wifiManager?.enableNetwork(networkId, true)
				val result = _wifiManager?.reconnect() ?: false
				emit(Resource.Success(result))
			} catch (e: Exception) {
				emit(Resource.Error(e))
			}
		}
	}
}