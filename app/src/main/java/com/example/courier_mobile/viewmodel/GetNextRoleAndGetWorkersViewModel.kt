package com.example.courier_mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.GetDetailArrive
import com.example.courier_mobile.data.model.GetNextRole
import com.example.courier_mobile.data.model.GetWorkersResponse
import com.example.courier_mobile.repository.GetDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetNextRoleAndGetWorkersViewModel @Inject constructor(
    private val repository: GetDetailRepository
): ViewModel() {
    private val _resultDataNextRole = MutableLiveData<GetNextRole>()
    val resultDataNextRole: LiveData<GetNextRole> get() = _resultDataNextRole

    private val _resultDataWorkers = MutableLiveData<GetWorkersResponse>()
    val resultDataWorkers: LiveData<GetWorkersResponse> get() = _resultDataWorkers

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchData(detailId: Int) {
        viewModelScope.launch {
            try {
                Log.d("GetNextRole", "Fetching detail for id=$detailId")
                val data = repository.getNextRole(detailId)
                Log.d("GetNextRole", "Success: $data")
                _resultDataNextRole.value = data
            } catch (e: Exception) {
                Log.e("GetNextRole", "Error: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }

    fun fetchDataWorkers(role: String) {
        viewModelScope.launch {
            try {
                Log.d("GetWorkers", "Fetching detail for id=$role")
                val data = repository.getWorkers(role)
                Log.d("GetWorkers", "Success: $data")
                _resultDataWorkers.value = data
            } catch (e: Exception) {
                Log.e("GetWorkers", "Error: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }
}