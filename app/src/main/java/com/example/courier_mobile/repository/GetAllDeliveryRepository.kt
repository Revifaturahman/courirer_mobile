package com.example.courier_mobile.repository

import com.example.courier_mobile.data.model.ResultDelivery
import com.example.courier_mobile.data.network.ApiService
import javax.inject.Inject

class GetAllDeliveryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllDelivery(): List<ResultDelivery> {
        val response = apiService.getAllDeliveries()

        if (response.isSuccessful) {
            val body = response.body()

            return body?.deliveries ?: emptyList()
        }

        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }
}
