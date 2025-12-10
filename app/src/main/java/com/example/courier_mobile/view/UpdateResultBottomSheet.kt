package com.example.courier_mobile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.courier_mobile.R
import com.example.courier_mobile.adapter.ResultInputAdapter
import com.example.courier_mobile.databinding.FragmentUpdateResultBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateResultBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentUpdateResultBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var processDate: String = ""
    private var productTypes: List<String> = emptyList<String>()
    private var onSubmit: ((String, Map<String, Int>) -> Unit)? = null

    private var detailId: Int = 0
    private var role: String? = null
    private var workerId: Int = 0
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            detailId = it.getInt("detailId")
            role = it.getString("role")
            workerId = it.getInt("workerId")
            status = it.getString("status")
            processDate = it.getString("processDate") ?: ""
            productTypes = it.getStringArrayList("productTypes") ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateResultBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ResultInputAdapter(productTypes)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())

        binding.btnSubmit.setOnClickListener {
            val results = adapter.getResults()
            onSubmit?.invoke(processDate, results)
            dismiss()
        }
    }

    fun setOnSubmitListener(listener: (String, Map<String, Int>) -> Unit) {
        onSubmit = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            detailId: Int,
            role: String?,
            workerId: Int?,
            status: String?,
            productTypes: ArrayList<String>,
            processDate: String
        ): UpdateResultBottomSheet {

            val fragment = UpdateResultBottomSheet()
            val args = Bundle()

            args.putInt("detailId", detailId)
            args.putString("role", role)
            args.putInt("workerId", workerId ?: 0)
            args.putString("status", status)
            args.putStringArrayList("productTypes", productTypes)
            args.putString("processDate", processDate)

            fragment.arguments = args
            return fragment
        }
    }
}

