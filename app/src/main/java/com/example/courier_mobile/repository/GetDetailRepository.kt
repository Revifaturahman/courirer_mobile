package com.example.courier_mobile.repository

import android.util.Log
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.network.ApiService
import javax.inject.Inject

class GetDetailRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getDetailDelivery(detailId: Int): GetDetailDelivery {
        Log.d("GetDetailRepo", "Request startDelivery detailId=$detailId")

        val response = api.startDelivery(detailId)

        // Log response code & message
        Log.d("GetDetailRepo", "Response Code: ${response.code()}")
        Log.d("GetDetailRepo", "Response Message: ${response.message()}")

        // Log raw JSON response
        Log.d("GetDetailRepo", "Raw Body: ${response.errorBody()?.string() ?: response.body()}")

        if (response.isSuccessful) {
            return response.body() ?: GetDetailDelivery().also {
                Log.e("GetDetailRepo", "Body null, returning empty model")
            }
        }

        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }
}
