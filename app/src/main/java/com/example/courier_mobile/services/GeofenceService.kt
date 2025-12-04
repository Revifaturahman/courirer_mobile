package com.example.courier_mobile.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.courier_mobile.utils.LocationTracker

class GeofencingService : Service() {

    companion object {
        var routeGeoJson: String = ""      // polyline OSRM (GeoJSON)
        var bufferRadiusKm: Double = 0.1   // default buffer
    }

    private val TAG = "GEOFENCE_SERVICE"

    private lateinit var locationTracker: LocationTracker

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "SERVICE CREATED")

        startForegroundNotification()

        // Init LocationTracker
        locationTracker = LocationTracker(this) { location ->
            Log.d(TAG, "Service Location → ${location.latitude}, ${location.longitude}")

            // Kirim location ke Activity untuk diproses JS
            val intent = Intent("GEOFENCE_LOCATION")
            intent.putExtra("lat", location.latitude)
            intent.putExtra("lng", location.longitude)
            intent.setPackage(packageName)
            sendBroadcast(intent)

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.w(TAG, "GeofencingService STARTED")

        // Start location tracker
        try {
            locationTracker.startLocationUpdates()
        } catch (e: Exception) {
            Log.e(TAG, "ERROR START TRACKER → ${e.message}")
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundNotification() {
        val channelId = "geofence_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notif = Notification.Builder(this, channelId)
            .setContentTitle("Tracking route…")
            .setContentText("Geofencing is active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notif)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SERVICE DESTROYED → STOP TRACKER")

        // Stop updates
        try {
            locationTracker.stopLocationUpdates()
        } catch (e: Exception) {
            Log.e(TAG, "ERROR STOP TRACKER → ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
