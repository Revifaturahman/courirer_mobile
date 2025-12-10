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
    private val activity: FragmentActivity
) : RecyclerView.Adapter<UpdateResultAdapter.TaskViewHolder>() {

    fun updateData(newDeliveries: List<ResultDelivery>) {
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val delivery = deliveries[position]

        holder.tvWorker.text = "Pekerja: ${delivery.worker_name ?: "-"}"
        holder.tvDate.text   = "Tanggal: ${delivery.delivery_date ?: "-"}"

        val productListText = delivery.product_type?.joinToString("\n") { product ->
            "${product.product_type} — ${product.weight} kg"
        } ?: "-"

        holder.tvProduct.text = productListText

        holder.btnStart.setOnClickListener {
            val activity = holder.itemView.context as AppCompatActivity

            // 🔥🔥🔥 **PASANG DI SINI (Tepat sebelum memanggil BottomSheet)** 🔥🔥🔥
            val productTypes = ArrayList<String>().apply {
                delivery.product_type?.forEach {
                    add(it.product_type ?: "")

                }
            }

            val processDate = delivery.delivery_date ?: ""

            // Setelah itu baru panggil bottom sheet
            val bottomSheet = UpdateResultBottomSheet.newInstance(
                delivery.id ?: 0,
                delivery.worker_role,
                delivery.worker_id,
                delivery.status,
                productTypes,
                processDate
            )

            bottomSheet.show(activity.supportFragmentManager, "UpdateResultBottomSheet")
        }
    }


    override fun getItemCount(): Int = deliveries.size
}
