package com.example.courier_mobile.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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

data class GetNextRole(
    @field:SerializedName("next_role")
    val next_role: String ?= null,
)

data class GetWorkersResponse(
    @field:SerializedName("workers")
    val workers: List<GetWorkers> ?= null,
)

@Parcelize
data class GetWorkers(
    @field:SerializedName("id")
    val id: Int ?= null,

    @field:SerializedName("name")
    val name: String ?= null,
): Parcelable
