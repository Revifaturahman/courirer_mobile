package com.example.courier_mobile.data.model

import com.google.gson.annotations.SerializedName


data class Courier(
    val id: Int,
    val name: String,
    val username: String,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("device_token")
    val deviceToken: String?,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)