package com.steve.utilities.presentation.rxjava

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steve.utilities.R
import kotlinx.android.synthetic.main.item_rxjava.view.*

class RxJavaAdapter(private val list: List<Item>) : RecyclerView.Adapter<RxJavaAdapter.RxJavaVH>() {

    inner class RxJavaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item: Item? = null
        @SuppressLint("SetTextI18n")
        fun bindData(item: Item) {
            this.item = item
            itemView.tvRxJava.text = "${item.title} : ${item.isActive}"
            val color = if (item.isActive) Color.RED else Color.WHITE
            itemView.tvRxJava.setBackgroundColor(color)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxJavaVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rxjava, parent, false)
        return RxJavaVH(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: RxJavaVH, position: Int) {
        holder.bindData(list[position])
    }
}