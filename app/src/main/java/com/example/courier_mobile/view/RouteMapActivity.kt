package com.example.courier_mobile.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.courier_mobile.databinding.ActivityRouteMapBinding
import com.example.courier_mobile.services.GeofencingService
import com.example.courier_mobile.utils.JsBridge
import com.example.courier_mobile.utils.WebViewHelper
import com.example.courier_mobile.utils.LocationTracker
import com.example.courier_mobile.utils.MapHelper
import com.example.courier_mobile.utils.Route
import com.example.courier_mobile.viewmodel.PostDetailArriveViewModel
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@AndroidEntryPoint
class RouteMapActivity : AppCompatActivity() {

    private val TAG = "RouteMapActivity"
    private val DEBUG = "ROUTE_DEBUG"

    private lateinit var binding: ActivityRouteMapBinding
    private lateinit var mapView: MapView
    private var geofenceReceiver: BroadcastReceiver? = null

    private lateinit var locationTracker: LocationTracker

    // -------------------------------
    // WEBVIEW + JS BRIDGE
    // -------------------------------
    private lateinit var webHelper: WebViewHelper
    private var jsBridge: JsBridge? = null
    private var isJsReady = false
    // -------------------------------

    private val routeLatLngs = mutableListOf<LatLng>()
    private val mapHelper = MapHelper()

    private val viewModel: PostDetailArriveViewModel by viewModels()

    private var destLat = 0.0
    private var destLng = 0.0
    private var workerName = ""
    private var detailId = -1
    private var role: String = ""
    private var status: String = ""
    private var workerId: Int = 0

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.w(DEBUG, "========== RouteMapActivity CREATED ==========")

        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        binding = ActivityRouteMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. REGISTER RECEIVER LEBIH DULU — TAPI PAKAI TRY/CATCH
        try {
            geofenceReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    try {
                        val lat = intent?.getDoubleExtra("lat", Double.NaN) ?: Double.NaN
                        val lng = intent?.getDoubleExtra("lng", Double.NaN) ?: Double.NaN

                        Log.d("WEB_DEBUG", "Received location from service → $lat, $lng")

                        if (lat.isNaN() || lng.isNaN()) return

                        // Pastikan WebView & JS masih aktif
                        runOnUiThread {
                            try {
                                jsBridge?.sendLocationToJs(lat, lng)
                            } catch (e: Exception) {
                                Log.e("WEB_DEBUG", "sendLocationToJs ERROR → ${e.message}", e)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("WEB_DEBUG", "Receiver internal error → ${e.message}", e)
                    }
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(
                        geofenceReceiver,
                        IntentFilter("GEOFENCE_LOCATION"),
                        Context.RECEIVER_EXPORTED
                    )
                } else {
                    registerReceiver(
                        geofenceReceiver,
                        IntentFilter("GEOFENCE_LOCATION")
                    )
                }
            } catch (e: Exception) {
                Log.e("WEB_DEBUG", "registerReceiver ERROR → ${e.message}", e)
            }

        } catch (e: Exception) {
            Log.e("WEB_DEBUG", "registerReceiver ERROR → ${e.message}", e)
        }

        // 2. SETUP MAPVIEW (setelah receiver aman)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        // 3. GET INTENT DATA
        destLat = intent.getDoubleExtra("destLat", 0.0)
        destLng = intent.getDoubleExtra("destLng", 0.0)
        workerName = intent.getStringExtra("workerName") ?: "-"
        detailId = intent.getIntExtra("detailId", -1)
        role = intent.getStringExtra("role").toString()
        status = intent.getStringExtra("status").toString()
        workerId = intent.getIntExtra("workerId", 0)

        Log.i(DEBUG, "Intent received → ($destLat, $destLng) worker=$workerName detailId=$detailId role=$role workerId=$workerId")

