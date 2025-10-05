package com.sam.qrforge.data.provider

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sam.qrforge.data.utils.await
import com.sam.qrforge.data.utils.hasCoarseLocationPermission
import com.sam.qrforge.data.utils.hasPreciseLocationPermission
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.provider.exception.CurrentLocationNotKnownException
import com.sam.qrforge.domain.provider.exception.LocationMissingPermissionException
import com.sam.qrforge.domain.provider.exception.LocationNotKnownException
import com.sam.qrforge.domain.provider.exception.LocationProviderMissingException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val TAG = "LOCATION_PROVIDER"

@SuppressLint("MissingPermission")
class LocationProviderImpl(private val context: Context) : LocationProvider {

	private val locationManager by lazy { context.getSystemService<LocationManager>() }
	private val locationProvider by lazy { LocationServices.getFusedLocationProviderClient(context) }

	@Suppress("DEPRECATION")
	private val isLocationEnabled: Boolean
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) locationManager?.isLocationEnabled == true
		else {
			// TODO: Check if this is working
			Settings.Secure.getInt(
				context.contentResolver,
				Settings.Secure.LOCATION_MODE,
				Settings.Secure.LOCATION_MODE_OFF
			) != Settings.Secure.LOCATION_MODE_OFF
		}

	private val isNetworkProviderEnabled: Boolean
		get() = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

	private val isGpsEnabled: Boolean
		get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true

	override val locationEnabledFlow: Flow<Boolean>
		get() = callbackFlow {

			trySend(isLocationEnabled)

			val receiver = object : BroadcastReceiver() {
				override fun onReceive(context: Context?, intent: Intent?) {
					if (intent?.action != LocationManager.PROVIDERS_CHANGED_ACTION) return
					trySend(isLocationEnabled)
				}
			}

			ContextCompat.registerReceiver(
				context,
				receiver,
				IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
				ContextCompat.RECEIVER_EXPORTED
			)

			Log.d(TAG, "RECEIVER FOR LOCATION PROVIDER ADDED")

			awaitClose {
				Log.d(TAG, "RECEIVER FOR LOCATION PROVIDER REMOVED")
				context.unregisterReceiver(receiver)
			}
		}


	override suspend fun readCurrentLocation(isPreciseLocation: Boolean): Result<BaseLocationModel> {
		if (!context.hasCoarseLocationPermission)
			return Result.failure(LocationMissingPermissionException())
		if (isPreciseLocation && !context.hasPreciseLocationPermission)
			return Result.failure(LocationMissingPermissionException())
		if (!isLocationEnabled || !isNetworkProviderEnabled || !isGpsEnabled)
			return Result.failure(LocationProviderMissingException())

		val tokenSource = CancellationTokenSource()

		return try {
			val granularity = if (isPreciseLocation) Granularity.GRANULARITY_FINE
			else Granularity.GRANULARITY_COARSE

			val priority = if (isPreciseLocation) Priority.PRIORITY_HIGH_ACCURACY
			else Priority.PRIORITY_BALANCED_POWER_ACCURACY

			val currentLocationRequest = CurrentLocationRequest.Builder()
				.setGranularity(granularity)
				.setMaxUpdateAgeMillis(60_000)
				.setDurationMillis(10_000)
				.setPriority(priority)
				.build()

			Log.d(TAG, "REQUESTING CURRENT LOCATION")

			val location = locationProvider
				.getCurrentLocation(currentLocationRequest, tokenSource.token)
				.await()
				?: return Result.failure(CurrentLocationNotKnownException())

			Log.d(TAG, "CURRENT LOCATION FETCHED")

			val currentLocation = BaseLocationModel(
				latitude = location.latitude,
				longitude = location.longitude
			)
			Result.success(currentLocation)
		} catch (e: Exception) {
			if (e is CancellationException) {
				Log.d(TAG, "CANCELLATION EXCEPTION OCCURRED TOKEN CANCELLED")
				// cancel the ongoing location request
				tokenSource.cancel()
				throw e
			}
			e.printStackTrace()
			Result.failure(e)
		}
	}


	override suspend fun readLastLocation(): Result<BaseLocationModel> {
		if (!context.hasCoarseLocationPermission)
			return Result.failure(LocationMissingPermissionException())
		if (!isLocationEnabled || !isNetworkProviderEnabled || !isGpsEnabled)
			return Result.failure(LocationProviderMissingException())
		return try {

			val locationRequest = LastLocationRequest.Builder()
				.setGranularity(Granularity.GRANULARITY_COARSE)
				.setMaxUpdateAgeMillis(10_000)
				.build()

			val location = locationProvider.getLastLocation(locationRequest).await()
				?: return Result.failure(LocationNotKnownException())

			val lastLocation = BaseLocationModel(
				latitude = location.latitude,
				longitude = location.longitude
			)
			Result.success(lastLocation)
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}

}