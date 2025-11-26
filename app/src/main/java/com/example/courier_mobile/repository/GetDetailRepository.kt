package com.example.courier_mobile.repository

import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.network.ApiService
import javax.inject.Inject

class GetDetailRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getDetailDelivery(detail: Int): GetDetailDelivery{
        val response = api.GetDetailDelivery(detail)
        if (response.isSuccessful){
            return  response.body() ?: GetDetailDelivery()
        }
        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }
}