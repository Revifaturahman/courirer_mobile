package com.example.courier_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.TrackingRequest
import com.example.courier_mobile.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: TrackingRepository
) : ViewModel() {

    private val _trackingResult = MutableLiveData<Boolean?>()
    val trackingResult: LiveData<Boolean?> get() = _trackingResult

    fun sendTracking(
        courierId: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "TrackingViewModel",
                    "🚀 Mengirim tracking ke repository: courier_id=$courierId, lat=$latitude, lng=$longitude"
                )

                val request = TrackingRequest(courierId, latitude, longitude)
                val success = repository.sendTrackingData(request)

                Log.d("TrackingViewModel", "📡 Hasil dari repository: $success")
                _trackingResult.postValue(success)
            } catch (e: Exception) {
                Log.e("TrackingViewModel", "❌ Exception saat kirim tracking: ${e.message}", e)
                _trackingResult.postValue(false)
            }
        }
    }
}
