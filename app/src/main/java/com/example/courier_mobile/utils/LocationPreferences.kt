package com.example.courier_mobile.utils

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance (global)
val Context.locationDataStore by preferencesDataStore(name = "location_prefs")

class LocationPreferences(private val context: Context) {

    companion object {
        private val LATITUDE_KEY = doublePreferencesKey("latitude")
        private val LONGITUDE_KEY = doublePreferencesKey("longitude")
    }

    /** Simpan lokasi terbaru */
    suspend fun saveLocation(lat: Double, lng: Double) {
        context.locationDataStore.edit { prefs ->
            prefs[LATITUDE_KEY] = lat
            prefs[LONGITUDE_KEY] = lng
        }
    }

    /** Flow untuk baca lokasi terbaru */
    val lastLocation: Flow<Pair<Double?, Double?>> =
        context.locationDataStore.data.map { prefs ->
            val lat = prefs[LATITUDE_KEY]
            val lng = prefs[LONGITUDE_KEY]
            lat to lng
        }
}
