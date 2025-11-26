package com.example.courier_mobile.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.courier_mobile.R
import com.example.courier_mobile.data.model.GetDetailDelivery
import com.example.courier_mobile.data.model.ResponseDelivery
import com.example.courier_mobile.data.model.ResultDelivery
import com.example.courier_mobile.view.DetailRouteActivity

class TaskAdapter(
    private var deliveries: List<ResultDelivery>
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

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

        // product_type is List<ProductType>
        val productListText = delivery.product_type?.joinToString("\n") { product ->
            "${product.product_type} — ${product.weight} kg"
        } ?: "-"
//        val totalWeight  = delivery.product_type?.sumOf { it.weight?.toDoubleOrNull() ?: 0.0 } ?: 0.0


        holder.tvProduct.text = productListText
//        holder.tvWeight.text  = "Total Berat: $totalWeight kg"

        holder.btnStart.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailRouteActivity::class.java).apply {
                putExtra("detailId", delivery.id)
            }
            Log.d("Button", "Klik ${delivery.id}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = deliveries.size
}

