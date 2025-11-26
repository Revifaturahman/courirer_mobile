package com.example.courier_mobile.data.network

import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.model.ResponseDelivery
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val username: String,
    val password: String
)

interface ApiService {
//    @POST("courier/login")
//    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

//    @GET("delivery")
//    suspend fun getAllDeliveries(@Header("Authorization") token: String
//    ): Response<RouteResponse>

    @GET("delivery")
    suspend fun getAllDeliveries(): Response<ResponseDelivery>


    @POST("material-details/{detail}/startDelivery")
    suspend fun startDelivery(
        @Path("detail") detailId: Int
    ): Response<GetDetailDelivery>

}