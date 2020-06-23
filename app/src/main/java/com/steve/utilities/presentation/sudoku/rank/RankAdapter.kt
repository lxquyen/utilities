package com.steve.utilities.presentation.sudoku.rank

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steve.utilities.R
import com.steve.utilities.core.extensions.inflate
import kotlinx.android.synthetic.main.item_rank.view.*
import kotlinx.android.synthetic.main.item_rank_header.view.lastPlayedTV
import kotlinx.android.synthetic.main.item_rank_header.view.nameTV
import kotlinx.android.synthetic.main.item_rank_header.view.positionTV
import kotlinx.android.synthetic.main.item_rank_header.view.scoreTV

class RankAdapter : RecyclerView.Adapter<RankAdapter.ViewHolder>() {
    companion object {
        const val POSITION_EXPANDED_CHANGE = "POSITION_EXPANDED_CHANGE"
    }

    private var positionExpanded = -1
        set(value) {
            val oldPosition = field

            field = if (field == value) {
                -1
            } else {
                value
            }

            if (oldPosition != field) {
                notifyItemChanged(oldPosition, POSITION_EXPANDED_CHANGE)
            }
            notifyItemChanged(field, POSITION_EXPANDED_CHANGE)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = if (viewType == 1) R.layout.item_rank else R.layout.item_rank_header
        val view = parent.inflate(layoutRes)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 11
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        payloads.forEach {
            when (it) {
                POSITION_EXPANDED_CHANGE -> holder.bindExpandBtn()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bindData() {
            if (absoluteAdapterPosition == 0) return
            itemView.container.setOnClickListener(this)
            itemView.positionTV.text = "$absoluteAdapterPosition"
            itemView.nameTV.text = "Steve"
            itemView.scoreTV.text = "1000"
            itemView.lastPlayedTV.text = "Tue 23"
            bindExpandBtn()
        }

        fun bindExpandBtn() {
            val isSelected = positionExpanded == absoluteAdapterPosition
            itemView.expandBtn.isSelected = isSelected
            itemView.descriptionView.visibility = if (isSelected) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            positionExpanded = absoluteAdapterPosition
        }

    }

}