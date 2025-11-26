package com.example.courier_mobile.repository

import android.util.Log
import com.example.courier_mobile.data.model.TrackingRequest
import com.example.courier_mobile.data.network.ApiService
import javax.inject.Inject

class TrackingRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun sendTrackingData(request: TrackingRequest): Boolean {
        return try {
            val response = api.sendTrackingData(request)
            if (response.isSuccessful) {
                Log.d("TrackingRepository", "Tracking data sent successfully")
                true
            } else {
                Log.e("TrackingRepository", "Failed: ${response.code()} ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("TrackingRepository", "Error: ${e.message}", e)
            false
        }
    }
}
