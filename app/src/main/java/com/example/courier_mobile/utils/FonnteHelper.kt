package com.example.courier_mobile.utils

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object FonnteHelper {

    private val client = OkHttpClient()
    private const val API_TOKEN = "1LSTTVeLyTBKuNQqGZJq"

    // --- 1. KURIR KELUAR RUTE ---
    fun sendExitAlert(courierName: String) {
        sendFonnteMessage(
            "⚠️ Kurir $courierName keluar dari rute!"
        )
    }

    // --- 2. KURIR KEMBALI MASUK RUTE ---
    fun sendBackAlert(courierName: String) {
        sendFonnteMessage(
            "✅ Kurir $courierName kembali ke dalam rute."
        )
    }

    // --- COMMON FUNCTION ---
    private fun sendFonnteMessage(msg: String) {

        val json = """
            {
              "target": "6288210951173",
              "message": "$msg"
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.fonnte.com/send")
            .addHeader("Authorization", API_TOKEN)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("FONNTE", "Gagal kirim WA: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                Log.d("FONNTE", "Response: $res")
            }
        })
    }
}

