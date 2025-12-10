package com.example.courier_mobile.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.courier_mobile.R
import com.example.courier_mobile.data.model.ResultDelivery
import com.example.courier_mobile.view.DetailRouteActivity
import com.example.courier_mobile.view.UpdateResultBottomSheet

class UpdateResultAdapter(
    private var deliveries: List<ResultDelivery>,
    private val activity: FragmentActivity,
    private var onSubmit: ((Int, String, Map<String, Int>) -> Unit)? = null
) : RecyclerView.Adapter<UpdateResultAdapter.TaskViewHolder>() {

    init {
        Log.d("UR_ADAPTER", "Adapter created. Items = ${deliveries.size}")
    }

    fun updateData(newDeliveries: List<ResultDelivery>) {
        Log.d("UR_ADAPTER", "updateData() called. New size = ${newDeliveries.size}")
        deliveries = newDeliveries
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWorker: TextView = itemView.findViewById(R.id.tvWorker)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvProduct: TextView = itemView.findViewById(R.id.tvProduct)
        val btnStart: TextView = itemView.findViewById(R.id.btnStart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d("UR_ADAPTER", "onCreateViewHolder() dipanggil")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d("UR_ADAPTER", "onBindViewHolder() position = $position")

        val delivery = deliveries[position]

        Log.d("UR_ADAPTER", "Bind delivery → id=${delivery.id}, worker=${delivery.worker_name}")

        holder.tvWorker.text = "Pekerja: ${delivery.worker_name ?: "-"}"
        holder.tvDate.text   = "Tanggal: ${delivery.delivery_date ?: "-"}"

        val productListText = delivery.product_type?.joinToString("\n") {
            "${it.product_type} — ${it.weight} kg"
        } ?: "-"

        holder.tvProduct.text = productListText

        holder.btnStart.setOnClickListener {

            Log.d("UR_ADAPTER", "btnStart CLICKED — preparing BottomSheet")
            val activity = holder.itemView.context as AppCompatActivity

            // Product types
            val productTypes = ArrayList<String>().apply {
                delivery.product_type?.forEach {
                    add(it.product_type ?: "")
                }
            }

            Log.d("UR_ADAPTER", "productTypes = $productTypes")

            val processDate = delivery.delivery_date ?: ""
            Log.d("UR_ADAPTER", "processDate = $processDate")

            try {
                val bottomSheet = UpdateResultBottomSheet.newInstance(
                    delivery.id ?: 0,
                    delivery.worker_role,
                    delivery.worker_id,
                    delivery.status,
                    productTypes,
                    processDate
                )

                Log.d("UR_ADAPTER", "BottomSheet created OK")

                bottomSheet.setOnSubmitListener { detailId, processDate, results ->
                    Log.d("UR_ADAPTER", "onSubmitListener → detailId=$detailId, processDate=$processDate")
                    Log.d("UR_ADAPTER", "results = $results")
                    onSubmit?.invoke(detailId, processDate, results)
                }

                Log.d("UR_ADAPTER", "Listener SET → showing bottom sheet")

                bottomSheet.show(activity.supportFragmentManager, "UpdateResultBottomSheet")
                Log.d("UR_ADAPTER", "BottomSheet.show() OK")

            } catch (e: Exception) {
                Log.e("UR_ADAPTER", "ERROR saat buka BottomSheet → ${e.message}")
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("UR_ADAPTER", "getItemCount() = ${deliveries.size}")
        return deliveries.size
    }
}

