package com.example.courier_mobile.data.network
import com.example.courier_mobile.data.model.ApiResponse
import com.example.courier_mobile.data.model.GetDetailArrive
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.model.GetNextRole
import com.example.courier_mobile.data.model.GetWorkers
import com.example.courier_mobile.data.model.GetWorkersResponse
import com.example.courier_mobile.data.model.NextProcess
import com.example.courier_mobile.data.model.ResponseDelivery
import com.example.courier_mobile.data.model.UpdateResult
import com.example.courier_mobile.data.model.UpdateResultRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getAllDeliveries(
        @Query("status") status: String? = null,
        @Query("current_role") role: String? = null,
        @Query("worker_id") workerId: Int? = null
    ): Response<ResponseDelivery>


    @POST("material-details/{detail}/startDelivery")
    suspend fun startDelivery(
        @Path("detail") detailId: Int
    ): Response<GetDetailDelivery>

    @POST("material-details/{detail}/arrive")
    suspend fun arriveDelivery(
        @Path("detail") detailId: Int
    ): Response<GetDetailArrive>

    @POST("material-details/{detail}/updateResult")
    suspend fun updateResult(
        @Path("detail") detailId: Int,
        @Body body: UpdateResultRequest
    ): Response<ApiResponse>

    @POST("material-details/{detail}/nextProcess")
    suspend fun nextProcess(
        @Path("detail") detailId: Int,
        @Body body: NextProcess
    ): Response<ApiResponse>

    @GET("material-details/detail/{detail}")
    suspend fun getNextRole(
        @Path("detail") detailId: Int,
    ): Response<GetNextRole>

    @GET("material-details/workers/{role}")
    suspend fun getWorkers(
        @Path("role") role: String,
    ): Response<GetWorkersResponse>

}