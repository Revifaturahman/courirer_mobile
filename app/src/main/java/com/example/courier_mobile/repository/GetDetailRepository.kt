package com.example.courier_mobile.repository

import android.util.Log
import com.example.courier_mobile.data.model.ApiResponse
import com.example.courier_mobile.data.model.GetDetailArrive
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.model.GetNextRole
import com.example.courier_mobile.data.model.GetWorkers
import com.example.courier_mobile.data.model.GetWorkersResponse
import com.example.courier_mobile.data.model.NextProcess
import com.example.courier_mobile.data.model.UpdateResult
import com.example.courier_mobile.data.model.UpdateResultRequest
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

    suspend fun getDetailArrive(detailId: Int): GetDetailArrive{
        Log.d("GetDetailRepo", "Request arriveDelivery detailId=$detailId")

        val response = api.arriveDelivery(detailId)

        // Log response code & message
        Log.d("GetDetailRepo", "Response Code: ${response.code()}")
        Log.d("GetDetailRepo", "Response Message: ${response.message()}")

        // Log raw JSON response
        Log.d("GetDetailRepo", "Raw Body: ${response.errorBody()?.string() ?: response.body()}")

        if (response.isSuccessful) {
            return response.body() ?: GetDetailArrive().also {
                Log.e("GetDetailRepo", "Body null, returning empty model")
            }
        }

        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }

    suspend fun updateResult(
        detailId: Int,
        body: UpdateResultRequest
    ): Result<ApiResponse> {
        return try {
            Log.d("UpdateResultRepo", "Sending updateResult... detailId=$detailId")
            Log.d("UpdateResultRepo", "Request Body: $body")

            val response = api.updateResult(detailId, body)

            // Log HTTP status
            Log.d("UpdateResultRepo", "Response Code: ${response.code()}")
            Log.d("UpdateResultRepo", "Response Message: ${response.message()}")

            // Ambil raw JSON
            val rawError = response.errorBody()?.string()
            if (rawError != null) {
                Log.e("UpdateResultRepo", "Error Body: $rawError")
            }

            val rawBody = response.body()
            Log.d("UpdateResultRepo", "Success Body: $rawBody")

            if (response.isSuccessful && rawBody != null) {
                Result.success(rawBody)
            } else {
                Result.failure(Exception("Update failed: HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Log.e("UpdateResultRepo", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun nextProcess(
        detailId: Int,
        body: NextProcess
    ): Result<ApiResponse> {
        return try {
            Log.d("NextProcessRepo", "Sending updateResult... detailId=$detailId")
            Log.d("NextProcessRepo", "Request Body: $body")

            val response = api.nextProcess(detailId, body)

            // Log HTTP status
            Log.d("NextProcessRepo", "Response Code: ${response.code()}")
            Log.d("NextProcessRepo", "Response Message: ${response.message()}")

            // Ambil raw JSON
            val rawError = response.errorBody()?.string()
            if (rawError != null) {
                Log.e("NextProcessRepo", "Error Body: $rawError")
            }

            val rawBody = response.body()
            Log.d("NextProcessRepo", "Success Body: $rawBody")

            if (response.isSuccessful && rawBody != null) {
                Result.success(rawBody)
            } else {
                Result.failure(Exception("Update failed: HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Log.e("NextProcessRepo", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getNextRole(detailId: Int): GetNextRole{
        Log.d("GetNextRole", "Request nextRole detailId=$detailId")

        val response = api.getNextRole(detailId)

        // Log response code & message
        Log.d("GetNextRole", "Response Code: ${response.code()}")
        Log.d("GetNextRole", "Response Message: ${response.message()}")

        // Log raw JSON response
        Log.d("GetNextRole", "Raw Body: ${response.errorBody()?.string() ?: response.body()}")

        if (response.isSuccessful) {
            return response.body() ?: GetNextRole().also {
                Log.e("GetNextRole", "Body null, returning empty model")
            }
        }

        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }

    suspend fun getWorkers(role: String): GetWorkersResponse{
        Log.d("GetNextRole", "Request nextRole detailId=$role")

        val response = api.getWorkers(role)

        // Log response code & message
        Log.d("GetWorkers", "Response Code: ${response.code()}")
        Log.d("GetWorkers", "Response Message: ${response.message()}")

        // Log raw JSON response
        Log.d("GetWorkers", "Raw Body: ${response.errorBody()?.string() ?: response.body()}")

        if (response.isSuccessful) {
            return response.body() ?: GetWorkersResponse().also {
                Log.e("GetWorkers", "Body null, returning empty model")
            }
        }
        throw Exception("Failed: ${response.code()} - ${response.message()}")
    }
}
