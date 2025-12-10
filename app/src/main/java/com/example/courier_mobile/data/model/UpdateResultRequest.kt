package com.example.courier_mobile.data.model

import com.google.gson.annotations.SerializedName

data class UpdateResultRequest(
    @SerializedName("process_date")
    val processDate: String,

    @SerializedName("results")
    val results: List<UpdateResult>
)

data class UpdateResult(
    @SerializedName("product_type")
    val productType: String,

    @SerializedName("pcs_finished")
    val pcsFinished: Int
)


data class ApiResponse(
    val success: Boolean,
    val message: String
)
