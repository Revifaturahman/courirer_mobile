package com.example.courier_mobile.utils

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

class JsBridge(
    private val web: WebView,
    private val courierLng: Double,
    private val courierLat: Double
) {
    companion object {
        private const val TAG = "JS_BRIDGE"
    }


    @JavascriptInterface
    fun onBufferCreated(polygonJson: String) {
        Log.d(TAG, "onBufferCreated() called")
        Log.d(TAG, "polygonJson length = ${polygonJson.length}")
        Log.d(TAG, "polygonJson = $polygonJson")

        val js = """
            try {
                console.log('[Android] checkPointInside() courier=[$courierLng, $courierLat]');
                console.log('[Android] polygon json length: ${polygonJson.length}');
                checkPointInside([$courierLng, $courierLat], $polygonJson);
            } catch(e) {
                Android.onJsError('checkPointInside error: ' + e.message);
            }
        """

        web.post {
            Log.d(TAG, "Evaluating JS for checkPointInside...")
            web.evaluateJavascript(js, null)
        }
    }

    @JavascriptInterface
    fun onPointCheck(result: String) {
        Log.d(TAG, "onPointCheck(): FINAL RESULT = $result")
        if (result == "outside") {
            FonnteHelper.sendAlert("Kurir A")
        }
    }


    @JavascriptInterface
    fun onJsError(err: String?) {
        Log.e(TAG, "JS ERROR: $err")
    }


}
