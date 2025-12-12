package com.example.courier_mobile.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.courier_mobile.adapter.UpdateResultAdapter
import com.example.courier_mobile.data.model.GetWorkers
import com.example.courier_mobile.data.model.NextProcess
import com.example.courier_mobile.data.model.UpdateResult
import com.example.courier_mobile.data.model.UpdateResultRequest
import com.example.courier_mobile.databinding.ActivityUpdateResultBinding
import com.example.courier_mobile.viewmodel.GetAllDeliveryViewModel
import com.example.courier_mobile.viewmodel.GetNextRoleAndGetWorkersViewModel
import com.example.courier_mobile.viewmodel.PostNextProcessViewModel
import com.example.courier_mobile.viewmodel.UpdateResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateResultActivity : AppCompatActivity(), NextRoleBottomSheetFragment.OnWorkerSelectedListener {

    private lateinit var binding: ActivityUpdateResultBinding
    private lateinit var adapter: UpdateResultAdapter

    private val viewModelArrive: GetAllDeliveryViewModel by viewModels()
    private val updateResultViewModel: UpdateResultViewModel by viewModels()
    private val getNextRole: GetNextRoleAndGetWorkersViewModel by viewModels()
    private val getWorkers: GetNextRoleAndGetWorkersViewModel by viewModels()
    private val nextProcessViewModel: PostNextProcessViewModel by viewModels()

    private var detailId: Int = -1
    private var role: String = ""
    private var status: String = ""
    private var workerId: Int = 0
    private var lastResults: Map<String, Int> = emptyMap()


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

        btnNextProcess(detailId)
        setupRecycler()
        observeArriveList()
        observeUpdateResult()

        viewModelArrive.fetchData(
            status = "arrived",
            role = role,
            workerId = workerId
        )
        observeNextProcess()
    }

    private fun setupRecycler() {
        adapter = UpdateResultAdapter(emptyList(), this) { detail, processDate, results ->

            Log.d("UR_DEBUG", "CALLBACK from adapter → detail=$detail, processDate=$processDate, results=$results")

            lastResults = results
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

    private fun btnNextProcess(detailId: Int) {

        // Observe NEXT ROLE
        getNextRole.resultDataNextRole.observe(this) { roleResponse ->
            roleResponse?.let { role ->
                getWorkers.fetchDataWorkers(role.next_role.toString())
            }
        }

        // Observe WORKERS
        getWorkers.resultDataWorkers.observe(this) { workersResponse ->

            val workerItems = workersResponse.workers?.map {
                GetWorkers(it.id, it.name)
            } ?: emptyList()

            val sheet = NextRoleBottomSheetFragment.newInstance(
                role = getNextRole.resultDataNextRole.value?.next_role ?: "",
                workers = workerItems
            )

            sheet.show(supportFragmentManager, "chooseWorker")
        }

        binding.btnSubmit.setOnClickListener {

            val totalFinished = lastResults.values.sum()

            Log.d("NEXT_LOGIC", "lastResults=$lastResults | totalFinished=$totalFinished")

            val nextRole = if (lastResults.isEmpty() || totalFinished == 0) {
                "konveksi"
            } else {
                getNextRole.fetchData(detailId)
                getNextRole.resultDataNextRole.value?.next_role ?: ""
            }

            Log.d("NEXT_LOGIC", "Next role based on PCS = $nextRole")

            // Ambil worker untuk nextRole
            getWorkers.fetchDataWorkers(nextRole)
        }

    }


    override fun onWorkerSelected(workerId: Int) {
        Log.d("NEXT_PROCESS", "Kirim nextProcess dengan PCS = $lastResults")

        val data = NextProcess(
            worker_id = workerId,
            pcs_finished = lastResults
        )

        nextProcessViewModel.updateProcess(detailId, data)
    }

    private fun observeNextProcess() {
        nextProcessViewModel.updateState.observe(this) { result ->

            result.onSuccess {
                Log.d("NEXT_PROCESS", "Next Process SUCCESS → ${it.message}")
                Toast.makeText(this, "Berhasil lanjut ke proses berikutnya!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("GO_TO_TASK", true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(intent)
                finish()


            }

            result.onFailure { err ->
                Log.e("NEXT_PROCESS", "Next Process FAILED → ${err.message}")
                Toast.makeText(this, "Gagal memproses: ${err.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
