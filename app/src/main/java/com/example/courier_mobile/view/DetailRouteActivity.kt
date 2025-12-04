package com.example.courier_mobile.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.courier_mobile.databinding.ActivityDetailRouteBinding
import com.example.courier_mobile.services.GeofencingService
import com.example.courier_mobile.viewmodel.GetDetailDeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast

@AndroidEntryPoint
class DetailRouteActivity : AppCompatActivity() {

    companion object {
        private const val REQ_PERMS = 1001
    }

    private val requiredPerms = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        // foreground service location only exists on API 34+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34 constant name
            add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }
        // notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    private lateinit var binding: ActivityDetailRouteBinding
    private val viewModel: GetDetailDeliveryViewModel by viewModels()

    private var destLat = 0.0
    private var destLng = 0.0
    private var workerName = ""
    private var detailId = -1
    private var polygonJson: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("DetailRouteDebug", "onCreate START")

        setupToolbar()

        detailId = intent.getIntExtra("detailId", -1)
        Log.d("DetailRouteDebug", "Received detailId = $detailId")

        if (detailId != -1) {
            Log.d("DetailRouteDebug", "Calling viewModel.fetchData()...")
            viewModel.fetchData(detailId)
        } else {
            Log.e("DetailRouteDebug", "ERROR: detailId = -1 (INVALID)")
        }

        observeDetail()
        setupButton()
        ensurePermissions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarDetail.setNavigationOnClickListener { finish() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeDetail() {
        viewModel.resultData.observe(this) { detail ->

            Log.d("DetailRouteDebug", "observeDetail TRIGGERED with data = $detail")

            workerName = detail.worker_name ?: "-"
            destLat = detail.latitude?.toDoubleOrNull() ?: 0.0
            destLng = detail.longitude?.toDoubleOrNull() ?: 0.0

            Log.d("DetailRouteDebug", "Parsed workerName=$workerName lat=$destLat lng=$destLng")

            binding.textWorkerName.text = workerName
            binding.textWorkerRole.text = detail.current_role ?: "-"
            binding.textWorkerAddress.text = "Lat: $destLat , Lng: $destLng"
        }
    }


    private fun setupButton() {
        binding.btnViewRoute.setOnClickListener {
            Log.d("DetailRouteDebug", "Opening RouteMapActivity...")
            val intent = Intent(this, RouteMapActivity::class.java)
            intent.putExtra("destLat", destLat)
            intent.putExtra("destLng", destLng)
            intent.putExtra("workerName", workerName)
            intent.putExtra("detailId", detailId)
            startActivity(intent)
        }
    }



    private fun ensurePermissions() {
        val missing = requiredPerms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), REQ_PERMS)
        } else {
            // already granted — nothing to do here
            Log.d("DetailRouteDebug", "All required permissions already granted")
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMS) {
            val denied = permissions.zip(grantResults.toTypedArray()).filter { it.second != PackageManager.PERMISSION_GRANTED }
            if (denied.isNotEmpty()) {
                Log.w("DetailRouteDebug", "Permissions denied: ${denied.map { it.first }}")
                Toast.makeText(this, "Permission lokasi diperlukan untuk tracking.", Toast.LENGTH_LONG).show()
                // optionally: disable start or show prompt
            } else {
                Log.d("DetailRouteDebug", "Permissions granted")
            }
        }
    }
}
