package com.example.courier_mobile.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.courier_mobile.R
import com.example.courier_mobile.data.model.GetWorkers
import com.example.courier_mobile.databinding.FragmentNextRoleBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NextRoleBottomSheetFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetTheme

    private var _binding: FragmentNextRoleBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var role: String? = null
    private var workers: List<GetWorkers> = emptyList()
    private var listener: OnWorkerSelectedListener? = null

    interface OnWorkerSelectedListener {
        fun onWorkerSelected(workerId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnWorkerSelectedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = arguments?.getString("role")
        workers = arguments?.getParcelableArrayList("workers") ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNextRoleBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.txtRole.text = "Pekerja Selanjutnya: $role"

        val names = workers.map { it.name ?: "" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
        binding.dropdownWorker.setAdapter(adapter)

        var selectedWorkerId: Int? = null

        // Simpan ID ketika item dipilih
        binding.dropdownWorker.setOnItemClickListener { _, _, position, _ ->
            selectedWorkerId = workers[position].id
        }

        binding.dropdownWorker.setOnClickListener {
            binding.dropdownWorker.showDropDown()
        }

        binding.btnConfirmWorker.setOnClickListener {
            selectedWorkerId?.let {
                listener?.onWorkerSelected(it)
                dismiss()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(role: String, workers: List<GetWorkers>): NextRoleBottomSheetFragment {
            return NextRoleBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("role", role)
                    putParcelableArrayList("workers", ArrayList(workers))
                }
            }
        }
    }
}
