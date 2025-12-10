package com.example.courier_mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.ResultDelivery
import com.example.courier_mobile.repository.GetAllDeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAllDeliveryViewModel @Inject constructor(
    private val repository: GetAllDeliveryRepository
): ViewModel() {

    private val _resultData = MutableLiveData<List<ResultDelivery>>()
    val resultData: LiveData<List<ResultDelivery>> get() = _resultData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchData(status: String? = null, role: String ?= null, workerId: Int ?= null) {
        viewModelScope.launch {
            try {
                val data = repository.getAllDelivery(
                    status = status,
                    role = role,
                    workerId = workerId
                )

                _resultData.value = data
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
