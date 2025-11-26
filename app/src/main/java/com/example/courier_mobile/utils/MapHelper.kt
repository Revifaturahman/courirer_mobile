package com.example.courier_mobile.utils

import android.graphics.Color
import android.util.Log
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
}
