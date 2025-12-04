package com.example.courier_mobile.utils

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject

class JsBridge(
    private val webView: WebView,
    private val courierName: String
) {

    companion object {
        private const val TAG = "JsBridge"
    }

    // buffer polygon (string JSON) disimpan di sini ketika JS memanggil onBufferCreated
    @Volatile
    var bufferPolygonJson: String? = null

    var isOutside = false

    // dipanggil ketika JS memanggil Android.onJsReady(...)
    var isJsReady: (() -> Unit)? = null

    // dipanggil ketika buffer selesai dibuat (Android side listener)
    var onBufferCreatedCallback: (() -> Unit)? = null

    private var jsLoaded = false
    private var pendingBuffer: Pair<String, Double>? = null

    // dipanggil DARI JS: ketika buffer sudah dibuat
    @JavascriptInterface
    fun onBufferCreated(polygonJson: String) {
        Log.d(TAG, "🔥 onBufferCreated() called from JS, length=${polygonJson.length}")
        bufferPolygonJson = polygonJson
        onBufferCreatedCallback?.invoke()
    }

    // NON-@JavascriptInterface: method untuk dipanggil DARI KOTLIN (Activity / Service)
    // ini akan menjalankan checkPointInside([$lng, $lat], <bufferPolygonJson>)
    fun sendLocationToJs(lat: Double, lng: Double) {
        Log.d(TAG, "sendLocationToJs CALLED ($lat, $lng), bufferJson=${bufferPolygonJson != null}")

        val polyJson = bufferPolygonJson ?: return

        val escaped = JSONObject.quote(polyJson)

        val jsCode = """
        try{
            const geo = JSON.parse($escaped);
            checkPointInside([$lng, $lat], geo);
        }catch(e){
            Android.onJsError("sendLocation ERROR: " + e.message);
        }
    """

        webView.post {
            webView.evaluateJavascript(jsCode, null)
        }
    }


    // dipanggil DARI KOTLIN: buat buffer; jika js belum ready => simpan pending
    fun makeBuffer(json: String, km: Double) {
        try {
            if (!jsLoaded) {
                pendingBuffer = Pair(json, km)
                Log.d(TAG, "makeBuffer: js not loaded yet, pending stored")
                return
            }
            Log.d(TAG, "makeBuffer CALLED with json length=${json.length}")
            Log.d(TAG, "jsLoaded = $jsLoaded")


            // escape json safely
            val escaped = JSONObject.quote(json)


            val initJs = """
                try {
                    window.__routeGeoJson = JSON.parse($escaped);
                    console.log("[JS] route loaded, coords =", window.__routeGeoJson.coordinates ? window.__routeGeoJson.coordinates.length : 'unknown');
                } catch(e) {
                    Android.onJsError("JSON PARSE ERROR: " + e.message);
                }
            """.trimIndent()

            val callJs = """
                try {
                    makeBuffer(window.__routeGeoJson, $km);
                } catch(e) {
                    Android.onJsError("makeBuffer ERROR: " + e.message);
                }
            """.trimIndent()

            webView.post {
                webView.evaluateJavascript(initJs, null)
                webView.evaluateJavascript(callJs, null)
            }

        } catch (e: Exception) {
            Log.e(TAG, "makeBuffer ERROR: ${e.message}", e)
        }
    }

    // dipanggil DARI JS => menandakan JS siap; jalankan callback isJsReady dan pendingBuffer
    @JavascriptInterface
    fun onJsReady(msg: String?) {
        Log.d(TAG, "onJsReady() from JS: $msg")
        jsLoaded = true
        isJsReady?.invoke()

        pendingBuffer?.let { (json, km) ->
            Log.d(TAG, "onJsReady -> sending pending buffer")
            makeBuffer(json, km)
            pendingBuffer = null
        }
    }

    // dipanggil DARI JS untuk hasil check point (inside/outside)
    @JavascriptInterface
    fun onPointCheck(result: String) {
        Log.d(TAG, "POINT CHECK RESULT = $result")

        when (result) {

            "outside" -> {
                if (!isOutside) {
                    isOutside = true
                    Log.w(TAG, "⚠️ KURIR KELUAR RUTE")

                    // Kirim WA SEKALI
                    FonnteHelper.sendExitAlert(courierName)
                }
            }

            "inside" -> {
                if (isOutside) {
                    isOutside = false
                    Log.i(TAG, "✔️ KURIR KEMBALI KE RUTE")

                    // Kirim WA SEKALI
                    FonnteHelper.sendBackAlert(courierName)
                }
            }
        }
    }


    @JavascriptInterface
    fun onJsError(err: String?) {
        Log.e(TAG, "JS ERROR: $err")
    }
}
