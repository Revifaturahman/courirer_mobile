package com.example.courier_mobile.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.courier_mobile.adapter.TaskAdapter
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
        Log.d("UR_DEBUG", "===== UpdateResultActivity CREATED =====")

        super.onCreate(savedInstanceState)
        binding = ActivityUpdateResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailId = intent.getIntExtra("detailId", -1)
        role = intent.getStringExtra("role").toString()
        status = intent.getStringExtra("status").toString()
        workerId = intent.getIntExtra("workerId", 0)

        Log.d("UR_DEBUG", "Intent data → detailId=$detailId, role=$role, status=$status, workerId=$workerId")

        Log.d("UR_DEBUG", "observeArriveList() DIPANGGIL")
        setupRecycler()
        observeArriveList()

        Log.d("UR_DEBUG", "fetchData() DIPANGGIL")
        viewModelArrive.fetchData(
            status = "arrived",
            role = role,
            workerId = workerId
        )
    }

    private fun setupRecycler() {
        adapter = UpdateResultAdapter(emptyList(), this)
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter

    }

    private fun observeArriveList() {
        Log.d("UR_DEBUG", ">>> MASUK observeArriveList()")

        try {
            viewModelArrive.resultData.observe(this) { list ->
                Log.d("UR_DEBUG", ">>> Observer dipanggil, list size = ${list.size}")
                adapter.updateData(list)
            }
        } catch (e: Exception) {
            Log.e("UR_DEBUG", "ERROR DI observerArriveList(): ${e.message}", e)
        }

        viewModelArrive.errorMessage.observe(this) {
            Log.e("UR_DEBUG", "API ERROR: $it")
        }
    }

}c


