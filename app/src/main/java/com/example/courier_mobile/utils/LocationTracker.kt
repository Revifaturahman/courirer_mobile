package com.example.courier_mobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

class LocationTracker(
    private val context: Context,
    private val onLocationUpdate: (Location) -> Unit
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000 // 10 detik
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (location in result.locations) {
                    Log.d("LocationHelper", "Update: ${location.latitude}, ${location.longitude}")
                    onLocationUpdate(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
