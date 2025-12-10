package com.example.courier_mobile.data.model

import com.google.gson.annotations.SerializedName
data class GetDetailDelivery(
    @field:SerializedName("detail_id")
    val detail_id: Int ?= null,

    @field:SerializedName("current_role")
    val current_role: String ?= null,

    @field:SerializedName("worker_id")
    val worker_id: Int ?= null,

    @field:SerializedName("worker_name")
    val worker_name: String ?= null,

    @field:SerializedName("latitude")
    val latitude: String ?= null,

    @field:SerializedName("longitude")
    val longitude: String ?= null,
)

data class GetDetailArrive(
    @field:SerializedName("detail_id")
    val detail_id: Int ?= null,

    @field:SerializedName("status")
    val status: String ?= null,

    @field:SerializedName("current_role")
    val current_role: String ?= null,
)