        // 4. SETUP TOOLBAR & LOCATION
        observeArrive()
        setupToolbar()
        initLocationTracker()
        getInitialCourierLocation()
    }

    private fun observeArrive() {
        val detailId = intent.getIntExtra("detailId", -1)
        Log.d("DetailRouteDebug", "Received detailId = $detailId")
        viewModel.fetchData(detailId)

        viewModel.resultData.observe(this) { detail ->

            // Set tombol setelah data siap
            binding.btnArrive.setOnClickListener {
                Log.d("RouteMap123", "Opening Detail...")

                val intent = Intent(this, UpdateResultActivity::class.java)
                intent.putExtra("detailId", detailId)
                intent.putExtra("workerId", workerId)
                intent.putExtra("role", role)
                intent.putExtra("status", status)

                startActivity(intent)
            }
        }
    }


    // ============================================================
    //  WEBVIEW SETUP
    // ============================================================
    private fun initWebView() {
        Log.d("WEB_DEBUG", "initWebView() CALLED")

        webHelper = WebViewHelper(this)

        // 1. Buat WebView + Bridge dulu
        val wv = webHelper.initWebView()

        // 2. Ambil JsBridge
        jsBridge = webHelper.bridge

        // 3. PASANG callback setelah bridge sudah ada
        jsBridge?.isJsReady = {
            Log.d("WEB_DEBUG", "JS FULLY READY, sending buffer...")
            isJsReady = true
            sendBufferToJs()
        }

        // 4. Tambahkan WebView ke layout
        binding.geofenceHiddenWebView.addView(wv)
    }





    private fun sendBufferToJs() {
        if (!isJsReady || jsBridge == null) {
            Log.e("WEB_DEBUG", "JS not ready, cannot send buffer")
            return
        }

        val geojson = GeofencingService.routeGeoJson
        val radius = GeofencingService.bufferRadiusKm
        Log.w("WEB_DEBUG", "geojson length = ${geojson.length}")
        Log.w("WEB_DEBUG", "radius = $radius")

        Log.d("WEB_DEBUG", "Sending buffer to JS...")
        Log.d("TEST1", "Calling makeBuffer() NOW")

        jsBridge?.makeBuffer(geojson, radius)

//        jsBridge?.onBufferCreatedCallback = {
//            Log.w("TESTING", "BUFFER READY → memulai test...")
//
//            binding.root.postDelayed({
//                Log.w("TESTING", "Test #1 → INSIDE")
//                jsBridge?.sendLocationToJs(-6.9661443849233295, 107.6058931604393)
//            }, 1500)
//
//            // Test #2 — Outside
//            binding.root.postDelayed({
//                Log.w("TESTING", "Test #2 → OUTSIDE")
//                jsBridge?.sendLocationToJs(-6.9539000, 107.5829000)
//            }, 3500)
//        }
    }
    // ============================================================

    private fun setupToolbar() {
        Log.d(DEBUG, "setupToolbar()")

        setSupportActionBar(binding.toolbarMap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarMap.setNavigationOnClickListener {
            Log.e(DEBUG, "Back pressed → closing Activity")
            finish()
        }
    }

    private fun initLocationTracker() {
        Log.d(DEBUG, "initLocationTracker() INITIALIZED")

        locationTracker = LocationTracker(this) { location ->
            Log.d(DEBUG, "Courier moved → ${location.latitude}, ${location.longitude}")

            val latLng = LatLng(location.latitude, location.longitude)
            mapHelper.updateCourierMarker(mapView, latLng)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private fun getInitialCourierLocation() {

        Log.w(DEBUG, "getInitialCourierLocation() CALLED")

        if (!hasLocationPermission()) {
            Log.e(DEBUG, "Permission missing → request again")
            requestLocationPermission()
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(this)

        fused.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Log.e(DEBUG, "lastLocation = NULL (maybe GPS off?)")
                return@addOnSuccessListener
            }

            Log.d(DEBUG, "Initial origin → ${location.latitude}, ${location.longitude}")
            Log.d(DEBUG, "Destination → $destLat, $destLng")

            val origin = LatLng(location.latitude, location.longitude)
            val destination = LatLng(destLat, destLng)

            fetchOsrmRoute(origin, destination)

        }.addOnFailureListener {
            Log.e(DEBUG, "FusedLocation FAILED → ${it.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchOsrmRoute(origin: LatLng, dest: LatLng) {

        Log.w(DEBUG, "fetchOsrmRoute() → Requesting OSRM route...")

        lifecycleScope.launch {

            val geoJson = Route.fetchOsrmRouteGeoJson(
                origin.longitude, origin.latitude,
                dest.longitude, dest.latitude
            )

            if (geoJson == null) {
                Log.e(DEBUG, "OSRM returned NULL route")
                return@launch
            }

            Log.i(DEBUG, "OSRM route RECEIVED → ${geoJson.length} chars")

            val points = Route.parseGeoJsonToLatLng(geoJson)

            Log.i(DEBUG, "Parsed ${points.size} polyline points")

            routeLatLngs.clear()
            routeLatLngs.addAll(points)

            Log.d(DEBUG, "Rendering polyline on map...")
            mapHelper.setupMap(mapView, routeLatLngs)

            // --------------- INIT WEBVIEW (IMPORTANT) ---------------
            Log.w(DEBUG, "Initializing WebView for Turf.js...")
            initWebView()
            // --------------------------------------------------------

            // -------- START GEOFENCE SERVICE ---------
            Log.w(DEBUG, "Starting GeofencingService...")

            GeofencingService.routeGeoJson = geoJson
            GeofencingService.bufferRadiusKm = 0.1

            val intent = Intent(this@RouteMapActivity, GeofencingService::class.java)
            intent.putExtra("polyline", geoJson)

            try {
                startForegroundService(intent)
                Log.i(DEBUG, "GeofencingService STARTED successfully")
            } catch (e: Exception) {
                Log.e(DEBUG, "FAILED to start service → ${e.message}")
            }

            // Start tracking user position
            Log.d(DEBUG, "Starting real-time location tracker...")
            locationTracker.startLocationUpdates()
        }
    }

    private fun hasLocationPermission(): Boolean {
        val ok = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Log.d(DEBUG, "hasLocationPermission() = $ok")
        return ok
    }

    private fun requestLocationPermission() {
        Log.w(DEBUG, "Requesting permission → ACCESS_FINE_LOCATION")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            101
        )
    }

    // LIFECYCLE DEBUG
    override fun onStart() { super.onStart(); Log.d(DEBUG, "onStart()"); mapView.onStart() }
    override fun onResume() { super.onResume(); Log.d(DEBUG, "onResume()"); mapView.onResume() }
    override fun onPause() { super.onPause(); Log.d(DEBUG, "onPause()"); mapView.onPause() }
    override fun onStop() { super.onStop(); Log.d(DEBUG, "onStop()"); mapView.onStop() }

    override fun onDestroy() {
        Log.e(DEBUG, "onDestroy() → stop tracker + map destroy")

        // Stop LocationTracker
        try {
            locationTracker.stopLocationUpdates()
        } catch (e: Exception) {
            Log.e(DEBUG, "stopLocationUpdates ERROR → ${e.message}")
        }

        // Destroy map
        try {
            mapView.onDestroy()
        } catch (e: Exception) {
            Log.e(DEBUG, "mapView.onDestroy ERROR → ${e.message}")
        }

        // UNREGISTER RECEIVER DENGAN AMAN
        try {
            if (geofenceReceiver != null) {
                unregisterReceiver(geofenceReceiver)
                Log.d("WEB_DEBUG", "Receiver unregistered OK")
                geofenceReceiver = null
            }
        } catch (e: IllegalArgumentException) {
            Log.w("WEB_DEBUG", "Receiver not registered → ${e.message}")
        } catch (e: Exception) {
            Log.e("WEB_DEBUG", "unregisterReceiver ERROR → ${e.message}")
        }

        super.onDestroy()
    }

}
