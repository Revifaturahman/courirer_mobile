package com.example.courier_mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courier_mobile.data.model.ApiResponse
import com.example.courier_mobile.data.model.UpdateResultRequest
import com.example.courier_mobile.repository.GetDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateResultViewModel @Inject constructor(
    private val repository: GetDetailRepository
) : ViewModel() {

    private val _updateState = MutableLiveData<Result<ApiResponse>>()
    val updateState: LiveData<Result<ApiResponse>> = _updateState

    fun updateResult(detailId: Int, request: UpdateResultRequest) {
        viewModelScope.launch {
            val result = repository.updateResult(detailId, request)
            _updateState.value = result
        }
    }
}