package com.example.courier_mobile.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.courier_mobile.adapter.TaskAdapter
import com.example.courier_mobile.databinding.FragmentTaskBinding
import com.example.courier_mobile.viewmodel.GetAllDeliveryViewModel
import com.example.courier_mobile.viewmodel.GetDetailDeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GetAllDeliveryViewModel by viewModels()

    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDeliveries.layoutManager = LinearLayoutManager(requireContext())
        adapter = TaskAdapter(emptyList())
        binding.rvDeliveries.adapter = adapter

        viewModel.fetchData()

        viewModel.resultData.observe(viewLifecycleOwner) { data ->
            if (!data.isNullOrEmpty()) {
                Log.d("TaskFragment", "Data diterima: $data")
                adapter.updateData(data)
            } else {
                Log.w("TaskFragment", "Data kosong")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

