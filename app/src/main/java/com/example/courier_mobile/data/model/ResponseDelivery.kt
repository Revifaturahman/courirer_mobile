package com.example.courier_mobile.data.model
import com.google.gson.annotations.SerializedName

data class ResponseDelivery (
    @field: SerializedName("deliveries")
    val deliveries: List<ResultDelivery> ?= null
)

data class ResultDelivery(
    @field:SerializedName("id")
    val id: Int ?= null,

    @field:SerializedName("status")
    val status: String ?= null,

    @field:SerializedName("delivery_date")
    val delivery_date: String ?= null,

    @field:SerializedName("worker_name")
    val worker_name: String ?= null,

    @field:SerializedName("worker_id")
    val worker_id: Int ?= null,

    @field:SerializedName("worker_role")
    val worker_role: String ?= null,

    @field:SerializedName("product_type")
    val product_type: List<ProductType> ?= null,

    @field:SerializedName("courier_id")
    val courier_id: Int ?= null,

    @field:SerializedName("courier_name")
    val courier_name: String ?= null,

)


data class ProductType(
    @field:SerializedName("id")
    val id: Int ?= null,

    @field:SerializedName("raw_material_id")
    val raw_material_id: Int ?= null,

    @field:SerializedName("product_type")
    val product_type: String ?= null,

    @field:SerializedName("weight")
    val weight: String ?= null,
)