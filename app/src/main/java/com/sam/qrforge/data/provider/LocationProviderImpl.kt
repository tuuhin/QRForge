package com.sam.qrforge.data.provider

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.getSystemService
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.sam.qrforge.data.utils.hasLocationPermission
import com.sam.qrforge.domain.provider.exception.LocationNotKnownException
import com.sam.qrforge.domain.provider.exception.LocationMissingPermissionException
import com.sam.qrforge.domain.provider.exception.LocationProviderMissingException
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.models.BaseLocationModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProviderImpl(private val context: Context) : LocationProvider {

	private val locationManager by lazy { context.getSystemService<LocationManager>() }

	private val locationProvider by lazy { LocationServices.getFusedLocationProviderClient(context) }

	private val lastLocationRequest: LastLocationRequest
		get() = LastLocationRequest.Builder()
			.setGranularity(Granularity.GRANULARITY_COARSE)
			.setMaxUpdateAgeMillis(10_000)
			.build()

	private val isGpsEnabled: Boolean
		get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true

	@Suppress("DEPRECATION")
	private val isLocationEnabled: Boolean
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) locationManager?.isLocationEnabled == true
		else {
			// TODO: Check if this is working
			val locationMode = Settings.Secure.getInt(
				context.contentResolver,
				Settings.Secure.LOCATION_MODE,
				Settings.Secure.LOCATION_MODE_OFF
			)
			locationMode != Settings.Secure.LOCATION_MODE_OFF
		}

	private val isNetworkProviderEnabled: Boolean
		get() = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true


	@OptIn(ExperimentalCoroutinesApi::class)
	@SuppressLint("MissingPermission")
	private suspend fun getLastKnownLocation(): BaseLocationModel? {
		return suspendCancellableCoroutine { cont ->
			locationProvider.getLastLocation(lastLocationRequest).apply {
				addOnCompleteListener {
					addOnSuccessListener { location ->
						if (location == null) {
							cont.resume(null)
							return@addOnSuccessListener
						}
						val evaluatedLocation = BaseLocationModel(
							latitude = location.latitude,
							longitude = location.longitude
						)
						cont.resume(value = evaluatedLocation)

					}
					addOnFailureListener { exp -> cont.cancel(exp) }
				}
				addOnCanceledListener {
					cont.cancel()
				}
			}
		}
	}

	override suspend fun invoke(): Result<BaseLocationModel> {
		if (!context.hasLocationPermission)
			return Result.failure(LocationMissingPermissionException())
		if (!isLocationEnabled || !isNetworkProviderEnabled || !isGpsEnabled)
			return Result.failure(LocationProviderMissingException())
		return try {
			val lastLocation = getLastKnownLocation()
				?: return Result.failure(LocationNotKnownException())

			Result.success(lastLocation)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

}