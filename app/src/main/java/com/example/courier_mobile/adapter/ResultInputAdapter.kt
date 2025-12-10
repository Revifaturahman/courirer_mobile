package com.example.courier_mobile.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.example.courier_mobile.R

class ResultInputAdapter(
    private val products: List<String>
) : RecyclerView.Adapter<ResultInputAdapter.ViewHolder>() {

    private val resultMap = mutableMapOf<String, Int>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProduct = itemView.findViewById<TextView>(R.id.tvProductType)
        val etPcs = itemView.findViewById<EditText>(R.id.etPcs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_input, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvProduct.text = product

        holder.etPcs.addTextChangedListener {
            val value = it.toString().toIntOrNull() ?: 0
            resultMap[product] = value
        }
    }

    fun getResults(): Map<String, Int> = resultMap
}


