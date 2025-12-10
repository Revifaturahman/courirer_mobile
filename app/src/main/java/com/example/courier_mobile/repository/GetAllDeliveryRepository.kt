package com.example.courier_mobile.repository

import com.example.courier_mobile.data.model.ResultDelivery
import com.example.courier_mobile.data.network.ApiService
import javax.inject.Inject

class GetAllDeliveryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllDelivery(
        status: String? = null,
        role: String? = null,
        workerId: Int? = null
    ): List<ResultDelivery> {

        val response = apiService.getAllDeliveries(
            status = status,
            role = role,
            workerId = workerId
        )

        if (response.isSuccessful) {
            return response.body()?.deliveries ?: emptyList()
        }

        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }
}

