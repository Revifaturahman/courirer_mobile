//package com.example.courier_mobile.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.liveData
//import com.example.courier_mobile.repository.CourierRepository
//import com.example.courier_mobile.utils.Result
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import javax.inject.Inject
//
//@HiltViewModel
//class LoginViewModel @Inject constructor(
//    private val repository: CourierRepository
//) : ViewModel() {
//
//    fun login(username: String, password: String) = liveData(Dispatchers.IO) {
//        emit(Result.loading())
//        Log.d("LoginViewModel", "Mulai login dengan username: $username")
//
//        try {
//            val response = repository.login(username, password)
//            Log.d("LoginViewModel", "Response code: ${response.code()}")
//            Log.d("LoginViewModel", "Response body: ${response.body()}")
//
//            if (response.isSuccessful) {
//                emit(Result.success(response.body()))
//                Log.d("LoginViewModel", "Login berhasil, data: ${response.body()}")
//            } else {
//                val errorMsg = "Login gagal: ${response.message()}"
//                Log.e("LoginViewModel", errorMsg)
//                emit(Result.error(errorMsg))
//            }
//        } catch (e: Exception) {
//            val errorMsg = "Terjadi error: ${e.localizedMessage}"
//            Log.e("LoginViewModel", errorMsg, e)
//            emit(Result.error(errorMsg))
//        }
//    }
//}
