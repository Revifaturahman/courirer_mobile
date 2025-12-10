package com.example.courier_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.GetDetailArrive
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.repository.GetDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDetailArriveViewModel @Inject constructor(
    private val repository: GetDetailRepository
): ViewModel() {
    private val _resultData = MutableLiveData<GetDetailArrive>()
    val resultData: LiveData<GetDetailArrive> get() = _resultData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchData(detailId: Int) {
        viewModelScope.launch {
            try {
                Log.d("GetDetailArrive", "Fetching detail for id=$detailId")
                val data = repository.getDetailArrive(detailId)
                Log.d("GetDetailArrive", "Success: $data")
                _resultData.value = data
            } catch (e: Exception) {
                Log.e("GetDetailArrive", "Error: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }
}