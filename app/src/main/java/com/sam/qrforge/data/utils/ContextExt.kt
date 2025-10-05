package com.sam.qrforge.data.utils

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

val Context.hasCoarseLocationPermission: Boolean
	get() = ContextCompat.checkSelfPermission(
		this,
		Manifest.permission.ACCESS_COARSE_LOCATION
	) == PermissionChecker.PERMISSION_GRANTED

val Context.hasPreciseLocationPermission: Boolean
	get() = ContextCompat.checkSelfPermission(
		this,
		Manifest.permission.ACCESS_FINE_LOCATION
	) == PermissionChecker.PERMISSION_GRANTED

val Context.hasReadContactsPermission: Boolean
	get() = ContextCompat.checkSelfPermission(
		this,
		Manifest.permission.READ_CONTACTS
	) == PermissionChecker.PERMISSION_GRANTED

val Context.hasWriteStoragePermission: Boolean
	get() = ContextCompat.checkSelfPermission(
		this,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
	) == PermissionChecker.PERMISSION_GRANTED

val Context.hasCameraPermission: Boolean
	get() = ContextCompat.checkSelfPermission(
		this,
		Manifest.permission.CAMERA
	) == PermissionChecker.PERMISSION_GRANTED
