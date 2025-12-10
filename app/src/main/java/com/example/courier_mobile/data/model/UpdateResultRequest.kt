package com.example.courier_mobile.data.model

data class UpdateResultRequest(
    val process_date: String,
    val results: List<UpdateResult>
)

data class UpdateResult(
    val productType: String,
    var pcsFinished: Int? = null
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)
