package com.example.courier_mobile.view

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import android.content.Context

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.courier_mobile.utils.JsBridge
import com.example.courier_mobile.utils.LocationPreferences
import com.example.courier_mobile.utils.LocationTracker
import com.example.courier_mobile.utils.Route
import com.example.courier_mobile.utils.WebViewHelper
import com.example.courier_mobile.viewmodel.GetDetailDeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.AppDataStore by preferencesDataStore(name = "location_prefs")
@AndroidEntryPoint

class DetailRouteActivity : AppCompatActivity() {

    private lateinit var webHelper: WebViewHelper
    private lateinit var jsBridge: JsBridge


    private lateinit var tracker: LocationTracker
    private lateinit var prefs: LocationPreferences


    private val viewModel: GetDetailDeliveryViewModel by viewModels()

    private var pageLoaded = false
    private val TAG = "DETAIL_ROUTE"
    private val TAG_KORDINAT =  "KORDINAT"

    private var startLat: Double = 0.0
    private var startLng: Double = 0.0

    private var destLat: Double = 0.0
    private var destLng: Double = 0.0

    private var courierLat: Double = 0.0
    private var courierLng: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webHelper = WebViewHelper(this) { onPageLoaded() }

        val web = webHelper.initWebView()

        // ONLY ONCE
        jsBridge = JsBridge(web)
        web.addJavascriptInterface(jsBridge, "Android")

        // Load HTML FIRST
        web.loadUrl("file:///android_asset/turf.html")

        setContentView(web)

        val detailId = intent.getIntExtra("detailId", -1)
        Log.d("detailId", "id: ${detailId}")

        if (detailId != -1){
            viewModel.fetchData(detailId)
        }

        viewModel.resultData.observe(this){detail ->
            Log.d(TAG_KORDINAT, "current_role:${detail.current_role}, name: ${detail.worker_name}, lat: ${detail.latitude}, long: ${detail.longitude}")

            val workerLat = detail.latitude?.toDoubleOrNull()
            val workerLng = detail.longitude?.toDoubleOrNull()

            if (workerLat == null || workerLng == null) {
                Log.e("DETAIL", "Invalid worker location from server")
                return@observe
            }

            Log.d("DETAIL", "Worker = $workerLat , $workerLng")

            destLat = workerLat
            destLng = workerLng
        }

        prefs = LocationPreferences(this)

        tracker = LocationTracker(this) { location ->

//            Log.d("GPS_RAW", "Lat:${location.latitude}, Lng:${location.longitude}")

            // Simpan ke DataStore
            lifecycleScope.launch {
                prefs.saveLocation(location.latitude, location.longitude)
            }
        }

        // Set observer untuk baca data store
        lifecycleScope.launch {
            prefs.lastLocation.collect { (lat, lng) ->
                val safeLat = lat ?: return@collect
                val safeLng = lng ?: return@collect

                // 🔥 FIX PENTING!
                if (startLat == 0.0 && startLng == 0.0) {
                    startLat = safeLat
                    startLng = safeLng
                    Log.d("ROUTE", "Start position initialized: $startLat , $startLng")
                }

                courierLat = safeLat
                courierLng = safeLng

                Log.d("JS_UPDATE", "Sending realtime courier position: $safeLat , $safeLng")

                if (JsBridge.bufferPolygonJson != null) {
                    jsBridge.sendLocationToJs(safeLat, safeLng)
                }
            }
        }



        webHelper.webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WEBVIEW_JS", "JS Console: ${consoleMessage?.message()} (line ${consoleMessage?.lineNumber()})")
                return true
            }
        }
    }

    private fun onPageLoaded() {
        pageLoaded = true
        Log.d(TAG, "Page fully loaded.")

        lifecycleScope.launch {

            Log.d("START_AND_DEST", "startlat: $startLat, startlng: $startLng, destlat: $destLat, destlng: $destLng")
            Log.d("DEBUGKURIR", "Sending to JS: $courierLat , $courierLng")

            // 🔥 KIRIM KOORDINAT KURIR YANG SUDAH VALID
            Log.d("DEBUG_JS_SEND", "Sending to JS -> lat: $courierLng , lng: $courierLat")

            val js = """
                setCourierPosition($courierLng, $courierLat);
                if (window.bufferPolygon) {
                    checkPointInside([$courierLng, $courierLat], window.bufferPolygon);
                }
            """.trimIndent()
            webHelper.webView.evaluateJavascript(js) { result ->
                Log.d("DEBUG_JS_CALLBACK", "JS response: $result")
            }

            // Ambil rute OSRM
            val route = Route.fetchOsrmRouteGeoJson(
                startLng, startLat,
                destLng, destLat
            )

            if (route != null) {
                withContext(Dispatchers.Main) {
                    sendRouteToJs(route)
                }
            }
        }
    }


    private fun sendRouteToJs(lineJson: String) {
        if (!pageLoaded) return

        val js = """
            console.log("Android -> makeBuffer called");
            window.__line = $lineJson;
            makeBuffer(window.__line, 0.1);
        """

        webHelper.webView.evaluateJavascript(js, null)
    }

    override fun onStart() {
        super.onStart()
        tracker.startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        tracker.stopLocationUpdates()
    }
}
