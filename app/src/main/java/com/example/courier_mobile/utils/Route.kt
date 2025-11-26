package com.example.courier_mobile.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.util.Log

object Route {
    private val client = OkHttpClient()
    private const val TAG = "ROUTE_UTIL"

    /**
     * Fetch OSRM route geometry (GeoJSON LineString) between two coords.
     * Returns geometry JSON string (e.g. {"type":"LineString","coordinates":[...]}) or null.
     */
    suspend fun fetchOsrmRouteGeoJson(
        startLng: Double, startLat: Double,
        destLng: Double, destLat: Double
    ): String? = withContext(Dispatchers.IO) {
        try {
            val url = "https://router.project-osrm.org/route/v1/driving/$startLng,$startLat;$destLng,$destLat?geometries=geojson&overview=full"
            Log.d(TAG, "OSRM URL = $url")

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                Log.d(TAG, "OSRM RESPONSE CODE = ${response.code}")

                if (!response.isSuccessful) {
                    Log.w(TAG, "OSRM not successful: ${response.code}")
                    return@withContext null
                }

                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    Log.w(TAG, "OSRM response body empty")
                    return@withContext null
                }

                Log.d(TAG, "OSRM RESPONSE (truncated) = ${body.take(400)}")

                val json = JSONObject(body)
                val routes = json.optJSONArray("routes")
                if (routes == null || routes.length() == 0) {
                    Log.w(TAG, "OSRM no routes")
                    return@withContext null
                }

                val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                return@withContext geometry.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchOsrmRouteGeoJson error", e)
            return@withContext null
        }
    }
}
