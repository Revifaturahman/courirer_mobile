package com.example.courier_mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.repository.GetDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDetailDeliveryViewModel @Inject constructor(
    private val repository: GetDetailRepository
): ViewModel() {
    private val _resultData = MutableLiveData<GetDetailDelivery>()
    val resultData: LiveData<GetDetailDelivery> get() = _resultData

    fun fetchData(detail: Int){
        viewModelScope.launch {
            val data = repository.getDetailDelivery(detail)
            _resultData.value = data
        }
    }
}