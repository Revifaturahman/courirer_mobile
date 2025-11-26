package com.example.courier_mobile.utils

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

class JsBridge(
    private val web: WebView
) {
    companion object {
        private const val TAG = "JS_BRIDGE"
        private var isOutside = false

        var bufferPolygonJson: String? = null
    }


    @JavascriptInterface
    fun onBufferCreated(polygonJson: String) {
        Log.d(TAG, "Polygon buffer READY, storing to memory...")

        bufferPolygonJson = polygonJson   // simpan polygon
    }

    fun sendLocationToJs(lat: Double, lng: Double) {
        val js = """
        checkPointInside([$lng, $lat], window.bufferPolygon);
    """
        web.post {
            web.evaluateJavascript(js, null)
        }
    }


    @JavascriptInterface
    fun onPointCheck(result: String) {
        Log.d(TAG, "onPointCheck(): FINAL RESULT = $result")
        Log.d(TAG, "onPointCheck(): FINAL RESULT = $result")

        if (result == "outside") {

            // Jika sebelumnya berada di dalam rute → sekarang keluar rute
            if (!isOutside) {
                FonnteHelper.sendAlert("Kurir A keluar rute!")
                isOutside = true
            }

        } else {

            // Jika sebelumnya berada di luar → sekarang masuk rute lagi
            if (isOutside) {
                FonnteHelper.sendAlert("Kurir A masuk rute kembali!")
                isOutside = false
            }
        }
    }


    @JavascriptInterface
    fun onJsError(err: String?) {
        Log.e(TAG, "JS ERROR: $err")
    }


}
