package com.example.courier_mobile.data.model

import com.google.gson.annotations.SerializedName

data class NextProcess(
    @SerializedName("worker_id")
    val worker_id: Int,

    @SerializedName("pcs_finished")
    val pcs_finished: Map<String, Int>,
)


