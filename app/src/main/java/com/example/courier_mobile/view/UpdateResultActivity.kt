package com.example.courier_mobile.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.courier_mobile.adapter.UpdateResultAdapter
import com.example.courier_mobile.data.model.UpdateResult
import com.example.courier_mobile.data.model.UpdateResultRequest
import com.example.courier_mobile.databinding.ActivityUpdateResultBinding
import com.example.courier_mobile.viewmodel.GetAllDeliveryViewModel
import com.example.courier_mobile.viewmodel.UpdateResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateResultBinding
    private lateinit var adapter: UpdateResultAdapter

    private val viewModelArrive: GetAllDeliveryViewModel by viewModels()
    private val updateResultViewModel: UpdateResultViewModel by viewModels()

    private var detailId: Int = -1
    private var role: String = ""
    private var status: String = ""
    private var workerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("UR_CRASH", "onCreate — mulai inflate binding")

        try {
            super.onCreate(savedInstanceState)
            binding = ActivityUpdateResultBinding.inflate(layoutInflater)
            Log.d("UR_CRASH", "Berhasil inflate binding")
            setContentView(binding.root)
            Log.d("UR_CRASH", "setContentView OK")

        } catch (e: Exception) {
            Log.e("UR_CRASH", "CRASH saat inflate: ${e.message}")
            e.printStackTrace()
            return
        }

        Log.d("UR_DEBUG", "===== UpdateResultActivity CREATED =====")

        // Safe intent parsing
        try {
            detailId = intent.getIntExtra("detailId", -1)
            role = intent.getStringExtra("role") ?: ""
            status = intent.getStringExtra("status") ?: ""
            workerId = intent.getIntExtra("workerId", 0)
        } catch (e: Exception) {
            Log.e("UR_DEBUG", "ERROR parsing intent: ${e.message}")
        }

        Log.d("UR_DEBUG", "Intent data → detailId=$detailId, role=$role, status=$status, workerId=$workerId")

        setupRecycler()
        observeArriveList()
        observeUpdateResult()

        viewModelArrive.fetchData(
            status = "arrived",
            role = role,
            workerId = workerId
        )
    }

    private fun setupRecycler() {
        adapter = UpdateResultAdapter(emptyList(), this) { detail, processDate, results ->

            Log.d("UR_DEBUG", "CALLBACK from adapter → detail=$detail, processDate=$processDate, results=$results")

            submitUpdateResult(detail, processDate, results)
        }

        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter
    }

    // =========== SAFE SUBMIT FUNCTION ===========
    private fun submitUpdateResult(detailId: Int, processDate: String, results: Map<String, Int>) {

        Log.d("UR_DEBUG", "submitUpdateResult() CALLED")
        Log.d("UR_DEBUG", "detailId=$detailId")
        Log.d("UR_DEBUG", "processDate=$processDate")
        Log.d("UR_DEBUG", "results=$results")

        // VALIDASI ANTI-CRASH
        if (detailId <= 0) {
            Log.e("UR_DEBUG", "❌ detailId INVALID")
            Toast.makeText(this, "Error: detailId tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        if (processDate.isBlank()) {
            Log.e("UR_DEBUG", "❌ processDate KOSONG")
            Toast.makeText(this, "Tanggal proses kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        if (results.isEmpty()) {
            Log.e("UR_DEBUG", "❌ results KOSONG")
            Toast.makeText(this, "Tidak ada hasil yang diinput!", Toast.LENGTH_SHORT).show()
            return
        }

        // SAFE REQUEST BUILD
        val request = try {
            UpdateResultRequest(
                processDate = processDate,
                results = results.map { (type, pcs) ->
                    UpdateResult(
                        productType = type,
                        pcsFinished = pcs
                    )
                }
            )
        } catch (e: Exception) {
            Log.e("UR_DEBUG", "❌ ERROR building request: ${e.message}")
            return
        }

        Log.d("UR_DEBUG", "Request Body → $request")

        updateResultViewModel.updateResult(detailId, request)
    }

    // =========== OBSERVER API LIST ===========
    private fun observeArriveList() {
        viewModelArrive.resultData.observe(this) { list ->
            Log.d("UR_DEBUG", "Arrived List Updated → size=${list.size}")
            adapter.updateData(list)
        }

        viewModelArrive.errorMessage.observe(this) {
            Log.e("UR_DEBUG", "API ERROR: $it")
        }
    }

    // =========== OBSERVER UPDATE RESULT ===========
    private fun observeUpdateResult() {
        updateResultViewModel.updateState.observe(this) { result ->

            Log.d("UR_DEBUG", "Observer updateState triggered")

            result.onSuccess {
                Log.d("UR_DEBUG", "Update SUCCESS → ${it.message}")
                Toast.makeText(this, "Berhasil update hasil!", Toast.LENGTH_SHORT).show()

                viewModelArrive.fetchData("arrived", role, workerId)
            }

            result.onFailure { err ->
                Log.e("UR_DEBUG", "Update FAILED → ${err.message}")
                Toast.makeText(this, "Gagal update: ${err.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
