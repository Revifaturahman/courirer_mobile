package com.example.courier_mobile.utils

import android.graphics.Color
import android.util.Log
import org.json.JSONArray
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MapHelper {

    var startMarker: Marker? = null
    var endMarker: Marker? = null
    var courierMarker: Marker? = null

    fun setupMap(mapView: MapView, routeLatLngs: List<LatLng>) {
        mapView.getMapAsync { map ->
            map.setStyle(
                Style.Builder().fromUri("https://api.maptiler.com/maps/bright/style.json?key=sJOeajil1MhmiilBkZQk")
            ) { _ ->
                map.cameraPosition = CameraPosition.Builder()
                    .target(routeLatLngs.firstOrNull() ?: LatLng(-6.940312, 107.573273))
                    .zoom(14.0)
                    .build()

                if (routeLatLngs.isNotEmpty()) {
                    val polyline = PolylineOptions()
                        .addAll(routeLatLngs)
                        .color(Color.BLUE)
                        .width(5f)
                    map.addPolyline(polyline)

                    startMarker = map.addMarker(MarkerOptions().position(routeLatLngs.first()).title("Start"))
                    endMarker = map.addMarker(MarkerOptions().position(routeLatLngs.last()).title("Finish"))
                }
            }
        }
    }

    fun updateCourierMarker(mapView: MapView, latLng: LatLng) {
        mapView.getMapAsync { map ->
            if (courierMarker == null) {
                courierMarker = map.addMarker(
                    MarkerOptions().position(latLng).title("Posisi Kurir")
                )
            } else {
                courierMarker?.position = latLng
            }
        }
    }

    fun parsePolygonJson(json: String): List<LatLng> {
        val arr = JSONArray(json)
        val list = mutableListOf<LatLng>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(LatLng(obj.getDouble("lat"), obj.getDouble("lng")))
        }
        return list
    }

    fun isPointInsidePolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        var intersectCount = 0
        for (j in polygon.indices) {
            val k = (j + 1) % polygon.size

            val p1 = polygon[j]
            val p2 = polygon[k]

            if (rayCastIntersect(point, p1, p2)) {
                intersectCount++
            }
        }
        return (intersectCount % 2 == 1)
    }

    private fun rayCastIntersect(point: LatLng, p1: LatLng, p2: LatLng): Boolean {
        val aY = p1.latitude
        val bY = p2.latitude
        val aX = p1.longitude
        val bX = p2.longitude
        val pY = point.latitude
        val pX = point.longitude

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
            return false
        }

        val m = (p2.latitude - p1.latitude) / (p2.longitude - p1.longitude)
        val x = (pY - p1.latitude) / m + p1.longitude
        return x > pX
    }

}
