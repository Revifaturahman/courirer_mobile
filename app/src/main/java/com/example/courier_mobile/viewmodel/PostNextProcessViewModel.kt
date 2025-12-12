package com.example.courier_mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.ApiResponse
import com.example.courier_mobile.data.model.NextProcess
import com.example.courier_mobile.data.model.UpdateResultRequest
import com.example.courier_mobile.repository.GetDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostNextProcessViewModel @Inject constructor(
    private val repository: GetDetailRepository
): ViewModel() {
    private val _updateState = MutableLiveData<Result<ApiResponse>>()
    val updateState: LiveData<Result<ApiResponse>> = _updateState

    fun updateProcess(detailId: Int, request: NextProcess) {
        viewModelScope.launch {
            val result = repository.nextProcess(detailId, request)
            _updateState.value = result
        }
    }
}